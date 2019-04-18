package com.monjaz.baiedu.ui.main.register.pupil

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.monjaz.baiedu.ui.main.register.school.SchoolRegisterActivity
import com.monjaz.baiedu.ui.viewmodel.LoginViewModel
import com.monjaz.baiedu.utils.AppUtils
import com.monjaz.baiedu.utils.FinishActivityManager
import kotlinx.android.synthetic.main.activity_pupil_register.*

class PupilRegisterActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_pupil_register

    private var mViewModel: LoginViewModel? = null

    private var type = "2"

    private var sex = "Male"

    override fun initViews() {
        initImage()
        initSpinner()
        initClickEvent()
    }

    private fun initImage() {
        Glide.with(this).asBitmap()
                .apply(RequestOptions().circleCrop())
                .load(R.mipmap.ic_launcher)
                .into(ivAppLogo)
    }

    private fun initSpinner() {
        val sexArray = arrayOf<String>(getString(R.string.male), getString(R.string.female))
        val sexSpinnerAdapter = ArrayAdapter(
                this,
                R.layout.spinner_select_item, sexArray
        )
        sexSpinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item)
        mSexSpinner.adapter = sexSpinnerAdapter
        mSexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sex = if (position == 0) "1" else "2"
            }
        }

        val typeArray = arrayOf<String>(getString(R.string.student), getString(R.string.parents))
        val typeSpinnerAdapter = ArrayAdapter(
                this,
                R.layout.spinner_select_item, typeArray
        )
        typeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item)
        mTypeSpinner.adapter = typeSpinnerAdapter
        mTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                type = if (position == 0) "2" else "3"
            }
        }
    }

    private fun initClickEvent() {
        ivBack.setOnClickListener { finish() }
        tvSchoolRegister.setOnClickListener { startActivity(Intent(this, SchoolRegisterActivity::class.java)) }
        tvRegister.setOnClickListener {
            userRegister()
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        if (!mViewModel!!.userRegister()!!.hasObservers()) {
            mViewModel!!.userRegister()!!.observe(this, Observer {
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
                        mViewModel!!.freshUserRegister(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUserRegister(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUserRegister(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.register_success))
                            AppUtils.setString(Contacts.TOKEN, it.data.data!!.access_token)
                            AppUtils.setString(Contacts.TYPE, type)
                            AppUtils.setString(Contacts.ID, it.data.data!!.id.toString())
                            FinishActivityManager.getInstance().finishAllActivity()
                            startActivity(Intent(this, MainNewActivity::class.java))
                        } else {
                            showToastMsg(getString(R.string.register_failed))
                        }
                    }
                }
            })
        }
    }

    private fun userRegister() {
        if (TextUtils.isEmpty(etAccount.text)) {
            showToastMsg(getString(R.string.edit_account_hint))
            return
        }
        if (TextUtils.isEmpty(etPassword.text)) {
            showToastMsg(getString(R.string.edit_password_hint))
            return
        }
        if (TextUtils.isEmpty(etName.text)) {
            showToastMsg(getString(R.string.edit_user_name_hint))
            return
        }
        if (TextUtils.isEmpty(etPhone.text)) {
            showToastMsg(getString(R.string.edit_user_phone_hint))
            return
        }
        if (TextUtils.isEmpty(etEmail.text)) {
            showToastMsg(getString(R.string.edit_user_email_hint))
            return
        }
        val map = HashMap<String, String>()
        map["username"] = etAccount.text.toString().trim()
        map["password"] = etPassword.text.toString().trim()
        map["name"] = etName.text.toString().trim()
        map["tel"] = etPhone.text.toString().trim()
        map["email"] = etEmail.text.toString().trim()
        map["tid"] = type
        map["sex"] = sex
        mViewModel?.freshUserRegister(map, true)
    }
}