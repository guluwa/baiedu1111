package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.monjaz.baiedu.data.bean.remote.FreshBean
import com.monjaz.baiedu.data.bean.remote.MessageListBean
import com.monjaz.baiedu.data.bean.remote.ResultBean
import com.monjaz.baiedu.data.bean.remote.ViewDataBean
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource

class MessageViewModel : BaseViewModel() {

    private var readMessageFresh = MutableLiveData<FreshBean>()

    private var readMessageResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun readMessage(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (readMessageResult == null) {
            readMessageResult = Transformations.switchMap(readMessageFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().readMessage(it.map)
                } else {
                    null
                }
            }
        }
        return readMessageResult!!
    }

    fun freshReadMessage(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) readMessageFresh.value = FreshBean(map, isFresh)
    }

    private var messageListFresh = MutableLiveData<FreshBean>()

    private var messageListResult: LiveData<ViewDataBean<ResultBean<MessageListBean>>>? = null

    fun messageList(): LiveData<ViewDataBean<ResultBean<MessageListBean>>>? {
        if (messageListResult == null) {
            messageListResult = Transformations.switchMap(messageListFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().messageList(it.map)
                } else {
                    null
                }
            }
        }
        return messageListResult!!
    }

    fun freshMessageList(map: HashMap<String, String>, isFresh: Boolean) {
        if (!judgeUser(map)) messageListFresh.value = FreshBean(map, isFresh)
    }
}