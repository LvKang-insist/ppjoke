@file:Suppress("UNCHECKED_CAST")

package com.lvkang.libcommon.extention

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 345 QQ:1831712732
 * @name ppjoke
 * @class name：com.lvkang.libcommon.extention
 * @time 2020/5/6 20:22
 * @description
 */
object LiveDataBus {

    val mHashMap: ConcurrentHashMap<String, StickeyLiveData<Any>> = ConcurrentHashMap()


    fun  with(eventName: String): StickeyLiveData<Any> {
        var liveData = mHashMap[eventName]
        if (liveData == null) {
            liveData = StickeyLiveData(eventName)
            mHashMap[eventName] = liveData
        }
        return liveData
    }

    class StickeyLiveData<T>(val eventName: String) : LiveData<T>() {
        private var mStickyData: T? = null
        private var mVersion = 0


        public override fun setValue(value: T) {
            mVersion++
            super.setValue(value)
        }

        public  override fun postValue(value: T) {
            mVersion++
            super.postValue(value)
        }

        fun setStickyData(stickyData: T) {
            this.mStickyData = stickyData
            setValue(stickyData)
        }

        fun postStickyData(stickyData: T) {
            this.mStickyData = stickyData
            postValue(stickyData)
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observerSticky(owner, observer, false)
        }

        private fun observerSticky(
            owner: LifecycleOwner,
            observer: Observer<in T>,
            sticky: Boolean
        ) {
            super.observe(owner, WrapperObserver<T>(this, observer, sticky))

            owner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    //宿主的生命周期发生变化时，该方法会被回调
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        mHashMap.remove(eventName)
                    }
                }
            })
        }

        class WrapperObserver<T>(
            private val liveData: StickeyLiveData<T>,
            private val observer: Observer<in T>,
            private val sticky: Boolean
        ) : Observer<T> {
            private var mLastVersion = 0

            init {
                mLastVersion = liveData.mVersion
            }

            override fun onChanged(t: T) {
                if (mLastVersion >= liveData.mVersion) {
                    if (sticky && liveData.mStickyData != null) {
                        observer.onChanged(liveData.mStickyData)
                    }
                    return
                }

                mLastVersion = liveData.mVersion
                observer.onChanged(t)
            }
        }

    }

}