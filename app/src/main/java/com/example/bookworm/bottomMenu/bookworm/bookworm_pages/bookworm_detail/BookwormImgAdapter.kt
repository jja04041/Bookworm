package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R


//사용자의 데이터를 받아서 BookWorm 이미지 뷰의 데이터를 세팅한다.
//요구 조건
//1. 전체 책볼레의 데이터가 필요함.
//2. 해당 책볼레를 얻었는지에 대한 데이터가 필요함 => 주기적인 업데이트
//3.

//루틴
//1. 전체 책볼레의 데이터를 어답터는 전달받는다.
//2. 해당 데이터가

class BookwormImgAdapter(val parentView: View, val context: Context) :
    ListAdapter<BookwormData, RecyclerView.ViewHolder>(MyDiffCallback) {
    var mItemClickListener: OnItemClickEventListener? = null

    interface OnItemClickEventListener {
        fun onItemClick(a_view: View?, a_position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        val selectedView: View
        when (viewType) {
            1 -> {
                view = inflater.inflate(R.layout.fragment_bw_item, parent, false)
                return HasBookwormViewHolder(view, mItemClickListener!!, context)
            }
            else -> {
                view = inflater.inflate(R.layout.fragment_bw_item, parent, false)
                return NotHasBookwormViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val safePosition = holder.bindingAdapterPosition
        if (holder is HasBookwormViewHolder) {
            holder.setItems(currentList[safePosition])
        }
    }

    //해당 데이터가 있는 경우에만 보여준다.
    override fun getItemViewType(pos: Int): Int {
        return if (currentList[pos].hasBw) 1 else 2
    }

    fun setOnItemClickListener(a_listener: OnItemClickEventListener) {
        mItemClickListener = a_listener
    }

    //값 업데이트를 위한 비교 콜백
    object MyDiffCallback : DiffUtil.ItemCallback<BookwormData>() {

        override fun areItemsTheSame(oldItem: BookwormData, newItem: BookwormData): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: BookwormData, newItem: BookwormData): Boolean {
            return oldItem == newItem
        }

    }


}