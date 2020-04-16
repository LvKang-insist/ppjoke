package com.lvkang.ppjoke.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.hjq.toast.ToastUtils
import com.lvkang.libnetwork.ApiResponse
import com.lvkang.libnetwork.ApiService
import com.lvkang.libnetwork.JsonCallback
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.model.User
import com.tencent.connect.UserInfo
import com.tencent.connect.auth.QQToken
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    var tencent: Tencent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        action_close.setOnClickListener { finish() }

        action_login.setOnClickListener {
            if (tencent == null) {
                tencent = Tencent.createInstance("101866843", applicationContext)
            }
            tencent?.login(this, "all", object : IUiListener {
                override fun onComplete(p0: Any?) {
                    val response = p0 as JSONObject
                    val openId = response.getString("openid")
                    val access_token = response.getString("access_token")
                    val expires_in = response.getString("expires_in")
                    val expires_time = response.getLong("expires_time")

                    tencent?.setAccessToken(access_token, expires_in)
                    tencent?.openId = openId
                    val qqToken = tencent?.qqToken
                    getUserInfo(qqToken, expires_time, openId)
                }

                override fun onCancel() {
                    ToastUtils.show("登陆取消")
                }

                override fun onError(p0: UiError?) {
                    ToastUtils.show("登陆失败")
                }

            })
        }
    }

    private fun getUserInfo(
        qqToken: QQToken?,
        expiresTime: Long,
        openId: String
    ) {
        if (qqToken != null) {
            val userInfo = UserInfo(application, qqToken)
            userInfo.getUserInfo(object : IUiListener {
                override fun onComplete(p0: Any?) {
                    val response = p0 as JSONObject
                    val nickName = response.getString("nickname")
                    val figureurl_2 = response.getString("figureurl_2")
                    save(nickName, figureurl_2, openId, expiresTime)
                }

                override fun onCancel() {
                    ToastUtils.show("登陆取消")
                }

                override fun onError(p0: UiError?) {
                    ToastUtils.show("登陆失败")
                }
            })
        }
    }

    fun save(
        nickName: String,
        figureurl2: String,
        openId: String,
        expiresTime: Long
    ) {
        ApiService.get<User>("")
            .addParam("name", nickName)
            .addParam("avatar", figureurl2)
            .addParam("qqOpenId", openId)
            .addParam("expires_time", expiresTime)
            .execute(object : JsonCallback<User>() {
                override fun onSuccess(response: ApiResponse<User>) {
                    if (response.body != null) {
                        UserManager.save(response.body!!)
                    } else {
                        GlobalScope.launch(Dispatchers.Main) {
                            ToastUtils.show("登陆失败")
                        }
                    }
                }

                override fun onError(response: ApiResponse<User>) {
                    GlobalScope.launch(Dispatchers.Main) {
                        ToastUtils.show("登陆失败")
                    }
                }
            })
    }

}