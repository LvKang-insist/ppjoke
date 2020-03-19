package com.lvkang.ppjoke.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lvkang.libnavannotation.FragmentDestination
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.model.Feed
import com.lvkang.ppjoke.ui.AbsListFragment
import com.lvkang.ppjoke.utils.loe
import com.scwang.smartrefresh.layout.api.RefreshLayout

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.home
 * @author 345 QQ:1831712732
 * @time 2020/3/5 0:05
 * @description
 */

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : AbsListFragment<Feed>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        loe("onCreateView", "HomeFragment")
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

    /**
     * 设置 Adapter
     */
    override fun getAdapter(): PagedListAdapter<Feed, RecyclerView.ViewHolder> {
        TODO()
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


}