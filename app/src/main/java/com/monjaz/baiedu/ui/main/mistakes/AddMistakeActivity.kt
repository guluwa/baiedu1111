package com.monjaz.baiedu.ui.main.mistakes

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
import com.monjaz.baiedu.ui.viewmodel.MistakesViewModel
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_add_mistake.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*

class AddMistakeActivity : BaseActivity() {

    private var mMistakeBookId = 0

    private val REQUEST_CODE = 101

    private var selectPosition = -1

    private var type = 1

    private var mImagePaths = mutableListOf<String>()

    private var isAdd = false

    private var mMistakeString = ""

    private var mAnswerString = ""

    private var mImageNumber = 0

    private var mCurrentImageIndex = 0

    private var mUploadType = 0

    private var mViewModel: MistakesViewModel? = null

    override val viewLayoutId: Int get() = R.layout.activity_add_mistake

    override fun initViews() {
        initData()
        initToolBar()
        initRecyclerView()
    }

    private fun initData() {
        if (intent.getIntExtra("mistakeBookId", 0) != 0) {
            mMistakeBookId = intent.getIntExtra("mistakeBookId", 0)
        } else {
            showToastMsg(getString(R.string.data_error))
            return
        }
    }

    private fun initToolBar() {
        tvToolBarTitle.text = getString(R.string.add_mistake)
        ivBack.setOnClickListener { onBackPressed() }
        tvRightBtn.text = getString(R.string.finish)
        tvRightBtn.visibility = View.VISIBLE
        tvRightBtn.setOnClickListener {
            addMistake()
        }
    }

    private fun initRecyclerView() {
        // mistake
        mRecyclerViewMistake.layoutManager = object : GridLayoutManager(this, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mRecyclerViewMistake.adapter = AddHomeWorkPicAdapter(arrayListOf(""), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                selectPosition = arg1
                type = 1
                val videoConfig = BoxingConfig(BoxingConfig.Mode.MULTI_IMG).needCamera(R.drawable.camera_white_icon)
                Boxing.of(videoConfig)
                        .withIntent(this@AddMistakeActivity, BoxingActivity::class.java)
                        .start(this@AddMistakeActivity, REQUEST_CODE)
            }
        })
        // answer
        mRecyclerViewMistakeAnswer.layoutManager = object : GridLayoutManager(this, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mRecyclerViewMistakeAnswer.adapter = AddHomeWorkPicAdapter(arrayListOf(""), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                selectPosition = arg1
                type = 2
                val videoConfig = BoxingConfig(BoxingConfig.Mode.MULTI_IMG).needCamera(R.drawable.camera_white_icon)
                Boxing.of(videoConfig)
                        .withIntent(this@AddMistakeActivity, BoxingActivity::class.java)
                        .start(this@AddMistakeActivity, REQUEST_CODE)
            }
        })
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(MistakesViewModel::class.java)
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
                                // 拼接
                                var images = ""
                                mImagePaths.forEach { arg ->
                                    images += "$arg,"
                                }
                                // 保存
                                if (mUploadType == 1) {
                                    mMistakeString = images.substring(0, images.length - 1)
                                    addMistake()
                                } else if (mUploadType == 2) {
                                    mAnswerString = images.substring(0, images.length - 1)
                                    addMistake()
                                }
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
            mViewModel!!.addMistake()!!.observe(this, Observer {
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
                        mViewModel!!.freshAddMistake(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshAddMistake(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshAddMistake(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.add_success))
                            isAdd = true
                            onBackPressed()
                        } else {
                            showToastMsg(getString(R.string.add_failed))
                        }
                    }
                }
            })
        }
    }

    private fun addMistake() {
        if (TextUtils.isEmpty(etMistakeContent.text) &&
                (mRecyclerViewMistake.adapter as AddHomeWorkPicAdapter).list.size == 1) {
            showToastMsg(getString(R.string.edit_mistake_content_hint))
            return
        }
        if (TextUtils.isEmpty(etMistakeAnswerContent.text) &&
                (mRecyclerViewMistakeAnswer.adapter as AddHomeWorkPicAdapter).list.size == 1) {
            showToastMsg(getString(R.string.edit_mistake_answer_content_hint))
            return
        }
        if ((mRecyclerViewMistake.adapter as AddHomeWorkPicAdapter).list.size != 1 &&
                mMistakeString == "") {
            mImagePaths.clear()
            mUploadType = 1
            uploadImages()
            return
        }
        if ((mRecyclerViewMistakeAnswer.adapter as AddHomeWorkPicAdapter).list.size != 1 &&
                mAnswerString == "") {
            mImagePaths.clear()
            mUploadType = 2
            uploadImages()
            return
        }
        val map = HashMap<String, String>()
        map["book_id"] = mMistakeBookId.toString()
        if (!TextUtils.isEmpty(etMistakeContent.text)) {
            map["content"] = etMistakeContent.text.toString().trim()
        }
        if (mMistakeString != "") {
            map["content_imgs"] = mMistakeString
        }
        if (!TextUtils.isEmpty(etMistakeAnswerContent.text)) {
            map["answer"] = etMistakeAnswerContent.text.toString().trim()
        }
        if (mAnswerString != "") {
            map["answer_img"] = mAnswerString
        }
        if (!TextUtils.isEmpty(etMistakeRemarkContent.text)) {
            map["remark"] = etMistakeRemarkContent.text.toString().trim()
        }
        mViewModel?.freshAddMistake(map, true)
    }

    private fun uploadImages() {
        if (mUploadType == 1) {
            mImageNumber = if ((mRecyclerViewMistake.adapter as AddHomeWorkPicAdapter).list.size == 9) {
                9
            } else {
                (mRecyclerViewMistake.adapter as AddHomeWorkPicAdapter).list.size - 1
            }
        } else if (mUploadType == 2) {
            mImageNumber = if ((mRecyclerViewMistakeAnswer.adapter as AddHomeWorkPicAdapter).list.size == 9) {
                9
            } else {
                (mRecyclerViewMistakeAnswer.adapter as AddHomeWorkPicAdapter).list.size - 1
            }
        }
        uploadImage()
    }

    private fun uploadImage() {
        if (mUploadType == 1) {
            val url = (mRecyclerViewMistake.adapter as AddHomeWorkPicAdapter).list[mCurrentImageIndex]
            if (url != "") uploadPicBase64(url)
        } else if (mUploadType == 2) {
            val url = (mRecyclerViewMistakeAnswer.adapter as AddHomeWorkPicAdapter).list[mCurrentImageIndex]
            if (url != "") uploadPicBase64(url)
        }
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
                    if (type == 1) {
                        (mRecyclerViewMistake.adapter as AddHomeWorkPicAdapter).addData(mImagePaths)
                    } else {
                        (mRecyclerViewMistakeAnswer.adapter as AddHomeWorkPicAdapter).addData(mImagePaths)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isAdd) {
            RxBus.get().post("freshMistakes", "")
        }
        super.onBackPressed()
    }
}
