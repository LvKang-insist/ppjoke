package com.lvkang.ppjoke.ui.detail

import androidx.paging.ItemKeyedDataSource
import com.alibaba.fastjson.TypeReference
import com.lvkang.libnetwork.ApiService
import com.lvkang.ppjoke.model.Comment
import com.lvkang.ppjoke.ui.AbsViewModel
import com.lvkang.ppjoke.ui.login.UserManager
import java.util.*

class FeedDetailViewModel : AbsViewModel<Int, Comment>() {

    var itemId: Long = 0

    override fun createDataSource(): DataSource<Int, Comment>? {
        return DataSource()
    }


    inner class DataSource<T, U> : ItemKeyedDataSource<Int, Comment>() {
        override fun loadInitial(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Comment>
        ) {
            loadData(params.requestedInitialKey!!, params.requestedLoadSize, callback)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Comment>) {
            loadData(params.key, params.requestedLoadSize, callback)
        }

        private fun loadData(
            key: Int,
            requestedLoadSize: Int,
            callback: LoadCallback<Comment>
        ) {
            val response = ApiService.get<List<Comment>>("/comment/queryFeedComments")
                .addParam("id", key)
                .addParam("itemId", itemId)
                .addParam("userId", UserManager.getUserId())
                .addParam("pageCount", requestedLoadSize)
                .responseType(object : TypeReference<List<Comment>>() {}.type)
                .execute()

            val list = response.body ?: Collections.emptyList()
            callback.onResult(list)
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Comment>) {
            callback.onResult(Collections.emptyList())
        }

        override fun getKey(item: Comment): Int {
            return item.id
        }

    }
}