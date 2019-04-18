package com.monjaz.baiedu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.ClassInfoBean
import com.monjaz.baiedu.databinding.ClassListItemBinding
import com.monjaz.baiedu.databinding.ClassTeacherItemBinding
import com.monjaz.baiedu.ui.listener.OnClickListener

class ClassTeacherListAdapter(var list: List<ClassInfoBean.TeacherListBean>, val listener: OnClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.class_teacher_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).databinding.tvClassTeacherSubjectName.text = String.format("%s %s", list[position].teachname, list[position].teachername)
    }

    inner class ViewHolder(val databinding: ClassTeacherItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {
            databinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(1, list[adapterPosition])
                }
            }
        }
    }
}