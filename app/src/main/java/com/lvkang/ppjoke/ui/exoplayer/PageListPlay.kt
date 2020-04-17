package com.lvkang.ppjoke.ui.exoplayer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.lvkang.libcommon.AppGlobals
import com.lvkang.ppjoke.R

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.exoplayer
 * @author 345 QQ:1831712732
 * @time 2020/4/17 15:43
 * @description
 */

class PageListPlay {

    var exoPlayer: SimpleExoPlayer?
    var playView: PlayerView?
    var controlView: PlayerControlView?
    var playUrl: String? = null

    @SuppressLint("InflateParams")
    constructor() {
        //播放器实例的创建
        val application = AppGlobals.getApplication()
        /**
         * DefaultRenderersFactory：视频渲染的工厂类
         * DefaultTrackSelector：音视频轨道选择器
         * DefaultLoadControl：缓存控制器
         */
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            application, DefaultRenderersFactory(application),
            DefaultTrackSelector(), DefaultLoadControl()
        )

        //视频View
        playView = LayoutInflater.from(application)
            .inflate(R.layout.layout_exo_player_view, null, false) as PlayerView
        //视频播放控制View
        controlView = LayoutInflater.from(application)
            .inflate(R.layout.layout_exo_player_contorller_view, null, false) as PlayerControlView

        //将播放器 和 视频View 关联起来
        playView?.player = exoPlayer
        //关联视频控制
        controlView?.player = exoPlayer
    }

    fun release() {
        if (exoPlayer != null) {
            exoPlayer?.playWhenReady = false
            exoPlayer?.stop(true)
            exoPlayer?.release()
            exoPlayer = null
        }
        if (playView != null) {
            playView?.player = null
            playView = null
        }
        if (controlView != null) {
            controlView?.player = null
            controlView?.setVisibilityListener(null)
            controlView = null
        }
    }
}

