package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class ChildrenViewModel:BaseViewModel() {

    private var getChildrenFresh = MutableLiveData<FreshBean>()

    private var getChildrenResult: LiveData<ViewDataBean<ResultBean<List<ChildBean>>>>? = null

    fun getChildren(): LiveData<ViewDataBean<ResultBean<List<ChildBean>>>>? {
        if (getChildrenResult == null) {
            getChildrenResult = Transformations.switchMap(getChildrenFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().getChildren(it.map)
                } else {
                    null
                }
            }
        }
        return getChildrenResult!!
    }

    fun freshGetChildren(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) getChildrenFresh.value = FreshBean(map, isFresh)
    }

    private var searchChildFresh = MutableLiveData<FreshBean>()

    private var searchChildResult: LiveData<ViewDataBean<ResultBean<ChildBean>>>? = null

    fun searchChild(): LiveData<ViewDataBean<ResultBean<ChildBean>>>? {
        if (searchChildResult == null) {
            searchChildResult = Transformations.switchMap(searchChildFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().searchChild(it.map)
                } else {
                    null
                }
            }
        }
        return searchChildResult!!
    }

    fun freshSearchChild(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) searchChildFresh.value = FreshBean(map, isFresh)
    }

    private var bindChildFresh = MutableLiveData<FreshBean>()

    private var bindChildResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun bindChild(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (bindChildResult == null) {
            bindChildResult = Transformations.switchMap(bindChildFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().bindChild(it.map)
                } else {
                    null
                }
            }
        }
        return bindChildResult!!
    }

    fun freshBindChild(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) bindChildFresh.value = FreshBean(map, isFresh)
    }

    private var studentLearnStatusFresh = MutableLiveData<FreshBean>()

    private var studentLearnStatusResult: LiveData<ViewDataBean<ResultBean<StudentLearnStatusBean>>>? = null

    fun studentLearnStatus(): LiveData<ViewDataBean<ResultBean<StudentLearnStatusBean>>>? {
        if (studentLearnStatusResult == null) {
            studentLearnStatusResult = Transformations.switchMap(studentLearnStatusFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().studentLearnStatus(it.map)
                } else {
                    null
                }
            }
        }
        return studentLearnStatusResult!!
    }

    fun freshStudentLearnStatus(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) studentLearnStatusFresh.value = FreshBean(map, isFresh)
    }
}