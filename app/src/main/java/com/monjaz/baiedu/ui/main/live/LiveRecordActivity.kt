package com.monjaz.baiedu.ui.main.live

import android.content.pm.ActivityInfo
import android.hardware.Camera
import android.util.Log
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.monjaz.baiedu.utils.AppUtils
import com.qiniu.android.dns.DnsManager
import com.qiniu.android.dns.IResolver
import com.qiniu.android.dns.NetworkInfo
import com.qiniu.android.dns.http.DnspodFree
import com.qiniu.android.dns.local.AndroidDnsServer
import com.qiniu.android.dns.local.Resolver
import com.qiniu.pili.droid.streaming.*
import kotlinx.android.synthetic.main.activity_live_record.*
import java.io.IOException
import java.net.InetAddress
import java.net.URISyntaxException
import java.util.*

import com.qiniu.pili.droid.streaming.AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC

class LiveRecordActivity : BaseActivity(), StreamingStateChangedListener {

    private val TAG = "LiveRecordActivity"

    override val viewLayoutId: Int get() = R.layout.activity_live_record

    private var mLiveUrl = ""

    private var mMediaStreamingManager: MediaStreamingManager? = null

    private var mProfile: StreamingProfile? = null

    override fun initViews() {
        mLiveUrl = intent!!.getStringExtra("liveUrl")
        initLive()
        ivCloseRoom.setOnClickListener {
            finish()
        }
    }

    private fun initLive() {
        mProfile = StreamingProfile()
        try {
            mProfile!!.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                    .setQuicEnable(false)//RMPT or QUIC
                    .setVideoQuality(StreamingProfile.VIDEO_QUALITY_MEDIUM1)
                    .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)//横竖屏
                    .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_720)
                    .setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto)//自适应码率
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
                    .setDnsManager(getMyDnsManager())
                    .setStreamStatusConfig(StreamingProfile.StreamStatusConfig(3))
                    .setSendingBufferProfile(StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, (20 * 1000).toLong()))
                    .publishUrl = mLiveUrl//设置推流地址

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//竖屏

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }


        val setting = CameraStreamingSetting()
        setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK) // 摄像头切换
                .setContinuousFocusModeEnabled(true)//开启对焦
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)//自动对焦
                .setBuiltInFaceBeautyEnabled(true)//开启美颜
                .setFaceBeautySetting(CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.7f))// 磨皮，美白，红润 取值范围为[0.0f, 1.0f]
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
        mMediaStreamingManager = MediaStreamingManager(this, cameraPreview_surfaceView, SW_VIDEO_WITH_SW_AUDIO_CODEC)
        mMediaStreamingManager!!.prepare(setting, mProfile)
        mMediaStreamingManager!!.setStreamingStateListener(this)
        dismissProgressDialog()
    }

    protected fun startStreaming() {
        Thread(Runnable {
            mMediaStreamingManager?.startStreaming()
        }).start()
    }

    override fun onResume() {
        super.onResume()
        mMediaStreamingManager?.resume()
    }

    override fun onPause() {
        super.onPause()
        mMediaStreamingManager?.pause()
    }

    override fun onStateChanged(streamingState: StreamingState?, p1: Any?) {
        when (streamingState) {
            StreamingState.PREPARING -> Log.d(TAG, "onStateChanged: ===>" + "准备")
            StreamingState.READY -> {
                startStreaming()
                Log.d(TAG, "onStateChanged: ===>" + "开始")
            }
            StreamingState.CONNECTING -> Log.d(TAG, "onStateChanged: ===>" + "连接")
            StreamingState.STREAMING -> Log.d(TAG, "onStateChanged: ===>" + "已发送")
            StreamingState.SHUTDOWN -> Log.d(TAG, "onStateChanged: ===>" + "推流结束")
            StreamingState.IOERROR -> Log.d(TAG, "onStateChanged: ===>" + "IO异常")
            StreamingState.SENDING_BUFFER_EMPTY -> Log.d(TAG, "onStateChanged: ===>" + "发送缓冲区为空")
            StreamingState.SENDING_BUFFER_FULL -> Log.d(TAG, "onStateChanged: ===>" + "发送缓冲区满")
            StreamingState.AUDIO_RECORDING_FAIL -> Log.d(TAG, "onStateChanged: ===>" + "录音失败")
            StreamingState.OPEN_CAMERA_FAIL -> Log.d(TAG, "onStateChanged: ===>" + "相机打开失败")
            StreamingState.DISCONNECTED -> Log.d(TAG, "onStateChanged: ===>" + "断开连接")
        }
    }

    private fun getMyDnsManager(): DnsManager {
        var r0: IResolver? = null
        val r1 = DnspodFree()
        val r2 = AndroidDnsServer.defaultResolver()
        try {
            r0 = Resolver(InetAddress.getByName("119.29.29.29"))
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return DnsManager(NetworkInfo.normal, arrayOf<IResolver>(r0!!, r1, r2))
    }
}