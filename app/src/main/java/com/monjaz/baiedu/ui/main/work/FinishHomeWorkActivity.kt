package com.monjaz.baiedu.ui.main.work

import android.content.Intent
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing.model.entity.impl.ImageMedia
import com.bilibili.boxing.utils.ImageCompressor
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.hwangjr.rxbus.RxBus
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.ui.adapter.AddHomeWorkPicAdapter
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.HomeWorkViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_finish_home_work.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class FinishHomeWorkActivity : BaseActivity() {

    private val REQUEST_CODE = 101

    private var selectPosition = -1

    private var mHomeWorkId = 0

    private var mImagePaths = mutableListOf<String>()

    private var isFinish = false

    private var mViewModel: HomeWorkViewModel? = null

    override val viewLayoutId: Int get() = R.layout.activity_finish_home_work

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
    }

    private fun initData() {
        if (intent.getIntExtra("homeWorkId", 0) == 0) {
            showToastMsg(getString(R.string.data_error))
            finish()
        } else {
            mHomeWorkId = intent.getIntExtra("homeWorkId", 0)
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.do_homework)
        ivBack.setOnClickListener { finish() }
        tvRightBtn.text = getString(R.string.do_btn)
        tvRightBtn.visibility = View.VISIBLE
        tvRightBtn.setOnClickListener {
            finishHomeWork()
        }
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = object : GridLayoutManager(this, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mRecyclerView.adapter = AddHomeWorkPicAdapter(arrayListOf(""), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                selectPosition = arg1
                val videoConfig = BoxingConfig(BoxingConfig.Mode.MULTI_IMG).needCamera(R.drawable.camera_white_icon)
                Boxing.of(videoConfig)
                        .withIntent(this@FinishHomeWorkActivity, BoxingActivity::class.java)
                        .start(this@FinishHomeWorkActivity, REQUEST_CODE)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(HomeWorkViewModel::class.java)
        if (!mViewModel!!.uploadPicBase64()!!.hasObservers()) {
            mViewModel!!.uploadPicBase64()!!.observe(this, Observer {
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
                        mViewModel!!.freshUploadPicBase64(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadPicBase64(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        mViewModel!!.freshUploadPicBase64(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            mImagePaths.add(it.data.data!!.url)
                            if (mImagePaths.size == mImageNumber) {
                                val map = HashMap<String, String>()
                                map["taskid"] = mHomeWorkId.toString()
                                map["content"] = etHomeWorkResultContent.text.toString().trim()
                                var images = ""
                                mImagePaths.forEach { arg ->
                                    images += "$arg,"
                                }
                                map["images"] = images.substring(0, images.length - 1)
                                mViewModel?.freshUploadHomeWorkResult(map, true)
                            } else {
                                mCurrentImageIndex++
                                uploadImage()
                            }
                        } else {
                            dismissProgressDialog()
                            showToastMsg(getString(R.string.avatar_upload_failed))
                        }
                    }
                }
            })
            mViewModel!!.uploadHomeWorkResult()!!.observe(this, Observer {
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
                        mViewModel!!.freshUploadHomeWorkResult(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadHomeWorkResult(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadHomeWorkResult(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.do_success))
                            isFinish = true
                            onBackPressed()
                        } else {
                            showToastMsg(getString(R.string.do_failed))
                        }
                    }
                }
            })
        }
    }

    private fun finishHomeWork() {
        if (TextUtils.isEmpty(etHomeWorkResultContent.text)) {
            showToastMsg(getString(R.string.edit_home_work_result_hint))
            return
        }
        if ((mRecyclerView.adapter as AddHomeWorkPicAdapter).list.size != 1) {
            mImagePaths.clear()
            uploadImages()
            return
        }
        val map = HashMap<String, String>()
        map["taskid"] = mHomeWorkId.toString()
        map["content"] = etHomeWorkResultContent.text.toString().trim()
        mViewModel?.freshUploadHomeWorkResult(map, true)
    }

    private var mImageNumber = 0

    private var mCurrentImageIndex = 0

    private fun uploadImages() {
        mImageNumber = if ((mRecyclerView.adapter as AddHomeWorkPicAdapter).list.size == 9) {
            9
        } else {
            (mRecyclerView.adapter as AddHomeWorkPicAdapter).list.size - 1
        }
        mCurrentImageIndex = 0
        uploadImage()
    }

    private fun uploadImage() {
        val url = (mRecyclerView.adapter as AddHomeWorkPicAdapter).list[mCurrentImageIndex]
        if (url != "") uploadPicBase64(url)
    }

    private fun uploadPicBase64(path: String) {
        val map = HashMap<String, String>()
        map["img"] = "data:image/jpg;base64," + AppUtils.encodeImage(BitmapFactory.decodeFile(path))
        mViewModel?.freshUploadPicBase64(map, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            return
        }
        if (data == null) {
            return
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                val medias = Boxing.getResult(data)
                if (medias != null) {
                    mImagePaths.clear()
                    medias.forEach {
                        (it as ImageMedia).compress(ImageCompressor(this))
                        if (it.compressPath != null) {
                            mImagePaths.add(it.compressPath)
                        }
                    }
                    (mRecyclerView.adapter as AddHomeWorkPicAdapter).addData(mImagePaths)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isFinish) {
            RxBus.get().post("freshHomeWorksResult", "")
        }
        super.onBackPressed()
    }
}