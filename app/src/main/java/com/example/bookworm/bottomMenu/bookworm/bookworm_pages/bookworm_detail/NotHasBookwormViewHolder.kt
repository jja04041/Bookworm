package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.databinding.FragmentBwItemBinding

class NotHasBookwormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding: FragmentBwItemBinding? = null

    init {
        binding = FragmentBwItemBinding.bind(itemView)
        binding!!.ivBwImage.setImageResource(R.drawable.ic_baseline_question_mark_24)
        binding!!.tvGenre.setText("??")
    }
}