package com.monjaz.baiedu.data.bean.remote

import com.google.gson.annotations.SerializedName

/**
 * Created by 俊康 on 2017/8/7.
 */

open class BaseBean(@SerializedName("msg") var message: String = "", var code: Int = -1)