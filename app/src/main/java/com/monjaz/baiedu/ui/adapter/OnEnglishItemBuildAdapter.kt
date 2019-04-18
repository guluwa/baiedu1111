package com.monjaz.baiedu.ui.adapter

import android.text.TextUtils
import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.listener.OnItemBuildAdapter


/**
 * 英文的课程文本设置
 */
class OnEnglishItemBuildAdapter : OnItemBuildAdapter() {

    override fun getItemText(schedule: Schedule?, isThisWeek: Boolean): String {
        if (schedule == null || TextUtils.isEmpty(schedule.name)) return "Unknow"
        if (schedule.teacher == null) {
            return if (!isThisWeek) "[Non]" + schedule.name else schedule.name
        }

        var r = schedule.name
        if (!isThisWeek) {
            r = "[Non]$r"
        }
        return r
    }
}