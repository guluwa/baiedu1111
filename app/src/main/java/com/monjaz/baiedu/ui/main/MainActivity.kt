package com.monjaz.baiedu.ui.main

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.local.MainMenuBean
import com.monjaz.baiedu.data.bean.remote.MessageListBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.UserInfoBean
import com.monjaz.baiedu.data.remote.retrofit.RemoteDataSource
import com.monjaz.baiedu.data.remote.retrofit.RetrofitWorker
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.manage.Contacts.PUSH_CHANNEL_ID
import com.monjaz.baiedu.manage.Contacts.PUSH_CHANNEL_NAME
import com.monjaz.baiedu.manage.Contacts.PUSH_NOTIFICATION_ID
import com.monjaz.baiedu.ui.adapter.MainMenuListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.children.MyChildrenActivity
import com.monjaz.baiedu.ui.main.classs.ClassDetailActivity
import com.monjaz.baiedu.ui.main.classs.JoinNewClassActivity
import com.monjaz.baiedu.ui.main.classs.MyClassActivity
import com.monjaz.baiedu.ui.main.message.MessageDetailActivity
import com.monjaz.baiedu.ui.main.message.UserMessageActivity
import com.monjaz.baiedu.ui.main.mistakes.MyMistakesActivity
import com.monjaz.baiedu.ui.main.setting.SettingActivity
import com.monjaz.baiedu.ui.main.work.ClassHomeWorksActivity
import com.monjaz.baiedu.ui.main.work.TeacherHomeWorkActivity
import com.monjaz.baiedu.ui.viewmodel.MainViewModel
import com.monjaz.baiedu.utils.AppUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import io.reactivex.disposables.CompositeDisposable

class MainActivity : BaseActivity() {

    private var mClassId = 0

    private var mClassName = ""

    private var mMenuList = mutableListOf<MainMenuBean>()

    private var mViewModel: MainViewModel? = null

    private var mMessagePushId = -1

    override val viewLayoutId: Int get() = R.layout.activity_main

    override fun initViews() {
        initData()
        initClickEvent()
        initRecyclerView()
        initMessageCheck()
    }

    private fun initClickEvent() {
        ivUserMessage.setOnClickListener {
            startActivity(Intent(this, UserMessageActivity::class.java))
        }
    }

    private fun initData() {
        val type = AppUtils.getString(Contacts.TYPE, "2")
        when (type) {
            "1" -> {
                mMenuList.add((MainMenuBean(1, getString(R.string.menu_my_class), getString(R.string.class_explain), R.color.main_menu_item_color1, R.drawable.ic_main_menu_class)))
                mMenuList.add((MainMenuBean(2, getString(R.string.menu_my_work), getString(R.string.work_explain), R.color.main_menu_item_color2, R.drawable.ic_main_menu_work)))
            }
            "2" -> {
                mMenuList.add((MainMenuBean(1, getString(R.string.menu_my_class), getString(R.string.class_explain), R.color.main_menu_item_color1, R.drawable.ic_main_menu_class)))
                mMenuList.add((MainMenuBean(2, getString(R.string.menu_my_work), getString(R.string.work_explain), R.color.main_menu_item_color2, R.drawable.ic_main_menu_work)))
                mMenuList.add((MainMenuBean(3, getString(R.string.menu_mistakes), getString(R.string.student_menu_mistakes), R.color.main_menu_item_color3, R.drawable.ic_main_menu_mistake)))
            }
            "3" -> {
                mMenuList.add((MainMenuBean(4, getString(R.string.menu_children), getString(R.string.children_explain), R.color.main_menu_item_color4, R.drawable.ic_main_menu_children)))
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = MainMenuListAdapter(mMenuList, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as MainMenuBean
                when (item.id) {
                    1 ->
                        if (AppUtils.getString(Contacts.TYPE, "2") == "2") {
                            if (mClassId == 0) {
                                startActivity(Intent(this@MainActivity, JoinNewClassActivity::class.java))
                            } else {
                                val intent = Intent(this@MainActivity, ClassDetailActivity::class.java)
                                intent.putExtra("classId", mClassId)
                                startActivity(intent)
                            }
                        } else {
                            startActivity(Intent(this@MainActivity, MyClassActivity::class.java))
                        }
                    2 ->
                        if (AppUtils.getString(Contacts.TYPE, "2") == "2") {
                            if (mClassId == 0 || mClassName == "") {
                                startActivity(Intent(this@MainActivity, JoinNewClassActivity::class.java))
                            } else {
                                val intent = Intent(this@MainActivity, ClassHomeWorksActivity::class.java)
                                intent.putExtra("classId", mClassId)
                                intent.putExtra("className", mClassName)
                                startActivity(intent)
                            }
                        } else {
                            startActivity(Intent(this@MainActivity, TeacherHomeWorkActivity::class.java))
                        }
                    3 ->
                        startActivity(Intent(this@MainActivity, MyMistakesActivity::class.java))
                    4 ->
                        startActivity(Intent(this@MainActivity, MyChildrenActivity::class.java))
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
        }
        getUserInfo()
    }

    private fun getUserInfo() {
        mViewModel?.freshGetUserInfo(HashMap(), true)
    }

    private fun showUserInfo(userInfoBean: UserInfoBean) {
        Glide.with(this).asBitmap()
                .apply(RequestOptions().circleCrop().placeholder(R.drawable.ic_class_student))
                .load(userInfoBean.faceimg)
                .into(ivUserImage)
        tvUserName.text = userInfoBean.name
        val type = AppUtils.getString(Contacts.TYPE, "2")
        tvUserIdentify.text = when (type) {
            "1" -> getString(R.string.teacher)
            "2" -> getString(R.string.student)
            else -> getString(R.string.parents)
        }
        tvUserIdentify.visibility = View.VISIBLE
        if (type == "2" && userInfoBean.classid != null && userInfoBean.classname != null) {
            mClassId = userInfoBean.classid!!
            mClassName = userInfoBean.classname!!
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("freshUserInfo"))])
    fun receiveData(fresh: String) {
        getUserInfo()
    }

    private var compositeDisposable: CompositeDisposable? = null

    private fun initMessageCheck() {
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
                    if (it.data != null && !it.data!!.isEmpty()) {
                        pushMessage(it.data!![0])
                    }
                }
        compositeDisposable?.add(disposable)
    }

    private fun pushMessage(message: MessageListBean.DataBean) {
        if (message.pushid != 0 && message.pushid != mMessagePushId) {
            mMessagePushId = message.pushid
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                        NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(this, PUSH_CHANNEL_ID)
            val notificationIntent = Intent(this, UserMessageActivity::class.java)
            notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
            builder.setContentTitle(getString(R.string.new_message))//设置通知栏标题
                    .setFullScreenIntent(pendingIntent, true)
                    .setContentIntent(pendingIntent)
                    .setContentText(message.content)
                    .setTicker(message.content) //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setSmallIcon(R.mipmap.ic_launcher)//设置通知小ICON
                    .setChannelId(PUSH_CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_ALL)

            val notification = builder.build()
            notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(PUSH_NOTIFICATION_ID, notification)
        }
    }

    override fun onStart() {
        compositeDisposable?.dispose()
        compositeDisposable = CompositeDisposable()
        super.onStart()
    }

    override fun onStop() {
        compositeDisposable?.dispose()
        super.onStop()
    }
}
