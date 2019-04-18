package com.monjaz.baiedu.data.remote.retrofit

import androidx.lifecycle.LiveData
import com.monjaz.baiedu.data.bean.remote.ViewDataBean

import io.reactivex.Observable

/**
 * Created by guluwa on 2018/1/4.
 */

object LiveDataObservableAdapter {

    fun <T> fromObservableViewData(observable: Observable<T>): LiveData<ViewDataBean<T>> {
        return ObservableViewLiveData(observable)
    }
}