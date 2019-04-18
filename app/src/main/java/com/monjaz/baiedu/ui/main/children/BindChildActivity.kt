package com.monjaz.baiedu.ui.main.children

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
import com.monjaz.baiedu.ui.viewmodel.ChildrenViewModel
import kotlinx.android.synthetic.main.activity_bind_child.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class BindChildActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_bind_child

    private var isQuery = false

    private var isBind = false

    private var mChildId = 0

    private var mViewModel: ChildrenViewModel? = null

    override fun initViews() {
        initToolBar()
        initEditText()
        initClickEvent()
    }

    private fun initEditText() {
        etChildName.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mQueryResultContainer.visibility = View.GONE
                isQuery = false
                mChildId = 0
                tvSure.text = getString(R.string.query)
            }
        })
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.bind_child)
        ivBack.setOnClickListener { finish() }
    }

    private fun initClickEvent() {
        tvSure.setOnClickListener {
            if (isQuery) {
                bindChild()
            } else {
                queryChild()
            }
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(ChildrenViewModel::class.java)
        if (!mViewModel!!.searchChild()!!.hasObservers()) {
            mViewModel!!.searchChild()!!.observe(this, Observer {
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
                        mViewModel!!.freshSearchChild(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSearchChild(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSearchChild(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            isQuery = true
                            mChildId = it.data.data!!.id
                            tvSure.text = getString(R.string.bind)
                            mQueryResultContainer.visibility = View.VISIBLE
                            tvChildInfo.text = String.format("%s %s\n%s %s",
                                    getString(R.string.name), it.data.data!!.name,
                                    getString(R.string.sex),
                                    if (it.data.data!!.sex == 1) {
                                        getString(R.string.male)
                                    } else {
                                        getString(R.string.female)
                                    })
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
            mViewModel!!.bindChild()!!.observe(this, Observer {
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
                        mViewModel!!.freshBindChild(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshBindChild(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshBindChild(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.bind_success))
                            isBind = true
                            onBackPressed()
                        } else {
                            showToastMsg(getString(R.string.bind_failed))
                        }
                    }
                }
            })
        }
    }

    private fun queryChild() {
        if (TextUtils.isEmpty(etChildName.text)) {
            showToastMsg(getString(R.string.edit_child_name_hint))
            return
        }
        val map = HashMap<String, String>()
        map["username"] = etChildName.text.toString().trim()
        mViewModel?.freshSearchChild(map, true)
    }

    private fun bindChild() {
        val map = HashMap<String, String>()
        map["student_id"] = mChildId.toString()
        mViewModel?.freshBindChild(map, true)
    }

    override fun onBackPressed() {
        if (isBind) {
            RxBus.get().post("freshChildren", "")
        }
        super.onBackPressed()
    }
}
