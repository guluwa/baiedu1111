package com.monjaz.baiedu.data.bean.remote

import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleEnable
import java.util.ArrayList

class ClassLessonItemBean : ScheduleEnable {

    var id = 0

    var teacherId = 0

    /**
     * 课程名
     */
    var name = ""

    /**
     * 教室
     */
    var room = ""

    /**
     * 教师
     */
    var teacher = ""

    /**
     * 第几周至第几周上
     */
    var weekList: List<Int> = ArrayList()

    /**
     * 开始上课的节次
     */
    var start = 0

    /**
     * 上课节数
     */
    var step = 0

    /**
     * 周几上
     */
    var day = 0

    /**
     * 上课时间
     */
    var time = ""

    /**
     * 一个随机数，用于对应课程的颜色
     */
    var colorRandom = 0


    override fun getSchedule(): Schedule {
        val item = Schedule()
        item.day = day
        item.name = name
        item.room = room
        item.start = start
        item.step = step
        item.teacher = teacher
        item.weekList = weekList
        item.colorRandom = 2
        return  item
    }
}