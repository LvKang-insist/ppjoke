package com.lvkang.ppjoke.utils

import android.content.ComponentName
import android.util.Log
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.utils
 * @author 345 QQ:1831712732
 * @time 2020/3/9 22:30
 * @description
 */
class NavGraphBuilder {
    companion object {
        fun build(navController: NavController) {
            val provider = navController.navigatorProvider
            val fragmentNavigator = provider.getNavigator(FragmentNavigator::class.java)
            val activityNavigator = provider.getNavigator(ActivityNavigator::class.java)

            val navGraph = NavGraph(NavGraphNavigator(provider))
            val destConfig = AppConfig.getDestConfig()
            destConfig.forEach {
                Log.e("-----------", "${it.key} --- ${it.value}")
                if (it.value.isFragment) {
                    val destination = fragmentNavigator.createDestination()
                    destination.className = it.value.className
                    destination.id = it.value.id
                    destination.addDeepLink(it.value.pageUrl)

                    navGraph.addDestination(destination)
                } else {
                    val destination = activityNavigator.createDestination()
                    destination.id = it.value.id
                    destination.addDeepLink(it.value.pageUrl)
                    destination.setComponentName(
                        ComponentName(
                            AppGlobals.getApplication().packageName,
                            it.value.className
                        )
                    )
                    navGraph.addDestination(destination)
                }

                if (it.value.asStarter) {
                    navGraph.startDestination = it.value.id
                }
            }
            navController.graph = navGraph
        }

    }
}