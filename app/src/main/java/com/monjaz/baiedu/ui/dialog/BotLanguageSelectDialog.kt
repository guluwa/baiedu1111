package com.monjaz.baiedu.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.ui.listener.OnClickListener

class BotLanguageSelectDialog (context: Context, themeResId: Int, private val listener: OnClickListener) :
        Dialog(context, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bot_select_language_dialog)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            dismiss()
        }
        findViewById<TextView>(R.id.tvEnglish).setOnClickListener {
            listener.click(1,"")
            dismiss()
        }
        findViewById<TextView>(R.id.tvArabic).setOnClickListener {
            listener.click(2,"")
            dismiss()
        }

        window!!.setGravity(Gravity.BOTTOM)
        window!!.setWindowAnimations(R.style.dialog_slide_anim)
        setCanceledOnTouchOutside(true)

        //设置dialog没有边距
        window!!.decorView.setPadding(0, 0, 0, 0)
        val lp = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = lp
    }
}