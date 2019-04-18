package com.monjaz.baiedu.ui.main.work

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hwangjr.rxbus.RxBus
import com.ilike.voicerecorder.widget.VoiceRecorderView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.data.bean.remote.HomeWorkDetailBean
import com.monjaz.baiedu.data.bean.remote.PageStatus
import com.monjaz.baiedu.manage.Contacts
import com.monjaz.baiedu.manage.MyApplication
import com.monjaz.baiedu.ui.adapter.HomeWorkCorrectionImagesAdapter
import com.monjaz.baiedu.ui.adapter.HomeWorkImagesAdapter
import com.monjaz.baiedu.ui.dialog.BotVoiceSelectDialog
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.ui.viewmodel.HomeWorkViewModel
import com.monjaz.baiedu.utils.AppCache
import com.monjaz.baiedu.utils.AppUtils
import kotlinx.android.synthetic.main.activity_correct_home_work.*
import kotlinx.android.synthetic.main.common_tool_bar_layout.*
import me.kareluo.imaging.IMGEditActivity
import java.io.File
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

class CorrectHomeWorkActivity : BaseActivity() {

    private var mHomeWorkResult: HomeWorkDetailBean.HomeWorkResultBean? = null

    private var mViewModel: HomeWorkViewModel? = null

    private var type = "2"

    private var mImagePaths = mutableListOf<String>()

    private var isCorrect = false

    private var mImageNumber = 0

    private var mCurrentImageIndex = 0

    private var mImageFile: File? = null

    private var mSelectPicPosition = 0

    private var mVoicePath = ""

    override val viewLayoutId: Int get() = R.layout.activity_correct_home_work

    override fun initViews() {
        initData()
        initToolBar()
        initTextView()
        initRecyclerView()
        initClickEvent()
    }

    private fun initClickEvent() {
        mVoiceRecord.setOnTouchListener { v, event ->
            mVoiceRecorder.onPressToSpeakBtnTouch(v, event) { voiceFilePath, voiceTimeLength ->
                if (voiceFilePath != null) {
                    mListenBtn.visibility = View.VISIBLE
                    mVoicePath = voiceFilePath
                    tvVoiceRecord.text = "${voiceTimeLength}s"
                }
            }
        }

        mListenBtn.setOnClickListener {
            BotVoiceSelectDialog(this, R.style.DialogStyle, object : OnClickListener {
                override fun click(arg1: Int, arg2: Any) {
                    if (arg1 == 1) {
                        if (mVoicePath != "" && AppCache.getPlayService() != null) {
                            AppCache.getPlayService().setImageView(ivVoiceRecord)
                            AppCache.getPlayService().stopPlayVoiceAnimation()
                            AppCache.getPlayService().play(mVoicePath)
                        }
                    } else {
                        mVoicePath = ""
                        mListenBtn.visibility = View.GONE
                        tvVoiceRecord.text = getString(R.string.hold_to_talk)
                        ivVoiceRecord.setImageResource(R.drawable.ic_voice_record)
                    }
                }
            }).show()
        }

        ivVoiceRecordListen.setOnClickListener {
            AppCache.getPlayService().stopPlayVoiceAnimation()
            AppCache.getPlayService().play(mHomeWorkResult!!.pigai_media!!)
        }
    }

    private fun initData() {
        if (intent.getSerializableExtra("homeWorkResult") == null) {
            showToastMsg(getString(R.string.data_error))
            finish()
        } else {
            mHomeWorkResult = intent.getSerializableExtra("homeWorkResult") as HomeWorkDetailBean.HomeWorkResultBean
        }
        type = AppUtils.getString(Contacts.TYPE, "2")
    }

    private fun initToolBar() {
        ivBack.setOnClickListener { onBackPressed() }
        tvToolBarTitle.text = getString(R.string.homework_result)
        if (mHomeWorkResult!!.ispigai == "0" && type == "1") {
            tvRightBtn.text = getString(R.string.correct)
            tvRightBtn.visibility = View.VISIBLE
            tvRightBtn.setOnClickListener {
                correctHomeWork()
            }
        }
    }

