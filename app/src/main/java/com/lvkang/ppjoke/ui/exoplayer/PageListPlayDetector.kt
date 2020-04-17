package com.lvkang.ppjoke.ui.exoplayer

import android.graphics.Point
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.exoplayer
 * @author 345 QQ:1831712732
 * @time 2020/4/17 16:59
 * @description 列表滚动式自动播放的逻辑
 */
class PageListPlayDetector {

    private val mTargets = arrayListOf<IPlayTarget>()
    private val mRecyclerView: RecyclerView
    private var rvLocation: Point? = null
    private var playingTarget: IPlayTarget? = null

    fun addTarget(target: IPlayTarget) {
        mTargets.add(target)
    }

    fun remoeTarget(target: IPlayTarget) {
        mTargets.remove(target)
    }

    constructor(owner: LifecycleOwner, recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        //监听宿主的生命周期
        owner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        recyclerView.adapter?.unregisterAdapterDataObserver(mDataObserver)
                        owner.lifecycle.removeObserver(this)
                    }
                    else -> {
                    }
                }
            }
        })

        recyclerView.adapter?.registerAdapterDataObserver(mDataObserver)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //列表滚动停止之后
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    autoPlay()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //如果当前视频正在播放，且不再屏幕内
                if (playingTarget != null && playingTarget!!.isPlaying()
                    && !isTargetInBound(playingTarget!!)
                ) {
                    playingTarget!!.inActive()
                }
            }
        })
    }

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            //当有数据被添加后，这里就会被回调
            autoPlay()
        }
    }

    /**
     * 播放逻辑
     */
    private fun autoPlay() {
        if (mTargets.size <= 0 || mRecyclerView.childCount <= 0) return

        //如果上一个视频正在播放，则直接 return
        if (playingTarget != null && playingTarget!!.isPlaying() && isTargetInBound(playingTarget!!)) return

        var activeTarget: IPlayTarget? = null
        mTargets.forEach {
            val inBounds = isTargetInBound(it)
            if (inBounds) {
                activeTarget = it
                return@forEach
            }
        }
        if (activeTarget != null) {
            if (playingTarget != null && playingTarget!!.isPlaying()) {
                playingTarget!!.inActive()
            }
            playingTarget = activeTarget
            playingTarget?.onActive()
        }
    }

    /**
     * view 是否在 recyclerview 的显示区域
     */
    private fun isTargetInBound(target: IPlayTarget): Boolean {
        ensureRecyclerViewLocation()
        val owner = target.getOwner()
        //owner 如果不可见 则返回 false
        if (!owner.isShown || !owner.isAttachedToWindow) return false

        //获取坐标
        val localtion = IntArray(2)
        owner.getLocationOnScreen(localtion)

        //owner 中心点在屏幕的位置
        val center = localtion[1] + owner.height / 2
        // owner 的中心点 如果大于 rv 的top 且小于 bottom，则返回true
        return center >= rvLocation!!.x && center < rvLocation!!.y
    }

    /**
     * recyclerview 的位置
     */
    private fun ensureRecyclerViewLocation(): Point {
        if (rvLocation == null) {
            val location = IntArray(2)
            mRecyclerView.getLocationOnScreen(location)
            val top = location[1]
            val bottom = top + mRecyclerView.height
            rvLocation = Point(top, bottom)
        }
        return rvLocation!!
    }

    fun onPause() {
        playingTarget?.inActive()
    }

    fun onResume() {
        playingTarget?.onActive()
    }
}