package com.lvkang.ppjoke

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSONObject
import com.hjq.toast.ToastUtils
import com.lvkang.libcommon.AppGlobals
import com.lvkang.libnetwork.ApiResponse
import com.lvkang.libnetwork.ApiService
import com.lvkang.libnetwork.JsonCallback
import com.lvkang.ppjoke.model.Comment
import com.lvkang.ppjoke.model.Feed
import com.lvkang.ppjoke.model.User
import com.lvkang.ppjoke.ui.ShareDialog
import com.lvkang.ppjoke.ui.login.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class InteractionPresenter {
    companion object {

        /**
         * 点赞，取消点赞的行为
         */
        @JvmStatic
        fun toggleFeedLike(owner: LifecycleOwner, feed: Feed) {
            if (!UserManager.isLogin()) {
                val loginLiveData = UserManager.login(AppGlobals.getApplication())
                loginLiveData.observe(owner, object : Observer<User> {
                    override fun onChanged(t: User?) {
                        if (t != null) {
                            toogeFeedLikeInternal(
                                feed
                            )
                        }
                        loginLiveData.removeObserver(this)
                    }
                })
                return
            }
            toogeFeedLikeInternal(feed)
        }

        @JvmStatic
        private fun toogeFeedLikeInternal(feed: Feed) {
            ApiService.get<JSONObject>("/ugc/toggleFeedLike")
                .addParam("userId", UserManager.getUserId())
                .addParam("itemId", feed.itemId)
                .execute(object : JsonCallback<JSONObject>() {
                    override fun onSuccess(response: ApiResponse<JSONObject>) {
                        val hasLiked = response.body?.getBoolean("hasLiked")
                        feed.ugc?.hasLiked = hasLiked!!
                    }
                })
        }

        /**
         * 踩
         */
        @JvmStatic
        fun toggleFeedDiss(owner: LifecycleOwner, feed: Feed) {
            if (!UserManager.isLogin()) {
                val loginLiveData = UserManager.login(AppGlobals.getApplication())
                loginLiveData.observe(owner, object : Observer<User> {
                    override fun onChanged(t: User?) {
                        if (t != null) {
                            toggleFeedDissInternal(
                                feed
                            )
                        }
                        loginLiveData.removeObserver(this)
                    }
                })
                toggleFeedDissInternal(feed)
            }
        }

        @JvmStatic
        private fun toggleFeedDissInternal(feed: Feed) {
            ApiService.get<JSONObject>("/ugc/dissFeed")
                .addParam("userId", UserManager.getUserId())
                .addParam("itemId", feed.itemId)
                .execute(object : JsonCallback<JSONObject>() {
                    override fun onSuccess(response: ApiResponse<JSONObject>) {
                        if (response.body != null) {
                            val hasdiss = response.body?.getBoolean("hasdiss")
                            feed.ugc?.hasdiss = hasdiss!!
                        }
                    }
                })
        }

        @JvmStatic
        fun openShare(context: Context, feed: Feed) {
            val format = String.format("", feed.itemId, Date().time, UserManager.getUserId())
            val shareDialog = ShareDialog(context)
            shareDialog.setShareContent("我是分享的内容")
            shareDialog.setShareItemClickListener(View.OnClickListener {
                ApiService.get<JSONObject>("ugc/increaseShareCount")
                    .addParam("itemId", feed.itemId)
                    .execute(object : JsonCallback<JSONObject>() {
                        override fun onSuccess(response: ApiResponse<JSONObject>) {
                            if (response.body != null) {
                                val count = response.body?.getIntValue("count")
                                feed.ugc?.shareCount = count!!
                            }
                        }
                    })
            })
            shareDialog.show()
        }

        @JvmStatic
        fun toggleCommentLike(owner: LifecycleOwner, comment: Comment) {
            if (!UserManager.isLogin()) {
                val liveData = UserManager.login(AppGlobals.getApplication())
                liveData.observe(owner, object : Observer<User> {
                    override fun onChanged(t: User?) {
                        liveData.removeObserver(this)
                        if (t != null) {
                            toggleCommentLikeInternal(
                                comment
                            )
                        }
                    }
                })
                return
            }
            toggleCommentLikeInternal(comment)
        }

        @JvmStatic
        private fun toggleCommentLikeInternal(comment: Comment) {
            ApiService.get<JSONObject>("/ugc/toggleCommentLike")
                .addParam("commentId", comment.id)
                .addParam("userId", UserManager.getUserId())
                .execute(object : JsonCallback<JSONObject>() {
                    override fun onSuccess(response: ApiResponse<JSONObject>) {
                        if (response.body != null) {
                            val hasLiked = response.body?.getBooleanValue("hasLiked")
                            comment.ugc!!.hasLiked = hasLiked!!
                        }
                    }
                })
        }

        /**
         * 收藏帖子
         */
        @JvmStatic
        fun toggleFeedFavorite(owner: LifecycleOwner, feed: Feed?) {
            if (feed == null) {
                ToastUtils.show("空")
                return
            }
            if (!UserManager.isLogin()) {
                val liveData = UserManager.login(AppGlobals.getApplication())
                liveData.observe(owner, object : Observer<User> {
                    override fun onChanged(t: User?) {
                        liveData.removeObserver(this)
                        if (t != null) {
                            toggleFeedFavorite(feed)
                        }
                    }
                })
                return
            }


            toggleFeedFavorite(feed)
        }

        @JvmStatic
        fun toggleFeedFavorite(feed: Feed) {
            ApiService.get<JSONObject>("/ugc/toggleFavorite")
                .addParam("itemId", feed.itemId)
                .addParam("userId", UserManager.getUserId())
                .execute(object : JsonCallback<JSONObject>() {
                    override fun onSuccess(response: ApiResponse<JSONObject>) {
                        if (response.body != null) {
                            val value = response.body?.getBooleanValue("hasFavorite")
                            feed.ugc?.hasFavorite = value!!
                        }
                    }

                    override fun onError(response: ApiResponse<JSONObject>) {
                        showToast(response.message)
                    }
                })
        }


        private fun showToast(message: String?) {
            GlobalScope.launch(Dispatchers.Main) {
                ToastUtils.show(message)
            }
        }
    }


}