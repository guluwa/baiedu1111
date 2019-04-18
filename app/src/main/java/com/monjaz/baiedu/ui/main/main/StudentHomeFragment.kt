package com.monjaz.baiedu.ui.main.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.LazyFragment
import com.monjaz.baiedu.data.bean.local.MainMenuBean
import com.monjaz.baiedu.data.bean.local.StudentMainMenuBean
import com.monjaz.baiedu.data.bean.remote.MessageListBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.UserInfoBean
import com.monjaz.baiedu.data.remote.retrofit.RetrofitWorker
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.StudentMainMenuListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.classs.ClassDetailActivity
import com.monjaz.baiedu.ui.main.classs.ClassLessonsActivity
import com.monjaz.baiedu.ui.main.classs.JoinNewClassActivity
import com.monjaz.baiedu.ui.main.classs.MyClassActivity
import com.monjaz.baiedu.ui.main.mistakes.MyMistakesActivity
import com.monjaz.baiedu.ui.main.quiz.SelectQuizTypeActivity
import com.monjaz.baiedu.ui.main.quiz.StudentQuizActivity
import com.monjaz.baiedu.ui.main.work.ClassHomeWorksActivity
import com.monjaz.baiedu.ui.receiver.MessageBroadcastReceiver
import com.monjaz.baiedu.ui.viewmodel.MainViewModel
import com.monjaz.baiedu.utils.AppUtils
import com.monjaz.baiedu.utils.GlideImageLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_login.*
import kotlinx.android.synthetic.main.fragment_student_home.*
import java.util.concurrent.TimeUnit

class StudentHomeFragment : LazyFragment() {

    private var mClassId = 0

    private var mClassName = ""

    private var mMenuList = mutableListOf<StudentMainMenuBean>()

    private var mViewModel: MainViewModel? = null

    private var mMessageId = -1

    private var isFirst = true

    override val viewLayoutId: Int get() = R.layout.fragment_student_home

