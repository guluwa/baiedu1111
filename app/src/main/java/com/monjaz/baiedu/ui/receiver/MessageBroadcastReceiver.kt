package com.monjaz.baiedu.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import androidx.core.content.ContextCompat.startActivity
import com.monjaz.baiedu.ui.main.main.MainNewActivity
import com.monjaz.baiedu.utils.AppUtils
import com.monjaz.baiedu.utils.FinishActivityManager


class MessageBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TYPE = "type"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val action = intent.action
            val type = intent.getIntExtra(TYPE, -1)

            if (type != -1) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(type)
            }

            if (AppUtils.isBackground(context)) {
                FinishActivityManager.getInstance().finishAllActivity()
                val i = Intent(context, MainNewActivity::class.java)
                i.putExtra("position", 1)
                i.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(context, i, null)
            } else {
                if (FinishActivityManager.getInstance().currentActivity() is MainNewActivity) {
                    (FinishActivityManager.getInstance().currentActivity() as MainNewActivity).goMessagePage()
                } else {
                    FinishActivityManager.getInstance().finishAllActivityExceptMain()
                    if (FinishActivityManager.getInstance().getActivity(MainNewActivity::class.java) != null) {
                        (FinishActivityManager.getInstance().getActivity(MainNewActivity::class.java) as MainNewActivity).goMessagePage()
                    }
                }
            }
        }
    }
}