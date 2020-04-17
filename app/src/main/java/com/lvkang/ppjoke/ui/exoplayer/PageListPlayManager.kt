package com.lvkang.ppjoke.ui.exoplayer

import android.net.Uri
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import com.lvkang.libcommon.AppGlobals

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.exoplayer
 * @author 345 QQ:1831712732
 * @time 2020/4/17 16:26
 * @description
 */

class PageListPlayManager {
    companion object {
        //每一个页面对应的 PageListPlay 对象
        @JvmStatic
        val sPageListPlayHashMap = mutableMapOf<String, PageListPlay>()
        //视频数据源的工厂
        private var mediaSourceFactory: ProgressiveMediaSource.Factory

        init {
            val application = AppGlobals.getApplication()

            //SourceFactory：根据提供的 url 去缓存视频
            val dataSourceFactory = DefaultHttpDataSourceFactory(
                Util.getUserAgent(application, application.packageName)
            )
            //缓存的位置和缓存策略，策略为：最近最少使用
            val cache = SimpleCache(application.cacheDir, LeastRecentlyUsedCacheEvictor(1024 * 200))
            //文件的写入
            val cacheDataSinkFactory = CacheDataSinkFactory(cache, Long.MAX_VALUE)
            //能够查询本地文件缓存的工厂类
            val cacheDataSourceFactory = CacheDataSourceFactory(
                cache, dataSourceFactory, FileDataSourceFactory(),
                cacheDataSinkFactory, CacheDataSource.FLAG_BLOCK_ON_CACHE, null
            )
            //根据 url 创建数据源，供视频播放器使用
            mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
        }


        /**
         * 获取视频播放源
         */
        fun createMediaSource(url: String): MediaSource {
            return mediaSourceFactory.createMediaSource(Uri.parse(url))
        }


        /**
         * 获取对应的 PageListPlay
         */
        fun get(pageName: String): PageListPlay {
            var pageListPlay = sPageListPlayHashMap[pageName]
            if (pageListPlay == null) {
                pageListPlay = PageListPlay()
                sPageListPlayHashMap[pageName] = pageListPlay
            }
            return pageListPlay
        }

        /**
         * 释放资源
         */
        fun release(pageName: String) {
            sPageListPlayHashMap[pageName]?.release()
        }
    }
}