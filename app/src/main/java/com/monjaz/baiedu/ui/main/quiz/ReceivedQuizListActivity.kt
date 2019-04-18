package com.monjaz.baiedu.ui.main.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.data.bean.remote.QuizListBean
import com.monjaz.baiedu.ui.adapter.QuizDetailListAdapter
import com.monjaz.baiedu.ui.dialog.InputWordDialog
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.QuizViewModel
import kotlinx.android.synthetic.main.activity_received_quiz_list.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class ReceivedQuizListActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_received_quiz_list

    private var mViewModel: QuizViewModel? = null

    private var mUserId = 0

    private var mContent = ""

    override fun initViews() {
        initToolBar()
        initRecyclerView()
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.received)
        ivBack.setOnClickListener { finish() }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = QuizDetailListAdapter(1, arrayListOf(PageTipBean("", 0, 1)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as QuizListBean
                mUserId = item.create_user_id
                InputWordDialog("${getString(R.string.reply)}${item.create_name}", this@ReceivedQuizListActivity, R.style.DialogStyle, object : OnClickListener {
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
        if (!mViewModel!!.receivedQuizList()!!.hasObservers()) {
            mViewModel!!.receivedQuizList()!!.observe(this, Observer {
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
                        mViewModel!!.freshReceivedQuizList(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshReceivedQuizList(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshReceivedQuizList(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showQuizList(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.add_failed))
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
                            showToastMsg(getString(R.string.send_success))
                        } else {
                            showToastMsg(getString(R.string.send_failed))
                        }
                    }
                }
            })
        }
        loadData()
    }

    private fun loadData() {
        mViewModel?.freshReceivedQuizList(HashMap(), true)
    }

    private fun sendQuiz() {
        val map = HashMap<String, String>()
        map["to_user_id"] = mUserId.toString()
        map["contents"] = mContent
        mViewModel?.freshSendQuiz(map, true)
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
}
