@file:Suppress("UNCHECKED_CAST")

package com.lvkang.libnetwork

import android.util.Log
import androidx.annotation.IntDef
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 21:35
 * @description
 */
abstract class Request<T, R : Request<T, R>>(mUrl: String) {
    private val headers = mutableMapOf<String, String>()
    protected val params = mutableMapOf<String, Any>()
    private var cacheKey: String? = null
    private var mType: Type? = null
    private var mClazz: Class<*>? = null

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
    fun addParam(key: String, value: Any): R {
        //TYPE 是原始的基本类型，通过TYPE 就可以得到基本类型的 Class
        val field = value.javaClass.getField("TYPE")
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

    fun execute(callback: JsonCallback<T>) {
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
            val mConvert = ApiService.mConvert!!

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
                else -> Log.e("request", "parseResponse无法解析")
            }
        } else {
            message = content
        }

        result.success = success
        result.status = status
        result.message = message
        return result
    }

    fun execute(): ApiResponse<T> {
        val response = getCall().execute()
        return parseResponse(response, null)
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

}