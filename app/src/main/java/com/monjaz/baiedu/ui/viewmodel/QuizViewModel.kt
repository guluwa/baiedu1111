package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class QuizViewModel : BaseViewModel() {

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

    private var sendQuizFresh = MutableLiveData<FreshBean>()

    private var sendQuizResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun sendQuiz(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (sendQuizResult == null) {
            sendQuizResult = Transformations.switchMap(sendQuizFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().sendQuiz(it.map)
                } else {
                    null
                }
            }
        }
        return sendQuizResult!!
    }

    fun freshSendQuiz(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) sendQuizFresh.value = FreshBean(map, isFresh)
    }

    private var sentQuizListFresh = MutableLiveData<FreshBean>()

    private var sentQuizListResult: LiveData<ViewDataBean<ResultBean<List<QuizListBean>>>>? = null

    fun sentQuizList(): LiveData<ViewDataBean<ResultBean<List<QuizListBean>>>>? {
        if (sentQuizListResult == null) {
            sentQuizListResult = Transformations.switchMap(sentQuizListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().sentQuizList(it.map)
                } else {
                    null
                }
            }
        }
        return sentQuizListResult!!
    }

    fun freshSentQuizList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) sentQuizListFresh.value = FreshBean(map, isFresh)
    }

    private var receivedQuizListFresh = MutableLiveData<FreshBean>()

    private var receivedQuizListResult: LiveData<ViewDataBean<ResultBean<List<QuizListBean>>>>? = null

    fun receivedQuizList(): LiveData<ViewDataBean<ResultBean<List<QuizListBean>>>>? {
        if (receivedQuizListResult == null) {
            receivedQuizListResult = Transformations.switchMap(receivedQuizListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().receivedQuizList(it.map)
                } else {
                    null
                }
            }
        }
        return receivedQuizListResult!!
    }

    fun freshReceivedQuizList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) receivedQuizListFresh.value = FreshBean(map, isFresh)
    }
}