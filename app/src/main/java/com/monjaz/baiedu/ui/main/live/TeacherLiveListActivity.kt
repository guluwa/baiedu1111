package com.monjaz.baiedu.ui.main.live

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.ui.adapter.LiveCourseListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_teacher_live_list.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import java.util.*

class TeacherLiveListActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_teacher_live_list

    override fun initViews() {
        initToolBar()
        initRecyclerView()
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.live_courses)
        ivBack.setOnClickListener { finish() }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = LiveCourseListAdapter(arrayListOf(1, 1, 1, 1, 1, 1), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                startActivity(Intent(this@TeacherLiveListActivity, LiveWatchActivity::class.java))
            }
        })
    }
}