    private fun initTextView() {
        tvHomeWorkResult.text = mHomeWorkResult!!.content
        tvHomeWorkResultDate.text = String.format("%s%s", getString(R.string.do_date), mHomeWorkResult!!.addtime)

        if (mHomeWorkResult!!.ispigai == "1") {
            mCorrectionContainer.visibility = View.VISIBLE
            mCorrectionEditTextContainer.visibility = View.GONE

            tvHomeWorkResultCorrection.text = mHomeWorkResult!!.pigaicontent
            tvHomeWorkResultCorrectionDate.text = String.format("%s%s", getString(R.string.correct_date), mHomeWorkResult!!.pitai_time)
            if (mHomeWorkResult!!.pigai_media != "" && mHomeWorkResult!!.pigai_media != null) {
                AppCache.getPlayService().setImageView(ivVoiceRecordListen)
                ivVoiceRecordListen.setImageResource(R.drawable.ease_chatto_voice_playing_f3_new)
                ivVoiceRecordListen.visibility = View.VISIBLE
            } else {
                ivVoiceRecordListen.visibility = View.GONE
            }
        } else {
            mCorrectionContainer.visibility = View.GONE
            mCorrectionEditTextContainer.visibility = View.VISIBLE
            if (AppUtils.checkArrayEmpty(mHomeWorkResult!!.images)) {
                mHomeWorkCorrectionImages.visibility = View.GONE
            } else {
                mHomeWorkCorrectionImages.visibility = View.VISIBLE
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerViewResultImages.layoutManager = GridLayoutManager(this, 3)
        mRecyclerViewResultImages.adapter = HomeWorkImagesAdapter(arrayListOf(), 2,
                !(mHomeWorkResult!!.ispigai == "0" && type == "1"), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                if (mHomeWorkResult!!.ispigai == "0" && type == "1") {
                    mSelectPicPosition = arg1
                    downloadPic(arg2 as String)
                } else {
                    val intent = Intent(this@CorrectHomeWorkActivity, ScanPicActivity::class.java)
                    intent.putExtra("pics", arg2 as Serializable)
                    intent.putExtra("position", arg1)
                    startActivity(intent)
                }
            }
        })
        (mRecyclerViewResultImages.adapter as HomeWorkImagesAdapter).setData(mHomeWorkResult!!.images)

        mRecyclerView.layoutManager = GridLayoutManager(this, 3)
        mRecyclerView.adapter = HomeWorkCorrectionImagesAdapter(arrayListOf(), object : OnClickListener {
            override fun click(arg1: Int, arg2: Any) {
                val intent = Intent(this@CorrectHomeWorkActivity, ScanPicActivity::class.java)
                intent.putExtra("pics", arg2 as Serializable)
                intent.putExtra("position", arg1)
                startActivity(intent)
            }
        })
    }

    private fun downloadPic(url: String) {
        showProgressDialog(getString(R.string.please_wait))
        Glide.with(MyApplication.getContext())
                .asBitmap()
                .load(url)
                .apply(RequestOptions().override(1000, 1000))
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap>,
                            isFirstResource: Boolean
                    ): Boolean {
                        dismissProgressDialog()
                        return false
                    }

