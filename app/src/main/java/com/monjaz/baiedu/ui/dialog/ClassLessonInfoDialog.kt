package com.monjaz.baiedu.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.ClassLessonItemBean
import com.monjaz.baiedu.ui.listener.OnClickListener

class ClassLessonInfoDialog(val lesson: ClassLessonItemBean, context: Context, themeResId: Int) :
    Dialog(context, themeResId) {

    private val date = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_lesson_info_dialog)
        initView()
    }

    private fun initView() {

        findViewById<TextView>(R.id.tvDialogTitle).text = lesson.name
        var info = ""
        info += date[lesson.day-1] + " " + lesson.time + "\n"
        info += lesson.teacher + " " +lesson.room

        findViewById<TextView>(R.id.tvLessonInfo).text = info
        findViewById<TextView>(R.id.tvDialogKnow).setOnClickListener {
            dismiss()
        }

        window!!.setWindowAnimations(R.style.scale_in_alpha_out)
        setCanceledOnTouchOutside(true)

        //设置dialog没有边距
        window!!.decorView.setPadding(0, 0, 0, 0)
        val lp = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = lp
    }
}