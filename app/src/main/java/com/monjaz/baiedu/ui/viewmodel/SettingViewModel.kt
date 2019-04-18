package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class SettingViewModel:BaseViewModel() {

    private var getUserInfoFresh = MutableLiveData<FreshBean>()

    private var getUserInfoResult: LiveData<ViewDataBean<ResultBean<UserInfoBean>>>? = null

    fun getUserInfo(): LiveData<ViewDataBean<ResultBean<UserInfoBean>>>? {
        if (getUserInfoResult == null) {
            getUserInfoResult = Transformations.switchMap(getUserInfoFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().getUserInfo(it.map)
                } else {
                    null
                }
            }
        }
        return getUserInfoResult!!
    }

    fun freshGetUserInfo(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) getUserInfoFresh.value = FreshBean(map, isFresh)
    }

    private var uploadPicBase64Fresh = MutableLiveData<FreshBean>()

    private var uploadPicBase64Result: LiveData<ViewDataBean<ResultBean<UploadBean>>>? = null

    fun uploadPicBase64(): LiveData<ViewDataBean<ResultBean<UploadBean>>>? {
        if (uploadPicBase64Result == null) {
            uploadPicBase64Result = Transformations.switchMap(uploadPicBase64Fresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().uploadPicture(it.map)
                } else {
                    null
                }
            }
        }
        return uploadPicBase64Result!!
    }

    fun freshUploadPicBase64(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) uploadPicBase64Fresh.value = FreshBean(map, isFresh)
    }

    private var updateUserInfoFresh = MutableLiveData<FreshBean>()

    private var updateUserInfoResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun updateUserInfo(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (updateUserInfoResult == null) {
            updateUserInfoResult = Transformations.switchMap(updateUserInfoFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().updateUserInfo(it.map)
                } else {
                    null
                }
            }
        }
        return updateUserInfoResult!!
    }

    fun freshUpdateUserInfo(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) updateUserInfoFresh.value = FreshBean(map, isFresh)
    }

    private var updateMobileFresh = MutableLiveData<FreshBean>()

    private var updateMobileResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun updateMobile(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (updateMobileResult == null) {
            updateMobileResult = Transformations.switchMap(updateMobileFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().updateMobile(it.map)
                } else {
                    null
                }
            }
        }
        return updateMobileResult!!
    }

    fun freshUpdateMobile(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) updateMobileFresh.value = FreshBean(map, isFresh)
    }

    private var versionCheckFresh = MutableLiveData<FreshBean>()

    private var versionCheckResult: LiveData<ViewDataBean<ResultBean<VersionCheckBean>>>? = null

    fun versionCheck(): LiveData<ViewDataBean<ResultBean<VersionCheckBean>>>? {
        if (versionCheckResult == null) {
            versionCheckResult = Transformations.switchMap(versionCheckFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().versionCheck(it.map)
                } else {
                    null
                }
            }
        }
        return versionCheckResult!!
    }

    fun freshVersionCheck(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) versionCheckFresh.value = FreshBean(map, isFresh)
    }
}