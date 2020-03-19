package com.lvkang.libnetwork

import java.lang.StringBuilder
import java.net.URLEncoder

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 22:24
 * @description 拼接 url
 */
class UrlCreator {
    companion object {
        fun createUrlFromParams(url: String, params: Map<String, Any?>): String {
            val builder = StringBuilder()
            if (params.isEmpty()) return url
            builder.append(url)
            if (url.indexOf('?') > 0 || url.indexOf('&') > 0) {
                builder.append('&')
            } else {
                builder.append('?')
            }
            for (map in params) {
                //由于 value 是 Any 类型的，所以需要转为 UTF-8
                builder.append(map.key).append("=")
                    .append(URLEncoder.encode(map.value.toString(), "UTF-8")).append('&')
            }
            builder.deleteCharAt(builder.length - 1)
            return builder.toString()
        }
    }
}