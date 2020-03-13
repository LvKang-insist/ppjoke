package com.lvkang.libnetwork

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork
 * @author 345 QQ:1831712732
 * @time 2020/3/12 22:09
 * @description 对请求返回结果的包装类
 */

class ApiResponse<T>() {
    var success = false
    var status = 0
    var message: String? = null
    var body: T? = null
}