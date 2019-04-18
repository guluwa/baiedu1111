package com.monjaz.baiedu.ui.main.setting

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing.model.entity.impl.ImageMedia
import com.bilibili.boxing.utils.ImageCompressor
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hwangjr.rxbus.RxBus
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.UserInfoBean
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.dialog.BotSelectSexDialog
import com.monjaz.baiedu.ui.dialog.InputMobileDialog
import com.monjaz.baiedu.ui.dialog.InputUserNameDialog
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.login.UserLoginActivity
import com.monjaz.baiedu.ui.viewmodel.SettingViewModel
import com.monjaz.baiedu.utils.AppUtils
import com.monjaz.baiedu.utils.DataCleanManager
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import java.util.HashMap

class SettingActivity : BaseActivity() {

    private val REQUEST_CODE = 101

    private var path = ""

    private var account = ""

    private var sex = "1"

    private var mobile = ""

    private var mChangeType = 0 //1:thumb 2:name 3:sex

    private var isChangeSuccess = false

    override val viewLayoutId: Int get() = R.layout.activity_setting

    private var mViewModel: SettingViewModel? = null

    override fun initViews() {
        initToolBar()
        initVersion()
        initCache()
        initClickEvent()
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.menu_setting)
        ivBack.setOnClickListener { onBackPressed() }
    }

    private fun initVersion() {
        tvVersion.text = AppUtils.getVersionName()
    }

    private fun initCache() {
        tvCache.text = DataCleanManager.getTotalCacheSize(this)
    }

    private fun initClickEvent() {
        mAvatarItem.setOnClickListener {
            val videoConfig = BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).needCamera(R.drawable.camera_white_icon)
            Boxing.of(videoConfig).withIntent(this, BoxingActivity::class.java).start(this, REQUEST_CODE)
        }

        mNameItem.setOnClickListener {
            InputUserNameDialog(this, R.style.DialogStyle, object : OnClickListener {
                override fun click(arg1: Int, arg2: Any) {
                    if (arg2 is String && arg2 == "") {
                        showToastMsg(getString(R.string.edit_account_hint))
                    } else {
                        account = arg2 as String
                        updateUserAccount()
                    }
                }
            }).show()
        }

        mSexItem.setOnClickListener {
            BotSelectSexDialog(this, R.style.DialogStyle, object : OnClickListener {
                override fun click(arg1: Int, arg2: Any) {
                    sex = arg1.toString()
                    updateUserSex()
                }
            }).show()
        }

        mPhoneItem.setOnClickListener {
            InputMobileDialog(this, R.style.DialogStyle, object : OnClickListener {
                override fun click(arg1: Int, arg2: Any) {
                    if (arg2 is String && arg2 == "") {
                        showToastMsg(getString(R.string.edit_account_hint))
                    } else {
                        mobile = arg2 as String
                        updateMobile()
                    }
                }
            }).show()
        }

        mCacheItem.setOnClickListener {
            DataCleanManager.clearAllCache(this)
            initCache()
            showToastMsg(getString(R.string.cache_clear_success))
        }

        mPassWordItem.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        tvLogout.setOnClickListener {
            logout()
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
        if (!mViewModel!!.getUserInfo()!!.hasObservers()) {
            mViewModel!!.getUserInfo()!!.observe(this, Observer {
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
                        mViewModel!!.freshGetUserInfo(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetUserInfo(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetUserInfo(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showUserInfo(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
            mViewModel!!.uploadPicBase64()!!.observe(this, Observer {
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
                        mViewModel!!.freshUploadPicBase64(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadPicBase64(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        mViewModel!!.freshUploadPicBase64(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            path = it.data.data!!.url
                            updateUserImage()
                        } else {
                            dismissProgressDialog()
                            showToastMsg(getString(R.string.avatar_upload_failed))
                        }
                    }
                }
            })
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
                            when (mChangeType) {
                                1 -> {
                                    Glide.with(this).asBitmap()
                                            .apply(RequestOptions().circleCrop().placeholder(R.drawable.ic_class_student))
                                            .load(path)
                                            .into(ivAvatar)
                                    isChangeSuccess = true
                                }
                                2 -> {
                                    tvName.text = account
                                    isChangeSuccess = true
                                }
                                else -> tvSex.text = if (sex == "1") getString(R.string.male) else getString(R.string.female)
                            }
                            showToastMsg(getString(R.string.change_success))
                        } else {
                            showToastMsg(getString(R.string.change_failed))
                        }
                    }
                }
            })
            mViewModel!!.updateMobile()!!.observe(this, Observer {
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
                        mViewModel!!.freshUpdateMobile(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUpdateMobile(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUpdateMobile(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {

                            showToastMsg(getString(R.string.change_success))
                        } else {
                            showToastMsg(getString(R.string.change_failed))
                        }
                    }
                }
            })
        }
        getUserInfo()
    }

    private fun getUserInfo() {
        mViewModel?.freshGetUserInfo(HashMap(), true)
    }

    private fun showUserInfo(data: UserInfoBean) {
        tvName.text = data.name
        tvSex.text = if (data.sex == "1") getString(R.string.male) else getString(R.string.female)
        Glide.with(this).asBitmap()
                .apply(RequestOptions().circleCrop().placeholder(R.drawable.ic_class_student))
                .load(data.faceimg)
                .into(ivAvatar)
    }

    private fun logout() {
        AppUtils.setString(Contacts.TOKEN, "")
        AppUtils.setString(Contacts.ID, "")
        AppUtils.setString(Contacts.TYPE, "")
        showToastMsg(getString(R.string.setting_menu_logout_success))
        startActivity(Intent(this, UserLoginActivity::class.java))
        finishActivityManager.finishAllActivity()
    }

    //头像选择回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            return
        }
        if (data == null) {
            return
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                val medias = Boxing.getResult(data)
                if (medias != null) {
                    (medias[0] as ImageMedia).compress(ImageCompressor(this))
                    if ((medias[0] as ImageMedia).compressPath != null) {
                        path = (medias[0] as ImageMedia).compressPath
                        uploadPicBase64()
                    } else {
                        showToastMsg(getString(R.string.avatar_upload_failed))
                    }
                }
            }
        }
    }

    private fun uploadPicBase64() {
        val map = HashMap<String, String>()
        map["img"] = "data:image/jpg;base64," + AppUtils.encodeImage(BitmapFactory.decodeFile(path))
        mViewModel?.freshUploadPicBase64(map, true)
    }

    private fun updateUserImage() {
        mChangeType = 1
        val map = HashMap<String, String>()
        map["faceimg"] = path
        mViewModel?.freshUpdateUserInfo(map, true)
    }

    private fun updateUserAccount() {
        mChangeType = 2
        val map = HashMap<String, String>()
        map["name"] = account
        mViewModel?.freshUpdateUserInfo(map, true)
    }

    private fun updateUserSex() {
        mChangeType = 3
        val map = HashMap<String, String>()
        map["sex"] = sex
        mViewModel?.freshUpdateUserInfo(map, true)
    }

    private fun updateMobile() {
        val map = HashMap<String, String>()
        map["mobile"] = mobile
        mViewModel?.freshUpdateMobile(map, true)
    }

    override fun onBackPressed() {
        if (isChangeSuccess) {
            RxBus.get().post("freshUserInfo", "")
        }
        super.onBackPressed()
    }
}
