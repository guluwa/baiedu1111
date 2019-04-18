package com.monjaz.baiedu.ui.main.classs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.data.bean.remote.TeacherClassBean
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.ClassListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.ClassViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_my_class.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class MyClassActivity : BaseActivity() {

    private var mViewModel: ClassViewModel? = null

    private var type = "2"

    override val viewLayoutId: Int get() = R.layout.activity_my_class

    override fun initViews() {
        initToolBar()
        initRefreshLayout()
        initRecyclerView()
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.menu_my_class)
        ivBack.setOnClickListener { finish() }
        type = AppUtils.getString(Contacts.TYPE, "2")
    }

    private fun initRefreshLayout() {
        mRefreshLayout.setFloatRefresh(true)
        mRefreshLayout.setAutoLoadMore(true)
        mRefreshLayout.setEnableLoadmore(false)
        mRefreshLayout.setEnableRefresh(false)
        mRefreshLayout.setEnableOverScroll(false)
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
                page = 1
                loadData()
            }
        })
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = ClassListAdapter(arrayListOf(PageTipBean("", 0, 1)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as TeacherClassBean
                val intent = Intent(this@MyClassActivity, ClassDetailActivity::class.java)
                intent.putExtra("classId", item.classid)
                startActivity(intent)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(ClassViewModel::class.java)
        if (!mViewModel!!.getTeacherClassList()!!.hasObservers()) {
            mViewModel!!.getTeacherClassList()!!.observe(this, Observer {
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
                        mViewModel!!.freshGetTeacherClassList(hashMapOf(), false)
                        showErrorMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetTeacherClassList(hashMapOf(), false)
                        showErrorMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetTeacherClassList(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showClassList(it.data.data!!)
                        } else {
                            showErrorMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        loadData()
    }

    private fun showClassList(data: List<TeacherClassBean>) {
        if (data.isEmpty()) {
            showErrorMsg(getString(R.string.have_no_class))
        } else {
            (mRecyclerView.adapter as ClassListAdapter).setData(data)
        }
    }

    private fun showErrorMsg(msg:String) {
        (mRecyclerView.adapter as ClassListAdapter).setPageTipBean(PageTipBean(msg, 0, 1))
    }

    private fun loadData() {
        if (type == "1") {//teacher
            mViewModel?.freshGetTeacherClassList(HashMap(), true)
        }
    }
}
