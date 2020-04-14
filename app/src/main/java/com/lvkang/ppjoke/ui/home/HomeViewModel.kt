package com.lvkang.ppjoke.ui.home

import android.util.Log
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.alibaba.fastjson.TypeReference
import com.lvkang.libnetwork.ApiResponse
import com.lvkang.libnetwork.ApiService
import com.lvkang.libnetwork.JsonCallback
import com.lvkang.libnetwork.Request
import com.lvkang.ppjoke.model.Feed
import com.lvkang.ppjoke.ui.AbsViewModel
import java.util.*

open class HomeViewModel : AbsViewModel<Int, Feed>() {

    @Volatile
    private var witchCache = true

    override fun createDataSource(): DataSource<Int, Feed> {
        return mDataSource
    }

    /**
     * 创建 DataSource<Key,Value> 数据源：
     * key 对应加载数据的条件信息，value 对应数据的实体类
     * 默认提供了三种类型
     * -- PageKeyedDataSource<Key,Value>：适用于目标数据根据页码信息请求数据的场景
     * -- ItemKeyedDataSource<Key,Value>：使用目标数据的加载依赖于特定的 item 信息
     * -- PositionalDataSource<T>：适用于目标数据总数固定，通过特定的位置加载数据
     */
    private val mDataSource = object : ItemKeyedDataSource<Int, Feed>() {
        override fun loadInitial(
            params: LoadInitialParams<Int>, callback: LoadInitialCallback<Feed>
        ) {
            // 先加载缓存，在加载网络数据，网络成功后在更新本地缓存
            loadData(0, callback)
            witchCache = false
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Feed>) {
            //加载分页数据的
            loadData(params.key, callback)
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Feed>) {
            callback.onResult(Collections.emptyList())
            //是能够向前加载数据的，目前用不到，如上写即可
        }

        override fun getKey(item: Feed): Int {
            //返回键
            return item.id
        }
    }

    private fun loadData(key: Int, callback: ItemKeyedDataSource.LoadCallback<Feed>) {
        val request = ApiService.get<List<Feed>>("/feeds/queryHotFeedsList")
            .addParam("feedType", null)
            .addParam("userId", 0)
            //feedId：最后一条item 的id
            .addParam("feedId", key)
            //每页多少条
            .addParam("pageCount", 10)
            .responseType(object : TypeReference<ArrayList<Feed>>() {}.type)


        //如果需要加载缓存
        if (witchCache) {
            //修改缓存策略
            request.cacheStratgy(Request.CACHE_ONLY)
            //读取缓存
            request.execute(object : JsonCallback<List<Feed>>() {
                override fun onCacheSuccess(response: ApiResponse<List<Feed>>) {
                    Log.e("onCacheSuccess：", "onCacheSuccess ${response.body?.size}")
                }
            })
        }
        //如果之前使用了缓存，则 clone 一个新的网络请求
        val netRequest = if (witchCache) {
            request.clone()
        } else {
            request
        }
        // 修改缓存策略，如果首次加载网络数据并进行缓存,否则只加载网络数据
        netRequest.cacheStratgy(if (key == 0) Request.NET_CACHE else Request.NET_ONLY)


        val response = netRequest.execute()
        val data = if (response.body == null) Collections.emptyList<Feed>() else response.body
        //将数据回传回去
        if (data != null && data.isNotEmpty()) {
            callback.onResult(data)
        }
        Log.e("url：", "${data!!.size}")

        Log.e("loadData", "loadData: key:$key")


        // key 大于 0 则会为下拉加载
        if (key > 0) {
            //关闭下拉加载动画
            boundaryPageData.postValue(data.isNotEmpty())
        }
    }
}