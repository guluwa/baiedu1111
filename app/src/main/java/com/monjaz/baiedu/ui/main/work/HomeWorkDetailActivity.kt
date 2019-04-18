package com.monjaz.baiedu.ui.main.work

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.HomeWorkDetailBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.ui.adapter.AddHomeWorkPicAdapter
import com.monjaz.baiedu.ui.adapter.HomeWorkImagesAdapter
import com.monjaz.baiedu.ui.adapter.HomeWorkResultListAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.HomeWorkViewModel
import com.monjaz.baiedu.utils.AppCache
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_add_home_work.*
import kotlinx.android.synthetic.main.activity_home_work_detail.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import java.io.Serializable

class HomeWorkDetailActivity : BaseActivity() {

    private var mViewModel: HomeWorkViewModel? = null

    private var mHomeWorkId = 0

    private var mChildId = 0

    private var type = "2"

    private var position = -1

    private var mVoicePath = ""

    override val viewLayoutId: Int get() = R.layout.activity_home_work_detail

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
        initClickEvent()
    }

    private fun initClickEvent() {
        ivVoiceRecord.setOnClickListener {
            if (mVoicePath != "") {
                AppCache.getPlayService().stopPlayVoiceAnimation()
                AppCache.getPlayService().play(mVoicePath)
            }
        }
    }

    private fun initData() {
        if (intent.getIntExtra("homeWorkId", 0) == 0) {
            showToastMsg(getString(R.string.data_error))
            finish()
        } else {
            mHomeWorkId = intent.getIntExtra("homeWorkId", 0)
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
        ivBack.setOnClickListener { finish() }
        tvToolBarTitle.text = getString(R.string.homework_details)
    }

    private fun initRightBtn() {
        if (type == "2") {
            tvRightBtn.text = getString(R.string.do_btn)
            tvRightBtn.visibility = View.VISIBLE
            tvRightBtn.setOnClickListener {
                val intent = Intent(this, FinishHomeWorkActivity::class.java)
                intent.putExtra("homeWorkId", mHomeWorkId)
                startActivity(intent)
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerViewImages.layoutManager = GridLayoutManager(this, 3)
        mRecyclerViewImages.adapter = HomeWorkImagesAdapter(arrayListOf(), 1, true, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val intent = Intent(this@HomeWorkDetailActivity, ScanPicActivity::class.java)
                intent.putExtra("pics", arg2 as Serializable)
                intent.putExtra("position", arg1)
                startActivity(intent)
            }
        })

        mRecyclerViewResults.layoutManager = LinearLayoutManager(this)
        mRecyclerViewResults.adapter = HomeWorkResultListAdapter(arrayListOf(), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                position = arg1
                val intent = Intent(this@HomeWorkDetailActivity, CorrectHomeWorkActivity::class.java)
                intent.putExtra("homeWorkResult", arg2 as Serializable)
                startActivity(intent)
            }
        })

        mRecyclerViewResultImages.layoutManager = GridLayoutManager(this, 3)
        mRecyclerViewResultImages.adapter = HomeWorkImagesAdapter(arrayListOf(), 1, true, object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val intent = Intent(this@HomeWorkDetailActivity, ScanPicActivity::class.java)
                intent.putExtra("pics", arg2 as Serializable)
                intent.putExtra("position", arg1)
                startActivity(intent)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(HomeWorkViewModel::class.java)
        if (!mViewModel!!.getHomeWorkDetail()!!.hasObservers()) {
            mViewModel!!.getHomeWorkDetail()!!.observe(this, Observer {
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
                        mViewModel!!.freshGetHomeWorkDetail(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetHomeWorkDetail(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshGetHomeWorkDetail(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showHomeWorkDetail(it.data.data!!)
                        } else {
                            showToastMsg(getString(R.string.query_failed))
                        }
                    }
                }
            })
        }
        loadData()
    }

    private fun showHomeWorkDetail(data: HomeWorkDetailBean) {
        mScrollView.visibility = View.VISIBLE
        tvHomeWorkDetail.text = data.content
        tvHomeWorkDate.text = String.format("%s%s", getString(R.string.publish_date), data.addtime)
        (mRecyclerViewImages.adapter as HomeWorkImagesAdapter).setData(data.images_list)
        tvHomeWorkResults.text = String.format("%s %s", data.tasksubmit.size, getString(R.string.people_have_submitted))

        if (type == "1") {
            mHomeWorkResult.visibility = View.GONE
            (mRecyclerViewResults.adapter as HomeWorkResultListAdapter).setData(data.tasksubmit)
        } else {
            mHomeWorkResult.visibility = View.VISIBLE
            tvHomeWorkResultTitle.text = if (type == "3") {
                getString(R.string.child_homework_result)
            } else {
                getString(R.string.my_homework_result)
            }
            var index = 0
            for (item in data.tasksubmit) {
                // judge child id
                val childId = if (type == "3") {
                    mChildId.toString()
                } else {
                    AppUtils.getString(Contacts.ID, "")
                }
                if (item.studentid.toString() == childId) {
                    tvHomeWorkResult.text = item.content
                    if (item.ispigai == "1") {
                        tvHomeWorkResultCorrection.visibility = View.VISIBLE
                        tvHomeWorkResultCorrection.text = item.pigaicontent
                        tvHomeWorkResultCorrectionDate.visibility = View.VISIBLE
                        tvHomeWorkResultCorrectionDate.text =
                                String.format("%s%s", getString(R.string.correct_date), item.pitai_time)
                        if (item.pigai_media != "" && item.pigai_media != null) {
                            AppCache.getPlayService().setImageView(ivVoiceRecord)
                            ivVoiceRecord.setImageResource(R.drawable.ease_chatto_voice_playing_f3_new)
                            ivVoiceRecord.visibility = View.VISIBLE
                            mVoicePath = item.pigai_media!!
                        } else {
                            ivVoiceRecord.visibility = View.GONE
                        }
                    } else {
                        tvHomeWorkResultCorrection.visibility = View.GONE
                        tvHomeWorkResultCorrectionDate.visibility = View.GONE
                    }
                    tvHomeWorkResultDate.text = String.format("%s%s", getString(R.string.do_date), item.addtime)
                    if (AppUtils.checkArrayEmpty(item.images)) {
                        mRecyclerViewResultImages.visibility = View.GONE
                    } else {
                        (mRecyclerViewResultImages.adapter as HomeWorkImagesAdapter).setData(item.images)
                        mRecyclerViewResultImages.visibility = View.VISIBLE
                    }
                    tvHomeWorkResultDate.visibility = View.VISIBLE
                    break
                }
                index++
            }
            if (index == data.tasksubmit.size) {
                tvHomeWorkResult.text = if (type == "3") {
                    getString(R.string.child_home_work_have_not_submit)
                } else {
                    getString(R.string.home_work_have_not_submit)
                }
                tvHomeWorkResultDate.visibility = View.GONE
                mRecyclerViewResultImages.visibility = View.GONE
                tvHomeWorkResultCorrection.visibility = View.GONE
                tvHomeWorkResultCorrectionDate.visibility = View.GONE
                initRightBtn()
            }
        }
    }

    private fun loadData() {
        val map = HashMap<String, String>()
        map["id"] = mHomeWorkId.toString()
        mViewModel?.freshGetHomeWorkDetail(map, true)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("freshHomeWorksResult"))])
    fun receiveData(fresh: String) {
        loadData()
    }

    override fun onBackPressed() {
        AppCache.getPlayService().stopPlaying()
        super.onBackPressed()
    }
}