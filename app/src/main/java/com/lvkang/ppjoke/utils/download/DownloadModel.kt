package com.lvkang.ppjoke.utils.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn

class DownloadModel : ViewModel() {

    val downloadStateLiveData =
        MutableLiveData<DownLoadManager.DownloadStatus>(DownLoadManager.DownloadStatus.None)



    suspend fun download(url: String, fileName: String) {
        DownLoadManager.download(url, fileName)
            .flowOn(Dispatchers.IO)
            .collect {
                //发送数据
                downloadStateLiveData.value = it
            }
    }
}