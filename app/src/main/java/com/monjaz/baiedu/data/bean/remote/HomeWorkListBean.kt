package com.monjaz.baiedu.data.bean.remote

class HomeWorkListBean {

    var limit: String = ""
    var totalcount: String = ""
    var data = arrayListOf<DataBean>()

    class DataBean {

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
    }
}
