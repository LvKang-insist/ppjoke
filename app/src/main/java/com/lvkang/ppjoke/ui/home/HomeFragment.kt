package com.lvkang.ppjoke.ui.home

import androidx.lifecycle.Observer
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.libnavannotation.FragmentDestination
import com.lvkang.ppjoke.model.Feed
import com.lvkang.ppjoke.ui.AbsListFragment
import com.lvkang.ppjoke.ui.MutableDataSource
import com.scwang.smartrefresh.layout.api.RefreshLayout

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.home
 * @author 345 QQ:1831712732
 * @time 2020/3/5 0:05
 * @description
 */

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : AbsListFragment<Feed, HomeViewModel>() {

    /**
     * 设置 Adapter
     */
    override fun getAdapter(): PagedListAdapter<Feed, RecyclerView.ViewHolder> {

        //mCategory
        val feedType: String? = if (arguments == null) "all" else arguments!!.getString("feedType")
        return FeedAdapter(context!!, feedType!!) as PagedListAdapter<Feed, RecyclerView.ViewHolder>
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


    override fun afterCreateView() {
        mViewModel?.cacheLiveData?.observe(this,
            Observer<PagedList<Feed>> { t -> if (t != null) submitList(t) })
    }


}