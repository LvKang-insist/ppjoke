package com.lvkang.ppjoke

import android.app.Application
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.facebook.stetho.Stetho
import com.hjq.toast.ToastUtils
import com.lvkang.libnetwork.ApiService


/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke
 * @author 345 QQ:1831712732
 * @time 2020/3/19 21:13
 * @description
 */
class JokeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApiService.init<Any>("http://123.56.232.18:8080/serverdemo", null)
        //log 和 Tost 工具
        XLog.init(LogConfiguration.Builder().t().tag("345").build())
        ToastUtils.init(this)

        initStetho()
    }

    private fun initStetho() {
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        )
    }
}