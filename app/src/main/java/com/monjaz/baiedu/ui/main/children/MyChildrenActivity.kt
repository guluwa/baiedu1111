package com.monjaz.baiedu.ui.main.children

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.ChildBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.ui.adapter.ChildrenListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.classs.ClassLessonsActivity
import com.monjaz.baiedu.ui.viewmodel.ChildrenViewModel
import com.monjaz.baiedu.ui.viewmodel.ClassViewModel
import kotlinx.android.synthetic.main.activity_my_children.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class MyChildrenActivity : BaseActivity() {

    private var mViewModel: ChildrenViewModel? = null

    override val viewLayoutId: Int get() = R.layout.activity_my_children

    override fun initViews() {
        initToolBar()
        initRecyclerView()
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.menu_children)
        ivBack.setOnClickListener { finish() }
        tvRightBtn.visibility = View.VISIBLE
        tvRightBtn.text = getString(R.string.bind)
        tvRightBtn.setOnClickListener {
            startActivity(Intent(this@MyChildrenActivity, BindChildActivity::class.java))
        }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = ChildrenListAdapter(arrayListOf(PageTipBean("", 0, 1)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as ChildBean
                val intent = Intent(this@MyChildrenActivity, ChildPageActivity::class.java)
                intent.putExtra("childName", if (item.name == null) "" else item.name)
                intent.putExtra("childId", item.student_id)
                intent.putExtra("classId", item.class_id)
                intent.putExtra("className", if (item.class_name == null) "" else item.class_name)
                startActivity(intent)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(ChildrenViewModel::class.java)
        if (!mViewModel!!.getChildren()!!.hasObservers()) {
            mViewModel!!.getChildren()!!.observe(this, Observer {
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
                        mViewModel!!.freshGetChildren(hashMapOf(), false)
                        showErrorMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetChildren(hashMapOf(), false)
                        showErrorMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetChildren(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showChildren(it.data.data!!)
                        } else {
                            showErrorMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        getChildren()
    }

    private fun showChildren(data: List<ChildBean>) {
        if (data.isEmpty()) {
            showErrorMsg(getString(R.string.have_no_child))
        } else {
            (mRecyclerView.adapter as ChildrenListAdapter).setData(data)
        }
    }

    private fun showErrorMsg(msg: String) {
        (mRecyclerView.adapter as ChildrenListAdapter).setPageTipBean(PageTipBean(msg, 0, 1))
    }

    private fun getChildren() {
        mViewModel?.freshGetChildren(HashMap(), true)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("freshChildren"))])
    fun receiveData(fresh: String) {
        getChildren()
    }
}
