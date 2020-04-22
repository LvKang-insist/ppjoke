package com.lvkang.ppjoke.ui.detail

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.libcommon.EmptyView
import com.lvkang.ppjoke.databinding.LayoutFeedDetailBottomInateractionBinding
import com.lvkang.ppjoke.model.Comment
import com.lvkang.ppjoke.model.Feed

abstract class ViewHandler(private val mActivity: FragmentActivity) {
    var mFeed: Feed? = null
    lateinit var mRecyclerView: RecyclerView
    lateinit var mInateractionBinding: LayoutFeedDetailBottomInateractionBinding
    val mFeedCommentAdapter: FeedCommentAdapter by lazy {
        FeedCommentAdapter(mActivity as AppCompatActivity)
    }
    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(FeedDetailViewModel::class.java)
    }


    @CallSuper
    open fun bindInitData(feed: Feed) {
        this.mFeed = feed
        mInateractionBinding.owner = mActivity
        mInateractionBinding.feed = mFeed
        mRecyclerView.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.itemAnimator = null
        mRecyclerView.adapter = mFeedCommentAdapter

        viewModel.itemId = feed.itemId
        viewModel.pageData.observe(mActivity,
            Observer<PagedList<Comment>> { t ->
                mFeedCommentAdapter.submitList(t)
                handleEmpty(t.size > 0)
            }
        )
    }

    var mEmptyView: EmptyView? = null
    fun handleEmpty(b: Boolean) {
        if (b) {
            if (mEmptyView != null) {
                mFeedCommentAdapter.removeHeaderView(mEmptyView!!)
            }
        } else {
            if (mEmptyView == null) {
                mEmptyView = EmptyView(mActivity)
                mEmptyView!!.layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                mEmptyView!!.setTitle("还没有评论，快来抢沙发吧！")
                mFeedCommentAdapter.addHeaderView(mEmptyView!!)
            }
        }
    }
}