package com.monjaz.baiedu.base

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.hwangjr.rxbus.RxBus
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.local.LanguageUtil
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.service.PlayService
import com.monjaz.baiedu.update.ApkInstallReceiver
import com.monjaz.baiedu.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by guluwa on 2018/3/14.
 */

abstract class BaseActivity : AppCompatActivity(), IBaseView {

    /**
     * 绑定布局文件
     */
    abstract val viewLayoutId: Int

    /**
     * ViewDataBinding对象
     */
    lateinit var mViewDataBinding: ViewDataBinding

    /**
     * 初始化视图控件
     */
    protected abstract fun initViews()

    /**
     * activity管理类
     */
    protected val finishActivityManager = FinishActivityManager.getInstance()

    /**
     * 需要进行检测的权限数组
     */
    private var needPermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WAKE_LOCK
    )

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private var isNeedCheck = true

    /**
     * 进度对话框
     */
    private var mProgressDialog: ProgressDialog? = null

    /**
     * 标记下拉刷新还是上拉加载
     */
    var isRefresh = true

    /**
     * 页码
     */
    var page = 1

    private var mPlayServiceConnection: PlayServiceConnection? = null

    override fun attachBaseContext(newBase: Context?) {
        var language = AppUtils.getString(Contacts.LANGUAGE, "")
        if (language == "") {
            language = "zh"
        }
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, viewLayoutId)
        //RxBus注册
        RxBus.get().register(this)
        finishActivityManager.addActivity(this)
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
        initViews()
        initService()
        initViewModel()
    }

    private fun initService() {
        val intent = Intent()
        intent.setClass(this, PlayService::class.java)
        mPlayServiceConnection = PlayServiceConnection()
        bindService(intent, mPlayServiceConnection!!, Context.BIND_AUTO_CREATE)
    }

    /**
     * 显示提示信息
     */
    private fun showMissingPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.notifyTitle)
        builder.setMessage(R.string.notifyMsg)
        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel, { _, _ -> finish() })
        builder.setPositiveButton(R.string.setting, { _, _ -> startAppSettings() })
        builder.setCancelable(false)
        builder.show()
    }

    /**
     * 启动应用的设置
     */
    private fun startAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        )
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (isNeedCheck) {
            checkPermissions(needPermissions)
        }
    }

    override fun onDestroy() {
        //结束Activity&从栈中移除该Activity
        finishActivityManager.finishActivity(this)
        RxBus.get().unregister(this)
        unbindService(mPlayServiceConnection)
        super.onDestroy()
    }

    /**
     * 权限检查是否全部申请
     */
    private fun checkPermissions(permissions: Array<String>) {
        val needRequestPermissionList = findDeniedPermissions(permissions)
        if (needRequestPermissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                needRequestPermissionList.toTypedArray(),
                Contacts.PERMISSION_REQUEST_CODE
            )
        } else {
            allPermAllow()
        }
    }

    /**
     * 全部权限都已获取
     */
    open fun allPermAllow() {

    }

    /**
     * 获取权限集中需要申请权限的列表
     */
    private fun findDeniedPermissions(permissions: Array<String>): List<String> {
        val needRequestPermissionList = ArrayList<String>()
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm
                )
            ) {
                needRequestPermissionList.add(perm)
            }
        }
        return needRequestPermissionList
    }

    /**
     * 权限申请情况
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, paramArrayOfInt: IntArray) {
        if (requestCode == Contacts.PERMISSION_REQUEST_CODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog()
                isNeedCheck = false
            }
        }
    }

    /**
     * 检测是否所有的权限都已经授权
     */
    private fun verifyPermissions(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        val packageURI = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        startActivityForResult(intent, 10086)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10086) {
            if (resultCode == Activity.RESULT_OK) {
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                ApkInstallReceiver.installApk(this, sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L))
            } else {
                showToastMsg(getString(R.string.open_permission))
            }
        }
    }

    /**
     * 点击空白位置 隐藏软键盘
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (null != this.currentFocus) {
            val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return mInputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.onTouchEvent(event)
    }

    /**
     * 弹出Toast
     */
    override fun showToastMsg(msg: String) {
        ToastUtil.getInstance().showToast(msg)
    }

    override fun showProgressDialog(msg: String) {
        if (!isFinishing) {//activity未关闭 显示dialog
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog(this)
                mProgressDialog!!.setCancelable(false)
                mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            }
            mProgressDialog!!.setMessage(msg)
            mProgressDialog!!.show()
        }
    }

    override fun dismissProgressDialog() {
        if (mProgressDialog != null && !isFinishing) {
            mProgressDialog!!.dismiss()
        }
    }

    private var disposableCountDown: Disposable? = null

    fun showCountDownProgressDialog(msg: String, num: Long) {
        showProgressDialog(msg)
        disposableCountDown = Observable.interval(0, num, TimeUnit.SECONDS)// 倒计时
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ aLong ->
                if (aLong != 0L) {
                    dismissProgressDialog()
                    disposableCountDown?.dispose()
                    disposableCountDown = null
                }
            }) {
                dismissProgressDialog()
                disposableCountDown?.dispose()
                disposableCountDown = null
            }
    }

    open fun initViewModel() {

    }

    /**
     * 异常数据解析
     */
    open fun parseErrorDate(msg: String): PageTipBean {
        return when {
            msg.contains("IllegalStateException") -> PageTipBean(getString(R.string.data_format_error), 0, 1)
            else -> PageTipBean(msg, 0, 1)
        }
    }

    private inner class PlayServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val playService = (service as PlayService.PlayBinder).service
            Log.e("onServiceConnected----", "onServiceConnected")
            AppCache.setPlayService(playService)
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }
}