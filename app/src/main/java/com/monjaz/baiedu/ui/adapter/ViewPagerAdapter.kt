package com.monjaz.baiedu.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by guluwa on 2018/3/14.
 */

class ViewPagerAdapter(fm: FragmentManager, private val mFragmentList: List<Fragment>, private val mNameList: List<String>) :
        FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mNameList[position]
    }
}