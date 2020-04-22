package com.lvkang.ppjoke.ui.detail

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.databinding.ActivityDetailTypeImageBinding
import com.lvkang.ppjoke.databinding.LayoutFeedDetailTypeImageHeaderBinding
import com.lvkang.ppjoke.model.Feed

class ImageViewHandler : ViewHandler {
    var mActivity: FragmentActivity
    var mImageBinding: ActivityDetailTypeImageBinding
    lateinit var mHeaderBinding: LayoutFeedDetailTypeImageHeaderBinding

    constructor(activity: FragmentActivity) : super(activity) {
        this.mActivity = activity
        mImageBinding =  DataBindingUtil.setContentView(
            mActivity, R.layout.activity_detail_type_image
        )
        mRecyclerView = mImageBinding.recyclerView
        mInateractionBinding = mImageBinding.inateraction
    }


    override fun bindInitData(feed: Feed) {
        super.bindInitData(feed)
        mImageBinding.feed = mFeed
        mHeaderBinding = LayoutFeedDetailTypeImageHeaderBinding.inflate(
            LayoutInflater.from(mActivity), mRecyclerView, false
        )
        mHeaderBinding.feed = feed
        val ppImageView = mHeaderBinding.headerImage
        ppImageView.bindData(
            feed.width, feed.height, if (feed.width > feed.height) 0 else 16, feed.cover
        )
        mFeedCommentAdapter.addHeaderView(mHeaderBinding.root)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visible = mHeaderBinding.root.top <= -mImageBinding.titleLayout.measuredHeight
                mImageBinding.authorInfoLayout.root.visibility =
                    if (visible) View.VISIBLE else View.GONE
                mImageBinding.title.visibility = if (visible) View.GONE else View.VISIBLE

            }
        })
    }
}