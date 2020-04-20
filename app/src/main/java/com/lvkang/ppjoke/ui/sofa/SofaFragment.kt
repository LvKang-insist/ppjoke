package com.lvkang.ppjoke.ui.sofa

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.elvishew.xlog.XLog
import com.google.android.exoplayer2.util.ColorParser
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lvkang.libnavannotation.FragmentDestination
import com.lvkang.ppjoke.databinding.FragmentSofaBinding
import com.lvkang.ppjoke.model.SofaTab
import com.lvkang.ppjoke.ui.home.HomeFragment
import com.lvkang.ppjoke.utils.AppConfig

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.sofa
 * @author 345 QQ:1831712732
 * @time 2020/3/10 23:51
 * @description
 */
@FragmentDestination(pageUrl = "main/tabs/sofa")
class SofaFragment : Fragment() {
    lateinit var binding: FragmentSofaBinding
    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout
    private lateinit var mTabConfig: SofaTab
    val tabs: MutableList<SofaTab.Tab> = arrayListOf()
    val mFragmentMap = mutableMapOf<Int, Fragment>()
    lateinit var mediator: TabLayoutMediator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSofaBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = binding.sofaViewapager
        tabLayout = binding.sofaTablayout

        mTabConfig = getTabConfig()
        mTabConfig.tabs.forEach {
            if (it.enable) tabs.add(it)
        }

        viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        viewPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun createFragment(position: Int): Fragment {
                val fragment = mFragmentMap[position]
                if (fragment == null) {
                    mFragmentMap[position] = getTabFragment(position)
                }
                return mFragmentMap[position]!!
            }

            override fun getItemCount(): Int {
                return tabs.size
            }
        }

        mediator = TabLayoutMediator(
            tabLayout, viewPager, false,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.customView = makeTabView(position)
            })
        mediator.attach()

        viewPager.registerOnPageChangeCallback(mPageCallback)
        viewPager.post {
            //设置默认选中项，需要等到初始化完成后调用，所以这里调用了 post
            viewPager.currentItem = mTabConfig.select
        }
    }

    private var mPageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            //s设置选中后的效果
            val tabCount = tabLayout.tabCount
            for (i in 0 until tabCount) {
                val tab = tabLayout.getTabAt(i)
                if (tab?.position == position) {
                    val textView = tab.customView as TextView
                    textView.textSize = mTabConfig.activeSize.toFloat()
                    textView.typeface = Typeface.DEFAULT_BOLD
                } else {
                    val textView = tab?.customView as TextView
                    textView.textSize = mTabConfig.normalSize.toFloat()
                    textView.typeface = Typeface.DEFAULT
                }
            }
        }
    }

    private fun makeTabView(position: Int): View {
        val tabView = TextView(context)
        tabView.gravity = Gravity.CENTER
        val states = Array(2) { intArrayOf() }
        states[0] = intArrayOf(android.R.attr.state_selected)
        states[1] = intArrayOf()

        val colors = intArrayOf(
            Color.parseColor(mTabConfig.activeColor),
            ColorParser.parseCssColor(mTabConfig.normalColor)
        )
        val stateList = ColorStateList(states, colors)
        tabView.setTextColor(stateList)
        tabView.text = tabs[position].title
        tabView.textSize = mTabConfig.normalSize.toFloat()
        return tabView
    }

    private fun getTabFragment(position: Int): Fragment {
        return HomeFragment.newInstance(tabs[position].tag)
    }

    private fun getTabConfig(): SofaTab {
        return AppConfig.getSofaTabConfig()
    }


    override fun onPause() {
        super.onPause()
        XLog.e("SofaFragment onPause")
    }

    override fun onResume() {
        super.onResume()
        XLog.e("SofaFragment onResume")

    }

    override fun onDestroy() {
        mediator.detach()
        viewPager.unregisterOnPageChangeCallback(mPageCallback)
        super.onDestroy()
    }
}