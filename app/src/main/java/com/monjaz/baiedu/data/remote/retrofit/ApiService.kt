package com.monjaz.baiedu.data.remote.retrofit

import com.monjaz.baiedu.data.bean.remote.*
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by guluwa on 2018/1/11.
 */

interface ApiService {

    /**
     * 用户登录
     */
    @GET("reg/login")
    fun userLogin(@QueryMap map: Map<String, String>): Observable<ResultBean<LoginBean>>


    /**
     * 学生家长注册
     */
    @POST("reg/index")
    @FormUrlEncoded
    fun userRegister(@FieldMap map: Map<String, String>): Observable<ResultBean<LoginBean>>

    /**
     * 学校注册
     */
    @POST("reg/school")
    @FormUrlEncoded
    fun schoolRegister(@FieldMap map: Map<String, String>): Observable<ResultBean<SchoolRegisterBean>>

    /**
     * 查找班级 http://baiedu.ali.zj0579.com/api/student/searchclass?classnumber=10
     */
    @GET("student/searchclass")
    @Headers("url_type:cookie")
    fun queryClass(@QueryMap map: Map<String, String>): Observable<ResultBean<ClassInfoBean>>

    /**
     * 加入班级
     */
    @GET("student/joinclass")
    @Headers("url_type:cookie")
    fun joinClass(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 图片上传
     */
    @POST("upload/picture")
    @FormUrlEncoded
    fun uploadPicture(@FieldMap map: Map<String, String>): Observable<ResultBean<UploadBean>>

    /**
     * 查询登录用户信息
     */
    @GET("pub/info")
    @Headers("url_type:cookie")
    fun getUserInfo(@QueryMap map: Map<String, String>): Observable<ResultBean<UserInfoBean>>

    /**
     * 老师所在的班级列表
     */
    @GET("teacher/myclass")
    @Headers("url_type:cookie")
    fun getTeacherClassList(@QueryMap map: Map<String, String>): Observable<ResultBean<List<TeacherClassBean>>>

    /**
     * 查询班级课表
     */
    @GET("pub/curriculumtable")
    @Headers("url_type:cookie")
    fun getClassLessons(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 发布作业
     */
    @POST("teacher/addtask")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun publishHomeWork(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 班级详情
     */
    @GET("pub/schoolclass")
    @Headers("url_type:cookie")
    fun queryClassDetail(@QueryMap map: Map<String, String>): Observable<ResultBean<ClassInfoBean>>

    /**
     * 获取作业列表
     */
    @GET("pub/tasklist")
    @Headers("url_type:cookie")
    fun getHomeWorkList(@QueryMap map: Map<String, String>): Observable<ResultBean<HomeWorkListBean>>

    /**
     * 获取作业详情
     */
    @GET("pub/taskview")
    @Headers("url_type:cookie")
    fun getHomeWorkDetail(@QueryMap map: Map<String, String>): Observable<ResultBean<HomeWorkDetailBean>>

    /**
     * 提交作业
     */
    @POST("student/tasksubmit")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun uploadHomeWorkResult(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 修改用户信息
     */
    @POST("pub/edituserinfo")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun updateUserInfo(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 批改作业
     */
    @POST("teacher/taskpigai")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun correctHomeWork(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 消息检测
     */
    @POST("pub/push")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun messageCheck(@FieldMap map: Map<String, String>): Observable<ResultBean<List<MessageListBean.DataBean>>>

    /**
     * 消息已读
     */
    @GET("pub/upread")
    @Headers("url_type:cookie")
    fun readMessage(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 消息列表
     */
    @GET("pub/pushlist")
    @Headers("url_type:cookie")
    fun messageList(@QueryMap map: Map<String, String>): Observable<ResultBean<MessageListBean>>

    /**
     * 错题本列表
     */
    @GET("student/cuotibook")
    @Headers("url_type:cookie")
    fun getMistakesList(@QueryMap map: Map<String, String>): Observable<ResultBean<List<MistakeListBean>>>

    /**
     * 增加错题本
     */
    @POST("student/cuotibook_add")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun addMistakeList(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 修改错题本
     */
    @POST("student/cuotibook_add")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun updateMistakeList(@Query("id") id: String, @FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 删除错题本
     */
    @GET("student/cuotibook_del")
    @Headers("url_type:cookie")
    fun deleteMistakeList(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 错题列表
     */
    @GET("student/cuotibooklist")
    @Headers("url_type:cookie")
    fun getMistakes(@QueryMap map: Map<String, String>): Observable<ResultBean<List<MistakeBean>>>

    /**
     * 增加错题
     */
    @POST("student/cuotibooklist_add")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun addMistake(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 修改错题
     */
    @POST("student/cuotibooklist_add")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun updateMistake(@Query("id") id: String, @FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 删除错题
     */
    @GET("student/cuotibooklist_del")
    @Headers("url_type:cookie")
    fun deleteMistake(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 孩子列表
     */
    @GET("parent/studentlist")
    @Headers("url_type:cookie")
    fun getChildren(@QueryMap map: Map<String, String>): Observable<ResultBean<List<ChildBean>>>

    /**
     * 孩子搜索
     */
    @GET("parent/searchstudent")
    @Headers("url_type:cookie")
    fun searchChild(@QueryMap map: Map<String, String>): Observable<ResultBean<ChildBean>>

    /**
     * 添加孩子
     */
    @GET("parent/joinstudent")
    @Headers("url_type:cookie")
    fun bindChild(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 修改手机号
     */
    @POST("teacher/editinfo")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun updateMobile(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 上传音频
     */
    @POST("upload/upmedia")
    @Headers("url_type:cookie")
    fun uploadMedia(@Body param: RequestBody): Observable<ResultBean<UploadBean>>

    /**
     * 更新检测
     */
    @POST("upload/checknew")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun versionCheck(@FieldMap map: Map<String, String>): Observable<ResultBean<VersionCheckBean>>

    /**
     * 留言
     */
    @POST("pub/addmessage")
    @FormUrlEncoded
    @Headers("url_type:cookie")
    fun sendQuiz(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 发出的留言
     */
    @GET("pub/message_sendlist")
    @Headers("url_type:cookie")
    fun sentQuizList(@QueryMap map: Map<String, String>): Observable<ResultBean<List<QuizListBean>>>

    /**
     * 收到的留言
     */
    @GET("pub/message_get")
    @Headers("url_type:cookie")
    fun receivedQuizList(@QueryMap map: Map<String, String>): Observable<ResultBean<List<QuizListBean>>>

    /**
     * 学校付款回调
     */
    @GET("reg/school_upstatus")
    fun schoolUpdate(@QueryMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * 学生首页轮播图
     */
    @GET("pub/indexflash")
    @Headers("url_type:cookie")
    fun studentHomeBanner(@QueryMap map: Map<String, String>): Observable<ResultBean<List<String>>>

    /**
     * 学生作业完成情况
     */
    @GET("pub/studentcount")
    @Headers("url_type:cookie")
    fun studentLearnStatus(@QueryMap map: Map<String, String>): Observable<ResultBean<StudentLearnStatusBean>>
}