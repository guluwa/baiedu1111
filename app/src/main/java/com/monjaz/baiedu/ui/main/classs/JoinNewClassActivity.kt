package com.monjaz.baiedu.ui.main.classs

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hwangjr.rxbus.RxBus
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.ui.viewmodel.ClassViewModel
import kotlinx.android.synthetic.main.activity_join_new_class.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class JoinNewClassActivity : BaseActivity() {

    private var mViewModel: ClassViewModel? = null

    private var isQuery = false

    private var mClassId = 0

    private var isJoin = false

    override val viewLayoutId: Int get() = R.layout.activity_join_new_class

    override fun initViews() {
        initToolBar()
        initEditText()
        initClickEvent()
    }

    private fun initEditText() {
        etClassNumText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mQueryResultContainer.visibility = View.GONE
                isQuery = false
                mClassId = 0
                tvSure.text = getString(R.string.query)
            }
        })
    }

    private fun initClickEvent() {
        tvSure.setOnClickListener {
            if (isQuery) {
                joinClass()
            } else {
                queryClass()
            }
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.join_class)
        ivBack.setOnClickListener { finish() }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(ClassViewModel::class.java)
        if (!mViewModel!!.queryClass()!!.hasObservers()) {
            mViewModel!!.queryClass()!!.observe(this, Observer {
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
                        mViewModel!!.freshQueryClass(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshQueryClass(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshQueryClass(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            isQuery = true
                            mClassId = it.data.data!!.classid
                            tvSure.text = getString(R.string.join)
                            mQueryResultContainer.visibility = View.VISIBLE
                            tvClassName.text = String.format(
                                "%s%s\n%s%s",
                                getString(R.string.class_school), it.data.data!!.schoolname,
                                getString(R.string.class_name), it.data.data!!.classname
                            )
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
            mViewModel!!.joinClass()!!.observe(this, Observer {
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
                        mViewModel!!.freshJoinClass(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshJoinClass(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshJoinClass(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.join_success))
                            isJoin = true
                            onBackPressed()
                        } else {
                            showToastMsg(getString(R.string.join_failed))
                        }
                    }
                }
            })
        }
    }

    private fun queryClass() {
        if (TextUtils.isEmpty(etClassNumText.text)) {
            showToastMsg(getString(R.string.edit_class_number_hint))
            return
        }
        val map = HashMap<String, String>()
        map["classnumber"] = etClassNumText.text.toString().trim()
        mViewModel?.freshQueryClass(map, true)
    }

    private fun joinClass() {
        if (mClassId != 0) {
            val map = HashMap<String, String>()
            map["classid"] = mClassId.toString()
            mViewModel?.freshJoinClass(map, true)
        }
    }

    override fun onBackPressed() {
        if (isJoin) {
            RxBus.get().post("freshUserInfo", "")
        }
        super.onBackPressed()
    }
}
