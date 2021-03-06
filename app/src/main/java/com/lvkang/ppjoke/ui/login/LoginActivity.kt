package com.lvkang.ppjoke.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elvishew.xlog.XLog
import com.hjq.toast.ToastUtils
import com.lvkang.libnetwork.ApiResponse
import com.lvkang.libnetwork.ApiService
import com.lvkang.libnetwork.JsonCallback
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.model.User
import com.tencent.connect.UserInfo
import com.tencent.connect.auth.QQToken
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), IUiListener {

    var tencent: Tencent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        action_close.setOnClickListener { finish() }
        action_login.setOnClickListener {
            if (tencent == null) {
                tencent = Tencent.createInstance("101866843", applicationContext)
            }
            tencent?.login(this, "all", this)
        }
    }

    /**
     * 进行qq登录
     */
    override fun onComplete(p0: Any?) {
        val response = p0 as JSONObject
        val openId = response.getString("openid")
        val accessToken = response.getString("access_token")
        val expiresIn = response.getString("expires_in")
        val expiresTime = response.getLong("expires_time")
        tencent?.setAccessToken(accessToken, expiresIn)
        tencent?.openId = openId
        val qqToken = tencent?.qqToken
        getUserInfo(qqToken, expiresTime, openId)
    }

    override fun onCancel() {
        ToastUtils.show("登陆取消")

    }

    override fun onError(p0: UiError?) {
        ToastUtils.show("登陆失败")
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfo(qqToken: QQToken?, expiresTime: Long, openId: String) {
        if (qqToken != null) {
            val userInfo = UserInfo(application, qqToken)
            userInfo.getUserInfo(object : IUiListener {
                override fun onComplete(p0: Any?) {
                    val response = p0 as JSONObject
                    val nickName = response.getString("nickname")
                    val figureurl2 = response.getString("figureurl_2")
                    save(nickName, figureurl2, openId, expiresTime)
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

    /**
     * 进行登录
     */
    fun save(nickName: String, figureurl2: String, openId: String, expiresTime: Long) {
        ApiService.get<User>("/user/insert")
            .addParam("name", nickName)
            .addParam("avatar", figureurl2)
            .addParam("qqOpenId", openId)
            .addParam("expires_time", expiresTime)
            .execute(object : JsonCallback<User>() {
                override fun onSuccess(response: ApiResponse<User>) {
                    if (response.body != null) {
                        UserManager.save(response.body!!)
                        finish()
                    } else {
                        loginError()
                    }
                }

                override fun onError(response: ApiResponse<User>) {
                    loginError()
                }
            })
    }

    fun loginError() {
        GlobalScope.launch(Dispatchers.Main) {
            ToastUtils.show("登陆失败")
        }
    }

    /**
     * 如果需要登录后收到回调，需要如下写法
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            //当QQ登录完成后，进行回调
            Tencent.onActivityResultData(requestCode, resultCode, data, this)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}