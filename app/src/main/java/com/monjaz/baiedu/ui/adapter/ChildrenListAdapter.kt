package com.monjaz.baiedu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.ChildBean
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.databinding.ChildListItemBinding
import com.monjaz.baiedu.ui.listener.OnClickListener

class ChildrenListAdapter(val list: MutableList<Any>, private val listener: OnClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setData(data: List<ChildBean>) {
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
                            LayoutInflater.from(parent.context), R.layout.child_list_item, parent, false
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
            val item = list[position] as ChildBean
            (holder as ViewHolder).databinding.tvChildName.text = String.format("%s %s", item.name,
                    if (item.sex == 1) {
                        holder.itemView.context.getString(R.string.male)
                    } else {
                        holder.itemView.context.getString(R.string.female)
                    })
            holder.databinding.tvChildId.text = item.student_id.toString()
            Glide.with(holder.itemView.context).asBitmap()
                    .apply(RequestOptions().circleCrop().placeholder(R.drawable.ic_class_student))
                    .load(item.faceimg)
                    .into(holder.databinding.ivChild)
        }
    }

    inner class ViewHolder(val databinding: ChildListItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {
            databinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(1, list[adapterPosition])
                }
            }
        }
    }
}