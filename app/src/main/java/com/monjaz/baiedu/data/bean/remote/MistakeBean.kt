package com.monjaz.baiedu.data.bean.remote

import java.io.Serializable

class MistakeBean : Serializable {
    /**
     * id : 7
     * book_id : 8
     * student_id : 9
     * content : 错题二
     * content_imgs : http://baiedu.ali.zj0579.com/upload/20190319/8aa2f9b3bc0e67f765af801ec585caa6.jpg
     * answer : 答案二
     * answer_img : http://baiedu.ali.zj0579.com/upload/20190319/5d999417fbceb609248c85c6f9716c9a.jpg
     * remark : 备注二
     * create_time : 2019-03-19 20:46:39
     */

    var id: Int = 0
    var book_id: Int = 0
    var student_id: Int = 0
    var content: String? = null
    var content_imgs: String? = null
    var answer: String? = null
    var answer_img: String? = null
    var remark: String? = null
    var create_time: String? = null
    var contentImgs = arrayListOf<String>()
    var answerImgs = arrayListOf<String>()
}