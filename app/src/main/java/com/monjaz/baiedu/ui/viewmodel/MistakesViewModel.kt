package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class MistakesViewModel : BaseViewModel() {

    private var getMistakesListFresh = MutableLiveData<FreshBean>()

    private var getMistakesListResult: LiveData<ViewDataBean<ResultBean<List<MistakeListBean>>>>? = null

    fun getMistakesList(): LiveData<ViewDataBean<ResultBean<List<MistakeListBean>>>>? {
        if (getMistakesListResult == null) {
            getMistakesListResult = Transformations.switchMap(getMistakesListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().getMistakesList(it.map)
                } else {
                    null
                }
            }
        }
        return getMistakesListResult!!
    }

    fun freshGetMistakesList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) getMistakesListFresh.value = FreshBean(map, isFresh)
    }

    private var addMistakeListFresh = MutableLiveData<FreshBean>()

    private var addMistakeListResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun addMistakeList(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (addMistakeListResult == null) {
            addMistakeListResult = Transformations.switchMap(addMistakeListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().addMistakeList(it.map)
                } else {
                    null
                }
            }
        }
        return addMistakeListResult!!
    }

    fun freshAddMistakeList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) addMistakeListFresh.value = FreshBean(map, isFresh)
    }

    private var updateMistakeListFresh = MutableLiveData<FreshBean>()

    private var updateMistakeListResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun updateMistakeList(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (updateMistakeListResult == null) {
            updateMistakeListResult = Transformations.switchMap(updateMistakeListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().updateMistakeList(it.map)
                } else {
                    null
                }
            }
        }
        return updateMistakeListResult!!
    }

    fun freshUpdateMistakeList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) updateMistakeListFresh.value = FreshBean(map, isFresh)
    }

    private var deleteMistakeListFresh = MutableLiveData<FreshBean>()

    private var deleteMistakeListResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun deleteMistakeList(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (deleteMistakeListResult == null) {
            deleteMistakeListResult = Transformations.switchMap(deleteMistakeListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().deleteMistakeList(it.map)
                } else {
                    null
                }
            }
        }
        return deleteMistakeListResult!!
    }

    fun freshDeleteMistakeList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) deleteMistakeListFresh.value = FreshBean(map, isFresh)
    }

    private var getMistakesFresh = MutableLiveData<FreshBean>()

    private var getMistakesResult: LiveData<ViewDataBean<ResultBean<List<MistakeBean>>>>? = null

    fun getMistakes(): LiveData<ViewDataBean<ResultBean<List<MistakeBean>>>>? {
        if (getMistakesResult == null) {
            getMistakesResult = Transformations.switchMap(getMistakesFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().getMistakes(it.map)
                } else {
                    null
                }
            }
        }
        return getMistakesResult!!
    }

    fun freshGetMistakes(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) getMistakesFresh.value = FreshBean(map, isFresh)
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

    private var addMistakeFresh = MutableLiveData<FreshBean>()

    private var addMistakeResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun addMistake(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (addMistakeResult == null) {
            addMistakeResult = Transformations.switchMap(addMistakeFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().addMistake(it.map)
                } else {
                    null
                }
            }
        }
        return addMistakeResult!!
    }

    fun freshAddMistake(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) addMistakeFresh.value = FreshBean(map, isFresh)
    }

    private var updateMistakeFresh = MutableLiveData<FreshBean>()

    private var updateMistakeResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun updateMistake(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (updateMistakeResult == null) {
            updateMistakeResult = Transformations.switchMap(updateMistakeFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().updateMistake(it.map)
                } else {
                    null
                }
            }
        }
        return updateMistakeResult!!
    }

    fun freshUpdateMistake(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) updateMistakeFresh.value = FreshBean(map, isFresh)
    }

    private var deleteMistakeFresh = MutableLiveData<FreshBean>()

    private var deleteMistakeResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun deleteMistake(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (deleteMistakeResult == null) {
            deleteMistakeResult = Transformations.switchMap(deleteMistakeFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().deleteMistake(it.map)
                } else {
                    null
                }
            }
        }
        return deleteMistakeResult!!
    }

    fun freshDeleteMistake(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) deleteMistakeFresh.value = FreshBean(map, isFresh)
    }
}