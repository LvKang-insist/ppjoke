package com.lvkang.ppjoke.exeception

import android.util.Log

class GlobalThreadUncaughtExceptionHandler  : Thread.UncaughtExceptionHandler {

    companion object {
        fun setUp() {
            Thread.setDefaultUncaughtExceptionHandler(GlobalThreadUncaughtExceptionHandler())
        }
    }

    //Don't use lazy here.
    private val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e(e.message, "Uncaugth exception in thread: ${t.name}")
        defaultUncaughtExceptionHandler?.uncaughtException(t, e)
    }
}