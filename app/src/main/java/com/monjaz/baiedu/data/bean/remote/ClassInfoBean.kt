package com.monjaz.baiedu.data.bean.remote

class ClassInfoBean {

    var classid: Int = 0
    var classnumber: String = ""
    var school_id: Int = 0
    var schoolname: String = ""
    var classname: String = ""
    var teacherlist = arrayListOf<TeacherListBean>()
    var studentlist = arrayListOf<StudentListBean>()

    class TeacherListBean {
        var teacherid: Int = 0
        var teachid: Int = 0
        var teachername: String? = null
        var teachname: String? = null
        var mobile: String? = null
    }

    class StudentListBean {
        var student_id: Int = 0
        var studentname: String? = null
        var username: String? = null
        var sex: Int = 0
    }
}
