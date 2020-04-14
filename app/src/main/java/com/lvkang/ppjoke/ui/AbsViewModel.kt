package com.lvkang.ppjoke.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedList.BoundaryCallback

/**
 * ViewModel
 */
abstract class AbsViewModel<K, V> : ViewModel {


    var dataSource: DataSource<K, V>? = null
    //列表数据
    val pageData: LiveData<PagedList<V>>
    //监听数据的边界
    val boundaryPageData = MutableLiveData<Boolean>()


    private var config: PagedList.Config = PagedList.Config.Builder()
        .setPageSize(10)
        .setInitialLoadSizeHint(12) // .setMaxSize(100)； // .setEnablePlaceholders(false) // .setPrefetchDistance()
        .build()

    constructor() {
        // 通过 pageData.observe() 加载分页数据
        pageData = LivePagedListBuilder<K, V>(factory, config)
            .setInitialLoadKey(0 as K) //.setFetchExecutor()
            .setBoundaryCallback(callback)
            .build()
    }

    var factory =
        object : DataSource.Factory<K, V>() {
            override fun create(): DataSource<K, V> {
                if (dataSource == null || dataSource!!.isInvalid) {
                    dataSource = createDataSource()
                }
                return dataSource!!
            }
        }

    fun getBoundaryPageData(): LiveData<Boolean> {
        return boundaryPageData
    }

    //PagedList数据被加载 情况的边界回调callback
    //但 不是每一次分页 都会回调这里，具体请看 ContiguousPagedList#mReceiver#onPageResult
    //deferBoundaryCallbacks
    var callback: BoundaryCallback<V> = object : PagedList.BoundaryCallback<V>() {

        override fun onZeroItemsLoaded() {
            //新提交的PagedList中没有数据
            boundaryPageData.postValue(false)
        }

        override fun onItemAtFrontLoaded(itemAtFront: V) {
            //新提交的PagedList中第一条数据被加载到列表上
            boundaryPageData.postValue(true)
        }

        override fun onItemAtEndLoaded(itemAtEnd: V) {
            //新提交的PagedList中最后一条数据被加载到列表上
        }
    }


    abstract fun createDataSource(): DataSource<K, V>?
    //可以在这个方法里 做一些清理 的工作
    override fun onCleared() {}


}