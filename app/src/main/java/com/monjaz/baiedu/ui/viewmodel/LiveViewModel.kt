package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class LiveViewModel : BaseViewModel() {

    private var queryClassDetailFresh = MutableLiveData<FreshBean>()

    private var queryClassDetailResult: LiveData<ViewDataBean<ResultBean<ClassInfoBean>>>? = null

    fun queryClassDetail(): LiveData<ViewDataBean<ResultBean<ClassInfoBean>>>? {
        if (queryClassDetailResult == null) {
            queryClassDetailResult = Transformations.switchMap(queryClassDetailFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().queryClassDetail(it.map)
                } else {
                    null
                }
            }
        }
        return queryClassDetailResult!!
    }

    fun freshQueryClassDetail(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) queryClassDetailFresh.value = FreshBean(map, isFresh)
    }
}