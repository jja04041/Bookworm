package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

//사용자의 데이터를 받아서 BookWorm 이미지 뷰의 데이터를 세팅한다.
//요구 조건
//1. 전체 책볼레의 데이터가 필요함.
//2. 해당 책볼레를 얻었는지에 대한 데이터가 필요함 => 주기적인 업데이트
//3.

//루틴
//1. 전체 책볼레의 데이터를 어답터는 전달받는다.
//2. 해당 데이터가

class BookwormImgAdapter : ListAdapter<Int,RecyclerView.ViewHolder>(MyDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    //값 업데이트를 위한 비교 콜백
    object MyDiffCallback : DiffUtil.ItemCallback<Int>() {

        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem.equals(newItem)
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem==newItem
        }

    }


}