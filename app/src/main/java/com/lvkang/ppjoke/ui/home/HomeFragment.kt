package com.lvkang.ppjoke.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lvkang.libnavannotation.FragmentDestination
import com.lvkang.ppjoke.R
import com.lvkang.ppjoke.utils.loe

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.home
 * @author 345 QQ:1831712732
 * @time 2020/3/5 0:05
 * @description
 */

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loe("onCreateView", "HomeFragment")
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val mTvHome = view.findViewById<TextView>(R.id.text_home)

        mTvHome.setOnClickListener {
            Toast.makeText(context, "首頁", Toast.LENGTH_LONG).show()
        }

        return view
    }
}