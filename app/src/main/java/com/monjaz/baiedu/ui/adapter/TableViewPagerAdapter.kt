package com.monjaz.baiedu.ui.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TableViewPagerAdapter(fm: FragmentManager, private val mFragmentList: List<Fragment>) :
        FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position % mFragmentList.size]
    }

    override fun getCount(): Int {
        return Int.MAX_VALUE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //处理position。让数组下标落在[0,fragmentList.size)中，防止越界
        val pos = position % mFragmentList.size
        return super.instantiateItem(container, pos)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }
}