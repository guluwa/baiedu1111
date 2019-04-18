package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class HomeWorkViewModel : BaseViewModel() {

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

    private var publishHomeWorkFresh = MutableLiveData<FreshBean>()

    private var publishHomeWorkResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun publishHomeWork(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (publishHomeWorkResult == null) {
            publishHomeWorkResult = Transformations.switchMap(publishHomeWorkFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().publishHomeWork(it.map)
                } else {
                    null
                }
            }
        }
        return publishHomeWorkResult!!
    }

    fun freshPublishHomeWork(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) publishHomeWorkFresh.value = FreshBean(map, isFresh)
    }

    private var getHomeWorkListFresh = MutableLiveData<FreshBean>()

    private var getHomeWorkListResult: LiveData<ViewDataBean<ResultBean<HomeWorkListBean>>>? = null

    fun getHomeWorkList(): LiveData<ViewDataBean<ResultBean<HomeWorkListBean>>>? {
        if (getHomeWorkListResult == null) {
            getHomeWorkListResult = Transformations.switchMap(getHomeWorkListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().getHomeWorkList(it.map)
                } else {
                    null
                }
            }
        }
        return getHomeWorkListResult!!
    }

    fun freshGetHomeWorkList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) getHomeWorkListFresh.value = FreshBean(map, isFresh)
    }

    private var getHomeWorkDetailFresh = MutableLiveData<FreshBean>()

    private var getHomeWorkDetailResult: LiveData<ViewDataBean<ResultBean<HomeWorkDetailBean>>>? = null

    fun getHomeWorkDetail(): LiveData<ViewDataBean<ResultBean<HomeWorkDetailBean>>>? {
        if (getHomeWorkDetailResult == null) {
            getHomeWorkDetailResult = Transformations.switchMap(getHomeWorkDetailFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().getHomeWorkDetail(it.map)
                } else {
                    null
                }
            }
        }
        return getHomeWorkDetailResult!!
    }

    fun freshGetHomeWorkDetail(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) getHomeWorkDetailFresh.value = FreshBean(map, isFresh)
    }

    private var uploadHomeWorkResultFresh = MutableLiveData<FreshBean>()

    private var uploadHomeWorkResultResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun uploadHomeWorkResult(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (uploadHomeWorkResultResult == null) {
            uploadHomeWorkResultResult = Transformations.switchMap(uploadHomeWorkResultFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().uploadHomeWorkResult(it.map)
                } else {
                    null
                }
            }
        }
        return uploadHomeWorkResultResult!!
    }

    fun freshUploadHomeWorkResult(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) uploadHomeWorkResultFresh.value = FreshBean(map, isFresh)
    }

    private var correctHomeWorkFresh = MutableLiveData<FreshBean>()

    private var correctHomeWorkResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun correctHomeWork(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (correctHomeWorkResult == null) {
            correctHomeWorkResult = Transformations.switchMap(correctHomeWorkFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().correctHomeWork(it.map)
                } else {
                    null
                }
            }
        }
        return correctHomeWorkResult!!
    }

    fun freshCorrectHomeWork(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) correctHomeWorkFresh.value = FreshBean(map, isFresh)
    }

    private var uploadMediaFresh = MutableLiveData<FreshBean>()

    private var uploadMediaResult: LiveData<ViewDataBean<ResultBean<UploadBean>>>? = null

    fun uploadMedia(): LiveData<ViewDataBean<ResultBean<UploadBean>>>? {
        if (uploadMediaResult == null) {
            uploadMediaResult = Transformations.switchMap(uploadMediaFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().uploadMedia(it.map)
                } else {
                    null
                }
            }
        }
        return uploadMediaResult!!
    }

    fun freshUploadMedia(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) uploadMediaFresh.value = FreshBean(map, isFresh)
    }
}