package com.lvkang.ppjoke.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedList.BoundaryCallback

abstract class AbsViewModel<T> : ViewModel() {
    protected var config: PagedList.Config
    var dataSource: DataSource<*, *>? = null
        private set
    val pageData: LiveData<PagedList<T>>
    val boundaryPageData =
        MutableLiveData<Boolean>()

    fun getBoundaryPageData(): LiveData<Boolean> {
        return boundaryPageData
    }

    //PagedList数据被加载 情况的边界回调callback
    //但 不是每一次分页 都会回调这里，具体请看 ContiguousPagedList#mReceiver#onPageResult
    //deferBoundaryCallbacks
    var callback: BoundaryCallback<Any> = object : BoundaryCallback<Any>() {
        override fun onZeroItemsLoaded() { //新提交的PagedList中没有数据
            boundaryPageData.postValue(false)
        }

        override fun onItemAtFrontLoaded(itemAtFront: Any) { //新提交的PagedList中第一条数据被加载到列表上
            boundaryPageData.postValue(true)
        }

        override fun onItemAtEndLoaded(itemAtEnd: Any) { //新提交的PagedList中最后一条数据被加载到列表上
        }
    }
    var factory: DataSource.Factory<Any?, Any?> =
        object : DataSource.Factory<Any?, Any?>() {
            override fun create(): DataSource<Any?, Any?> {
                if (dataSource == null || dataSource!!.isInvalid) {
                    dataSource = createDataSource()
                }
                return dataSource!! as DataSource<Any?, Any?>
            }
        }

    abstract fun createDataSource(): DataSource<*, *>?
    //可以在这个方法里 做一些清理 的工作
    override fun onCleared() {}

    init {
        config = PagedList.Config.Builder()
            .setPageSize(10)
            .setInitialLoadSizeHint(12) // .setMaxSize(100)；
// .setEnablePlaceholders(false)
// .setPrefetchDistance()
            .build()
        pageData = LivePagedListBuilder<Any, Any>(factory, config)
            .setInitialLoadKey(0) //.setFetchExecutor()
            .setBoundaryCallback(callback)
            .build() as LiveData<PagedList<T>>
    }
}