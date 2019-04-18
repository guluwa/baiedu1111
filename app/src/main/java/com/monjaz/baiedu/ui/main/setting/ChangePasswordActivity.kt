package com.monjaz.baiedu.ui.main.setting

import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.ui.viewmodel.SettingViewModel
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class ChangePasswordActivity : BaseActivity() {

    private var mViewModel: SettingViewModel? = null

    override val viewLayoutId: Int get() = R.layout.activity_change_password

    override fun initViews() {
        initToolBar()
        initClickEvent()
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.change_password)
        ivBack.setOnClickListener { finish() }
    }

    private fun initClickEvent() {
        tvSure.setOnClickListener {
            changePassword()
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
        if (!mViewModel!!.updateUserInfo()!!.hasObservers()) {
            mViewModel!!.updateUserInfo()!!.observe(this, Observer {
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
                        mViewModel!!.freshUpdateUserInfo(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUpdateUserInfo(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUpdateUserInfo(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.change_success))
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
    }

    private fun changePassword() {
        if (TextUtils.isEmpty(etPasswordOld.text)) {
            showToastMsg(getString(R.string.edit_password_old_hint))
            return
        }
        if (TextUtils.isEmpty(etPasswordNew.text)) {
            showToastMsg(getString(R.string.edit_password_new_hint))
            return
        }
        if (TextUtils.isEmpty(etPasswordRepeat.text)) {
            showToastMsg(getString(R.string.edit_password_repeat_hint))
            return
        }
        if (etPasswordNew.text.toString() != etPasswordRepeat.text.toString()) {
            showToastMsg(getString(R.string.change_password_tip))
            return
        }
        val map = HashMap<String, String>()
        map["password"] = etPasswordNew.text.toString().trim()
        mViewModel?.freshUpdateUserInfo(map, true)
    }
}
