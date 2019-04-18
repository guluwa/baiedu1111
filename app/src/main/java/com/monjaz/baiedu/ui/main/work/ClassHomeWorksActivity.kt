package com.monjaz.baiedu.ui.main.work

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.HomeWorkListBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.HomeWorkListAdapter
import com.monjaz.baiedu.ui.adapter.MessageListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.HomeWorkViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_class_home_works.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class ClassHomeWorksActivity : BaseActivity() {

    private var mViewModel: HomeWorkViewModel? = null

    private var mClassId = 0

    private var mClassName = ""

    private var mChildId = 0

    private var mMaxNum = -1

    private var type = "2"

    override val viewLayoutId: Int get() = R.layout.activity_class_home_works

    override fun initViews() {
        initData()
        initToolBar()
        initRefreshLayout()
        initRecyclerView()
    }

    private fun initData() {
        if (intent.getIntExtra("classId", 0) == 0) {
            showToastMsg(getString(R.string.data_error))
            finish()
        } else {
            mClassId = intent.getIntExtra("classId", 0)
        }
        if (intent.getStringExtra("className") == "") {
            showToastMsg(getString(R.string.data_error))
            finish()
        } else {
            mClassName = intent.getStringExtra("className")
        }
        type = AppUtils.getString(Contacts.TYPE, "2")
        if (type == "3") {
            if (intent.getIntExtra("childId", 0) == 0) {
                showToastMsg(getString(R.string.data_error))
                finish()
            } else {
                mChildId = intent.getIntExtra("childId", 0)
            }
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = if (type == "3") {
            getString(R.string.menu_child_work)
        } else {
            getString(R.string.menu_my_work)
        }
        ivBack.setOnClickListener { finish() }
        if (type == "1") {
            tvRightBtn.text = getString(R.string.publish)
            tvRightBtn.visibility = View.VISIBLE
            tvRightBtn.setOnClickListener {
                val intent = Intent(this, AddHomeWorkActivity::class.java)
                intent.putExtra("classId", mClassId)
                startActivity(intent)
            }
        }
    }

    private fun initRefreshLayout() {
        mRefreshLayout.setFloatRefresh(true)
        mRefreshLayout.setAutoLoadMore(true)
        mRefreshLayout.setEnableLoadmore(true)
        mRefreshLayout.setEnableRefresh(true)
        mRefreshLayout.setEnableOverScroll(true)
        val header = ProgressLayout(this)
        header.setColorSchemeResources(R.color.colorAccent)
        mRefreshLayout.setHeaderView(header)
        mRefreshLayout.setBottomView(LoadingView(this))
        mRefreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                isRefresh = false
                loadData()
            }

            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                isRefresh = true
                mMaxNum = -1
                page = 1
                loadData()
            }
        })
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = HomeWorkListAdapter(arrayListOf(PageTipBean("", 0, 1)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as HomeWorkListBean.DataBean
                val intent = Intent(this@ClassHomeWorksActivity, HomeWorkDetailActivity::class.java)
                intent.putExtra("homeWorkId", item.id)
                if (type == "3") {
                    intent.putExtra("childId", mChildId)
                }
                startActivity(intent)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(HomeWorkViewModel::class.java)
        if (!mViewModel!!.getHomeWorkList()!!.hasObservers()) {
            mViewModel!!.getHomeWorkList()!!.observe(this, Observer {
                if (it == null) {
                    dismissProgressDialog()
                    showErrorMsg(getString(R.string.data_wrong))
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog(getString(R.string.please_wait))
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetHomeWorkList(hashMapOf(), false)
                        showErrorMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetHomeWorkList(hashMapOf(), false)
                        showErrorMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetHomeWorkList(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showHomeWork(it.data.data!!)
                        } else {
                            showErrorMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        loadData()
    }

    private fun showHomeWork(data: HomeWorkListBean) {
        mMaxNum = data.totalcount.toInt()
        refreshLayoutRelease()
        if (data.data.isNotEmpty()) {
            page++
            (mRecyclerView.adapter as HomeWorkListAdapter).setData(data.data, page)
        } else {
            if (page == 1) {
                (mRecyclerView.adapter as HomeWorkListAdapter).setPageTipBean(
                    PageTipBean(
                        getString(R.string.have_no_work),
                        0,
                        1
                    )
                )
            } else {
                showToastMsg(getString(R.string.no_more_records))
            }
        }
    }

    private fun showErrorMsg(msg: String) {
        refreshLayoutRelease()
        if (page == 1) {
            (mRecyclerView.adapter as HomeWorkListAdapter).setPageTipBean(PageTipBean(msg, 0, 1))
        } else {
            showToastMsg(msg)
        }
    }

    private fun refreshLayoutRelease() {
        if (page == 1 && isRefresh) {
            mRefreshLayout.finishRefreshing()
        } else {
            mRefreshLayout.finishLoadmore()
        }
    }

    private fun loadData() {
        if (mMaxNum != -1 && page != 1 && page > (mMaxNum / 20)) {
            showToastMsg(getString(R.string.no_more_records))
            refreshLayoutRelease()
            return
        }
        val map = HashMap<String, String>()
        map["classid"] = mClassId.toString()
        if (type == "1") {
            map["teacherid"] = AppUtils.getString(Contacts.ID, "")
        }
        map["limit"] = "20"
        map["page"] = page.toString()
        mViewModel?.freshGetHomeWorkList(map, true)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("freshHomeWorks"))])
    fun receiveData(fresh: String) {
        mMaxNum = -1
        page = 1
        loadData()
    }
}
