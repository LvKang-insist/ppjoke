package com.lvkang.libcommon

class PixUtils {
    companion object {
        /**
         * 将 dp 转为 px
         */
        fun dp2px(dpValue: Int): Int {
            val metrics = AppGlobals.getApplication().resources.displayMetrics
            return (metrics.density * dpValue + 0.5f).toInt()
        }

        /**
         * 获取屏幕宽度
         */
        fun getScreenWidth(): Int {
            val metrics = AppGlobals.getApplication().resources.displayMetrics
            return metrics.widthPixels
        }

        /**
         * 获取屏幕高度
         */
        fun getScreenHeight(): Int {
            val metrics = AppGlobals.getApplication().resources.displayMetrics
            return metrics.heightPixels
        }
    }
}