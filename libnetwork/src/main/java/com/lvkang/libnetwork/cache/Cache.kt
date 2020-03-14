package com.lvkang.libnetwork.cache

import java.io.Serializable

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork.cache
 * @author 345 QQ:1831712732
 * @time 2020/3/14 20:58
 * @description
 */
class Cache : Serializable {
    var key: String? = null
    var data: Array<Byte>? = null
}