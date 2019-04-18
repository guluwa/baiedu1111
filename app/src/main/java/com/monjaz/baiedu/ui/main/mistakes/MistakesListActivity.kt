package com.monjaz.baiedu.ui.main.mistakes

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
import com.monjaz.baiedu.data.bean.remote.MistakeBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.MistakeBookListAdapter
import com.monjaz.baiedu.ui.adapter.MistakeListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.MistakesViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_mistakes_list.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import java.io.Serializable

class MistakesListActivity : BaseActivity() {

    private var mMistakeBookName = ""

    private var mMistakeBookId = 0

    private var type = "2"

    private var mChildId = 0

    private var mViewModel: MistakesViewModel? = null

    override val viewLayoutId: Int get() = R.layout.activity_mistakes_list

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
    }

    private fun initData() {
        type = AppUtils.getString(Contacts.TYPE, "2")
        if (type == "3") {
            if (intent.getIntExtra("childId", 0) != 0) {
                mChildId = intent.getIntExtra("childId", 0)
            } else {
                showToastMsg(getString(R.string.data_error))
                return
            }
        }
        if (intent.getIntExtra("mistakeBookId", 0) != 0) {
            mMistakeBookId = intent.getIntExtra("mistakeBookId", 0)
        } else {
            showToastMsg(getString(R.string.data_error))
            return
        }
        if (intent.getStringExtra("mistakeBookName") != "") {
            mMistakeBookName = intent.getStringExtra("mistakeBookName")
        } else {
            showToastMsg(getString(R.string.data_error))
            return
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = mMistakeBookName
        ivBack.setOnClickListener { finish() }
        if (type == "2") {
            tvRightBtn.text = getString(R.string.add)
            tvRightBtn.visibility = View.VISIBLE
            tvRightBtn.setOnClickListener {
                val intent = Intent(this, AddMistakeActivity::class.java)
                intent.putExtra("mistakeBookId", mMistakeBookId)
                startActivity(intent)
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = MistakeListAdapter(mutableListOf(PageTipBean("", 0, 1)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val intent = Intent(this@MistakesListActivity, MistakeDetailActivity::class.java)
                intent.putExtra("mistake", arg2 as Serializable)
                startActivity(intent)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(MistakesViewModel::class.java)
        if (!mViewModel!!.getMistakes()!!.hasObservers()) {
            mViewModel!!.getMistakes()!!.observe(this, Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showErrorMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog(getString(R.string.please_wait))
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetMistakes(hashMapOf(), false)
                        showErrorMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetMistakes(hashMapOf(), false)
                        showErrorMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetMistakes(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showMistakes(it.data.data!!)
                        } else {
                            showErrorMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        getMistakes()
    }

    private fun showMistakes(data: List<MistakeBean>) {
        if (data.isEmpty()) {
            if (type == "3") {
                showErrorMsg(getString(R.string.empty))
            } else {
                showErrorMsg(getString(R.string.have_no_mistake))
            }
        } else {
            (mRecyclerView.adapter as MistakeListAdapter).setData(data)
        }
    }

    private fun showErrorMsg(msg: String) {
        (mRecyclerView.adapter as MistakeListAdapter).setPageTipBean(PageTipBean(msg, 0, 1))
    }

    private fun getMistakes() {
        val map = HashMap<String, String>()
        if (AppUtils.getString(Contacts.TYPE, "2") != "2") {
            map["student_id"] = mChildId.toString()
        }
        map["book_id"] = mMistakeBookId.toString()
        mViewModel?.freshGetMistakes(map, true)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("freshMistakes"))])
    fun receiveData(fresh: String) {
        getMistakes()
    }
}
