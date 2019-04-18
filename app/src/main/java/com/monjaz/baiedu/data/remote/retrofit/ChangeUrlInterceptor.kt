package com.monjaz.baiedu.data.remote.retrofit


import android.util.Log
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.utils.AppUtils
import java.io.IOException
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Created by guluwa on 2018/6/5.
 */
class ChangeUrlInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //获取request
        val request = chain.request()
        //从request中获取原有的HttpUrl实例oldHttpUrl
        val oldHttpUrl = request.url()
        //获取request的创建者builder
        val builder1 = request.newBuilder()
        //从request中获取headers，通过给定的键url_name
        val headerValues = request.headers("url_type")
        if (headerValues != null && headerValues.size > 0) {
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            builder1.removeHeader("url_type")

            //重建新的HttpUrl，修改需要修改的url部分
            val newFullUrl = oldHttpUrl
                    .newBuilder()
//                    .removePathSegment(0)//移除第一个参数
                    .build()
            //重建这个request，通过builder.url(newFullUrl).build()；
            // 然后返回一个response至此结束修改
            Log.e("Url", "intercept: $newFullUrl")

            var cookie = AppUtils.getString(Contacts.ID, "")
            cookie += " "
            cookie += AppUtils.getString(Contacts.TOKEN, "")
            println(cookie)
            return chain.proceed(builder1.url(newFullUrl)
                    .addHeader("Authorization", cookie).build())
        }
        return chain.proceed(request)
    }
}
