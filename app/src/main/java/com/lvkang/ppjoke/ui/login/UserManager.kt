package com.lvkang.ppjoke.ui.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hjq.toast.ToastUtils
import com.lvkang.libnetwork.cache.CacheManager
import com.lvkang.ppjoke.model.User

object UserManager {

    private const val KEY_CACHE_USER = "cache_user"
    private val userLiveData = MutableLiveData<User>()
    private var mUser: User? = null

    init {
        val cache = CacheManager.getCache(KEY_CACHE_USER) as User?
        if (cache != null && cache.expires_time > System.currentTimeMillis()) {
            mUser = cache
        }else{
            ToastUtils.show("获取登录数据失败")
        }
    }

    fun save(user: User) {
        mUser = user
        CacheManager.save(KEY_CACHE_USER, user)
        // 如果 liveData 有观察者
        if (userLiveData.hasObservers()) {
            userLiveData.postValue(user)
        }
    }

    fun login(context: Context): LiveData<User> {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        context.startActivity(intent)
        return userLiveData
    }

    fun isLogin(): Boolean {
        return if (mUser == null) false else mUser!!.expires_time > System.currentTimeMillis()
    }

    fun getUser(): User? {
        return if (isLogin()) mUser!! else null
    }

    fun getUserId(): Long {
        return if (isLogin()) mUser!!.userId else 0
    }

}