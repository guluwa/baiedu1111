package com.monjaz.baiedu.ui.main.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.MessageListBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.ui.viewmodel.MessageViewModel
import kotlinx.android.synthetic.main.activity_message_detail.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class MessageDetailActivity : BaseActivity() {

    private var mViewModel: MessageViewModel? = null

    var message: MessageListBean.DataBean? = null

    override val viewLayoutId: Int get() = R.layout.activity_message_detail

    override fun initViews() {
        initData()
        initToolBar()
    }

    private fun initData() {
        if (intent.getSerializableExtra("message") == null) {
            showToastMsg(getString(R.string.data_error))
            finish()
        } else {
            message = intent.getSerializableExtra("message") as MessageListBean.DataBean
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.message_detail)
        ivBack.setOnClickListener { finish() }
        tvMessageSendDate.text = String.format("%s%s", getString(R.string.message_date), message!!.gettime)
        tvMessageContent.text = message!!.content
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java)
        if (!mViewModel!!.readMessage()!!.hasObservers()) {
            mViewModel!!.readMessage()!!.observe(this, Observer {
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
                        mViewModel!!.freshReadMessage(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshReadMessage(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshReadMessage(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {

                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        readMessage()
    }

    private fun readMessage() {
        val map = HashMap<String, String>()
        map["pushid"] = message!!.pushid.toString()
        mViewModel?.freshReadMessage(map, true)
    }
}