    companion object {
        fun newInstance(): StudentHomeFragment {
            val fragment = StudentHomeFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    override fun initViews() {
        if (isFirst) {
            initData()
            initBanner()
            initRecyclerView()
            isFirst = false
        }
    }

    private fun initData() {
        mMenuList.add(
                StudentMainMenuBean(
                        1,
                        getString(R.string.student_menu_join_class),
                        R.drawable.ic_student_join_class
                )
        )
        mMenuList.add(StudentMainMenuBean(2, getString(R.string.student_menu_class), R.drawable.ic_student_class))
        mMenuList.add(
                StudentMainMenuBean(
                        3,
                        getString(R.string.student_menu_curriculum),
                        R.drawable.ic_student_lessons
                )
        )
        mMenuList.add(StudentMainMenuBean(4, getString(R.string.student_menu_homework), R.drawable.ic_student_homework))
        mMenuList.add(StudentMainMenuBean(5, getString(R.string.student_menu_mistakes), R.drawable.ic_student_mistake))
        mMenuList.add(StudentMainMenuBean(6, getString(R.string.student_menu_quiz), R.drawable.ic_student_quiz))
    }

    private fun initBanner() {
        mBanner.setDelayTime(3000)
        mBanner.setImageLoader(GlideImageLoader())
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = object : GridLayoutManager(context, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mRecyclerView.adapter = StudentMainMenuListAdapter(mMenuList, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as StudentMainMenuBean
                when (item.id) {
                    1 -> {
                        if (mClassId == 0) {
                            startActivity(Intent(context, JoinNewClassActivity::class.java))
                        } else {
                            showToastMsg(getString(R.string.have_join_class))
                        }
                    }
                    2 -> {
                        if (mClassId == 0) {
                            showToastMsg(getString(R.string.have_not_join_class))
                            startActivity(Intent(context, JoinNewClassActivity::class.java))
                        } else {
                            val intent = Intent(context, ClassDetailActivity::class.java)
                            intent.putExtra("classId", mClassId)
                            startActivity(intent)
                        }
                    }
                    3 -> {
                        if (mClassId == 0) {
                            showToastMsg(getString(R.string.have_not_join_class))
                            startActivity(Intent(context, JoinNewClassActivity::class.java))
                        } else {
                            val intent = Intent(context, ClassLessonsActivity::class.java)
                            intent.putExtra("classId", mClassId)
                            startActivity(intent)
                        }
                    }
                    4 -> {
                        if (mClassId == 0 || mClassName == "") {
                            showToastMsg(getString(R.string.have_not_join_class))
                            startActivity(Intent(context, JoinNewClassActivity::class.java))
                        } else {
                            val intent = Intent(context, ClassHomeWorksActivity::class.java)
                            intent.putExtra("classId", mClassId)
                            intent.putExtra("className", mClassName)
                            startActivity(intent)
                        }
                    }
                    5 -> {
                        startActivity(Intent(context, MyMistakesActivity::class.java))
                    }
                    6 -> {
                        if (mClassId == 0) {
                            showToastMsg(getString(R.string.have_not_join_class))
                            startActivity(Intent(context, JoinNewClassActivity::class.java))
                        } else {
                            val intent = Intent(context, SelectQuizTypeActivity::class.java)
                            intent.putExtra("classId", mClassId)
                            startActivity(intent)
                        }
                    }
                }
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        if (!mViewModel!!.getUserInfo()!!.hasObservers()) {
            mViewModel!!.getUserInfo()!!.observe(this, Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showToastMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog(getString(R.string.please_wait))
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetUserInfo(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetUserInfo(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetUserInfo(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showUserInfo(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
            mViewModel!!.studentHomeBanner()!!.observe(this, Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showToastMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog(getString(R.string.please_wait))
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshStudentHomeBanner(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshStudentHomeBanner(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshStudentHomeBanner(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showBanner(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
    }

    private fun showBanner(list: List<String>) {
        if (list.size >= 2) {
            mBanner.setImages(list.subList(0, list.size - 1))
            mBanner.start()
            val pic = list[list.size - 1]
            Glide.with(this).asBitmap()
                    .apply(RequestOptions().placeholder(R.drawable.ic_home_banner))
                    .load(pic)
                    .into(ivBanner)
        } else {
            mBanner.setImages(arrayListOf(R.drawable.ic_banner1, R.drawable.ic_banner2, R.drawable.ic_banner3))
            mBanner.start()
            Glide.with(this).asBitmap()
                    .apply(RequestOptions().placeholder(R.drawable.ic_home_banner))
                    .load(R.drawable.ic_home_banner)
                    .into(ivBanner)
        }
    }

    override fun lazyLoad() {
        getUserInfo()
        studentHomeBanner()
    }

    private fun getUserInfo() {
        mViewModel?.freshGetUserInfo(HashMap(), true)
    }

    private fun studentHomeBanner() {
        mViewModel?.freshStudentHomeBanner(HashMap(), true)
    }

    private fun showUserInfo(userInfoBean: UserInfoBean) {
        val type = AppUtils.getString(Contacts.TYPE, "2")
        if (type == "2" && userInfoBean.classid != null && userInfoBean.classname != null) {
            mClassId = userInfoBean.classid!!
            mClassName = userInfoBean.classname!!
            if (activity is MainNewActivity) {
                (activity as MainNewActivity).updateClassId(mClassId)
            }
        }
    }

    private var compositeDisposable: CompositeDisposable? = null

    private fun initMessageCheck() {
        compositeDisposable = CompositeDisposable()
        val disposable = RetrofitWorker.retrofitWorker.messageCheck(HashMap())
                .doOnSubscribe {
                    Log.e("error", "loopSequence subscribe")
                }
                .doOnNext {
                    Log.e("error", "loopSequence doOnNext")
                }
                .doOnError {
                    Log.e("error", "loopSequence doOnError ${it.message}")
                }
                .delay(
                        10,
                        TimeUnit.SECONDS,
                        true
                )       // 设置delayError为true，表示出现错误的时候也需要延迟5s进行通知，达到无论是请求正常还是请求失败，都是5s后重新订阅，即重新请求。
                .subscribeOn(Schedulers.io())
                .repeat()   // repeat保证请求成功后能够重新订阅。
                .retry()    // retry保证请求失败后能重新订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //                    Log.e("error","result result result")
                    if (it.data != null && !it.data!!.isEmpty()) {
                        pushMessage(it.data!![0])
                    }
                    val message = MessageListBean.DataBean()
                    message.id = 123
                    message.content = "权威和气候危机恶化请我喝酒"
                    pushMessage(message)
                }
        compositeDisposable?.add(disposable)
    }

    private fun pushMessage(message: MessageListBean.DataBean) {
        if (message.id != 0 && message.id != mMessageId) {
            mMessageId = message.id
            val notificationManager =
                    context!!.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(Contacts.PUSH_CHANNEL_ID, Contacts.PUSH_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(context!!, Contacts.PUSH_CHANNEL_ID)
            val intentClick = Intent(context, MessageBroadcastReceiver::class.java)
            intentClick.action = "notification_clicked"
            intentClick.putExtra(MessageBroadcastReceiver.TYPE, 1)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intentClick, PendingIntent.FLAG_ONE_SHOT)
            builder.setContentTitle(getString(R.string.new_message))//设置通知栏标题
                    .setContentIntent(pendingIntent)
                    .setContentText(message.content)
                    .setTicker(message.content) //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setSmallIcon(com.monjaz.baiedu.R.mipmap.ic_launcher)//设置通知小ICON
                    .setChannelId(Contacts.PUSH_CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_ALL)
            val notification = builder.build()
            notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(Contacts.PUSH_NOTIFICATION_ID, notification)
        }
    }

    override fun onStart() {
        Log.e("error", "onStart 订阅")
//        initMessageCheck()
        // 开始轮播
        mBanner.startAutoPlay()
        super.onStart()
    }

    override fun onStop() {
        Log.e("error", "onStop 取消订阅")
        // 将所有的 observer 取消订阅
//        compositeDisposable?.dispose()
//        compositeDisposable?.clear()
//        compositeDisposable = null
        // 结束轮播
        mBanner.stopAutoPlay()
        super.onStop()
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("freshUserInfo"))])
    fun receiveData(fresh: String) {
        getUserInfo()
    }
}