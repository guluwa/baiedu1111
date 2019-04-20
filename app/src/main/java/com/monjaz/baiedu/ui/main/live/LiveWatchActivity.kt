package com.monjaz.baiedu.ui.main.live

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import com.pili.pldroid.player.PLOnBufferingUpdateListener
import com.pili.pldroid.player.PLOnCompletionListener
import com.pili.pldroid.player.PLOnErrorListener
import com.pili.pldroid.player.PLOnInfoListener
import com.pili.pldroid.player.widget.PLVideoView
import kotlinx.android.synthetic.main.activity_live_watch.*

class LiveWatchActivity : BaseActivity() {

    private val TAG = LiveWatchActivity::class.java.simpleName

    override val viewLayoutId: Int get() = R.layout.activity_live_watch

    override fun initViews() {
        initVideoPlayer()
        tvPlay.setOnClickListener {
            if (!TextUtils.isEmpty(etLiveUrl.text)) {
                mVideoPlayer.setVideoPath(etLiveUrl.text.toString().trim())
                mVideoPlayer.start()
            }
        }
    }

    private fun initVideoPlayer() {
        mVideoPlayer.displayAspectRatio = PLVideoView.ASPECT_RATIO_ORIGIN
        mVideoPlayer.setOnInfoListener(mOnInfoListener)
        mVideoPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener)
        mVideoPlayer.setOnCompletionListener(mOnCompletionListener)
        mVideoPlayer.setOnErrorListener(mOnErrorListener)
    }

    override fun onResume() {
        super.onResume()
        mVideoPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mVideoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoPlayer.stopPlayback()
    }

    private val mOnInfoListener = PLOnInfoListener { what, extra ->
        Log.i(TAG, "OnInfo, what = $what, extra = $extra")
        when (what) {
            PLOnInfoListener.MEDIA_INFO_BUFFERING_START -> {
            }
            PLOnInfoListener.MEDIA_INFO_BUFFERING_END -> {
            }
            PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START -> {
                showToastMsg("first video render time: " + extra + "ms")
                Log.i(TAG, "Response: " + mVideoPlayer.getResponseInfo())
            }
            PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START -> {
            }
            PLOnInfoListener.MEDIA_INFO_VIDEO_FRAME_RENDERING -> Log.i(TAG, "video frame rendering, ts = $extra")
            PLOnInfoListener.MEDIA_INFO_AUDIO_FRAME_RENDERING -> Log.i(TAG, "audio frame rendering, ts = $extra")
            PLOnInfoListener.MEDIA_INFO_VIDEO_GOP_TIME -> Log.i(TAG, "Gop Time: $extra")
            PLOnInfoListener.MEDIA_INFO_SWITCHING_SW_DECODE -> Log.i(TAG, "Hardware decoding failure, switching software decoding!")
            PLOnInfoListener.MEDIA_INFO_METADATA -> Log.i(TAG, mVideoPlayer.getMetadata().toString())
            PLOnInfoListener.MEDIA_INFO_VIDEO_BITRATE, PLOnInfoListener.MEDIA_INFO_VIDEO_FPS -> updateStatInfo()
            PLOnInfoListener.MEDIA_INFO_CONNECTED -> Log.i(TAG, "Connected !")
            PLOnInfoListener.MEDIA_INFO_VIDEO_ROTATION_CHANGED -> Log.i(TAG, "Rotation changed: $extra")
            PLOnInfoListener.MEDIA_INFO_LOOP_DONE -> Log.i(TAG, "Loop done")
            PLOnInfoListener.MEDIA_INFO_CACHE_DOWN -> Log.i(TAG, "Cache done")
            PLOnInfoListener.MEDIA_INFO_STATE_CHANGED_PAUSED -> Log.i(TAG, "State paused")
            PLOnInfoListener.MEDIA_INFO_STATE_CHANGED_RELEASED -> Log.i(TAG, "State released")
            else -> {
            }
        }
    }
    private val mOnErrorListener = PLOnErrorListener { errorCode ->
        Log.e(TAG, "Error happened, errorCode = $errorCode")
        when (errorCode) {
            PLOnErrorListener.ERROR_CODE_IO_ERROR -> {
                /**
                 * SDK will do reconnecting automatically
                 */
                /**
                 * SDK will do reconnecting automatically
                 */
                Log.e(TAG, "IO Error!")
                return@PLOnErrorListener false
            }
            PLOnErrorListener.ERROR_CODE_OPEN_FAILED -> showToastMsg("failed to open player !")
            PLOnErrorListener.ERROR_CODE_SEEK_FAILED -> {
                showToastMsg("failed to seek !")
                return@PLOnErrorListener true
            }
            PLOnErrorListener.ERROR_CODE_CACHE_FAILED -> showToastMsg("failed to cache url !")
            else -> showToastMsg("unknown error !")
        }
        finish()
        true
    }

    private val mOnCompletionListener = PLOnCompletionListener {
        Log.i(TAG, "Play Completed !")
        showToastMsg("Play Completed !")
        //finish();
    }

    private val mOnBufferingUpdateListener = PLOnBufferingUpdateListener { precent -> Log.i(TAG, "onBufferingUpdate: $precent") }

    private fun updateStatInfo() {
        val bitrate = mVideoPlayer.videoBitrate / 1024
        val stat = "${bitrate}kbps, " + mVideoPlayer.videoFps + "fps"
    }
}
