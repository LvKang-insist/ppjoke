package com.lvkang.ppjoke.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lvkang.libcommon.EmptyView
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.databinding.LayoutRefreshViewBinding
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import java.lang.reflect.ParameterizedType

/**
 * 基础的 ListFragment，配置列表通用的配置
 */
@Suppress("UNCHECKED_CAST")
abstract class AbsListFragment<V, M : AbsViewModel<Int, V>> : Fragment(),
    OnRefreshListener, OnLoadMoreListener {

    private var binding: LayoutRefreshViewBinding? = null
    var mAdapter: PagedListAdapter<V, ViewHolder>? = null
    private var mRefreshLayout: SmartRefreshLayout? = null
    var mRecyclerView: RecyclerView? = null
    private var mEmptyView: EmptyView? = null

    var mViewModel: M? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutRefreshViewBinding.inflate(inflater, container, false)

        mRecyclerView = binding?.recyclerView
        mRefreshLayout = binding?.refreshLayout
        mEmptyView = binding?.emptyView

        //注册事件
        mRefreshLayout?.setEnableRefresh(true)
        mRefreshLayout?.setEnableLoadMore(true)
        mRefreshLayout?.setOnRefreshListener(this)
        mRefreshLayout?.setOnLoadMoreListener(this)


        mRecyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRecyclerView?.itemAnimator = null
        mAdapter = getAdapter()
        mRecyclerView?.adapter = mAdapter

        //分割线
        val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_divider)!!)
        mRecyclerView?.addItemDecoration(decoration)


        init()
        return binding?.root
    }


    private fun init() {
        //利用 子类传递的泛型参数实例化出 absViewModel 对象
        val type = javaClass.genericSuperclass as ParameterizedType
        val arguments = type.actualTypeArguments
        if (arguments.size > 1) {
            val argument = arguments[1]
            val modeClazz = (argument as Class<*>).asSubclass(AbsViewModel::class.java)
            mViewModel = ViewModelProvider.NewInstanceFactory().create(modeClazz) as M

            //触发页面初始化数据加载的逻辑
            mViewModel!!.pageData
                .observe(viewLifecycleOwner,
                    Observer<PagedList<V>> { t -> mAdapter!!.submitList(t) })

            //监听数据边界，已决定是否关闭上拉加载的动画
            mViewModel!!.boundaryPageData.observe(viewLifecycleOwner,
               Observer {
                      finishRefresh(it)
               })
        }

    }

    /**
     * Pading 框架在做下拉刷新的时候数据必须是 PagedList，
     * 通过 Adapter 的 submitList 完成数据的加载
     * 下拉刷新后的数据
     */
    fun submitList(pageList: PagedList<V>) {
        //加载数据
        if (pageList.size > 0) mAdapter?.submitList(pageList)

        finishRefresh(pageList.size > 0)
    }

    private fun finishRefresh(hasData: Boolean) {
        val currentList = mAdapter!!.currentList
        var has: Boolean = false

        if (hasData || currentList != null && currentList.size > 0) has = true
        val state = mRefreshLayout!!.state
        if (state.isFooter && state.isOpening) {
            mRefreshLayout?.finishLoadMore()
        } else {
            mRefreshLayout?.finishRefresh()
        }

        //有数据隐藏，否则展示
        if (has) mEmptyView?.visibility = View.GONE else mEmptyView?.visibility = View.VISIBLE

    }


    /**
     * 如果要使用 padding 分页，则适配器必须是 PagedListAdapter
     */
    abstract fun getAdapter(): PagedListAdapter<V, ViewHolder>

}