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

class HomeWorkCorrectionImagesAdapter(val list: MutableList<String>,
                                      private val listener: OnClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun addData(pic: String, position: Int) {
        if (list.size < 9) {
            if (position < list.size) {
                list.removeAt(position)
                list.add(position, pic)
            } else {
                list.add(pic)
            }
            notifyDataSetChanged()
        }
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
                    listener.click(adapterPosition, list)
                }
            }

            databinding.ivDeletePic.setOnClickListener {
                if (adapterPosition != -1) {
                    list.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            }
        }
    }
}