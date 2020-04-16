package com.lvkang.libcommon.view

import android.annotation.SuppressLint
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import com.lvkang.libcommon.R

class ViewHelper {

    companion object {
        const val RADIUS_ALL = 0
        const val RADIUS_LEFT = 1
        const val RADIUS_TOP = 2
        const val RADIUS_RIGHT = 3
        const val RADIUS_BOTTOM = 4


        @SuppressLint("Recycle")
        @JvmStatic
        fun setViewOutLine(
            view: View, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
        ) {
            val array = view.context.obtainStyledAttributes(
                attributeSet, R.styleable.ViewOutLineStrategy, defStyleAttr, defStyleRes
            )
            val radius =
                array.getDimensionPixelOffset(R.styleable.ViewOutLineStrategy_radius, 0)
            val radiusSide =
                array.getIndex(R.styleable.ViewOutLineStrategy_radiusSide)
            array.recycle()

            setViewOutLine(view, radius, radiusSide)
        }

        fun setViewOutLine(view: View, radius: Int, radiusSide: Int) {
            if (radius <= 0) return
            view.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline?) {
                    val width = view.width
                    val height = view.height
                    if (width <= 0 || height <= 0) return
                    if (radiusSide != RADIUS_ALL) {
                        var left = 0
                        var right = width
                        var top = 0
                        var bottom = height

                        when (radiusSide) {
                            RADIUS_LEFT -> right += radius
                            RADIUS_TOP -> bottom += radius
                            RADIUS_RIGHT -> left -= radius
                            RADIUS_BOTTOM -> top -= radius
                        }
                        outline?.setRoundRect(left, top, right, bottom, radius.toFloat())
                        return
                    } else {
                        if (radius > 0) {
                            outline?.setRoundRect(0, 0, width, height, radius.toFloat())
                        }
                    }
                }
            }
            view.clipToOutline = radius > 0
            view.invalidate()
        }
    }
}
