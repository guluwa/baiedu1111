package com.monjaz.baiedu.ui.main.mistakes

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.MistakeListBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.MistakeBookListAdapter
import com.monjaz.baiedu.ui.dialog.BotMistakeBookDialog
import com.monjaz.baiedu.ui.dialog.InputMistakeBookDialog
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.MistakesViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_my_mistakes.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class MyMistakesActivity : BaseActivity() {

    private var id = 0

    private var mChildId = 0

    private var type = "2"

    override val viewLayoutId: Int get() = R.layout.activity_my_mistakes

    private var mViewModel: MistakesViewModel? = null

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
    }

    private fun initData() {
        type = AppUtils.getString(Contacts.TYPE, "2")
        if (type == "3") {
            if (intent.getIntExtra("childId", 0) != 0) {
                mChildId = intent.getIntExtra("childId", 0)
            } else {
                showToastMsg(getString(R.string.data_error))
                return
            }
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.menu_mistakes)
        ivBack.setOnClickListener { finish() }
        if (type == "2") {
            tvRightBtn.text = getString(R.string.add)
            tvRightBtn.visibility = View.VISIBLE
            tvRightBtn.setOnClickListener {
                showInputMistakeBookDialog()
            }
        }
    }

    private fun showInputMistakeBookDialog() {
        InputMistakeBookDialog(id, this, R.style.DialogStyle, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                if (arg2 is String && arg2 == "") {
                    showToastMsg(getString(R.string.edit_mistakes_collection_name_hint))
                } else {
                    if (id != 0) {
                        updateMistakeList(arg2 as String)
                    } else {
                        addMistakesList(arg2 as String)
                    }
                }
            }
        }).show()
    }

    private fun addMistakesList(name: String) {
        val map = HashMap<String, String>()
        map["bookname"] = name
        mViewModel?.freshAddMistakeList(map, true)
    }

    private fun updateMistakeList(name: String) {
        val map = HashMap<String, String>()
        map["id"] = id.toString()
        map["bookname"] = name
        mViewModel?.freshUpdateMistakeList(map, true)
    }

    private fun deleteMistakesList() {
        if (id != 0) {
            val map = HashMap<String, String>()
            map["id"] = id.toString()
            mViewModel?.freshDeleteMistakeList(map, true)
        }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = MistakeBookListAdapter(mutableListOf(PageTipBean("", 0, 1)), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val item = arg2 as MistakeListBean
                if (arg1 == 1) {
                    val intent = Intent(this@MyMistakesActivity, MistakesListActivity::class.java)
                    intent.putExtra("mistakeBookId", item.id)
                    intent.putExtra("mistakeBookName", item.bookname)
                    if (type == "3") {
                        intent.putExtra("childId", mChildId)
                    }
                    startActivity(intent)
                } else {
                    BotMistakeBookDialog(this@MyMistakesActivity, R.style.DialogStyle, object : OnClickListener {
                        override fun click(arg1: Int, arg2: Any) {
                            if (arg1 == 1) {
                                id = item.id
                                showInputMistakeBookDialog()
                            } else {
                                id = item.id
                                deleteMistakesList()
                            }
                        }
                    }).show()
                }
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(MistakesViewModel::class.java)
        if (!mViewModel!!.getMistakesList()!!.hasObservers()) {
            mViewModel!!.getMistakesList()!!.observe(this, Observer {
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
                        mViewModel!!.freshGetMistakesList(hashMapOf(), false)
                        showErrorMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetMistakesList(hashMapOf(), false)
                        showErrorMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetMistakesList(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showMistakeBookList(it.data.data!!)
                        } else {
                            showErrorMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
            mViewModel!!.addMistakeList()!!.observe(this, Observer {
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
                        mViewModel!!.freshAddMistakeList(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshAddMistakeList(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshAddMistakeList(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            getMistakesList()
                        } else {
                            showToastMsg(getString(R.string.add_failed))
                        }
                    }
                }
            })
            mViewModel!!.updateMistakeList()!!.observe(this, Observer {
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
                        mViewModel!!.freshUpdateMistakeList(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                        id = 0
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUpdateMistakeList(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                        id = 0
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUpdateMistakeList(hashMapOf(), false)
                        id = if (it.data!!.code == 0 && it.data.data != null) {
                            getMistakesList()
                            0
                        } else {
                            showToastMsg(getString(R.string.update_failed))
                            0
                        }
                    }
                }
            })
            mViewModel!!.deleteMistakeList()!!.observe(this, Observer {
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
                        mViewModel!!.freshDeleteMistakeList(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshDeleteMistakeList(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshDeleteMistakeList(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            getMistakesList()
                            id = 0
                        } else {
                            showToastMsg(getString(R.string.delete_failed))
                        }
                    }
                }
            })
        }
        getMistakesList()
    }

    private fun showMistakeBookList(data: List<MistakeListBean>) {
        if (data.isEmpty()) {
            if (type == "3") {
                showErrorMsg(getString(R.string.child_have_no_mistake_book))
            } else {
                showErrorMsg(getString(R.string.have_no_mistake_book))
            }
        } else {
            (mRecyclerView.adapter as MistakeBookListAdapter).setData(data)
        }
    }

    private fun showErrorMsg(msg: String) {
        (mRecyclerView.adapter as MistakeBookListAdapter).setPageTipBean(PageTipBean(msg, 0, 1))
    }

    private fun getMistakesList() {
        val map = HashMap<String, String>()
        if (type == "3") {
            map["student_id"] = mChildId.toString()
        }
        mViewModel?.freshGetMistakesList(map, true)
    }
}
