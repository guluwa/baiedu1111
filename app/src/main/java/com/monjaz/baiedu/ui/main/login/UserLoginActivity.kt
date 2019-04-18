package com.monjaz.baiedu.ui.main.login

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.main.MainActivity
import com.monjaz.baiedu.ui.main.main.MainNewActivity
import com.monjaz.baiedu.ui.main.register.pupil.PupilRegisterActivity
import com.monjaz.baiedu.ui.viewmodel.LoginViewModel
import com.monjaz.baiedu.utils.AppUtils
import com.monjaz.baiedu.utils.FinishActivityManager
import kotlinx.android.synthetic.main.activity_user_login.*

class UserLoginActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_user_login

    private var mViewModel: LoginViewModel? = null

    override fun initViews() {
        initImage()
        initClickEvent()
    }

    private fun initImage() {
        Glide.with(this).asBitmap()
                .apply(RequestOptions().circleCrop())
                .load(R.mipmap.ic_launcher)
                .into(ivAppLogo)
    }

    private fun initClickEvent() {
        ivBack.setOnClickListener { finish() }
        tvRegister.setOnClickListener { startActivity(Intent(this, PupilRegisterActivity::class.java)) }
        tvLogin.setOnClickListener {
            userLogin()
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        if (!mViewModel!!.passwordLogin()!!.hasObservers()) {
            mViewModel!!.passwordLogin()!!.observe(this, Observer {
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
                        mViewModel!!.freshPasswordLogin(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshPasswordLogin(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshPasswordLogin(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.login_success))
                            AppUtils.setString(Contacts.TOKEN, it.data.data!!.access_token)
                            AppUtils.setString(Contacts.TYPE, it.data.data!!.tid.toString())
                            AppUtils.setString(Contacts.ID, it.data.data!!.id.toString())
                            FinishActivityManager.getInstance().finishAllActivity()
                            startActivity(Intent(this, MainNewActivity::class.java))
                        } else {
                            showToastMsg(getString(R.string.login_failed))
                        }
                    }
                }
            })
        }
    }

    private fun userLogin() {
        if (TextUtils.isEmpty(etAccount.text)) {
            showToastMsg(getString(R.string.edit_account_hint))
            return
        }
        if (TextUtils.isEmpty(etPassword.text)) {
            showToastMsg(getString(R.string.edit_password_hint))
            return
        }
        val map = HashMap<String, String>()
        map["username"] = etAccount.text.toString().trim()
        map["password"] = etPassword.text.toString().trim()
        mViewModel?.freshPasswordLogin(map, true)
    }
}
