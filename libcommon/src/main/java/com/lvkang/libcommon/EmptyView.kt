package com.lvkang.libcommon

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat

class EmptyView : LinearLayoutCompat {

    private var icon: AppCompatImageView? = null
    private var title: AppCompatTextView? = null
    private var action: Button? = null

    constructor(context: Context) : this(context, null) {}

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {}

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context, attributeSet, defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.layout_empty_view, this, true)

        orientation = VERTICAL
        gravity = Gravity.CENTER

        icon = findViewById(R.id.empty_icon)
        title = findViewById(R.id.empty_text)
        action = findViewById(R.id.empty_action)
    }

    fun setEmptyIcon(@DrawableRes iconRes: Int) {
        icon?.setImageResource(iconRes)
    }

    fun setTitle(text: String) {
        if (TextUtils.isEmpty(text)) {
            title?.visibility = View.GONE
        } else {
            title?.text = text
            title?.visibility = View.VISIBLE

        }
    }

    fun setButton(text: String, listener: View.OnClickListener) {
        if (TextUtils.isEmpty(text)) {
            action?.visibility = View.GONE
        } else {
            action?.text = text
            action?.visibility = View.VISIBLE
            action?.setOnClickListener(listener)
        }
    }


}