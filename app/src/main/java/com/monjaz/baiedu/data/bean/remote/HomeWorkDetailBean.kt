package com.monjaz.baiedu.data.bean.remote

import java.io.Serializable

class HomeWorkDetailBean {

    var id: Int = 0
    var schoolid: Int = 0
    var schoolname: String = ""
    var classid: Int = 0
    var teacherid: Int = 0
    var classname: String = ""
    var teachername: String = ""
    var content: String = ""
    var addtime: String = ""
    var images_list = arrayListOf<String>()
    var tasksubmit = arrayListOf<HomeWorkResultBean>()

    class HomeWorkResultBean : Serializable {
        var id: Int = 0
        var studentid: Int = 0
        var studentname: String = ""
        var sex: Int = 0
        var faceimg: String = ""
        var content: String = ""
        var images = arrayListOf<String>()
        var addtime: String = ""
        var ispigai: String = ""
        var pigaicontent: String? = null
        var pigai_time: String = ""
        var pitai_time: String = ""
        var pigai_media: String? = null
    }
}