package com.lvkang.libcommon.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class RoundFrameLayout : FrameLayout {
    constructor(context: Context) : this(context, null) {}

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {}


    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(
        context, attributeSet, defStyleAttr, 0
    )

    constructor(
        context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(
        context, attributeSet, defStyleAttr, defStyleRes
    ) {
        ViewHelper.setViewOutLine(this, attributeSet, defStyleAttr, defStyleRes)
    }

    fun setViewOutLine(radius: Int, radiusSize: Int) {
        ViewHelper.setViewOutLine(this,radius,radiusSize)
    }
}