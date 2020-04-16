package com.lvkang.ppjoke

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.lvkang.ppjoke.model.User
import com.lvkang.ppjoke.ui.login.UserManager
import com.lvkang.ppjoke.ui.view.AppBottomBar
import com.lvkang.ppjoke.utils.AppConfig
import com.lvkang.ppjoke.utils.NavGraphBuilder

class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null
    lateinit var bottomBar: AppBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        //由于 启动时设置了 R.style.launcher 的windowBackground属性
        //势必要在进入主页后,把窗口背景清理掉
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar = findViewById(R.id.nav_view)

        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navController = fragment!!.findNavController()

        NavigationUI.setupWithNavController(bottomBar, navController!!)

        NavGraphBuilder.build(navController!!, this, fragment.id)

        //AppBottomBar的点击事件 和 navController 关联起来
        bottomBar.setOnNavigationItemSelectedListener(this::onNavItemSelected)
    }

    private fun onNavItemSelected(menuItem: MenuItem): Boolean {
        AppConfig.getDestConfig().forEach {
            val value = it.value
            if ((!UserManager.isLogin()) && value.needLogin && value.id == menuItem.itemId) {
                UserManager.login(this).observe(this,
                    Observer<User> { bottomBar.selectedItemId = menuItem.itemId })
                return@forEach
            }
        }
        navController?.navigate(menuItem.itemId)
        //返回 false 代表按钮没有被选中，也不会着色。如果为 true，就会着色，有一个上下浮动的效果
        return !TextUtils.isEmpty(menuItem.title)
    }
}
