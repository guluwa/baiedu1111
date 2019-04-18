package com.monjaz.baiedu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.local.MainMenuBean
import com.monjaz.baiedu.databinding.ActMainMenuItemBinding
import com.monjaz.baiedu.ui.listener.OnClickListener

class MainMenuListAdapter(val list: MutableList<MainMenuBean>, private val listener: OnClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.act_main_menu_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        (holder as ViewHolder).databinding.mMenuItemContainer.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context,item.color))
        holder.databinding.tvMenuTitle.text = item.title
        holder.databinding.tvMenuDisc.text = item.explain
        holder.databinding.ivMenu.setImageResource(item.pic)
    }

    inner class ViewHolder(val databinding: ActMainMenuItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {
            databinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(1, list[adapterPosition])
                }
            }
        }
    }
}