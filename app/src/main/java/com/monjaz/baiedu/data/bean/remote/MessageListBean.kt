package com.monjaz.baiedu.data.bean.remote

import java.io.Serializable

class MessageListBean {

    var limit: String = ""
    var totalcount: String = ""
    var data = arrayListOf<DataBean>()

    class DataBean : Serializable {
        /**
         * id : 10
         * pushid : 5
         * gettime : 2019-03-07 11:28:33
         * isread : 0
         * readtime : 1970-01-01 08:00:00
         * content : sadfsdf的沙发上单防守打法士大夫啥的发生的发生的发的货阿稍等哈士大夫啥地方啥地方啥地方啥地方啥都发松岛枫上的发松岛枫
         * pushtime : 2019-03-07 10:21:53
         */

        var id: Int = 0
        var pushid: Int = 0
        var gettime: String = ""
        var isread: Int = 0
        var readtime: String = ""
        var content: String = ""
        var pushtime: String = ""

        var addtime: String = ""
    }
}
