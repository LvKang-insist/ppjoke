package com.lvkang.ppjoke

import androidx.lifecycle.ViewModel
import androidx.paging.PagedList

abstract class AbsViewModel<T> : ViewModel() {
    init {
        PagedList.Config.Builder()
            //分页加载数据的数量
            .setPageSize(10)
            //初始化时加载的数量
            .setInitialLoadSizeHint(12)
            //总共少数据
//            .setMaxSize()
//            .setEnablePlaceholders(false)
//            .setPrefetchDistance()
            .build()
    }
}