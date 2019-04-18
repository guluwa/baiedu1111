package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.FreshBean
import com.monjaz.baiedu.data.bean.remote.ResultBean
import com.monjaz.baiedu.data.bean.remote.UserInfoBean
import com.monjaz.baiedu.data.bean.remote.ViewDataBean
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class MainViewModel : BaseViewModel() {

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

    private var studentHomeBannerFresh = MutableLiveData<FreshBean>()

    private var studentHomeBannerResult: LiveData<ViewDataBean<ResultBean<List<String>>>>? = null

    fun studentHomeBanner(): LiveData<ViewDataBean<ResultBean<List<String>>>>? {
        if (studentHomeBannerResult == null) {
            studentHomeBannerResult = Transformations.switchMap(studentHomeBannerFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().studentHomeBanner(it.map)
                } else {
                    null
                }
            }
        }
        return studentHomeBannerResult!!
    }

    fun freshStudentHomeBanner(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) studentHomeBannerFresh.value = FreshBean(map, isFresh)
    }
}