package com.monjaz.baiedu.data.bean.remote

class VersionCheckBean {

    var android: AndroidBean = AndroidBean()

    class AndroidBean {
        /**
         * versioncode : 1
         * versionname : 1.0
         * app : http://baiedu.ali.zj0579.com/upload/apk/aaa.apk
         */

        var versioncode: Int = 0
        var versionname: String? = null
        var app: String? = null
    }
}
