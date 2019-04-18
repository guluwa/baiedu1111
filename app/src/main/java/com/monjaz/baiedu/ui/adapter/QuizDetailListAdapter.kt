package com.monjaz.baiedu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.monjaz.baiedu.data.bean.remote.PageTipBean
import com.monjaz.baiedu.databinding.QuizDetailListItemBinding
import com.monjaz.baiedu.ui.listener.OnClickListener
import com.monjaz.baiedu.R
import com.monjaz.baiedu.data.bean.remote.QuizListBean

class QuizDetailListAdapter(val type: Int, val list: MutableList<Any>, private val listener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setData(data: List<QuizListBean>) {
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
                    LayoutInflater.from(parent.context), R.layout.quiz_detail_list_item, parent, false
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
            val item = list[position] as QuizListBean
            (holder as ViewHolder).databinding.tvDate.text = item.addtime
            holder.databinding.tvContent.text = item.contents
            if (type == 1) { //received
                holder.databinding.tvUserName.text = item.create_name
                Glide.with(holder.itemView.context).asBitmap()
                    .apply(RequestOptions().circleCrop().placeholder(R.drawable.ic_class_student))
                    .load(item.create_faceimg)
                    .into(holder.databinding.ivUserImage)
            } else { //sent
                holder.databinding.tvUserName.text = item.to_name
                Glide.with(holder.itemView.context).asBitmap()
                    .apply(RequestOptions().circleCrop().placeholder(R.drawable.ic_class_student))
                    .load(item.to_faceimg)
                    .into(holder.databinding.ivUserImage)
            }
        }
    }

    inner class ViewHolder(val databinding: QuizDetailListItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        init {
            databinding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    listener.click(1, list[adapterPosition])
                }
            }
        }
    }
}