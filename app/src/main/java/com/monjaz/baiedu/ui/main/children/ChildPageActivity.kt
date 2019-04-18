package com.monjaz.baiedu.ui.main.children

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.local.MainMenuBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.MainMenuListAdapter
import com.monjaz.baiedu.ui.dialog.ChildLearnStatusDialog
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.classs.ClassDetailActivity
import com.monjaz.baiedu.ui.main.classs.JoinNewClassActivity
import com.monjaz.baiedu.ui.main.classs.MyClassActivity
import com.monjaz.baiedu.ui.main.mistakes.MyMistakesActivity
import com.monjaz.baiedu.ui.main.quiz.SelectQuizTypeActivity
import com.monjaz.baiedu.ui.main.quiz.StudentQuizActivity
import com.monjaz.baiedu.ui.main.work.ClassHomeWorksActivity
import com.monjaz.baiedu.ui.main.work.TeacherHomeWorkActivity
import com.monjaz.baiedu.ui.viewmodel.ChildrenViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class ChildPageActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_child_page

    private var mChildName = ""

    private var mChildId = 0

    private var mClassId = 0

    private var mClassName = ""

    private var type = "2"

    private var mMenuList = mutableListOf<MainMenuBean>()

    private var mViewModel: ChildrenViewModel? = null

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
    }

    private fun initData() {
        type = AppUtils.getString(Contacts.TYPE, "2")
        if (intent.getStringExtra("childName") != "") {
            mChildName = intent.getStringExtra("childName")
        } else {
            showToastMsg(getString(R.string.data_error))
            return
        }
        if (intent.getIntExtra("childId", 0) != 0) {
            mChildId = intent.getIntExtra("childId", 0)
        } else {
            showToastMsg(getString(R.string.data_error))
            return
        }
        mClassId = intent.getIntExtra("classId", 0)
        mClassName = intent.getStringExtra("className")
        mMenuList.add(
                (MainMenuBean(
                        1,
                        getString(R.string.menu_child_class),
                        getString(R.string.children_class_explain),
                        R.color.main_menu_item_color1,
                        R.drawable.ic_main_menu_class
                ))
        )
        mMenuList.add(
                (MainMenuBean(
                        2,
                        getString(R.string.menu_child_work),
                        getString(R.string.children_work_explain),
                        R.color.main_menu_item_color2,
                        R.drawable.ic_main_menu_work
                ))
        )
        mMenuList.add(
                (MainMenuBean(
                        3,
                        getString(R.string.menu_mistakes),
                        getString(R.string.children_mistake_explain),
                        R.color.main_menu_item_color3,
                        R.drawable.ic_main_menu_mistake
                ))
        )
        mMenuList.add(
                (MainMenuBean(
                        4,
                        getString(R.string.student_menu_quiz),
                        getString(R.string.children_quiz_explain),
                        R.color.main_menu_item_color5,
                        R.drawable.ic_main_menu_quiz
                ))
        )
    }

    private fun initToolBar() {
        tvToolBarTitle.text = mChildName
        ivBack.setOnClickListener { finish() }
        if (type == "3") {
            ivRightBtn.setImageResource(R.drawable.ic_child_learn_status)
            ivRightBtn.visibility = View.VISIBLE
            ivRightBtn.setOnClickListener {
                showChildLearnStatus()
            }
        }
    }

    private fun showChildLearnStatus() {
        val map = HashMap<String, String>()
        map["student_id"] = mChildId.toString()
        mViewModel?.freshStudentLearnStatus(map, true)
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = MainMenuListAdapter(mMenuList, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as MainMenuBean
                when (item.id) {
                    1 ->
                        if (mClassId == 0) {
                            showToastMsg(getString(R.string.child_have_no_class))
                        } else {
                            val intent = Intent(this@ChildPageActivity, ClassDetailActivity::class.java)
                            intent.putExtra("classId", mClassId)
                            startActivity(intent)
                        }
                    2 ->
                        if (mClassId == 0 || mClassName == "") {
                            showToastMsg(getString(R.string.child_have_no_class))
                        } else {
                            val intent = Intent(this@ChildPageActivity, ClassHomeWorksActivity::class.java)
                            intent.putExtra("classId", mClassId)
                            intent.putExtra("className", mClassName)
                            intent.putExtra("childId", mChildId)
                            startActivity(intent)
                        }
                    3 -> {
                        val intent = Intent(this@ChildPageActivity, MyMistakesActivity::class.java)
                        intent.putExtra("childId", mChildId)
                        startActivity(intent)
                    }
                    4 -> {
                        if (mClassId == 0) {
                            showToastMsg(getString(R.string.child_have_no_class))
                        } else {
                            val intent = Intent(this@ChildPageActivity, SelectQuizTypeActivity::class.java)
                            intent.putExtra("classId", mClassId)
                            startActivity(intent)
                        }
                    }
                }
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(ChildrenViewModel::class.java)
        if (!mViewModel!!.studentLearnStatus()!!.hasObservers()) {
            mViewModel!!.studentLearnStatus()!!.observe(this, Observer {
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
                        mViewModel!!.freshStudentLearnStatus(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshStudentLearnStatus(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshStudentLearnStatus(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            ChildLearnStatusDialog(it.data.data!!, themedContext, R.style.DialogStyle).show()
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
    }
}
