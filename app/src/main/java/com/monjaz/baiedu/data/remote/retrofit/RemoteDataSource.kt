package com.monjaz.baiedu.data.remote.retrofit

import androidx.lifecycle.LiveData
import com.monjaz.baiedu.data.bean.remote.*
import com.monjaz.baiedu.utils.AppUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by guluwa on 2018/1/12.
 */

class RemoteDataSource {

    object SingletonHolder {
        //单例（静态内部类）
        val instance = RemoteDataSource()
    }

    companion object {

        fun getInstance() = SingletonHolder.instance
    }

    /**
     * 用户登录
     */
    fun userLogin(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<LoginBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.userLogin(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 学生家长注册
     */
    fun userRegister(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<LoginBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.userRegister(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 学校注册
     */
    fun schoolRegister(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<SchoolRegisterBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.schoolRegister(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 查找班级
     */
    fun queryClass(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<ClassInfoBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.queryClass(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 查找班级
     */
    fun joinClass(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.joinClass(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 图片上传
     */
    fun uploadPicture(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<UploadBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.uploadPicture(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 查询登录用户信息
     */
    fun getUserInfo(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<UserInfoBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.getUserInfo(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 老师所在的班级列表
     */
    fun getTeacherClassList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<TeacherClassBean>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.getTeacherClassList(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map {
                            for (item in it.data!!) {
                                item.color = AppUtils.getRandomColor()
                            }
                            return@map it
                        }
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 查询班级课表
     */
    fun getClassLessons(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<ClassLessonsBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.getClassLessons(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map {
                            val result = ClassLessonsBean()
                            result.lessons.addAll(AppUtils.parseClassLessons(it.data))
                            result.maxLessonsNum = AppUtils.getMaxLessonsNum(result) ?: 10
                            it.data = result
                            return@map it as ResultBean<ClassLessonsBean>
                        }
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 发布作业
     */
    fun publishHomeWork(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.publishHomeWork(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 班级详情
     */
    fun queryClassDetail(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<ClassInfoBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.queryClassDetail(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 获取作业列表
     */
    fun getHomeWorkList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<HomeWorkListBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.getHomeWorkList(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 获取作业详情
     */
    fun getHomeWorkDetail(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<HomeWorkDetailBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.getHomeWorkDetail(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 提交作业
     */
    fun uploadHomeWorkResult(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.uploadHomeWorkResult(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 修改用户信息
     */
    fun updateUserInfo(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.updateUserInfo(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 批改作业
     */
    fun correctHomeWork(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.correctHomeWork(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 消息已读
     */
    fun readMessage(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.readMessage(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 消息列表
     */
    fun messageList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<MessageListBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.messageList(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 错题本列表
     */
    fun getMistakesList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<MistakeListBean>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.getMistakesList(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 增加错题本
     */
    fun addMistakeList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.addMistakeList(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 增加错题本
     */
    fun updateMistakeList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.updateMistakeList(map.getValue("id"), mapOf(Pair("bookname", map.getValue("bookname"))))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 删除错题本
     */
    fun deleteMistakeList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.deleteMistakeList(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 错题列表
     */
    fun getMistakes(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<MistakeBean>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.getMistakes(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map {
                            if (it.data != null) {
                                for (item in it.data!!) {
                                    if (item.content_imgs != null && item.content_imgs != "") {
                                        item.contentImgs.addAll(item.content_imgs!!.split(","))
                                    }
                                    if (item.answer_img != null && item.answer_img != "") {
                                        item.answerImgs.addAll(item.answer_img!!.split(","))
                                    }
                                }
                            }
                            it
                        }
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 增加错题
     */
    fun addMistake(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.addMistake(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 修改错题
     */
    fun updateMistake(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        //修改。。。
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.updateMistake(map.getValue("id"), mapOf(Pair("bookname", map.getValue("bookname"))))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 删除错题
     */
    fun deleteMistake(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.deleteMistake(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 孩子列表
     */
    fun getChildren(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<ChildBean>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.getChildren(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 孩子搜索
     */
    fun searchChild(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<ChildBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.searchChild(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 添加孩子
     */
    fun bindChild(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.bindChild(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 修改手机号
     */
    fun updateMobile(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.updateMobile(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 上传音频
     */
    fun uploadMedia(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<UploadBean>>> {
        val path = map["path"]
        val file = File(path)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.uploadMedia(
                        MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("media", file.name, requestFile).build())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 更新检测
     */
    fun versionCheck(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<VersionCheckBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.versionCheck(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 留言
     */
    fun sendQuiz(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.sendQuiz(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 发出的留言
     */
    fun sentQuizList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<QuizListBean>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.sentQuizList(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 收到的留言
     */
    fun receivedQuizList(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<QuizListBean>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.receivedQuizList(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 学校付款回调
     */
    fun schoolUpdate(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.schoolUpdate(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 学生首页轮播图
     */
    fun studentHomeBanner(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<String>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.studentHomeBanner(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }

    /**
     * 学生作业完成情况
     */
    fun studentLearnStatus(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<StudentLearnStatusBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.studentLearnStatus(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        )
    }
}