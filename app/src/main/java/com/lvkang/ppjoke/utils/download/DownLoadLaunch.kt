package com.lvkang.ppjoke.utils.download

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch
import java.io.File


interface onStateListener {
    fun start()
    fun process(value: Int)
    fun error(throwable: Throwable)
    fun donel(file: File)
}

object DownLoadLaunch {

    private val mDownloadModel: DownloadModel = DownloadModel()

    fun create(
        owner: LifecycleOwner,
        url: String,
        fileName: String,
        stateListener: onStateListener
    ) {
        mDownloadModel.downloadStateLiveData.observe(owner) { status ->
            when (status) {
                DownLoadManager.DownloadStatus.None -> {
                    owner.lifecycleScope.launch {
                        mDownloadModel.download(url, fileName)
                    }
                }
                is DownLoadManager.DownloadStatus.Progress -> {
                    stateListener.process(status.value)
                }
                is DownLoadManager.DownloadStatus.Error -> {
                    stateListener.error(status.throwable)
                }
                is DownLoadManager.DownloadStatus.Donel -> {
                    stateListener.donel(status.file)
                }
            }
        }
    }
}