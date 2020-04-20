package com.lvkang.ppjoke.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.libnavannotation.FragmentDestination
import com.lvkang.ppjoke.model.Feed
import com.lvkang.ppjoke.ui.AbsListFragment
import com.lvkang.ppjoke.ui.MutableDataSource
import com.lvkang.ppjoke.ui.exoplayer.PageListPlayDetector
import com.lvkang.ppjoke.ui.exoplayer.PageListPlayManager
import com.scwang.smartrefresh.layout.api.RefreshLayout

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.home
 * @author 345 QQ:1831712732
 * @time 2020/3/5 0:05
 * @description
 */

@Suppress("UNCHECKED_CAST")
@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : AbsListFragment<Feed, HomeViewModel>() {

    var playDetector: PageListPlayDetector? = null

    var feedType: String? = null

    companion object {
        @JvmStatic
        fun newInstance(feedType: String): HomeFragment {
            val args = Bundle()
            args.putString("feedType", feedType)
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel?.cacheLiveData?.observe(this,
            Observer<PagedList<Feed>> { t -> if (t != null) submitList(t) })
        //列表滚动自动播放
        playDetector = PageListPlayDetector(this, mRecyclerView!!)

        mViewModel?.feedType = feedType
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            playDetector?.onPause()
        } else {
            playDetector?.onResume()
        }
    }

    /**
     * 设置 Adapter
     */
    override fun getAdapter(): PagedListAdapter<Feed, RecyclerView.ViewHolder> {

        //mCategory
        feedType = if (arguments == null) "all" else arguments!!.getString("feedType")

        return object : FeedAdapter(context!!, feedType!!) {
            //视图被添加到窗口时调用
            override fun onViewAttachedToWindow(holder: ViewHolder) {
                super.onViewAttachedToWindow(holder)
                if (holder.isVideoItem()) {
                    playDetector?.addTarget(holder.getListPlayerView())
                }
            }

            //视图被划出时
            override fun onViewDetachedFromWindow(holder: ViewHolder) {
                super.onViewDetachedFromWindow(holder)
                if (holder.isVideoItem()) {
                    playDetector?.remoeTarget(holder.getListPlayerView())
                }
            }

        } as PagedListAdapter<Feed, RecyclerView.ViewHolder>
    }

    /**
     * 刷新监听
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.dataSource?.invalidate()
    }

    /**
     * 加载更多
     */
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        val feed = mAdapter!!.currentList?.get(mAdapter!!.itemCount - 1)
        if (feed != null) {
            mViewModel?.loadAfter(feed.id, object : ItemKeyedDataSource.LoadCallback<Feed>() {
                override fun onResult(data: MutableList<Feed>) {
                    if (data.size > 0) {
                        val dataSource = MutableDataSource<Int, Feed>()
                        dataSource.data.addAll(data)
                        val pageList =
                            dataSource.buildNewPageList(mAdapter!!.currentList!!.config)
                        submitList(pageList)
                    }
                }
            })
        }
    }


    override fun onPause() {
        super.onPause()
        playDetector?.onPause()
    }

    override fun onResume() {
        super.onResume()
        //判断父布局是否为 fragment
        if (parentFragment != null) {
            if (parentFragment!!.isVisible && isVisible) {
                playDetector?.onResume()
            }
        } else {
            if (isVisible) {
                playDetector?.onResume()
            }
        }
    }

    override fun onDestroy() {
        PageListPlayManager.release(feedType!!)
        super.onDestroy()
    }
}