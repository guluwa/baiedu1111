package com.monjaz.baiedu.ui.main.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.LazyFragment
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.ui.adapter.LiveClassTeacherListAdapter
import com.monjaz.baiedu.ui.adapter.LiveCourseListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.live.LiveRecordActivity
import com.monjaz.baiedu.ui.main.live.LiveWatchActivity
import com.monjaz.baiedu.ui.main.live.TeacherLiveListActivity
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import kotlinx.android.synthetic.main.fragment_teacher_live.*
import java.util.*

class TeacherLiveFragment : LazyFragment() {

    override val viewLayoutId: Int get() = R.layout.fragment_teacher_live

    private val GENERATE_STREAM_TEXT = "http://api-demo.qnsdk.com/v1/live/stream/"

    companion object {
        fun newInstance(): TeacherLiveFragment {
            val fragment = TeacherLiveFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    override fun initViews() {
        initToolBar()
        initRecyclerView()
    }

    override fun lazyLoad() {

    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.live)
        ivBack.visibility = View.GONE
        tvRightBtn.text = ""
        tvRightBtn.visibility = View.VISIBLE
        tvRightBtn.setOnClickListener {
            Thread(Runnable {
                val mLiveUrl = AppUtils.syncRequest(GENERATE_STREAM_TEXT + UUID.randomUUID())
                        ?: ""
                if (mLiveUrl == "") {
                    showToastMsg("直播链接生成失败")
                    return@Runnable
                }
                println(mLiveUrl)
                val intent = Intent(context, LiveRecordActivity::class.java)
                intent.putExtra("liveUrl", mLiveUrl)
                startActivity(intent)
            }).start()
        }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = LiveCourseListAdapter(arrayListOf(1, 1, 1, 1, 1), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {

            }
        })
    }
}