package com.lvkang.ppjoke.ui.exoplayer

import android.view.ViewGroup

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.exoplayer
 * @author 345 QQ:1831712732
 * @time 2020/4/17 17:09
 * @description
 */
interface IPlayTarget {

    /**
     * PlayerView 所在的容器
     */
    fun getOwner(): ViewGroup

    /**
     * 满足播放条件
     */
    fun onActive()

    /**
     * 不满足播放条件，需要停止播放
     */
    fun inActive()

    /**
     * 当前是否播放
     */
    fun isPlaying(): Boolean
}