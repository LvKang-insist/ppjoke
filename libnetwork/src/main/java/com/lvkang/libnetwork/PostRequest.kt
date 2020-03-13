package com.lvkang.libnetwork

import okhttp3.FormBody

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 22:36
 * @description
 */
class PostRequest<T>(private val url: String) : Request<T, PostRequest<T>>(url) {
    override fun generateRequest(builder: okhttp3.Request.Builder): okhttp3.Request {
        val bodyBuild = FormBody.Builder()
        for (map in params) {
            bodyBuild.add(map.key, map.value.toString())
        }
        return builder.url(url).post(bodyBuild.build()).build()
    }
}