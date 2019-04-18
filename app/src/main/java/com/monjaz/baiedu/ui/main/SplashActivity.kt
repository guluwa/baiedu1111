package com.monjaz.baiedu.ui.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing.model.entity.impl.ImageMedia
import com.bilibili.boxing.utils.ImageCompressor
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.bumptech.glide.Glide
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.main.login.UserLoginActivity
import com.monjaz.baiedu.ui.main.main.MainNewActivity
import com.monjaz.baiedu.utils.AppUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_splash

    private var disposable: Disposable? = null

    override fun initViews() {
        initStatusBar()
    }

    private fun initStatusBar() {
        //5.0以上状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//禁止横屏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun allPermAllow() {
        if (disposable == null)
            disposable = Observable.interval(0, 1, TimeUnit.SECONDS)//10s 倒计时
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ aLong -> if (aLong != 0L) goAhead() }) { goAhead() }
    }

    private fun goAhead() {
        if (AppUtils.getString(Contacts.TOKEN, "") == "") {
            startActivity(Intent(this, UserLoginActivity::class.java))
        } else {
            startActivity(Intent(this, MainNewActivity::class.java))
        }
        disposable?.dispose()
        disposable == null
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        disposable = null
    }
}
