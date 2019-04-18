package com.monjaz.baiedu.ui.main.quiz

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.data.bean.remote.QuizListBean
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.HomeWorkResultListAdapter
import com.monjaz.baiedu.ui.adapter.QuizDetailListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.QuizViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_home_work_detail.*
import kotlinx.android.synthetic.main.activity_quiz_detail.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class SentQuizListActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_quiz_detail

    private var type = "2"

    private var mClassId = 0

    private var mViewModel: QuizViewModel? = null

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
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
        tvToolBarTitle.text = getString(R.string.sent)
        ivBack.setOnClickListener { finish() }
    }

    private fun initClickEvent() {
        if (type != "1") {
            tvInputWord.visibility = View.VISIBLE
            tvInputWord.setOnClickListener {
                val intent = Intent(this, StudentQuizActivity::class.java)
                intent.putExtra("classId", mClassId)
                startActivity(intent)
            }
        } else {
            tvInputWord.visibility = View.GONE
        }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = QuizDetailListAdapter(2, arrayListOf(PageTipBean("", 0, 1)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {

            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(QuizViewModel::class.java)
        if (!mViewModel!!.sentQuizList()!!.hasObservers()) {
            mViewModel!!.sentQuizList()!!.observe(this, Observer {
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
                        mViewModel!!.freshSentQuizList(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSentQuizList(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSentQuizList(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showQuizList(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.add_failed))
                        }
                    }
                }
            })
        }
        loadData()
    }

    private fun loadData() {
        mViewModel?.freshSentQuizList(HashMap(), true)
    }

    private fun showQuizList(data: List<QuizListBean>) {
        if (data.isNotEmpty()) {
            (mRecyclerView.adapter as QuizDetailListAdapter).setData(data)
        } else {
            showErrorMsg(getString(R.string.no_more_records))
        }
    }

    private fun showErrorMsg(msg: String) {
        (mRecyclerView.adapter as QuizDetailListAdapter).setPageTipBean(PageTipBean(msg, 0, 1))
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("freshSentQuiz"))])
    fun receiveData(fresh: String) {
        loadData()
    }
}
