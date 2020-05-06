package com.lvkang.libnetwork.cache

import com.elvishew.xlog.XLog
import com.hjq.toast.ToastUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object CacheManager {

    /**
     * 反序列，把二进制转成 java object 对象
     */
    private fun toObject(data: ByteArray): Any? {
        ObjectInputStream(ByteArrayInputStream(data)).use {
            return it.readObject()
        }
    }

    /**
     * 序列化存储，将数据转为二进制
     */
    private fun <T> toByteArray(body: T): ByteArray {
        val byte = ByteArrayOutputStream()
        ObjectOutputStream(byte).use {
            it.writeObject(body)
            it.flush()
            return byte.toByteArray()
        }
    }

    /**
     * 缓存数据
     */
    fun <T> save(key: String, body: T) {
        val cache = Cache()
        cache.key = key
        cache.data = toByteArray(body)

        CacheDatabase.get().getCache().save(cache)
    }

    /**
     * 获取指定 key 的缓存
     */
    fun getCache(key: String): Any? {
        val cache: Cache? = CacheDatabase.get().getCache().getCache(key)
        if (cache?.data != null) {
            return toObject(cache.data!!)
        }
        return null
    }

}