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
import androidx.recyclerview.widget.LinearLayoutManager
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.LazyFragment
import com.monjaz.baiedu.data.bean.local.MainMenuBean
import com.monjaz.baiedu.data.bean.remote.MessageListBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.UserInfoBean
import com.monjaz.baiedu.data.remote.retrofit.RetrofitWorker
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.MainMenuListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.children.MyChildrenActivity
import com.monjaz.baiedu.ui.main.classs.ClassDetailActivity
import com.monjaz.baiedu.ui.main.classs.JoinNewClassActivity
import com.monjaz.baiedu.ui.main.classs.MyClassActivity
import com.monjaz.baiedu.ui.main.mistakes.MyMistakesActivity
import com.monjaz.baiedu.ui.main.quiz.SelectQuizTypeActivity
import com.monjaz.baiedu.ui.main.quiz.SentQuizListActivity
import com.monjaz.baiedu.ui.main.work.ClassHomeWorksActivity
import com.monjaz.baiedu.ui.main.work.TeacherHomeWorkActivity
import com.monjaz.baiedu.ui.receiver.MessageBroadcastReceiver
import com.monjaz.baiedu.ui.viewmodel.MainViewModel
import com.monjaz.baiedu.utils.AppUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_login.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.concurrent.TimeUnit

class HomeFragment : LazyFragment() {

    override val viewLayoutId: Int get() = R.layout.fragment_home

    private var mClassId = 0

    private var mClassName = ""

    private var mMenuList = mutableListOf<MainMenuBean>()

    private var mViewModel: MainViewModel? = null

    private var mMessageId = -1

    private var isFirst = true

    companion object {
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    override fun lazyLoad() {
        getUserInfo()
    }

    override fun initViews() {
        initToolBar()
        if (isFirst) {
            initData()
            initRecyclerView()
            isFirst = false
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.home)
        ivBack.visibility = View.GONE
    }

    private fun initData() {
        val type = AppUtils.getString(Contacts.TYPE, "2")
        when (type) {
            "1" -> {
                mMenuList.add((MainMenuBean(1, getString(R.string.menu_my_class), getString(R.string.class_explain), R.color.main_menu_item_color1, R.drawable.ic_main_menu_class)))
                mMenuList.add((MainMenuBean(2, getString(R.string.menu_my_work), getString(R.string.work_explain), R.color.main_menu_item_color2, R.drawable.ic_main_menu_work)))
                mMenuList.add((MainMenuBean(5, getString(R.string.student_menu_quiz), getString(R.string.teacher_quiz_explain), R.color.main_menu_item_color5, R.drawable.ic_main_menu_quiz)))
            }
            "2" -> {
                mMenuList.add((MainMenuBean(1, getString(R.string.menu_my_class), getString(R.string.class_explain), R.color.main_menu_item_color1, R.drawable.ic_main_menu_class)))
                mMenuList.add((MainMenuBean(2, getString(R.string.menu_my_work), getString(R.string.work_explain), R.color.main_menu_item_color2, R.drawable.ic_main_menu_work)))
                mMenuList.add((MainMenuBean(3, getString(R.string.menu_mistakes), getString(R.string.children_mistake_explain), R.color.main_menu_item_color3, R.drawable.ic_main_menu_mistake)))
            }
            "3" -> {
                mMenuList.add((MainMenuBean(4, getString(R.string.menu_children), getString(R.string.children_explain), R.color.main_menu_item_color4, R.drawable.ic_main_menu_children)))
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = MainMenuListAdapter(mMenuList, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as MainMenuBean
                when (item.id) {
                    1 ->
                        if (AppUtils.getString(Contacts.TYPE, "2") == "2") {
                            if (mClassId == 0) {
                                startActivity(Intent(context, JoinNewClassActivity::class.java))
                            } else {
                                val intent = Intent(context, ClassDetailActivity::class.java)
                                intent.putExtra("classId", mClassId)
                                startActivity(intent)
                            }
                        } else {
                            startActivity(Intent(context, MyClassActivity::class.java))
                        }
                    2 ->
                        if (AppUtils.getString(Contacts.TYPE, "2") == "2") {
                            if (mClassId == 0 || mClassName == "") {
                                startActivity(Intent(context, JoinNewClassActivity::class.java))
                            } else {
                                val intent = Intent(context, ClassHomeWorksActivity::class.java)
                                intent.putExtra("classId", mClassId)
                                intent.putExtra("className", mClassName)
                                startActivity(intent)
                            }
                        } else {
                            startActivity(Intent(context, TeacherHomeWorkActivity::class.java))
                        }
                    3 ->
                        startActivity(Intent(context, MyMistakesActivity::class.java))
                    4 ->
                        startActivity(Intent(context, MyChildrenActivity::class.java))
                    5 ->
                        startActivity(Intent(context, SelectQuizTypeActivity::class.java))
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
    }

    private fun getUserInfo() {
        mViewModel?.freshGetUserInfo(HashMap(), true)
    }

    private fun showUserInfo(userInfoBean: UserInfoBean) {
        val type = AppUtils.getString(Contacts.TYPE, "2")
        if (type == "2" && userInfoBean.classid != null && userInfoBean.classname != null) {
            mClassId = userInfoBean.classid!!
            mClassName = userInfoBean.classname!!
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("freshUserInfo"))])
    fun receiveData(fresh: String) {
        getUserInfo()
    }
}