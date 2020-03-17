package com.lvkang.ppjoke.utils

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.utils
 * @author 345 QQ:1831712732
 * @time 2020/3/17 23:08
 * @description
 */

class StringConvert {
    companion object {
        fun convertFeedUgc(count: Int): String {
            if (count < 10000)
                return count.toString()
            return "${count / 10000}万"
        }
    }
}