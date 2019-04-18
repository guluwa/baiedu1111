package com.monjaz.baiedu.ui.main.quiz

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.hwangjr.rxbus.RxBus
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.ClassInfoBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.ui.adapter.StudentQuizListAdapter
import com.monjaz.baiedu.ui.dialog.InputWordDialog
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.ClassViewModel
import com.monjaz.baiedu.ui.viewmodel.QuizViewModel
import kotlinx.android.synthetic.main.activity_student_quiz.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class StudentQuizActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_student_quiz

    private var mViewModel: QuizViewModel? = null

    private var mClassId = 0

    private var mUserId = 0

    private var mContent = ""

    private var isSend = false

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
    }

    private fun initData() {
        if (intent.getIntExtra("classId", 0) != 0) {
            mClassId = intent.getIntExtra("classId", 0)
        } else {
            showToastMsg(getString(R.string.data_error))
            onBackPressed()
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.student_menu_quiz)
        ivBack.setOnClickListener { finish() }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = StudentQuizListAdapter(mutableListOf(PageTipBean("", 0, 1)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as ClassInfoBean.TeacherListBean
                mUserId = item.teacherid
                InputWordDialog("", this@StudentQuizActivity, R.style.DialogStyle, object : OnClickListener {
                    override fun click(arg1: Int, arg2: Any) {
                        if (arg2 is String && arg2 == "") {
                            showToastMsg(getString(R.string.plz_say_something))
                        } else {
                            mContent = arg2 as String
                            sendQuiz()
                        }
                    }
                }).show()
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(QuizViewModel::class.java)
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
            mViewModel!!.sendQuiz()!!.observe(this, Observer {
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
                        mViewModel!!.freshSendQuiz(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSendQuiz(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSendQuiz(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            isSend = true
                            showToastMsg(getString(R.string.send_success))
                        } else {
                            showToastMsg(getString(R.string.send_failed))
                        }
                    }
                }
            })
        }
        queryClassDetail()
    }

    private fun showClassInfo(data: ClassInfoBean) {
        if (data.teacherlist.isEmpty()) {
            showErrorMsg(getString(R.string.empty))
        } else {
            (mRecyclerView.adapter as StudentQuizListAdapter).setData(data.teacherlist)
        }
    }

    private fun showErrorMsg(msg: String) {
        (mRecyclerView.adapter as StudentQuizListAdapter).setPageTipBean(PageTipBean(msg, 0, 1))
    }

    private fun queryClassDetail() {
        if (mClassId != 0) {
            val map = HashMap<String, String>()
            map["classid"] = mClassId.toString()
            mViewModel?.freshQueryClassDetail(map, true)
        }
    }

    private fun sendQuiz() {
        val map = HashMap<String, String>()
        map["to_user_id"] = mUserId.toString()
        map["contents"] = mContent
        mViewModel?.freshSendQuiz(map, true)
    }

    override fun onBackPressed() {
        if (isSend) {
            RxBus.get().post("freshSentQuiz", "")
        }
        super.onBackPressed()
    }
}