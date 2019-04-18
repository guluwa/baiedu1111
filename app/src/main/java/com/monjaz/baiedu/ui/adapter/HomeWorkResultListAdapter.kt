package com.monjaz.baiedu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.HomeWorkDetailBean
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.databinding.HomeWorkResultListItemBinding

class HomeWorkResultListAdapter(val list: MutableList<HomeWorkDetailBean.HomeWorkResultBean>, val listener: OnClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setData(data: List<HomeWorkDetailBean.HomeWorkResultBean>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    fun changeCorrectStatus(position: Int, content: String) {
        if (list.size > position) {
            list[position].ispigai = "1"
            list[position].pigaicontent = content
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.home_work_result_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.itemView.context).asBitmap()
                .apply(RequestOptions().circleCrop().placeholder(R.drawable.ic_class_student))
                .load(item.faceimg)
                .into((holder as ViewHolder).databinding.ivUserImage)
        holder.databinding.tvUserName.text = item.studentname
        holder.databinding.tvUploadDate.text = item.addtime
        holder.databinding.tvHomeWorkResultStatus.text = if (item.ispigai == "0") {
            holder.itemView.context.getString(R.string.uncorrected)
        } else {
            holder.itemView.context.getString(R.string.corrected)
        }
        holder.databinding.tvHomeWorkResultContent.text = item.content
        holder.databinding.tvUserIndex.text = item.studentid.toString()
    }

    inner class ViewHolder(val databinding: HomeWorkResultListItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {
            databinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(adapterPosition, list[adapterPosition])
                }
            }
        }
    }
}