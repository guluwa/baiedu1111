package com.monjaz.baiedu.ui.adapter

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.HomeWorkListBean
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.databinding.HomeWorkListItemBinding
import com.monjaz.baiedu.ui.listener.OnClickListener

class HomeWorkListAdapter(val list: MutableList<Any>, private val listener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setData(data: ArrayList<HomeWorkListBean.DataBean>, page: Int) {
        if (page == 2) list.clear()
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
                    LayoutInflater.from(parent.context), R.layout.home_work_list_item, parent, false
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
            val item = list[position] as HomeWorkListBean.DataBean
            (holder as ViewHolder).databinding.tvHomeWorkDate.text = item.addtime
            holder.databinding.tvHomeWorkTeacher.text = item.teachername
            holder.databinding.tvHomeWorkContent.text = if (item.content == "") {
                holder.itemView.context.getString(R.string.empty)
            } else {
                item.content
            }
        }
    }

    inner class ViewHolder(val databinding: HomeWorkListItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {
            databinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(1, list[adapterPosition])
                }
            }
        }
    }
}