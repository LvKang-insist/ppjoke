package com.lvkang.ppjoke.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.view
 * @author 345 QQ:1831712732
 * @time 2020/3/17 22:00
 * @description 用于辅助在视图上加载图片
 */
class PPImageView(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
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
    }
}