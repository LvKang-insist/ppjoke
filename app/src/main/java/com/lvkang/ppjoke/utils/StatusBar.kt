package com.lvkang.ppjoke.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

object StatusBar {

    fun fitSystemBar(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val window = activity.window
        val decorView = window.decorView
        //SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ：能够使页面布局延伸到状态栏之下，但不会隐藏状态栏。也就是相当于状态栏是遮盖在布局之上的
        //SYSTEM_UI_FLAG_FULLSCREEN ：使页面布局延伸到状态栏，但是会隐藏状态栏
        //SYSTEM_UI_FLAG_LAYOUT_STABLE:不管虚拟导航是否显示，都能够保证布局在状态栏之下
        //SYSTEM_UI_FLAG_LIGHT_STATUS_BAR：白底黑字的状态栏
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //运行 window 对状态栏背景进行绘制
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //状态栏背景色
        window.statusBarColor = Color.TRANSPARENT
    }
}