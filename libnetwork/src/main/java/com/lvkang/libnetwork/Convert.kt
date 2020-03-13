package com.lvkang.libnetwork

import java.lang.reflect.Type

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 22:56
 * @description
 */
interface Convert<T> {
    fun convert(response: String, type: Type): T?
    fun convert(response: String, clazz: Class<T>): T?
}