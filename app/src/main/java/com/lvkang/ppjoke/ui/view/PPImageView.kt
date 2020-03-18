package com.lvkang.ppjoke.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lvkang.libcommon.PixUtils

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.view
 * @author 345 QQ:1831712732
 * @time 2020/3/17 22:00
 * @description 用于辅助在视图上加载图片
 */
class PPImageView(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    /**
     * dataBinding 提供的注解，用来标记一个方法，能够给布局文件去使用
     * value ：在 xml 中生成对应的方法
     * requireAll：如果为 false，只有调用了一个参数，都会调用这个方法。否则要将 value 的参数全部调用才会调用此方法
     * 方法的第一个参数必须是当前类本身
     * 在调用方法的时候 dataBinding 会把参数按照顺序传进来
     */
    @SuppressLint("CheckResult")
    @BindingAdapter(value = ["image_url", "isCircle"], requireAll = false)
    fun setImageUrl(view: PPImageView, imageUrl: String, isCircle: Boolean) {
        val load = Glide.with(view).load(imageUrl)
        if (isCircle) {
            load.transform(CircleCrop())
        }
        val layoutParams = view.layoutParams

        //将图给定为指定的大小，以防止资源浪费
        if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
            load.override(layoutParams.width, layoutParams.height)
        }
        load.into(view)
    }

    fun bindData(
        widthPx: Int, heightPx: Int, marginLeft: Int, imageUrl: String
    ) {
        bindData(
            widthPx, heightPx, marginLeft,
            PixUtils.getScreenWidth(),
            PixUtils.getScreenHeight(),
            imageUrl
        )
    }

    fun bindData(
        widthPx: Int, heightPx: Int, marginLeft: Int,
        maxWidth: Int, maxHeight: Int, imageUrl: String
    ) {
        //如果图片的宽度或者高度小于等于0
        if (widthPx <= 0 || heightPx <= 0) {
            Glide.with(this).load(imageUrl).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable, transition: Transition<in Drawable>?
                ) {
                    val height = resource.intrinsicHeight
                    val width = resource.intrinsicWidth
                    setSize(width, height, marginLeft, maxWidth, maxHeight)
                    //设置图片
                    setImageDrawable(resource)
                }
            })
            return
        }
        setSize(widthPx, heightPx, marginLeft, maxWidth, maxHeight)
        setImageUrl(this, imageUrl, false)

    }

    /**
     * 设置 imageView 宽高
     */
    private fun setSize(width: Int, height: Int, marginLeft: Int, maxWidth: Int, maxHeight: Int) {
        val finalWidth: Int
        val finalHeight: Int
        //图片的宽度大于高度
        if (width > height) {
            //宽为最大值
            finalWidth = maxWidth
            //高度自适应
            finalHeight = height / (width * 1.0f / finalWidth).toInt()
        } else {
            finalHeight = maxHeight
            finalWidth = width / (height * 1.0f / finalHeight).toInt()
        }

        //设置 宽高
        val params = ViewGroup.MarginLayoutParams(finalWidth, finalHeight)
        if (height > width) {
            params.leftMargin = PixUtils.dp2px(marginLeft)
        }
        layoutParams = params
    }


    fun setBllurImageUrl(coverUrl: String, i: Int) {
        Glide.with(this).load(coverUrl).override(50)
            .dontAnimate()
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable, transition: Transition<in Drawable>?
                ) {
                    //注意不能是 setImageDrawable，否则可能图片无法充满控件的宽和高
                    background = resource
                }
            })
    }
}