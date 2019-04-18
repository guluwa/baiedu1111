package com.monjaz.baiedu.ui.main.classs

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.monjaz.baiedu.base.LazyFragment
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.ClassLessonItemBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.ui.adapter.OnEnglishDateBuildAdapter
import com.monjaz.baiedu.ui.adapter.OnEnglishItemBuildAdapter
import com.monjaz.baiedu.ui.dialog.ClassLessonInfoDialog
import com.monjaz.baiedu.ui.viewmodel.ClassViewModel
import com.zhuangfei.timetable.listener.ISchedule
import kotlinx.android.synthetic.main.fragment_class_lessons.*
import java.util.*

class ClassLessonsFragment : LazyFragment() {

    private var mViewModel: ClassViewModel? = null

    private var mySubjects = mutableListOf<ClassLessonItemBean>()

    private var mMaxLessonsNum = 10

    private var mCurrentDate: String = ""

    private var mClassId = 0

    private var mCurrentWeek = 1

    override val viewLayoutId: Int get() = R.layout.fragment_class_lessons

    companion object {
        fun newInstance(date: String, week: Int, id: Int): ClassLessonsFragment {
            val fragment = ClassLessonsFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            bundle.putInt("week", week)
            bundle.putString("date", date)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initViews() {
        initData()
        initTableView()
    }

    private fun initData() {
        mClassId = arguments!!.getInt("id")
        mCurrentWeek = arguments!!.getInt("week")
        mCurrentDate = arguments!!.getString("date")!!
    }

    private fun initTableView() {
        mTableView.source(mySubjects)
                .curWeek(1)
                .curTerm(getString(R.string.primary_school))
                .monthWidthDp(30)
                .callback(OnEnglishDateBuildAdapter())
                .callback(OnEnglishItemBuildAdapter())
                .callback(ISchedule.OnItemClickListener { v, scheduleList ->
                    mySubjects.map {
                        if (scheduleList[0].day == it.day &&
                                scheduleList[0].start == it.start){
                            ClassLessonInfoDialog(it, context!!, R.style.DialogStyle).show()
                        }
                    }
                })
                .callback(object : ISchedule.OnSpaceItemClickListener {
                    override fun onInit(
                            flagLayout: LinearLayout?,
                            monthWidth: Int,
                            itemWidth: Int,
                            itemHeight: Int,
                            marTop: Int,
                            marLeft: Int
                    ) {

                    }

                    override fun onSpaceItemClick(day: Int, start: Int) {

                    }
                })
                .showView()
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(ClassViewModel::class.java)
        if (!mViewModel!!.getClassLessons()!!.hasObservers()) {
            mViewModel!!.getClassLessons()!!.observe(this, Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showToastMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog(getString(R.string.please_wait))
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetClassLessons(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetClassLessons(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetClassLessons(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            mySubjects.clear()
                            mySubjects.addAll(it.data.data!!.lessons)
                            mMaxLessonsNum = it.data.data!!.maxLessonsNum
                            showTableView()
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        loadData()
    }

    private fun showTableView() {
        mTableView.maxSlideItem(mMaxLessonsNum).source(mySubjects).updateView()
        val cur = mTableView.curWeek()
        mTableView.onDateBuildListener().onUpdateDate(cur, mCurrentWeek)
    }

    private fun loadData() {
        if (mCurrentDate == "") return
        val map = HashMap<String, String>()
        map["date"] = mCurrentDate
        map["classid"] = mClassId.toString()
        mViewModel?.freshGetClassLessons(map, true)
    }

    override fun lazyLoad() {

    }

    fun freshData(date: String, week: Int) {
        if (mCurrentDate != date) {
            mCurrentDate = date
            mCurrentWeek = week
            initViewModel()
        }
    }

    override fun onStart() {
        super.onStart()
        mTableView.onDateBuildListener().onHighLight()
    }
}