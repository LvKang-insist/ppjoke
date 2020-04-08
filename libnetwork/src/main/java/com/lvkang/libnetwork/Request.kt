@file:Suppress("UNCHECKED_CAST")

package com.lvkang.libnetwork

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import androidx.annotation.IntDef
import androidx.arch.core.executor.ArchTaskExecutor
import com.lvkang.libnetwork.cache.CacheManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 21:35
 * @description
 */
abstract class Request<T, R : Request<T, R>>(private var mUrl: String) : Cloneable {
    private val headers = mutableMapOf<String, String>()
    protected val params = mutableMapOf<String, Any?>()
    private var mType: Type? = null
    private var mClazz: Class<*>? = null


    private var cacheKey: String? = null
    private var mCacheStartegy: Int = NET_ONLY;

    companion object {
        //只访问缓存，即便本地缓存存不存在，也不会发起网络请求
        const val CACHE_ONLY = 1
        //先访问缓存，同时发起网络的请求，成功后缓存到本地
        const val CACHE_FIRST = 2
        //只访问服务器，不进行任何存储
        const val NET_ONLY = 3
        //先访问网络，成功后缓存到本地
        const val NET_CACHE = 4
    }


    @IntDef(CACHE_ONLY, CACHE_FIRST, NET_CACHE, NET_ONLY)
    annotation class CacheStrategy


    fun Request(url: String) {
        mUrl = url
    }


    /**
     * 添加头文件
     */
    fun addHeader(key: String, value: String): R {
        headers[key] = value
        return this as R
    }

    /**
     * value 只能为基本数据类型
     */
    fun addParam(key: String, value: Any?): R {
        if (value == null) return this as R
        //TYPE 是原始的基本类型，通过TYPE 就可以得到基本类型的 Class
        val field = value!!.javaClass.getField("TYPE")
        val clazz = field.get(null) as Class<*>
        //判断 clazz 是否为基本类型
        if (clazz.isPrimitive) {
            params[key] = value
        }
        return this as R
    }

    /**
     * 缓存的 Key
     */
    fun cacheKey(key: String): R {
        this.cacheKey = key
        return this as R
    }

    fun responseType(type: Type): R {
        mType = type
        return this as R
    }

    fun responseClass(clazz: Class<*>): R {
        mClazz = clazz
        return this as R
    }

    fun cacheStratgy(@CacheStrategy cacheStrategy: Int): R {
        mCacheStartegy = cacheStrategy
        return this as R
    }


    /**
     * 同步请求
     */
    fun execute(): ApiResponse<T> {
        if (mCacheStartegy == CACHE_ONLY) {
            return readCache()
        }
        val response = getCall().execute()
        return parseResponse(response, null)
    }

    /**
     * 异步请求
     */
    @SuppressLint("RestrictedApi")
    fun execute(callback: JsonCallback<T>) {
        if (mCacheStartegy != NET_ONLY) {
            //异步读取缓存
            ArchTaskExecutor.getIOThreadExecutor().execute {
                val response = readCache()
                callback.onCacheSuccess(response)
            }
        }

        if (mCacheStartegy != CACHE_ONLY) {
            //发起异步网络请求
            getCall().enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val result = ApiResponse<T>()
                    result.message = e.message
                    callback.onError(result)
                }

                override fun onResponse(call: Call, response: Response) {
                    val apiResponse = parseResponse(response, callback)
                    if (apiResponse.success) {
                        callback.onSuccess(apiResponse)
                    } else {
                        callback.onError(apiResponse)
                    }
                }
            })
        }
    }

    private fun readCache(): ApiResponse<T> {
        val key = if (TextUtils.isEmpty(cacheKey)) generateCacheKey() else cacheKey
        val cache = CacheManager.getCache(key!!)
        val result = ApiResponse<T>()
        result.status = 304
        result.message = "缓存获取成功"
        result.body = cache as T
        result.success = true
        return result
    }

    private fun parseResponse(
        response: Response,
        callback: JsonCallback<T>?
    ): ApiResponse<T> {
        var message: String? = null
        val status = response.code
        val success = response.isSuccessful
        val result = ApiResponse<T>()
        val content = response.body?.string()
        if (success) {
            Log.e("url：", mUrl)
            Log.e("-----------", content)
            val mConvert = ApiService.mConvert ?: JsonConvert<T>()

            when {
                callback != null -> {
                    //获取 callback 的实际泛型类型
                    val type: ParameterizedType =
                        callback.javaClass.genericSuperclass as ParameterizedType
                    val argument = type.actualTypeArguments[0]
                    result.body = mConvert.convert(content!!, argument) as T
                }
                mType != null -> {
                    result.body = mConvert.convert(content!!, mType!!) as T
                }
                mClazz != null -> {
                    result.body = mConvert.convert(content!!, mClazz!!) as T
                }
                else -> {
                    result.body = content as T
                }
            }
        } else {
            message = content
        }
        result.success = success
        result.status = status
        result.message = message

        if (mCacheStartegy != NET_ONLY && result.success && result.body != null && result.body is Serializable) {
            saveCache(result.body as T)
        }
        return result
    }

    /**
     * 进行缓存
     */
    private fun saveCache(body: T) {
        val key = if (TextUtils.isEmpty(cacheKey)) generateCacheKey() else cacheKey;
        CacheManager.save(key!!, body)
    }

    /**
     * 获取 key，key 由 url + 参数构成
     */
    private fun generateCacheKey(): String {
        cacheKey = UrlCreator.createUrlFromParams(mUrl, params)
        return cacheKey!!
    }


    private fun getCall(): Call {
        val builder = okhttp3.Request.Builder()
        addHeaders(builder)
        val request = generateRequest(builder)
        return ApiService.okHttpClient!!.newCall(request)
    }

    /**
     * 获取 Request
     */
    abstract fun generateRequest(builder: okhttp3.Request.Builder): okhttp3.Request

    /**
     * 添加头文件
     */
    private fun addHeaders(builder: okhttp3.Request.Builder) {
        for (map in headers) {
            builder.addHeader(map.key, map.value)
        }
    }

    @Throws(CloneNotSupportedException::class)// 克隆失败抛出异常
    public override fun clone(): Request<T, R> {
        return super.clone() as Request<T, R>
    }

}