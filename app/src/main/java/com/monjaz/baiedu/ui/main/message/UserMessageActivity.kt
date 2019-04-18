package com.monjaz.baiedu.ui.main.message

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.MessageListBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.ui.adapter.MessageListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.MessageViewModel
import kotlinx.android.synthetic.main.activity_user_message.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import java.io.Serializable

class UserMessageActivity : BaseActivity() {

    private var mViewModel: MessageViewModel? = null

    private var mMaxNum = -1

    override val viewLayoutId: Int get() = R.layout.activity_user_message

    override fun initViews() {
        initToolBar()
        initRefreshLayout()
        initRecyclerView()
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.my_messages)
        ivBack.setOnClickListener { finish() }
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
                page = 1
                loadData()
            }
        })
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = MessageListAdapter(arrayListOf(), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val intent = Intent(this@UserMessageActivity, MessageDetailActivity::class.java)
                intent.putExtra("message", arg2 as Serializable)
                startActivity(intent)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java)
        if (!mViewModel!!.messageList()!!.hasObservers()) {
            mViewModel!!.messageList()!!.observe(this, Observer {
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
                        mViewModel!!.freshMessageList(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshMessageList(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshMessageList(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showMessages(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        loadData()
    }

    private fun showMessages(data: MessageListBean) {
        mMaxNum = data.totalcount.toInt()
        refreshLayoutRelease()
        if (data.data.isNotEmpty()) {
            page++
            (mRecyclerView.adapter as MessageListAdapter).setData(data.data, page)
        } else {
            showToastMsg(getString(R.string.no_more_records))
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
        map["limit"] = "20"
        map["page"] = page.toString()
        mViewModel?.freshMessageList(map, true)
    }
}
