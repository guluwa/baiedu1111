package com.monjaz.baiedu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.monjaz.baiedu.R
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.monjaz.baiedu.data.bean.remote.MistakeBean
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.databinding.MistakeListItemBinding
import com.monjaz.baiedu.ui.listener.OnClickListener

class MistakeListAdapter(val list: MutableList<Any>, private val listener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setData(data: List<MistakeBean>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    fun setPageTipBean(bean: PageTipBean) {
        list.clear()
        list.add(bean)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            ViewHolderTip(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), R.layout.page_list_tip_item, parent, false
                )
            )
        } else {
            ViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context), R.layout.mistake_list_item, parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is PageTipBean) {
            0
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            (holder as ViewHolderTip).databinding.pageTipBean = list[position] as PageTipBean
        } else {
            val item = list[position] as MistakeBean
            (holder as ViewHolder).databinding.tvMistakeContent.text = if (item.content == "") {
                holder.itemView.context.getString(R.string.empty)
            } else {
                item.content
            }
            holder.databinding.tvMistakeDate.text = String.format(
                "%s %s",
                holder.itemView.context.getString(R.string.add_date), item.create_time
            )
        }
    }

    inner class ViewHolder(val databinding: MistakeListItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {
            databinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(1, list[adapterPosition])
                }
            }
        }
    }
}