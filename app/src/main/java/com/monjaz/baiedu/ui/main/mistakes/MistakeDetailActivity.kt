package com.monjaz.baiedu.ui.main.mistakes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.hwangjr.rxbus.RxBus
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.MistakeBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.HomeWorkImagesAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.main.work.ScanPicActivity
import com.monjaz.baiedu.ui.viewmodel.MistakesViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_home_work_detail.*
import kotlinx.android.synthetic.main.activity_mistake_detail.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import java.io.Serializable

class MistakeDetailActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_mistake_detail

    private var mMistake: MistakeBean? = null

    private var mViewModel: MistakesViewModel? = null

    private var type = "2"

    override fun initViews() {
        initData()
        initToolBar()
        initMistake()
    }

    private fun initData() {
        type = AppUtils.getString(Contacts.TYPE, "2")
        if (intent.getSerializableExtra("mistake") != null) {
            mMistake = intent.getSerializableExtra("mistake") as MistakeBean
        } else {
            showToastMsg(getString(R.string.data_error))
            return
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.mistake_detail)
        ivBack.setOnClickListener { finish() }
        if (type == "2") {
            tvRightBtn.text = getString(R.string.delete_btn)
            tvRightBtn.visibility = View.VISIBLE
            tvRightBtn.setOnClickListener {
                deleteMistake()
            }
        }
    }

    private fun deleteMistake() {
        val map = HashMap<String, String>()
        map["id"] = mMistake!!.id.toString()
        mViewModel?.freshDeleteMistake(map, true)
    }

    private fun initMistake() {
        tvMistakeDetail.text = mMistake!!.content
        tvMistakeDate.text = String.format(
            "%s %s",
            getString(R.string.add_date), mMistake!!.create_time
        )
        tvAnswer.text = mMistake!!.answer
        if (mMistake!!.remark != null && mMistake!!.remark != "") {
            tvRemark.text = mMistake!!.remark
        } else {
            tvRemark.visibility = View.GONE
        }

        mRecyclerViewMistake.layoutManager = GridLayoutManager(this, 3)
        mRecyclerViewMistake.adapter = HomeWorkImagesAdapter(mMistake!!.contentImgs, 1, true, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val intent = Intent(this@MistakeDetailActivity, ScanPicActivity::class.java)
                intent.putExtra("pics", arg2 as Serializable)
                intent.putExtra("position", arg1)
                startActivity(intent)
            }
        })

        mRecyclerViewAnswer.layoutManager = GridLayoutManager(this, 3)
        mRecyclerViewAnswer.adapter = HomeWorkImagesAdapter(mMistake!!.answerImgs, 1, true, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val intent = Intent(this@MistakeDetailActivity, ScanPicActivity::class.java)
                intent.putExtra("pics", arg2 as Serializable)
                intent.putExtra("position", arg1)
                startActivity(intent)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(MistakesViewModel::class.java)
        if (!mViewModel!!.deleteMistake()!!.hasObservers()) {
            mViewModel!!.deleteMistake()!!.observe(this, Observer {
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
                        mViewModel!!.freshDeleteMistake(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshDeleteMistake(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshDeleteMistake(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            RxBus.get().post("freshMistakes", "")
                            finish()
                        } else {
                            showToastMsg(getString(R.string.delete_failed))
                        }
                    }
                }
            })
        }
    }
}
