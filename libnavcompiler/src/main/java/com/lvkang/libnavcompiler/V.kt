package com.lvkang.libnavcompiler

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.google.auto.service.AutoService
import com.lvkang.libnavannotation.ActivityDestination
import com.lvkang.libnavannotation.FragmentDestination
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.StandardLocation

/**
 * @author 345 QQ:1831712732
 * @name ppjoke
 * @class name：com.lvkang.libnavcompiler
 * @time 2020/3/5 22:32
 * @description APP 页面导航收集注解处理器
 *
 *
 * AutoService：标记后 annotationProcessor Project() 应用一下，编译时就能自动执行该类了
 *
 *
 *
 * SupportedSourceVersion：声明我们所支持的 jdk 版本
 *
 *
 *
 * SupportedAnnotationTypes：生命该注解处理想要处理那些注解
 *
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(
    "com.lvkang.libnavannotation.FragmentDestination",
    "com.lvkang.libnavannotation.ActivityDestination"
)
class V : AbstractProcessor() {
    private var messager: Messager? = null
    private var filer: Filer? = null
    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        //日志打印,在java环境下不能使用android.util.log.e()
        messager = processingEnv.messager
        //文件处理工具
        filer = processingEnv.filer
    }

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean { //通过处理器环境上下文roundEnv分别获取 项目中标记的FragmentDestination.class 和ActivityDestination.class注解。
//此目的就是为了收集项目中哪些类 被注解标记了
        val fragmentElements =
            roundEnv.getElementsAnnotatedWith(
                FragmentDestination::class.java
            )
        val activityElements =
            roundEnv.getElementsAnnotatedWith(
                ActivityDestination::class.java
            )
        if (!fragmentElements.isEmpty() || !activityElements.isEmpty()) {
            val destMap =
                HashMap<String?, JSONObject?>()
            //分别 处理FragmentDestination  和 ActivityDestination 注解类型
//并收集到destMap 这个map中。以此就能记录下所有的页面信息了
            handleDestination(fragmentElements, FragmentDestination::class.java, destMap)
            handleDestination(activityElements, ActivityDestination::class.java, destMap)
            //app/src/main/assets
            var fos: FileOutputStream? = null
            var writer: OutputStreamWriter? = null
            try { //filer.createResource()意思是创建源文件
//我们可以指定为class文件输出的地方，
//StandardLocation.CLASS_OUTPUT：java文件生成class文件的位置，/app/build/intermediates/javac/debug/classes/目录下
//StandardLocation.SOURCE_OUTPUT：java文件的位置，一般在/ppjoke/app/build/generated/source/apt/目录下
//StandardLocation.CLASS_PATH 和 StandardLocation.SOURCE_PATH用的不多，指的了这个参数，就要指定生成文件的pkg包名了
                val resource = filer!!.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    OUTPUT_FILE_NAME
                )
                val resourcePath = resource.toUri().path
                messager!!.printMessage(Diagnostic.Kind.NOTE, "resourcePath:$resourcePath")
                //由于我们想要把json文件生成在app/src/main/assets/目录下,所以这里可以对字符串做一个截取，
//以此便能准确获取项目在每个电脑上的 /app/src/main/assets/的路径
                val appPath =
                    resourcePath.substring(0, resourcePath.indexOf("app") + 4)
                val assetsPath = appPath + "src/main/assets/"
                val file = File(assetsPath)
                if (!file.exists()) {
                    file.mkdirs()
                }
                //此处就是稳健的写入了
                val outPutFile =
                    File(file, OUTPUT_FILE_NAME)
                if (outPutFile.exists()) {
                    outPutFile.delete()
                }
                outPutFile.createNewFile()
                //利用fastjson把收集到的所有的页面信息 转换成JSON格式的。并输出到文件中
                val content = JSON.toJSONString(destMap)
                fos = FileOutputStream(outPutFile)
                writer = OutputStreamWriter(fos, "UTF-8")
                writer.write(content)
                writer.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (writer != null) {
                    try {
                        writer.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return true
    }

    private fun handleDestination(
        elements: Set<Element>,
        annotationClaz: Class<out Annotation>,
        destMap: HashMap<String?, JSONObject?>
    ) {
        for (element in elements) { //TypeElement是Element的一种。
//如果我们的注解标记在了类名上。所以可以直接强转一下。使用它得到全类名
            val typeElement = element as TypeElement
            //全类名com.mooc.ppjoke.home
            val clazName = typeElement.qualifiedName.toString()
            //页面的id.此处不能重复,使用页面的类名做hascode即可
            val id = Math.abs(clazName.hashCode())
            //页面的pageUrl相当于隐士跳转意图中的host://schem/path格式
            var pageUrl: String? = null
            //是否需要登录
            var needLogin = false
            //是否作为首页的第一个展示的页面
            var asStarter = false
            //标记该页面是fragment 还是activity类型的
            var isFragment = false
            val annotation = element.getAnnotation(annotationClaz)
            if (annotation is FragmentDestination) {
                val dest = annotation
                pageUrl = dest.pageUrl
                asStarter = dest.asStarter
                needLogin = dest.needLogin
                isFragment = true
            } else if (annotation is ActivityDestination) {
                val dest = annotation
                pageUrl = dest.pageUrl
                asStarter = dest.asStarter
                needLogin = dest.needLogin
                isFragment = false
            }
            if (destMap.containsKey(pageUrl)) {
                messager!!.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl：$clazName")
            } else {
                val `object` = JSONObject()
                `object`["id"] = id
                `object`["needLogin"] = needLogin
                `object`["asStarter"] = asStarter
                `object`["pageUrl"] = pageUrl
                `object`["className"] = clazName
                `object`["isFragment"] = isFragment
                destMap[pageUrl] = `object`
            }
        }
    }

    companion object {
        private const val OUTPUT_FILE_NAME = "destination.json"
    }
}