package com.lvkang.ppjoke.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.utils.AppConfig

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.view
 * @author 345 QQ:1831712732
 * @time 2020/3/10 21:56
 * @description
 */
class AppBottomBar : BottomNavigationView {

    companion object {
        val icons = intArrayOf(
            R.drawable.icon_tab_home, R.drawable.icon_tab_sofa,
            R.drawable.icon_tab_publish, R.drawable.icon_tab_find, R.drawable.icon_tab_mine
        )
    }

    constructor(context: Context) : this(context, null) {}

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {}

    @SuppressLint("RestrictedApi")
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr) {
        val bottomBar = AppConfig.getBottomBar()
        val tabs = bottomBar.tabs

        val states = Array(2) { intArrayOf() }
        states[0] = intArrayOf(android.R.attr.state_selected)
        states[1] = intArrayOf()
        val colors = intArrayOf(
            //被选中的颜色
            Color.parseColor(bottomBar.activeColor),
            //按钮默认的颜色
            Color.parseColor(bottomBar.inActiveColor)
        )
        val colorStateList = ColorStateList(states, colors)

        itemTextColor = colorStateList
        itemIconTintList = colorStateList

        //所有的按钮在任何时候都需要显示文本
        labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        //设置默认选中的按钮
        selectedItemId = bottomBar.selectTab

        //将按钮添加到 BottomBar 上面
        for (tab in tabs) {
            if (!tab.enable) {
                return
            }
            val itemId = getId(tab.pageUrl)
            if (itemId < 0) {
                return
            }
            val item = menu.add(0,itemId, tab.index, tab.title)
            item?.setIcon(icons[tab.index])
        }
        tabs.forEach {
            val menuView = getChildAt(0) as BottomNavigationMenuView
            val itemView = menuView.getChildAt(it.index) as BottomNavigationItemView
            itemView.setIconSize(dp2px(it.size))

            //给中间的按钮设置着色
            if (TextUtils.isEmpty(it.title)) {
                itemView.setIconTintList(ColorStateList.valueOf(Color.parseColor(it.tintColor)))
                //点击的时候不会有上下浮动的效果
                itemView.setShifting(false)
            }
        }
    }

    private fun dp2px(dpValue: Int): Int {
        val value = context.resources.displayMetrics.density * dpValue + 0.5f
        return value.toInt()
    }

    private fun getId(pageUrl: String): Int {
        val destination = AppConfig.getDestConfig()[pageUrl] ?: return -1
        return destination.id
    }

}