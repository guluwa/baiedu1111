package com.monjaz.baiedu.ui.main.register.school

import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.monjaz.baiedu.base.BaseActivity
import kotlinx.android.synthetic.main.activity_school_register.*
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.ui.viewmodel.LoginViewModel
import com.paytabs.paytabs_sdk.utils.PaymentParams
import com.paytabs.paytabs_sdk.payment.ui.activities.PayTabActivity
import android.content.Intent
import com.monjaz.baiedu.R
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.utils.AppUtils
import android.widget.Toast
import android.app.Activity
import android.util.Log

class SchoolRegisterActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_school_register

    private var mViewModel: LoginViewModel? = null

    private var type = "1"

    private var mSchoolId = ""

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
        val array = arrayOf<String>(
                getString(R.string.primary_school), getString(R.string.junior_middle_school),
                getString(R.string.senior_middle_school), getString(R.string.university)
        )
        val spinnerAdapter = ArrayAdapter(
                this,
                R.layout.spinner_select_item, array
        )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item)
        mSpinner.adapter = spinnerAdapter
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                type = when (position) {
                    0 -> "1"
                    1 -> "2"
                    2 -> "3"
                    else -> "4"
                }
            }
        }
    }

    private fun initClickEvent() {
        ivBack.setOnClickListener { finish() }
        tvRegister.setOnClickListener {
            schoolRegister()
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        if (!mViewModel!!.schoolRegister()!!.hasObservers()) {
            mViewModel!!.schoolRegister()!!.observe(this, Observer {
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
                        mViewModel!!.freshSchoolRegister(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSchoolRegister(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSchoolRegister(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            if (it.data.data!!.pay_price.toDoubleOrNull() != null) {
                                mSchoolId = it.data.data!!.id
                                payMoney(it.data.data!!.id, it.data.data!!.pay_price.toDouble())
                                showToastMsg(getString(R.string.register_success))
                            } else {
                                showToastMsg(getString(R.string.register_failed))
                            }
                        } else {
                            showToastMsg(getString(R.string.register_failed))
                        }
                    }
                }
            })
            mViewModel!!.schoolUpdate()!!.observe(this, Observer {
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
                        mViewModel!!.freshSchoolUpdate(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSchoolUpdate(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshSchoolUpdate(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.pay_success))
                            finish()
                        } else {
                            showToastMsg(getString(R.string.pay_failed))
                        }
                    }
                }
            })
        }
    }

    private fun schoolRegister() {
        if (TextUtils.isEmpty(etAccount.text)) {
            showToastMsg(getString(R.string.edit_account_hint))
            return
        }
        if (TextUtils.isEmpty(etPassword.text)) {
            showToastMsg(getString(R.string.edit_password_hint))
            return
        }
        if (TextUtils.isEmpty(etName.text)) {
            showToastMsg(getString(R.string.edit_school_name_hint))
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
        if (TextUtils.isEmpty(etAddress.text)) {
            showToastMsg(getString(R.string.edit_school_address_hint))
            return
        }
        val map = HashMap<String, String>()
        map["username"] = etAccount.text.toString().trim()
        map["password"] = etPassword.text.toString().trim()
        map["schoolname"] = etName.text.toString().trim()
        map["tel"] = etPhone.text.toString().trim()
        map["email"] = etEmail.text.toString().trim()
        map["schooltid"] = type
        map["address"] = etAddress.text.toString().trim()
        mViewModel?.freshSchoolRegister(map, true)
    }

    private fun payMoney(schoolId: String, pay_price: Double) {
        val intent = Intent(applicationContext, PayTabActivity::class.java)
        intent.putExtra(PaymentParams.MERCHANT_EMAIL, "www555net@gmail.com")
        intent.putExtra(
                PaymentParams.SECRET_KEY,
                "dtAnYDflRPTEgvPhG3BQ43PplSpROIZ2bAqadGw4vqJLWy2CCEtGHj52buU0dU2ovtzJ95OEvxqr6t4I7xoPQtR1YM1bH4Acx9VH"
        )//Add your Secret Key Here
        intent.putExtra(PaymentParams.LANGUAGE, PaymentParams.ENGLISH)
        intent.putExtra(PaymentParams.TRANSACTION_TITLE, getString(R.string.app_name))
        intent.putExtra(PaymentParams.AMOUNT, pay_price)
        intent.putExtra(PaymentParams.ORDER_ID, schoolId)
        intent.putExtra(PaymentParams.CURRENCY_CODE, "BHD")

        intent.putExtra(PaymentParams.CUSTOMER_PHONE_NUMBER, etPhone.text.toString().trim())
        intent.putExtra(PaymentParams.CUSTOMER_EMAIL, etEmail.text.toString().trim())
        intent.putExtra(PaymentParams.PRODUCT_NAME, "")

        //Billing Address
        intent.putExtra(PaymentParams.ADDRESS_BILLING, "")
        intent.putExtra(PaymentParams.CITY_BILLING, "")
        intent.putExtra(PaymentParams.STATE_BILLING, "")
        intent.putExtra(PaymentParams.COUNTRY_BILLING, "")
        intent.putExtra(
                PaymentParams.POSTAL_CODE_BILLING,
                ""
        ) //Put Country Phone code if Postal code not available '00973'

        //Shipping Address
        intent.putExtra(PaymentParams.ADDRESS_SHIPPING, "")
        intent.putExtra(PaymentParams.CITY_SHIPPING, "")
        intent.putExtra(PaymentParams.STATE_SHIPPING, "")
        intent.putExtra(PaymentParams.COUNTRY_SHIPPING, "")
        intent.putExtra(
                PaymentParams.POSTAL_CODE_SHIPPING,
                ""
        ) //Put Country Phone code if Postal code not available '00973'

        var language = AppUtils.getString(Contacts.LANGUAGE, "")
        if (language == "") language = "en"
        intent.putExtra(PaymentParams.LANGUAGE, language)
        startActivityForResult(intent, PaymentParams.PAYMENT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PaymentParams.PAYMENT_REQUEST_CODE) {
            Log.e("Tag", data!!.getStringExtra(PaymentParams.RESPONSE_CODE))
            Log.e("Tag", data.getStringExtra(PaymentParams.TRANSACTION_ID))
            if (data.getStringExtra(PaymentParams.RESPONSE_CODE) == "100") {
                schoolUpdate()
            } else {
                showToastMsg(getString(R.string.pay_failed))
            }
        }
    }

    private fun schoolUpdate() {
        val map = HashMap<String, String>()
        map["id"] = mSchoolId
        map["status"] = "1"
        mViewModel?.freshSchoolUpdate(map, true)
    }
}
