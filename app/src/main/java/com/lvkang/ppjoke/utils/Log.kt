package com.lvkang.ppjoke.utils

import android.util.Log

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.utils
 * @author 345 QQ:1831712732
 * @time 2020/3/11 22:19
 * @description
 */

fun loe(title: String, message: String) {
    Log.e(toString(title), message)
}

fun lod(title: String, message: String) {
    Log.d(toString(title), message)
}

fun loi(title: String, message: String) {
    Log.i(toString(title), message)
}

fun lov(title: String, message: String) {
    Log.v(toString(title), message)
}

private fun toString(str: String): String {
    return "${str}:"
}