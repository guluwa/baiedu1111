package com.monjaz.baiedu.ui.main.classs

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.ClassInfoBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.ClassStudentListAdapter
import com.monjaz.baiedu.ui.adapter.ClassTeacherListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.ClassViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_class_detail.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class ClassDetailActivity : BaseActivity() {

    private var mViewModel: ClassViewModel? = null

    private var mClassId = 0

    override val viewLayoutId: Int get() = R.layout.activity_class_detail

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
    }

    private fun initData() {
        if (intent.getIntExtra("classId", 0) == 0) {
            showToastMsg(getString(R.string.data_error))
            finish()
        } else {
            mClassId = intent.getIntExtra("classId", 0)
        }
    }

    private fun initToolBar() {
        ivBack.setOnClickListener { finish() }
        tvRightBtn.visibility = View.VISIBLE
        tvRightBtn.text = getString(R.string.curriculum)
        tvRightBtn.setOnClickListener {
            val intent = Intent(this@ClassDetailActivity, ClassLessonsActivity::class.java)
            intent.putExtra("classId", mClassId)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        mRecyclerViewTeachers.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mRecyclerViewTeachers.adapter = ClassTeacherListAdapter(arrayListOf(), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as ClassInfoBean.TeacherListBean
                if (item.mobile == null) {
                    showToastMsg(getString(R.string.teacher_have_no_mobile))
                } else {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.mobile}"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        })

        mRecyclerViewStudents.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mRecyclerViewStudents.adapter = ClassStudentListAdapter(arrayListOf())
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(ClassViewModel::class.java)
        if (!mViewModel!!.queryClassDetail()!!.hasObservers()) {
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
                            showClassInfo(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        queryClassDetail()
    }

    private fun showClassInfo(classInfoBean: ClassInfoBean) {
        mScrollView.visibility = View.VISIBLE
        tvToolBarTitle.text = classInfoBean.classname

        tvClassNum.text = String.format("%s%s", getString(R.string.class_number), classInfoBean.classnumber)
        tvClassName.text = String.format("%s%s", getString(R.string.class_name), classInfoBean.classname)
        tvClassSchool.text = String.format("%s%s", getString(R.string.class_school), classInfoBean.schoolname)

        (mRecyclerViewTeachers.adapter as ClassTeacherListAdapter).list = classInfoBean.teacherlist
        (mRecyclerViewTeachers.adapter as ClassTeacherListAdapter).notifyDataSetChanged()

        (mRecyclerViewStudents.adapter as ClassStudentListAdapter).list = classInfoBean.studentlist
        (mRecyclerViewStudents.adapter as ClassStudentListAdapter).notifyDataSetChanged()
    }

    private fun queryClassDetail() {
        if (mClassId != 0) {
            val map = HashMap<String, String>()
            map["classid"] = mClassId.toString()
            mViewModel?.freshQueryClassDetail(map, true)
        }
    }
}
