package com.lvkang.libnetwork

import android.os.Build
import androidx.annotation.RequiresApi
import com.alibaba.fastjson.JSON
import java.lang.reflect.Type

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 22:58
 * @description
 */

class JsonConvert<T> : Convert<T> {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun convert(response: String, type: Type): T? {

        val jsonObject = JSON.parseObject(response)
        val data = jsonObject.getJSONObject("data")
        if (data != null) {
            val any = data["data"]
            return JSON.parseObject(any.toString(), type)
        }

        return JSON.parseObject(response, type)
    }

    override fun convert(response: String, clazz: Class<T>): T? {
        val jsonObject = JSON.parseObject(response)
        val data = jsonObject.getJSONObject("data")
        if (data != null) {
            val any = data["data"]
            return JSON.parseObject(any.toString(), clazz)
        }
        return null
    }


}