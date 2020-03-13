package com.lvkang.libnetwork

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 22:10
 * @description
 */

abstract class JsonCallback<T> {

    open fun onSuccess(response: ApiResponse<T>) {

    }

    open fun onError(response: ApiResponse<T>) {

    }

    open fun onCacheSuccess(response: ApiResponse<T>) {

    }
}