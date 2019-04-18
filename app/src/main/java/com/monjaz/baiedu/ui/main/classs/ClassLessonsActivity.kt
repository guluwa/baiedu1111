package com.monjaz.baiedu.ui.main.classs

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.ClassLessonItemBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.ui.adapter.OnEnglishDateBuildAdapter
import com.monjaz.baiedu.ui.adapter.OnEnglishItemBuildAdapter
import com.monjaz.baiedu.ui.adapter.TableViewPagerAdapter
import com.monjaz.baiedu.ui.viewmodel.ClassViewModel
import com.monjaz.baiedu.utils.AppUtils
import com.zhuangfei.timetable.TimetableView
import com.zhuangfei.timetable.listener.ISchedule
import kotlinx.android.synthetic.main.activity_class_lessons.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import java.util.*

class ClassLessonsActivity : BaseActivity() {

    private var mClassId = 0

    private var mCurrentPosition = 1000 * 3 + 1

    private var mCurrentDate: Date? = null

    private var mCurrentWeek = 1

    private var mPagers = arrayListOf<Fragment>()

    override val viewLayoutId: Int get() = R.layout.activity_class_lessons

    override fun initViews() {
        initData()
        initToolBar()
    }

    private fun initData() {
        if (intent.getIntExtra("classId", 0) == 0) {
            showToastMsg(getString(R.string.data_error))
            finish()
        } else {
            mClassId = intent.getIntExtra("classId", 0)
        }

        initViewPager()
    }

    private fun initViewPager() {
        mCurrentDate = AppUtils.getBeginDayOfWeek()
        with(mPagers) {
            add(
                ClassLessonsFragment.newInstance(
                    AppUtils.getDateString(AppUtils.getBeginDayOfLastWeek(mCurrentDate!!)),
                    0, mClassId
                )
            )
            add(ClassLessonsFragment.newInstance(AppUtils.getDateString(mCurrentDate!!), 1, mClassId))
            add(
                ClassLessonsFragment.newInstance(
                    AppUtils.getDateString(AppUtils.getBeginDayOfNextWeek(mCurrentDate!!)), 2, mClassId
                )
            )
        }
        mCurrentPosition = 1000 * 3 + 1
        mViewPager.adapter = TableViewPagerAdapter(supportFragmentManager, mPagers)
        mViewPager.currentItem = mCurrentPosition
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (mCurrentPosition > position) {
                    mCurrentDate = AppUtils.getBeginDayOfLastWeek(mCurrentDate!!)
                    mCurrentWeek--
                } else if (mCurrentPosition < position) {
                    mCurrentDate = AppUtils.getBeginDayOfNextWeek(mCurrentDate!!)
                    mCurrentWeek++
                }
                mCurrentPosition = position
                loadData()
            }
        })
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.curriculum)
        ivBack.setOnClickListener { finish() }
    }

    private fun loadData() {
        if (mCurrentDate == null) return
        (mPagers[mCurrentPosition % 3] as ClassLessonsFragment).freshData(
            AppUtils.getDateString(mCurrentDate!!),
            mCurrentWeek
        )
    }
}