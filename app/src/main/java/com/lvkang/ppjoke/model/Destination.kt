package com.lvkang.ppjoke.model

/**
 * @author 345 QQ:1831712732
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.model
 * @time 2020/3/9 21:47
 * @description
 */
data class Destination(
    val asStarter: Boolean,
    val className: String,
    val id: Int,
    val isFragment: Boolean,
    val needLogin: Boolean,
    val pageUrl: String
)