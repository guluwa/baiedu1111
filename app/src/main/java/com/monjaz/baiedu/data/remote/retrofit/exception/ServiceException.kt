package com.monjaz.baiedu.data.remote.retrofit.exception

import com.monjaz.baiedu.data.remote.retrofit.exception.BaseException

/**
 * Created by 俊康 on 2017/9/27.
 */

class ServiceException(msg: String) : BaseException() {

    init {
        this.msg = msg
    }

    companion object {

        private val serialVersionUID = 2211853108336484888L
    }
}
