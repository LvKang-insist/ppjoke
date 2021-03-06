package com.lvkang.ppjoke.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.libcommon.extention.LiveDataBus
import com.lvkang.ppjoke.InteractionPresenter
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.databinding.LayoutFeedTypeImageBinding
import com.lvkang.ppjoke.databinding.LayoutFeedTypeVideoBinding
import com.lvkang.ppjoke.model.Feed
import com.lvkang.ppjoke.ui.detail.FeedDetailActivity
import com.lvkang.ppjoke.ui.view.ListPlayerView

/**
 * 适配器
 */
open class FeedAdapter(private val mContext: Context, private val mCategory: String) :
    PagedListAdapter<Feed, FeedAdapter.ViewHolder>(ItemCallBack()) {

    var inflater: LayoutInflater? = null

    init {
        inflater = LayoutInflater.from(mContext)
    }

    /**
     * 用于计算列表中两个条目直接差异的回调
     */
    class ItemCallBack : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            //用于判断两个条目是否为相同项
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            //用于判断两个条目的内容是否相同
            return false
        }

    }

    /**
     * 返回条目
     */
    override fun getItemViewType(position: Int): Int {
        val feed = getItem(position)
        if (feed != null) {
            if (feed.itemType == Feed.TYPE_IMAGE) {
                return R.layout.layout_feed_type_image
            } else if (feed.itemType == Feed.TYPE_VIDEO) {
                return R.layout.layout_feed_type_video
            }
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            R.layout.layout_feed_type_image -> {
                val binding = DataBindingUtil.inflate<LayoutFeedTypeImageBinding>(
                    inflater!!, viewType, parent, false
                )
                ViewHolder(binding.root, binding, mCategory)
            }
            else -> {
                val binding = DataBindingUtil.inflate<LayoutFeedTypeVideoBinding>(
                    inflater!!, viewType, parent, false
                )
                ViewHolder(binding.root, binding, mCategory)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //
        holder.bindData(getItem(position)!!, mContext)
        holder.itemView.setOnClickListener {
            FeedDetailActivity.startFeedDetailActivity(mContext, getItem(position), mCategory)
            if (mFeedObserver == null) {
                mFeedObserver = FeedObserver()
                LiveDataBus.with(InteractionPresenter.DATA_FROM_INTERACTION)
                    .observe(mContext as LifecycleOwner, mFeedObserver as Observer<in Any>)
            }
        }

    }

    private var mFeedObserver: FeedObserver? = null

    class FeedObserver : Observer<Feed> {
        var mFeed: Feed? = null
        override fun onChanged(t: Feed?) {
            if (mFeed != null) {
                if (mFeed!!.id != t?.id){
                    return
                }
                mFeed?.author = t.author
                mFeed?.ugc = t.ugc
                mFeed?.notifyChange()
            }
        }

    }

    class ViewHolder(val view: View, val binding: ViewDataBinding, private val mCategory: String) :
        RecyclerView.ViewHolder(view) {
        var listPlayView: ListPlayerView? = null

        fun bindData(item: Feed, context: Context) {
            if (binding is LayoutFeedTypeImageBinding) {
                //绑定数据
                binding.feed = item
                binding.feedIamge.bindData(item.width, item.height, 16, item.cover)
                binding.owner = context as LifecycleOwner
            } else if (binding is LayoutFeedTypeVideoBinding) {
                binding.feed = item
                binding.listPlayerView.bindData(
                    mCategory, item.width, item.height, item.cover!!, item.url!!
                )
                binding.owner = context as LifecycleOwner
                listPlayView = binding.listPlayerView
            }
        }

        fun isVideoItem(): Boolean {
            return binding is LayoutFeedTypeVideoBinding
        }

        fun getListPlayerView(): ListPlayerView {
            return listPlayView!!
        }
    }


}