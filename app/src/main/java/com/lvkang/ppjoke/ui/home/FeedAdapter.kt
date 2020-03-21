package com.lvkang.ppjoke.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.databinding.LayoutFeedTypeImageBinding
import com.lvkang.ppjoke.databinding.LayoutFeedTypeVideoBinding
import com.lvkang.ppjoke.model.Feed

class FeedAdapter(val mContext: Context, val mCategory: String) :
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
//         return   oldItem.equals(newItem)
            return false
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = if (viewType == Feed.TYPE_IMAGE) {
            DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.layout_feed_type_image, parent,
                false
            )
        } else {
            DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.layout_feed_type_video, parent,
                false
            )
        }
        return ViewHolder(binding.root, binding, mCategory)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position)!!)
    }

    class ViewHolder(val view: View, val binding: ViewDataBinding, val mCategory: String) :
        RecyclerView.ViewHolder(view) {
        fun bindData(item: Feed) {
            if (binding is LayoutFeedTypeImageBinding) {
                //绑定数据
                binding.feed = item
                binding.feedIamge.bindData(item.width, item.height, 16, item.cover!!)
            } else if (binding is LayoutFeedTypeVideoBinding) {
                binding.feed = item
                binding.listPlayerView.bindData(
                    mCategory, item.width,
                    item.height, item.cover!!, item.url!!
                )
            }
        }

    }

}