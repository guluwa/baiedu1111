package com.monjaz.baiedu.ui.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.MessageListBean
import com.monjaz.baiedu.data.remote.retrofit.RetrofitWorker
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.main.SplashActivity
import com.monjaz.baiedu.ui.main.main.MainNewActivity
import com.monjaz.baiedu.ui.receiver.MessageBroadcastReceiver
import com.xdandroid.hellodaemon.AbsWorkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MessageService : AbsWorkService() {

    private var mMessageId = -1

    private var sShouldStopService: Boolean = false

    private var compositeDisposable: CompositeDisposable? = null

    override fun onBind(intent: Intent?, alwaysNull: Void?): IBinder? {
        return null
    }

    override fun startWork(intent: Intent?, flags: Int, startId: Int) {
        initMessageCheck()
    }

    override fun isWorkRunning(intent: Intent?, flags: Int, startId: Int): Boolean {
        //若还没有取消订阅, 就说明任务仍在运行.
        return compositeDisposable != null && !compositeDisposable!!.isDisposed
    }

    override fun shouldStopService(intent: Intent?, flags: Int, startId: Int): Boolean {
        return sShouldStopService
    }

    override fun stopWork(intent: Intent?, flags: Int, startId: Int) {
//        stopService()
        println("app close")
    }

    override fun onServiceKilled(rootIntent: Intent?) {
        System.out.println("保存数据到磁盘。")
    }

    private fun stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true
        //取消对任务的订阅
        if (compositeDisposable != null) compositeDisposable?.dispose()
        //取消 Job / Alarm / Subscription
        AbsWorkService.cancelJobAlarmSub()
    }

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
            }
        compositeDisposable?.add(disposable)
    }

    private fun pushMessage(message: MessageListBean.DataBean) {
        if (message.id != 0 && message.id != mMessageId) {
            mMessageId = message.id
            val notificationManager = getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        Contacts.PUSH_CHANNEL_ID,
                        Contacts.PUSH_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                notificationManager.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(this, Contacts.PUSH_CHANNEL_ID)
            val intentClick = Intent(this, MessageBroadcastReceiver::class.java)
            intentClick.action = "notification_clicked"
            intentClick.putExtra(MessageBroadcastReceiver.TYPE, 1)
            val pendingIntent =
                PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_ONE_SHOT)
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
}
//1.家长端孩子列表接口 parent/studentlist 返回数据有问题 返回的是所有孩子列表
//2.家长端绑定还是加判断是否已经绑定该孩子 如果已经绑定则不能再绑定
//3.家长端添加统计孩子一个月内作业完成次数、错题本、错题集添加数量
//4.所有注册接口添加邮箱'手机号2个字段