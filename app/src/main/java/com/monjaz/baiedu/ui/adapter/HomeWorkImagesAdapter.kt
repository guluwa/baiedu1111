package com.monjaz.baiedu.ui.adapter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.monjaz.baiedu.R
import com.monjaz.baiedu.databinding.HomeWorkImagesListItemBinding
import com.monjaz.baiedu.databinding.HomeWorkListItemBinding
import com.monjaz.baiedu.ui.listener.OnClickListener

class HomeWorkImagesAdapter(val list: MutableList<String>, val type: Int, val canSee: Boolean, val listener: OnClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setData(data: List<String>) {
        list.clear()
        data.map {
            if (!TextUtils.isEmpty(it)) {
                list.add(it)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.home_work_images_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).asBitmap()
                .apply(RequestOptions().centerCrop().placeholder(R.drawable.ic_pic_place_holder))
                .load(list[position])
                .into((holder as ViewHolder).databinding.ivImage)
    }

    inner class ViewHolder(val databinding: HomeWorkImagesListItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {
            databinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    if (type == 1) {
                        listener.click(adapterPosition, list)
                    } else {
                        if (canSee) {
                            listener.click(adapterPosition, list)
                        } else {
                            listener.click(adapterPosition, list[adapterPosition])
                        }
                    }
                }
            }
        }
    }
}