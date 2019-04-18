package com.monjaz.baiedu.ui.main.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.ViewPagerAdapter
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_main_new.*

class MainNewActivity : BaseActivity(), View.OnClickListener {

    override val viewLayoutId: Int get() = R.layout.activity_main_new

    private var mCurrentPosition = 0

    private var mLastPosition = 0

    private var mAdapter: ViewPagerAdapter? = null

    private var mTitles = arrayListOf<String>()

    private var mPagers = arrayListOf<Fragment>()

    private var type = ""

    override fun initViews() {
        type = AppUtils.getString(Contacts.TYPE, "2")
        initViewPager()
        initNavigation()
        initData()
    }

    private fun initData() {
        mCurrentPosition = intent.getIntExtra("position", 0)
        mViewPager.currentItem = mCurrentPosition
    }

    private fun initViewPager() {
        with(mTitles) {
            add(getString(R.string.home))
            if (type == "2") {
                add(getString(R.string.live))
            }
            add(getString(R.string.message))
            add(getString(R.string.me))
        }
        with(mPagers) {
            if (type != "2") {
                add(HomeFragment.newInstance())
            } else {
                add(StudentHomeFragment.newInstance())
                add(LiveFragment.newInstance())
            }
            add(MessageFragment.newInstance())
            add(MeFragment.newInstance())
        }
        mAdapter = ViewPagerAdapter(supportFragmentManager, mPagers, mTitles)
        mViewPager.adapter = mAdapter
        mViewPager.setScroll(false)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
                resetNavigation()
                mLastPosition = position
                val view = mTabLayout.getTabAt(position)!!.customView
                if (view != null) {
                    val textView = view.findViewById<TextView>(R.id.tvBotNavTitle)
                    when (mTabLayout.getTabAt(position)!!.position) {
                        0 -> {
                            textView.setTextColor(
                                    ContextCompat.getColor(
                                            this@MainNewActivity,
                                            R.color.bot_nav_text_select_color
                                    )
                            )
                            view.findViewById<View>(R.id.ivBotNavPic)
                                    .setBackgroundResource(R.drawable.ic_bot_nav_home_select)
                        }
                        1 -> {
                            textView.setTextColor(
                                    ContextCompat.getColor(
                                            this@MainNewActivity,
                                            R.color.bot_nav_text_select_color
                                    )
                            )
                            if (type != "2") {
                                view.findViewById<View>(R.id.ivBotNavPic)
                                        .setBackgroundResource(R.drawable.ic_bot_nav_message_select)
                            } else {
                                view.findViewById<View>(R.id.ivBotNavPic)
                                        .setBackgroundResource(R.drawable.ic_bot_nav_live_select)
                            }
                        }
                        2 -> {
                            textView.setTextColor(
                                    ContextCompat.getColor(
                                            this@MainNewActivity,
                                            R.color.bot_nav_text_select_color
                                    )
                            )
                            if (type != "2") {
                                view.findViewById<View>(R.id.ivBotNavPic)
                                        .setBackgroundResource(R.drawable.ic_bot_nav_me_select)
                            } else {
                                view.findViewById<View>(R.id.ivBotNavPic)
                                        .setBackgroundResource(R.drawable.ic_bot_nav_message_select)
                            }
                        }
                        3 -> {
                            textView.setTextColor(
                                    ContextCompat.getColor(
                                            this@MainNewActivity,
                                            R.color.bot_nav_text_select_color
                                    )
                            )
                            view.findViewById<View>(R.id.ivBotNavPic)
                                    .setBackgroundResource(R.drawable.ic_bot_nav_me_select)
                        }
                    }
                }
            }
        })
    }

    private fun resetNavigation() {
        val tab = mTabLayout.getTabAt(mLastPosition)
        if (tab != null) {
            val view = tab.customView
            if (view != null) {
                val textView = view.findViewById<TextView>(R.id.tvBotNavTitle)
                textView.setTextColor(ContextCompat.getColor(this@MainNewActivity, R.color.bot_nav_text_default_color))
                when (mLastPosition) {
                    0 -> view.findViewById<View>(R.id.ivBotNavPic).setBackgroundResource(R.drawable.ic_bot_nav_home_normal)
                    1 -> if (type != "2") {
                        view.findViewById<View>(R.id.ivBotNavPic).setBackgroundResource(R.drawable.ic_bot_nav_message_normal)
                    } else {
                        view.findViewById<View>(R.id.ivBotNavPic).setBackgroundResource(R.drawable.ic_bot_nav_live_normal)
                    }
                    2 -> if (type != "2") {
                        view.findViewById<View>(R.id.ivBotNavPic).setBackgroundResource(R.drawable.ic_bot_nav_me_normal)
                    } else {
                        view.findViewById<View>(R.id.ivBotNavPic).setBackgroundResource(R.drawable.ic_bot_nav_message_normal)
                    }
                    3 -> view.findViewById<View>(R.id.ivBotNavPic).setBackgroundResource(R.drawable.ic_bot_nav_me_normal)
                }
            }
        }
    }

    private fun initNavigation() {
        if (type != "2") {
            val width = AppUtils.getDisplayMetrics(this).widthPixels / 3
            for (i in 0..2) {
                val view = LayoutInflater.from(this).inflate(R.layout.home_bot_nav_item, null, false)
                view.layoutParams = LinearLayout.LayoutParams(width, AppUtils.dip2px(this, 49f))
                val imageView = view.findViewById<ImageView>(R.id.ivBotNavPic)
                val textView = view.findViewById<TextView>(R.id.tvBotNavTitle)
                textView.text = mTitles[i]
                textView.setTextColor(ContextCompat.getColor(this@MainNewActivity, R.color.bot_nav_text_default_color))
                when (i) {
                    0 -> {
                        textView.setTextColor(
                                ContextCompat.getColor(
                                        this@MainNewActivity,
                                        R.color.bot_nav_text_select_color
                                )
                        )
                        imageView.setBackgroundResource(R.drawable.ic_bot_nav_home_select)
                    }
                    1 -> imageView.setBackgroundResource(R.drawable.ic_bot_nav_message_normal)
                    2 -> imageView.setBackgroundResource(R.drawable.ic_bot_nav_me_normal)
                }
                view.tag = i
                val newTab = mTabLayout.newTab()
                newTab.customView = view
                newTab.customView!!.setOnClickListener(this)
                mTabLayout.addTab(newTab)
            }
        } else {
            val width = AppUtils.getDisplayMetrics(this).widthPixels / 4
            for (i in 0..3) {
                val view = LayoutInflater.from(this).inflate(R.layout.home_bot_nav_item, null, false)
                view.layoutParams = LinearLayout.LayoutParams(width, AppUtils.dip2px(this, 49f))
                val imageView = view.findViewById<ImageView>(R.id.ivBotNavPic)
                val textView = view.findViewById<TextView>(R.id.tvBotNavTitle)
                textView.text = mTitles[i]
                textView.setTextColor(ContextCompat.getColor(this@MainNewActivity, R.color.bot_nav_text_default_color))
                when (i) {
                    0 -> {
                        textView.setTextColor(ContextCompat.getColor(this@MainNewActivity, R.color.bot_nav_text_select_color))
                        imageView.setBackgroundResource(R.drawable.ic_bot_nav_home_select)
                    }
                    1 -> imageView.setBackgroundResource(R.drawable.ic_bot_nav_live_normal)
                    2 -> imageView.setBackgroundResource(R.drawable.ic_bot_nav_message_normal)
                    3 -> imageView.setBackgroundResource(R.drawable.ic_bot_nav_me_normal)
                }
                view.tag = i
                val newTab = mTabLayout.newTab()
                newTab.customView = view
                newTab.customView!!.setOnClickListener(this)
                mTabLayout.addTab(newTab)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.tag) {
            0 -> {
                mViewPager.setCurrentItem(0, false)
            }
            1 -> {
                mViewPager.setCurrentItem(1, false)
            }
            2 -> {
                mViewPager.setCurrentItem(2, false)
            }
            3 -> {
                mViewPager.setCurrentItem(3, false)
            }
        }
    }

    fun goMessagePage() {
        if (type == "2") {
            mViewPager.currentItem = 2
        } else {
            mViewPager.currentItem = 1
        }
    }

    fun updateClassId(classId: Int) {
        Log.e("error", "updateClassId")
        if (mPagers[1] is LiveFragment) {
            (mPagers[1] as LiveFragment).queryClassDetail(classId)
        }
    }
}
