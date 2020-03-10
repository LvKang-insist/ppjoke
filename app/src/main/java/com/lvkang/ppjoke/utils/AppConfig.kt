package com.lvkang.ppjoke.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.lvkang.ppjoke.model.BottomBar
import com.lvkang.ppjoke.model.Destination
import java.util.*

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.utils
 * @author 345 QQ:1831712732
 * @time 2020/3/9 21:53
 * @description
 */
class AppConfig {

    companion object {

        private var mDestConfig: Map<String, Destination>? = null
        private var mBottomBar: BottomBar? = null


        /**
         * 获取底部 Tab 的信息
         */
        fun getBottomBar(): BottomBar {
            if (mBottomBar == null) {
                mBottomBar =
                    JSON.parseObject(parseFile("main_tabs_config.json"), BottomBar::class.java)
            }
            return mBottomBar!!
        }


        /**
         * 返回被注解页面的数据
         */
        fun getDestConfig(): Map<String, Destination> {
            if (mDestConfig.isNullOrEmpty()) {
                mDestConfig = JSON.parseObject(parseFile("destination.json"),
                    object : TypeReference<HashMap<String, Destination>>() {})
            }
            return mDestConfig!!
        }

        /**
         * 获取文件内容
         */
        private fun parseFile(fileName: String): String {
            val assets = AppGlobals.getApplication().resources.assets
            assets.open(fileName).use {
                return it.bufferedReader().readText()
            }
        }
    }

}