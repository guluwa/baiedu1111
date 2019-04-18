package com.monjaz.baiedu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.ClassInfoBean
import com.monjaz.baiedu.databinding.ClassStudentItemBinding

class ClassStudentListAdapter(var list: List<ClassInfoBean.StudentListBean>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.class_student_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).databinding.tvClassStudentName.text = list[position].studentname
        holder.databinding.tvClassStudentNum.text = list[position].student_id.toString()
        holder.databinding.tvClassStudentSex.text = if (list[position].sex == 1) holder.itemView.context.getString(R.string.male)
                else holder.itemView.context.getString(R.string.female)
    }

    inner class ViewHolder(val databinding: ClassStudentItemBinding) : RecyclerView.ViewHolder(databinding.root)
}