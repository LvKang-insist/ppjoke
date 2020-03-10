package com.lvkang.ppjoke.model

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.model
 * @author 345 QQ:1831712732
 * @time 2020/3/10 21:46
 * @description
 */
data class BottomBar(
    val activeColor: String,
    val inActiveColor: String,
    //默认选中的 tabId
    val selectTab: Int,
    val tabs: List<Tab>
)

data class Tab(
    val enable: Boolean,
    val index: Int,
    val pageUrl: String,
    val size: Int,
    val tintColor: String?,
    val title: String
)