package com.monjaz.baiedu.data.remote.gson

import android.util.Log

import com.google.gson.Gson
import com.monjaz.baiedu.data.remote.retrofit.exception.NoDataException
import com.monjaz.baiedu.data.remote.retrofit.exception.OtherException
import com.monjaz.baiedu.data.remote.retrofit.exception.ReLoginException
import com.monjaz.baiedu.data.remote.retrofit.exception.TokenException
import com.monjaz.baiedu.data.bean.remote.ResultBean
import com.monjaz.baiedu.manage.Contacts

import java.io.IOException
import java.lang.reflect.Type

import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * Created by 俊康 on 2017/8/8.
 */

class MyGsonResponseBodyConverter<T> internal constructor(private val gson: Gson, private val type: Type) : Converter<ResponseBody, T> {

    /**
     * 针对数据返回成功、错误不同类型字段处理
     */
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {

        val jsonStr = value.string()

        Log.d("guluwa",jsonStr)

        val resultBean = gson.fromJson(jsonStr, ResultBean::class.java)
        value.use {
            if ("" == jsonStr || resultBean == null) {
                throw NoDataException()
            } else if (resultBean.code == 0) {
                return gson.fromJson<T>(jsonStr, type)
            } else if (resultBean.code == 5) {
                throw ReLoginException()
            } else if (resultBean.code == 10) {
                Contacts.TOKEN_NEED_FRESH = true
                throw TokenException()
            } else {
                throw OtherException(resultBean.code, resultBean.message)
            }
        }
    }

    /**
     * 数据解析
     *
     * @param jsonStr JSON字符串
     * @return UniApiResult<GoodsInfoModel> 数据对象
    </GoodsInfoModel> */
    //    public ResultBean<HeadBean> parseJson(String jsonStr) {
    //        Gson gson = new Gson();
    //        Type jsonType = new TypeToken<ResultBean<HeadBean>>() {
    //        }.getType();
    //        return gson.fromJson(jsonStr, jsonType);
    //    }
}