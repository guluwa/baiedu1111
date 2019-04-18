package com.monjaz.baiedu.ui.main.live

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import kotlinx.android.synthetic.main.activity_live_watch.*

class LiveWatchActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_live_watch

    override fun initViews() {
        initVideoPlayer()
    }

    private fun initVideoPlayer() {
        mVideoPlayer.setUp("", "", JzvdStd.SCREEN_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        Jzvd.resetAllVideos()
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }
}
