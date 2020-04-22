package com.lvkang.ppjoke.exeception

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class AbsPagedListAdapter<T, VH : RecyclerView.ViewHolder>(diffcallback: DiffUtil.ItemCallback<T>) :
    PagedListAdapter<T, VH>(diffcallback) {

    private val mHeaders = SparseArray<View>()
    private val mFooters = SparseArray<View>()

    private var BASE_ITEM_TYPE_HEADER = 100000
    private var BASE_ITEM_TYPE_FOOTER = 200000

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        //查找 key 对应的索引是否大于 =0
        if (mHeaders.indexOfKey(viewType) >= 0) {
            //根据 key 拿到值
            val view = mHeaders.get(viewType)
            return object : RecyclerView.ViewHolder(view) {} as VH
        }
        if (mFooters.indexOfKey(viewType) >= 0) {
            val view = mFooters.get(viewType)
            return object : RecyclerView.ViewHolder(view) {} as VH
        }
        //正常的 itemType
        return onCreateViewHolder2(parent, viewType)
    }

    abstract fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (isHeaderPosition(position) || isFooterPostition(position)) return

        val pos = position - mHeaders.size()
        onBindViewHolder2(holder, pos)
    }

    abstract fun onBindViewHolder2(holder: VH, pos: Int)

    fun addHeaderView(view: View) {
        if (mHeaders.indexOfValue(view) < 0) {
            mHeaders.put(BASE_ITEM_TYPE_HEADER++, view)
            notifyDataSetChanged()
        }
    }

    fun addFooterView(view: View) {
        if (mFooters.indexOfValue(view) < 0) {
            mFooters.put(BASE_ITEM_TYPE_FOOTER++, view)
            notifyDataSetChanged()
        }
    }

    fun removeHeaderView(view: View) {
        val index = mHeaders.indexOfValue(view)
        if (index <0 ) return
        mHeaders.remove(index)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + mHeaders.size() + mFooters.size()
    }

    /**
     * 正常类型的条目数量
     */
    fun getOriginItemCount(): Int {
        return itemCount - mHeaders.size() - mFooters.size()
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeaderPosition(position)) {
            //返回该 position 对应的 headerView 的 viewType
            return mHeaders.keyAt(position)
        }
        if (isFooterPostition(position)) {
            //keyAt：根据索引获取对应的 key
            return mFooters.keyAt(position - getOriginItemCount() - mHeaders.size())
        }
        //子类实现
        return getItemViewType2(position - mHeaders.size())
    }

     fun getItemViewType2(position: Int): Int  = 0

    /**
     * 该 pos 是否在 mFooters 中
     */
    private fun isFooterPostition(position: Int): Boolean {
        return position >= getOriginItemCount() + mFooters.size()
    }

    /**
     * 该 pos 是否在 mHeaders
     */
    private fun isHeaderPosition(position: Int): Boolean {
        return position < mHeaders.size()
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(AdapterDataObserverProxy(observer))
    }

    inner class AdapterDataObserverProxy(private val observer: RecyclerView.AdapterDataObserver) :
        RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            observer.onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            observer.onItemRangeChanged(positionStart + mHeaders.size(), itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            observer.onItemRangeChanged(positionStart + mHeaders.size(), itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            observer.onItemRangeInserted(positionStart + mHeaders.size(), itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            observer.onItemRangeRemoved(positionStart + mHeaders.size(), itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            observer.onItemRangeMoved(
                fromPosition + mHeaders.size(), toPosition + mHeaders.size(), itemCount
            )
        }

    }
}