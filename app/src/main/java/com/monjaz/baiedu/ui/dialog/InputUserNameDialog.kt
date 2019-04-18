package com.monjaz.baiedu.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.ui.listener.OnClickListener

class InputUserNameDialog(context: Context, themeResId: Int, private val listener: OnClickListener) :
        Dialog(context, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.input_user_name_dialog)
        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tvDialogKnow).setOnClickListener {
            if (TextUtils.isEmpty(findViewById<EditText>(R.id.etDialogContent).text.toString().trim())) {
                listener.click(0, "")
                return@setOnClickListener
            }
            listener.click(0, findViewById<EditText>(R.id.etDialogContent).text.toString().trim())
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