                    override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                    ): Boolean {
                        dismissProgressDialog()
                        if (resource != null) {
                            val file = AppUtils.saveBitmap(resource, UUID.randomUUID().toString() + ".jpg", false)
                            editImage(file.absolutePath)
                            return true
                        }
                        return false
                    }
                }).into(ImageView(this))
    }

    private fun editImage(path: String) {
        val uri = Uri.fromFile(File(path))
        mImageFile = File(cacheDir, UUID.randomUUID().toString() + ".jpg")
        startActivityForResult(
                Intent(this, IMGEditActivity::class.java)
                        .putExtra(IMGEditActivity.EXTRA_IMAGE_URI, uri)
                        .putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, mImageFile!!.absolutePath), 10000
        )
    }

    override fun initViewModel() {
        if (mViewModel == null) mViewModel = ViewModelProviders.of(this).get(HomeWorkViewModel::class.java)
        if (!mViewModel!!.getHomeWorkDetail()!!.hasObservers()) {
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
                                if (mVoicePath == "") {
                                    val map = HashMap<String, String>()
                                    map["id"] = mHomeWorkResult!!.id.toString()
                                    map["pigaicontent"] = etHomeWorkCorrectionContent.text.toString().trim()
                                    var images = ""
                                    mImagePaths.forEach { arg ->
                                        images += "$arg,"
                                    }
                                    map["images"] = images.substring(0, images.length - 1)
                                    mViewModel?.freshCorrectHomeWork(map, true)
                                } else {
                                    uploadMedia()
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
            mViewModel!!.correctHomeWork()!!.observe(this, Observer {
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
                        mViewModel!!.freshCorrectHomeWork(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshCorrectHomeWork(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshCorrectHomeWork(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            showToastMsg(getString(R.string.correct_success))
                            isCorrect = true
                            onBackPressed()
                        } else {
                            showToastMsg(getString(R.string.correct_failed))
                        }
                    }
                }
            })
            mViewModel!!.uploadMedia()!!.observe(this, Observer {
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
                        mViewModel!!.freshUploadMedia(hashMapOf(), false)
                        showToastMsg(parseErrorDate(it.error!!.message).tip)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadMedia(hashMapOf(), false)
                        showToastMsg(getString(R.string.data_wrong))
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadMedia(hashMapOf(), false)
                        if (it.data!!.code == 0 && it.data.data != null) {
                            val map = HashMap<String, String>()
                            map["id"] = mHomeWorkResult!!.id.toString()
                            map["pigaicontent"] = etHomeWorkCorrectionContent.text.toString().trim()
                            var images = ""
                            mImagePaths.forEach { arg ->
                                images += "$arg,"
                            }
                            map["images"] = images.substring(0, images.length - 1)
                            map["pigai_media"] = it.data.data!!.url
                            mViewModel?.freshCorrectHomeWork(map, true)
                        } else {
                            showToastMsg(getString(R.string.correct_failed))
                        }
                    }
                }
            })
        }
    }

    private fun correctHomeWork() {
        if ((mRecyclerView.adapter as HomeWorkCorrectionImagesAdapter).list.size !=
                (mRecyclerViewResultImages.adapter as HomeWorkImagesAdapter).list.size
        ) {
            showToastMsg(getString(R.string.plz_correct_all_home_work_pics))
            return
        }
        if (TextUtils.isEmpty(etHomeWorkCorrectionContent.text)) {
            showToastMsg(getString(R.string.edit_home_work_correct_hint))
            return
        }
        if ((mRecyclerView.adapter as HomeWorkCorrectionImagesAdapter).list.size != 0) {
            mImagePaths.clear()
            uploadImages()
            return
        }
        if (mVoicePath != "") {
            uploadMedia()
            return
        }
        val map = HashMap<String, String>()
        map["id"] = mHomeWorkResult!!.id.toString()
        map["pigaicontent"] = etHomeWorkCorrectionContent.text.toString().trim()
        mViewModel?.freshCorrectHomeWork(map, true)
    }

    private fun uploadImages() {
        mImageNumber = (mRecyclerView.adapter as HomeWorkCorrectionImagesAdapter).list.size
        mCurrentImageIndex = 0
        uploadImage()
    }

    private fun uploadImage() {
        val url = (mRecyclerView.adapter as HomeWorkCorrectionImagesAdapter).list[mCurrentImageIndex]
        if (url != "") uploadPicBase64(url)
    }

    private fun uploadPicBase64(path: String) {
        val map = HashMap<String, String>()
        map["img"] = "data:image/jpg;base64," + AppUtils.encodeImage(BitmapFactory.decodeFile(path))
        mViewModel?.freshUploadPicBase64(map, true)
    }

    private fun uploadMedia() {
        val map = HashMap<String, String>()
        map["path"] = mVoicePath
        mViewModel?.freshUploadMedia(map, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10000 && resultCode == -1) {
            if (mImageFile != null) {
                (mRecyclerView.adapter as HomeWorkCorrectionImagesAdapter).addData(
                        mImageFile!!.absolutePath,
                        mSelectPicPosition
                )
            }
        }
    }

    override fun onBackPressed() {
        if (isCorrect) {
            RxBus.get().post("freshHomeWorksResult", etHomeWorkCorrectionContent.text.toString().trim())
        }
        AppCache.getPlayService().stopPlaying()
        super.onBackPressed()
    }
}
