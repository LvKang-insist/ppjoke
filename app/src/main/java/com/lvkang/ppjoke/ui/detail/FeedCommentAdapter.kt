package com.lvkang.ppjoke.ui.detail


import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.libcommon.PixUtils
import com.lvkang.ppjoke.databinding.LayoutFeedCommentListItemBinding
import com.lvkang.ppjoke.exeception.AbsPagedListAdapter
import com.lvkang.ppjoke.model.Comment
import com.lvkang.ppjoke.ui.login.UserManager

class FeedCommentAdapter(val activity: AppCompatActivity) :
    AbsPagedListAdapter<Comment, FeedCommentAdapter.ViewHolder>(ItemCallback()) {

    private class ItemCallback : DiffUtil.ItemCallback<Comment>() {

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutFeedCommentListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder2(holder: ViewHolder, pos: Int) {
        val item = getItem(pos)
        if (item != null)
            holder.bindData(item)
    }

    inner class ViewHolder(val view: View, val binding: LayoutFeedCommentListItemBinding) :
        RecyclerView.ViewHolder(view) {
        fun bindData(item: Comment) {
            binding.owner = activity
            binding.comment = item
            binding.labelAuthor.visibility =
                if (item.author!!.userId == UserManager.getUserId()) View.VISIBLE else View.GONE
            binding.commentDelete.visibility =
                if (item.author!!.userId == UserManager.getUserId()) View.VISIBLE else View.GONE

            if (TextUtils.isEmpty(item.imageUrl)) {
                binding.commentCover.visibility = View.VISIBLE
                binding.commentCover.bindData(
                    item.width, item.height, 0,
                    PixUtils.dp2px(200), PixUtils.dp2px(200), item.imageUrl!!
                )
                if (TextUtils.isEmpty(item.videoUrl)) {
                    binding.videoIcon.visibility = View.VISIBLE
                } else {
                    binding.videoIcon.visibility = View.GONE
                }
            } else {
                binding.commentCover.visibility = View.GONE
                binding.videoIcon.visibility = View.GONE
            }
        }
    }
}