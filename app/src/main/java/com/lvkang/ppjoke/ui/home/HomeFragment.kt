package com.lvkang.ppjoke.ui.home

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.libnavannotation.FragmentDestination
import com.lvkang.ppjoke.model.Feed
import com.lvkang.ppjoke.ui.AbsListFragment
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

        val feedType: String? = if (arguments == null) "all" else arguments!!.getString("feedType")

        return FeedAdapter(context!!, feedType!!) as PagedListAdapter<Feed, RecyclerView.ViewHolder>
    }

    /**
     * 刷新监听
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    /**
     * 加载监听
     */
    override fun onLoadMore(refreshLayout: RefreshLayout) {

    }


    override fun afterCreateView() {

    }


}