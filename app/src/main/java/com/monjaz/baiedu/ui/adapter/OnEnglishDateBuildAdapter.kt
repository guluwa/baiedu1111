package com.monjaz.baiedu.ui.adapter

import com.monjaz.baiedu.utils.WeekDataUtils
import com.zhuangfei.timetable.listener.OnDateBuildAapter
import com.zhuangfei.timetable.model.ScheduleSupport
import java.util.*


/**
 * 英语日期栏
 */
class OnEnglishDateBuildAdapter : OnDateBuildAapter() {

    override fun getStringArray(): Array<String?> {
        return arrayOf(null, "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
//        return arrayOf(null, "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }

    override fun onHighLight() {
        initDateBackground()

        //获取周几，1->7
        val now = Calendar.getInstance()
        //一周第一天是否为星期天
        val isFirstSunday = now.firstDayOfWeek == Calendar.SUNDAY
        var weekDay = now.get(Calendar.DAY_OF_WEEK)
        //若一周第一天为星期天，则-1
        if (isFirstSunday) {
            weekDay -= 1
            if (weekDay == 0) {
                weekDay = 7
            }
        }

        activeDateBackground(weekDay)
    }

    override fun onUpdateDate(curWeek: Int, targetWeek: Int) {
        if (textViews == null || textViews.size < 8) return

        val monthArray = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec")
        weekDates = WeekDataUtils.getDateStringFromWeek(curWeek, targetWeek)
        val month = Integer.parseInt(weekDates[0])
        textViews[0].text = monthArray[month - 1]
        for (i in 1..7) {
            if (textViews[i] != null) {
                textViews[i].text = weekDates[i]
            }
        }
    }
}