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

open class HomeViewModel : AbsViewModel<Feed>() {

    @Volatile
    private var witchCache = true

    override fun createDataSource(): DataSource<Int, Feed> {
        return mDataSource
    }

    private val mDataSource = object : ItemKeyedDataSource<Int, Feed>() {
        override fun loadInitial(
            params: LoadInitialParams<Int>, callback: LoadInitialCallback<Feed>
        ) {
            //做加载初始化数据时使用
            loadData(0, callback)
            witchCache = false
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Feed>) {
            //加载分页数据的
            loadData(params.key, callback)
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Feed>) {
            callback.onResult(Collections.emptyList())
            //是能够向前加载数据的
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
            .addParam("feedId", 0)
            .addParam("pageCount", 10)
            .responseType(object : TypeReference<ArrayList<Feed>>() {}.type)

        //如果需要加载缓存
        if (witchCache) {
            //修改缓存策略
            request.cacheStratgy(Request.CACHE_ONLY)
            request.execute(object : JsonCallback<List<Feed>>() {
                override fun onCacheSuccess(response: ApiResponse<List<Feed>>) {
                    Log.e("onCacheSuccess：", "onCacheSuccess ${response.body?.size}")
                }
            })
        }

        var netRequest: Request<List<Feed>, *>? = null

        netRequest = if (witchCache) {
            request.clone()
        } else {
            request
        }

        netRequest.cacheStratgy(
            if (key == 0) {
                Request.NET_CACHE
            } else Request.NET_ONLY
        )

        val response = request.execute()
        val data = if (response.body == null) Collections.emptyList<Feed>() else response.body
        callback.onResult(data!!)
        Log.e("url：", "${data.size}")
        //通过 liveData 发送数据，告诉 UI ，是否应该主动关闭上拉加载分页的动画
       /* if (key > 0) {
            boundaryPageData.postValue(data.isNotEmpty())
        }*/
    }
}