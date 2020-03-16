package com.lvkang.ppjoke.ui.my

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.lvkang.libnavannotation.FragmentDestination
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.utils.alert
import com.lvkang.ppjoke.utils.download.DownLoadLaunch
import com.lvkang.ppjoke.utils.download.DownloadModel
import com.lvkang.ppjoke.utils.download.onStateListener
import com.lvkang.ppjoke.utils.loe
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.coroutines.launch
import java.io.File

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.sofa
 * @author 345 QQ:1831712732
 * @time 2020/3/10 23:51
 * @description
 */
@FragmentDestination(pageUrl = "main/tabs/my")
class MyFragment : Fragment() {

    private val mDownloadModel: DownloadModel = DownloadModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        loe("onCreateView", "MyFragment")
        return inflater.inflate(R.layout.fragment_my, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        showDialogButtom.setOnClickListener {
            lifecycleScope.launch {
                val myChoice = context?.alert("Warning!", "Do you want this?")
                Toast.makeText(context, "My choice is ${myChoice}", Toast.LENGTH_LONG).show()
            }
        }

        DownLoadLaunch.create(this, "https://kotlinlang.org/docs/kotlin-docs.pdf",
            "Kotlin-Docs.pdf", object : onStateListener {
                override fun start() {
                    Toast.makeText(context, "开始下载", Toast.LENGTH_LONG).show()
                }

                override fun process(value: Int) {
                    downloadButton.text = "Downloading (${value}%)"
                }

                override fun error(throwable: Throwable) {
                    Toast.makeText(context, "下在出错：${throwable.message}", Toast.LENGTH_LONG).show()
                }

                override fun donel(file: File) {
                    downloadButton.text = "下载成功"
                    Toast.makeText(context, "下载完成：" + file.path, Toast.LENGTH_LONG).show()
                }
            })
    }

}