package com.lvkang.ppjoke.utils

class TimeUtils {

    companion object {

        @JvmStatic
        fun calculate(time: Long): String {
            val timeMillis = System.currentTimeMillis()
            val diff = (timeMillis - time) / 1000

            if (diff < 60) {
                return "$diff 秒前"
            } else if (diff < 3600) {
                return "$diff 分钟"
            } else if (diff < 3600 * 24) {
                return "$diff 小时前"
            } else {
                return "${diff / (3600 * 24)} 天前"
            }
        }
    }

}