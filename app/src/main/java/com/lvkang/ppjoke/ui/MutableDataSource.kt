package com.lvkang.ppjoke.ui

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import java.util.*

class MutableDataSource<Key, Valule> : PageKeyedDataSource<Key, Valule>() {
    val data = mutableListOf<Valule>()

    fun buildNewPageList(config: PagedList.Config): PagedList<Valule> {
        return PagedList.Builder<Key, Valule>(this, config)
            .setFetchExecutor(ArchTaskExecutor.getIOThreadExecutor())
            .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
            .build()
    }

    override fun loadInitial(
        params: LoadInitialParams<Key>,
        callback: LoadInitialCallback<Key, Valule>
    ) {
        callback.onResult(data, null, null)
    }

    override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Key, Valule>) {
        callback.onResult(Collections.emptyList(), null)
    }

    override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Key, Valule>) {
        callback.onResult(Collections.emptyList(), null)
    }

}