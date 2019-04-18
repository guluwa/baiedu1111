package com.monjaz.baiedu.ui.main.quiz

import android.content.Intent
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.viewmodel.QuizViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_select_quiz_type.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class SelectQuizTypeActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_select_quiz_type

    private var mClassId = 0

    private var type = "2"

    override fun initViews() {
        initData()
        initToolBar()
        initClickEvent()
    }

    private fun initData() {
        type = AppUtils.getString(Contacts.TYPE, "2")
        if (type != "1") {
            if (intent.getIntExtra("classId", 0) != 0) {
                mClassId = intent.getIntExtra("classId", 0)
            } else {
                showToastMsg(getString(R.string.data_error))
                finish()
            }
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.student_menu_quiz)
        ivBack.setOnClickListener { finish() }
    }

    private fun initClickEvent() {
        tvReceived.setOnClickListener {
            startActivity(Intent(this, ReceivedQuizListActivity::class.java))
        }

        tvSent.setOnClickListener {
            val intent = Intent(this, SentQuizListActivity::class.java)
            if (type != "1") {
                intent.putExtra("classId", mClassId)
            }
            startActivity(intent)
        }
    }
}
