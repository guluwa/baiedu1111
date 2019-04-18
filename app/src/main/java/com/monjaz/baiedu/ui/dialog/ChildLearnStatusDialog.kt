package com.monjaz.baiedu.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.StudentLearnStatusBean

class ChildLearnStatusDialog(val status: StudentLearnStatusBean, context: Context, themeResId: Int) :
        Dialog(context, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.child_learn_status_dialog)
        initView()
    }

    private fun initView() {
        var info = context.getString(R.string.num_of_homework_times) + " " + status.tasksubmitcount + "\n"
        info += context.getString(R.string.num_of_mistake_books) + " " + status.bookcount + "\n"
        info += context.getString(R.string.num_of_homework_times) + " " + status.booklistcocunt

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