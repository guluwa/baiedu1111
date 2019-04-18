package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class ClassViewModel : BaseViewModel() {

    private var queryClassFresh = MutableLiveData<FreshBean>()

    private var queryClassResult: LiveData<ViewDataBean<ResultBean<ClassInfoBean>>>? = null

    fun queryClass(): LiveData<ViewDataBean<ResultBean<ClassInfoBean>>>? {
        if (queryClassResult == null) {
            queryClassResult = Transformations.switchMap(queryClassFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().queryClass(it.map)
                } else {
                    null
                }
            }
        }
        return queryClassResult!!
    }

    fun freshQueryClass(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) queryClassFresh.value = FreshBean(map, isFresh)
    }

    private var joinClassFresh = MutableLiveData<FreshBean>()

    private var joinClassResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun joinClass(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (joinClassResult == null) {
            joinClassResult = Transformations.switchMap(joinClassFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().joinClass(it.map)
                } else {
                    null
                }
            }
        }
        return joinClassResult!!
    }

    fun freshJoinClass(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) joinClassFresh.value = FreshBean(map, isFresh)
    }

    private var getTeacherClassListFresh = MutableLiveData<FreshBean>()

    private var getTeacherClassListResult: LiveData<ViewDataBean<ResultBean<List<TeacherClassBean>>>>? = null

    fun getTeacherClassList(): LiveData<ViewDataBean<ResultBean<List<TeacherClassBean>>>>? {
        if (getTeacherClassListResult == null) {
            getTeacherClassListResult = Transformations.switchMap(getTeacherClassListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().getTeacherClassList(it.map)
                } else {
                    null
                }
            }
        }
        return getTeacherClassListResult!!
    }

    fun freshGetTeacherClassList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) getTeacherClassListFresh.value = FreshBean(map, isFresh)
    }

    private var getClassLessonsFresh = MutableLiveData<FreshBean>()

    private var getClassLessonsResult: LiveData<ViewDataBean<ResultBean<ClassLessonsBean>>>? = null

    fun getClassLessons(): LiveData<ViewDataBean<ResultBean<ClassLessonsBean>>>? {
        if (getClassLessonsResult == null) {
            getClassLessonsResult = Transformations.switchMap(getClassLessonsFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().getClassLessons(it.map)
                } else {
                    null
                }
            }
        }
        return getClassLessonsResult!!
    }

    fun freshGetClassLessons(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) getClassLessonsFresh.value = FreshBean(map, isFresh)
    }

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