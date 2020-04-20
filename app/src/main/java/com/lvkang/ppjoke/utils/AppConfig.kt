package com.lvkang.ppjoke.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lvkang.ppjoke.model.BottomBar
import com.lvkang.ppjoke.model.Destination
import com.lvkang.ppjoke.model.SofaTab
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

        private var mDestConfig: Map<String, Destination>? = null
        private var mBottomBar: BottomBar? = null
        private var sSofaTab: SofaTab? = null

        /**
         * 获取沙发页面的 tab 配置
         */
        fun getSofaTabConfig(): SofaTab {
            if (sSofaTab == null) {
                sSofaTab = Gson().fromJson<SofaTab>(
                    parseFile("sofa_tabs_config.json"),
                    SofaTab::class.java
                )
                Collections.sort(sSofaTab!!.tabs, object : Comparator<SofaTab.Tab> {
                    override fun compare(o1: SofaTab.Tab, o2: SofaTab.Tab): Int {
                        return if (o1.index < o2.index) -1 else 1
                    }
                })
            }
            return sSofaTab!!
        }

        /**
         * 获取底部 Tab 的信息
         */
        fun getBottomBar(): BottomBar {
            if (mBottomBar == null) {
                mBottomBar = Gson().fromJson<BottomBar>(
                    parseFile("main_tabs_config.json"),
                    BottomBar::class.java
                )
            }
            return mBottomBar!!
        }


        /**
         * 返回被注解页面的数据
         */
        fun getDestConfig(): Map<String, Destination> {
            if (mDestConfig.isNullOrEmpty()) {
                mDestConfig = Gson().fromJson<HashMap<String, Destination>>(
                    parseFile("destination.json"),
                    object : TypeToken<HashMap<String, Destination>>() {}.type
                )
            }
            return mDestConfig!!
        }

        /**
         * 获取文件内容
         */
        private fun parseFile(fileName: String): String {
            val assets = com.lvkang.libcommon.AppGlobals.getApplication().resources.assets
            assets.open(fileName).use {
                return it.bufferedReader().readText()
            }
        }
    }

}