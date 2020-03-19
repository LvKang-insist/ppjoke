package com.lvkang.ppjoke.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

abstract class AbsListFragment<T, M : AbsViewModel<T>> : Fragment(), OnRefreshListener,
    OnLoadMoreListener {

    private var binding: LayoutRefreshViewBinding? = null
    private var mAdapter: PagedListAdapter<T, ViewHolder>? = null
    private var mRefreshLayout: SmartRefreshLayout? = null
    private var mRecyclerView: RecyclerView? = null
    private var mEmptyView: EmptyView? = null

    private var mViewModel: M? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutRefreshViewBinding.inflate(inflater, container, false)

        mRecyclerView = binding?.recyclerView
        mRefreshLayout = binding?.refreshLayout
        mEmptyView = binding?.emptyView


        mRefreshLayout?.setEnableRefresh(true)
        mRefreshLayout?.setEnableLoadMore(true)
        mRefreshLayout?.setOnRefreshListener(this)
        mRefreshLayout?.setOnLoadMoreListener(this)


        mRecyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRecyclerView?.itemAnimator = null
        mAdapter = getAdapter()
        mRecyclerView?.adapter = mAdapter
        val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.list_divider)!!)
        mRecyclerView?.addItemDecoration(decoration)

        afterCreateView()

        init(binding!!.root,null)
        return binding?.root
    }

    abstract fun afterCreateView()

     fun init(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = javaClass.genericSuperclass as ParameterizedType
        var arguments = type.actualTypeArguments
        if (arguments.size > 1) {
            val argument = arguments[1]
            val modeClazz = (argument as Class<*>).asSubclass(AbsViewModel::class.java)
            mViewModel = ViewModelProviders.of(this).get(modeClazz) as M
            //触发页面初始化数据加载的逻辑
            //触发页面初始化数据加载的逻辑
            mViewModel!!.pageData
                .observe(viewLifecycleOwner,
                    object : Observer<PagedList<*>> {
                        override fun onChanged(t: PagedList<*>?) {
                            mAdapter!!.submitList(t as PagedList<T>?)
                        }
                    })

            mViewModel!!.boundaryPageData.observe(viewLifecycleOwner,
                Observer<Boolean> { t -> finishRefresh(t) })
        }

    }

    /**
     * 下拉刷新后的数据
     */
    fun submitList(pageList: PagedList<T>) {
        //加载数据
        if (pageList.size > 0) mAdapter?.submitList(pageList)

        finishRefresh(pageList.size > 0)
    }

    fun finishRefresh(hasData: Boolean) {
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
    abstract fun getAdapter(): PagedListAdapter<T, ViewHolder>

}