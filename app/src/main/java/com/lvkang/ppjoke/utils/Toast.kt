package com.lvkang.ppjoke.utils

import android.widget.Toast

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.utils
 * @author 345 QQ:1831712732
 * @time 2020/3/11 22:30
 * @description
 */

fun toast(message: String) {
    Toast.makeText(com.lvkang.libcommon.AppGlobals.getApplication(), message, Toast.LENGTH_LONG).show()
}