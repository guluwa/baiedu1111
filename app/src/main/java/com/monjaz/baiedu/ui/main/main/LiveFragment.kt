package com.monjaz.baiedu.ui.main.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.LazyFragment
import com.monjaz.baiedu.data.bean.remote.ClassInfoBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.ui.adapter.ClassTeacherListAdapter
import com.monjaz.baiedu.ui.adapter.LiveClassTeacherListAdapter
import com.monjaz.baiedu.ui.adapter.LiveListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.live.TeacherLiveListActivity
import com.monjaz.baiedu.ui.viewmodel.LiveViewModel
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import kotlinx.android.synthetic.main.fragment_live.*

class LiveFragment : LazyFragment() {

    override val viewLayoutId: Int get() = R.layout.fragment_live

    private var mViewModel: LiveViewModel? = null

    private var mClassId = 0

    companion object {
        fun newInstance(): LiveFragment {
            val fragment = LiveFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    override fun initViews() {
        initToolBar()
        initRecyclerView()
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.live)
        ivBack.visibility = View.GONE
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = LiveClassTeacherListAdapter(arrayListOf(PageTipBean("", 0, 0)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                startActivity(Intent(context, TeacherLiveListActivity::class.java))
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(LiveViewModel::class.java)
        if (!mViewModel!!.queryClassDetail()!!.hasActiveObservers()) {
            mViewModel!!.queryClassDetail()!!.observe(this, Observer {
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
                        mViewModel!!.freshQueryClassDetail(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshQueryClassDetail(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshQueryClassDetail(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showTeacherList(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
    }

    override fun lazyLoad() {
        if (mClassId != 0) {
            val map = HashMap<String, String>()
            map["classid"] = mClassId.toString()
            mViewModel?.freshQueryClassDetail(map, true)
        } else {
            if (mRecyclerView != null && mRecyclerView.adapter != null) {
                (mRecyclerView.adapter as LiveClassTeacherListAdapter)
                        .setPageTipBean(PageTipBean(getString(R.string.have_not_join_class), 0, 1))
            }
        }
    }

    fun queryClassDetail(classId: Int) {
        Log.e("error", "queryClassDetail")
        mClassId = classId
        lazyLoad()
    }

    private fun showTeacherList(data: ClassInfoBean) {
        if (data.teacherlist.isNotEmpty()) {
            (mRecyclerView.adapter as LiveClassTeacherListAdapter).setData(data.teacherlist)
        } else {
            (mRecyclerView.adapter as LiveClassTeacherListAdapter)
                    .setPageTipBean(PageTipBean(getString(R.string.no_more_records), 0, 1))
        }
    }
}