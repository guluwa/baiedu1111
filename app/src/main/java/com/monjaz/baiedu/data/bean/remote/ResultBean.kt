package com.monjaz.baiedu.data.bean.remote

import com.google.gson.annotations.SerializedName

/**
 * Created by 俊康 on 2017/8/5.
 */

class ResultBean<T> : BaseBean() {

    @SerializedName("result")
    var data: T? = null
}
