package com.lvkang.ppjoke.utils

import android.annotation.SuppressLint
import android.app.Application
import com.alibaba.fastjson.TypeReference

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.utils
 * @author 345 QQ:1831712732
 * @time 2020/3/9 21:57
 * @description 获取 Application 对象
 */

class AppGlobals {

    companion object {
        private var sApplication: Application? = null;
        /**
         * 反射获取 Application
         */
        @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
        fun getApplication(): Application {
            if (sApplication == null) {
                val method = Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentApplication")
                sApplication = method.invoke(null) as Application
            }
            return sApplication!!
        }
    }
}
