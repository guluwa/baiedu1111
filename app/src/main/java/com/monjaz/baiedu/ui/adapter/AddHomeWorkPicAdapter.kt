package com.monjaz.baiedu.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.monjaz.baiedu.R
import com.monjaz.baiedu.databinding.AddHomeWordPicItemBinding
import com.monjaz.baiedu.ui.listener.OnClickListener

class AddHomeWorkPicAdapter(val list: MutableList<String>,
                            private val listener: OnClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun addData(pics: List<String>) {
        if (list.size != 9) {
            list.removeAt(list.size - 1)
        }
        val count = 9 - list.size
        if (count > 0) {
            if (pics.size > count) {
                list.addAll(pics.subList(0, count))
            } else {
                list.addAll(pics)
            }
        }
        if (list.size < 9) {
            list.add("")
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.add_home_word_pic_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        if (list[position] == "") {
            holder.databinding.ivSelectPic.setImageResource(R.drawable.ic_select_pic)
            holder.databinding.ivDeletePic.visibility = View.GONE
        } else {
            Glide.with(holder.itemView).asBitmap()
                    .apply(RequestOptions().centerCrop())
                    .load(list[position])
                    .into(holder.databinding.ivSelectPic)
            holder.databinding.ivDeletePic.visibility = View.VISIBLE
        }
    }

    inner class ViewHolder(val databinding: AddHomeWordPicItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {

            databinding.ivSelectPic.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(adapterPosition, list[adapterPosition])
                }
            }

            databinding.ivDeletePic.setOnClickListener {
                if (adapterPosition != -1) {
                    if (list.size == 9 && list[8] != "") {
                        list.removeAt(adapterPosition)
                        list.add("")
                    } else {
                        list.removeAt(adapterPosition)
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }
}