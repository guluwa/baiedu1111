package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class LoginViewModel : BaseViewModel() {

    private var userLoginFresh = MutableLiveData<FreshBean>()

    private var userLoginResult: LiveData<ViewDataBean<ResultBean<LoginBean>>>? = null

    fun passwordLogin(): LiveData<ViewDataBean<ResultBean<LoginBean>>>? {
        if (userLoginResult == null) {
            userLoginResult = Transformations.switchMap(userLoginFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().userLogin(it.map)
                } else {
                    null
                }
            }
        }
        return userLoginResult!!
    }

    fun freshPasswordLogin(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) userLoginFresh.value = FreshBean(map, isFresh)
    }

    private var userRegisterFresh = MutableLiveData<FreshBean>()

    private var userRegisterResult: LiveData<ViewDataBean<ResultBean<LoginBean>>>? = null

    fun userRegister(): LiveData<ViewDataBean<ResultBean<LoginBean>>>? {
        if (userRegisterResult == null) {
            userRegisterResult = Transformations.switchMap(userRegisterFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().userRegister(it.map)
                } else {
                    null
                }
            }
        }
        return userRegisterResult!!
    }

    fun freshUserRegister(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) userRegisterFresh.value = FreshBean(map, isFresh)
    }

    private var schoolRegisterFresh = MutableLiveData<FreshBean>()

    private var schoolRegisterResult: LiveData<ViewDataBean<ResultBean<SchoolRegisterBean>>>? = null

    fun schoolRegister(): LiveData<ViewDataBean<ResultBean<SchoolRegisterBean>>>? {
        if (schoolRegisterResult == null) {
            schoolRegisterResult = Transformations.switchMap(schoolRegisterFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().schoolRegister(it.map)
                } else {
                    null
                }
            }
        }
        return schoolRegisterResult!!
    }

    fun freshSchoolRegister(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) schoolRegisterFresh.value = FreshBean(map, isFresh)
    }

    private var schoolUpdateFresh = MutableLiveData<FreshBean>()

    private var schoolUpdateResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun schoolUpdate(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (schoolUpdateResult == null) {
            schoolUpdateResult = Transformations.switchMap(schoolUpdateFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().schoolUpdate(it.map)
                } else {
                    null
                }
            }
        }
        return schoolUpdateResult!!
    }

    fun freshSchoolUpdate(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) schoolUpdateFresh.value = FreshBean(map, isFresh)
    }
}