package com.lvkang.ppjoke.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.lvkang.ppjoke.model.Destination
import java.util.*

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.utils
 * @author 345 QQ:1831712732
 * @time 2020/3/9 21:53
 * @description
 */
class AppConfig {

    companion object {

        private var sDestConfig: Map<String, Destination>? = null

        fun getDestConfig(): Map<String, Destination> {
            if (sDestConfig.isNullOrEmpty()) {
                sDestConfig = JSON.parseObject(parseFile("destination.json"),
                    object : TypeReference<HashMap<String, Destination>>() {})
            }
            return sDestConfig!!
        }

        private fun parseFile(fileName: String): String {
            val assets = AppGlobals.getApplication().resources.assets
            assets.open(fileName).use {
                return it.bufferedReader().readText()
            }
        }
    }

}