package com.lvkang.ppjoke.ui.sofa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lvkang.libnavannotation.FragmentDestination
import com.lvkang.ppjoke.R

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui.sofa
 * @author 345 QQ:1831712732
 * @time 2020/3/10 23:51
 * @description
 */
@FragmentDestination(pageUrl = "main/tabs/sofa")
class SofaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sofa, container, false)
    }
}