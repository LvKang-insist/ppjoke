package com.lvkang.libnetwork

import android.util.Log

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 22:21
 * @description
 */
class GetRequest<T>(private val url: String) : Request<T, GetRequest<T>>(url) {
    override fun generateRequest(builder: okhttp3.Request.Builder): okhttp3.Request {
        Log.e("0000000000000000000",UrlCreator.createUrlFromParams(url, params))
        return builder.get().url(UrlCreator.createUrlFromParams(url, params)).build()
    }
}