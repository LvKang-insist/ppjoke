package com.lvkang.ppjoke.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.exoplayer2.Player
import com.lvkang.libcommon.PixUtils
import com.lvkang.libcommon.view.PPImageView
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.ui.exoplayer.IPlayTarget
import com.lvkang.ppjoke.ui.exoplayer.PageListPlayManager
import kotlin.math.exp

class ListPlayerView : FrameLayout, IPlayTarget, Player.EventListener {

    private var bufferView: View? = null
    var cover: PPImageView? = null
    var blur: PPImageView? = null
    var playBtn: AppCompatImageView? = null
    var mCategory: String? = null
    var mVideoUrl: String? = null

    private var isPlaying = false

    constructor(context: Context) : this(context, null) {}

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {}

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context, attributeSet, defStyleAttr
    ) {

        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true)
        bufferView = findViewById(R.id.buffer_view)
        cover = findViewById(R.id.cover)
        blur = findViewById(R.id.blur_background)
        playBtn = findViewById(R.id.play_btn)
        playBtn?.setOnClickListener {
            if (isPlaying()) {
                inActive()
            } else {
                onActive()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        PageListPlayManager.get(mCategory!!).controlView?.show()
        return true
    }

    /**
     * @category :页面的标志
     */
    fun bindData(
        category: String, widthPx: Int, heightPx: Int,
        coverUrl: String, videoUrl: String
    ) {
        mCategory = category
        mVideoUrl = videoUrl

        //封面图片
        PPImageView.setImageUrl(cover!!, coverUrl, false)

        //宽小于高，则高斯模糊背景显示出来
        if (widthPx < heightPx) {
            PPImageView.setBlurImageUrl(blur!!, coverUrl, 10)
            blur!!.visibility = View.VISIBLE
        } else {
            blur!!.visibility = View.INVISIBLE
        }
        setSize(widthPx, heightPx)

    }

    protected fun setSize(widthPx: Int, heightPx: Int) {
        //这里主要是做视频宽大于高，或者高大于宽时，视频的等比缩放
        val maxWidth = PixUtils.getScreenWidth()
        val maxHeight = maxWidth

        //当前 View 的宽高
        val layoutwidth = maxHeight
        val layoutHeight: Int

        //封面的宽高
        val coverWidth: Int
        val coverHeight: Int
        //如果宽大于高
        if (widthPx >= heightPx) {
            coverWidth = maxWidth
            coverHeight = (heightPx / (widthPx * 1.0f / maxWidth)).toInt()
            layoutHeight = coverHeight
        } else {
            //宽度小于高，则高度为最大值，并计算宽度
            coverHeight = maxHeight
            layoutHeight = coverHeight
            coverWidth = (widthPx / (heightPx * 1.0f / maxHeight)).toInt()
        }
        //当前 View 的宽高
        val params = layoutParams
        params.width = layoutwidth
        params.height = layoutHeight
        layoutParams = params

        //高斯模糊的宽高
        val blurParams = blur!!.layoutParams
        blurParams.width = layoutwidth
        blurParams.height = layoutHeight
        blur!!.layoutParams = blurParams

        //封面的位置 和 宽高
        val coverParams: LayoutParams = cover!!.layoutParams as LayoutParams
        coverParams.width = coverWidth
        coverParams.height = coverHeight
        coverParams.gravity = Gravity.CENTER
        cover!!.layoutParams = coverParams

        //播放按钮居中
        val playBtnParams: LayoutParams =
            playBtn!!.layoutParams as LayoutParams
        playBtnParams.gravity = Gravity.CENTER
        playBtn!!.layoutParams = playBtnParams
    }

    override fun getOwner(): ViewGroup {
        return this
    }

    override fun onActive() {
        val pageListPlay = PageListPlayManager.get(mCategory!!)
        val exoPlayer = pageListPlay.exoPlayer
        val playView = pageListPlay.playView
        val controlView = pageListPlay.controlView

        if (playView != null && exoPlayer != null && controlView != null) {
            val parent = playView.parent
            //把展示视频画面的 View 添加到 ItemView 的容器上
            if (parent != this) {
                if (parent != null) {
                    (parent as ViewGroup).removeView(playView)
                    (parent as ListPlayerView).inActive()
                }
                //显示在高斯模糊的背景之上
                this.addView(playView, 1, cover?.layoutParams)
            }


            val ctrlParent = controlView.parent
            //把视频控制器添加到 ItemView 的容器上
            if (ctrlParent != this) {
                if (ctrlParent != null) {
                    (ctrlParent as ViewGroup).removeView(controlView)
                }
                val params = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.gravity = Gravity.BOTTOM
                this.addView(controlView, params)
                controlView.setVisibilityListener {
                    playBtn?.visibility = it
                    playBtn?.setImageResource(if (isPlaying()) R.drawable.icon_video_pause else R.drawable.icon_video_play)
                }
            }
            controlView.show()

            //如果需要播放的 url 和 正在播放的 url 相同
            if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {

            } else {
                val mediaSource = PageListPlayManager.createMediaSource(mVideoUrl!!)
                exoPlayer.prepare(mediaSource)
                //循环播放
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                //播放状态监听
                exoPlayer.addListener(this)
            }
            //当视频资源缓冲好之后立马播放
            exoPlayer.playWhenReady = true
        }
    }

    override fun inActive() {
        val pageListPlay = PageListPlayManager.get(mCategory!!)
        //暂停视频播放
        pageListPlay.exoPlayer?.playWhenReady = false
        playBtn?.visibility = View.VISIBLE
        playBtn?.setImageResource(R.drawable.icon_video_play)
    }

    /**
     * 监听视频的播放状态
     */
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        //如果视频已经开始播放了
        val pageListPlay = PageListPlayManager.get(mCategory!!)
        val exoPlayer = pageListPlay.exoPlayer
        if (paddingStart == Player.STATE_READY && exoPlayer!!.bufferedPosition.toInt() != 0) {
            cover?.visibility = View.INVISIBLE
            bufferView?.visibility = View.INVISIBLE
        } else if (paddingStart == Player.STATE_BUFFERING) {
            bufferView?.visibility = View.VISIBLE
        }
        isPlaying =
            playbackState == Player.STATE_READY && exoPlayer!!.bufferedPosition.toInt() != 0 && playWhenReady
        playBtn?.setImageResource(if (isPlaying) R.drawable.icon_video_pause else R.drawable.icon_video_play)
    }

    /**
     * 由于列表有复用的情况，所以需要在下面方法中做一下状态的重置
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isPlaying = false
        bufferView?.visibility = View.GONE
        cover?.visibility = View.VISIBLE
        playBtn?.visibility = View.VISIBLE
        playBtn?.setImageResource(R.drawable.icon_video_play)
    }


    override fun isPlaying(): Boolean {
        return isPlaying
    }

}

