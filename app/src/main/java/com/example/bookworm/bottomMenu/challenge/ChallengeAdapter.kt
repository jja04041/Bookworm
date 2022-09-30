package com.example.bookworm.bottomMenu.challenge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.DdayCounter
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.databinding.LayoutChallengeItemBinding
import com.example.bookworm.databinding.LayoutItemLoadingBinding
import com.github.ybq.android.spinkit.style.Circle


/** 챌린지 객체를 리사이클러뷰에 표시할 수 있는 아이템으로 변환하여 리사이클러뷰와 연결 시켜주는 어댑터
 * */
class ChallengeAdapter(val context: Context) : ListAdapter<Challenge, RecyclerView.ViewHolder>(Companion) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            0 -> {
                LoadingItemViewHolder(inflater.inflate(R.layout.layout_item_loading, parent, false))
            }
            else -> {
                ChallengeItemViewHolder(inflater.inflate(R.layout.layout_challenge_item, parent, false))

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val safetPos = holder.bindingAdapterPosition
        when (holder) {
            is ChallengeItemViewHolder -> {
                holder.bindItem(currentList[safetPos])
            }
            else -> {
                showLoadingView(holder as LoadingItemViewHolder, safetPos)
            }
        }
    }

    /**
     * 챌린지 아이템을 그려주는 뷰홀더
     * */
    private inner class ChallengeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by lazy {
            LayoutChallengeItemBinding.bind(itemView)
        }

        fun bindItem(challenge: Challenge) {
            binding.apply {
                challenge.apply {
                    //책정보를 화면에 띄워준다.
                    book.apply {
                        tvBookTitle.text = title
                        Glide.with(context)
                                .load(imgUrl)
                                .into(ivThumb)
                    }
                    tvPerson.text = currentPart.size.toString() //현재 참여자 수
                    tvDday.text = DdayCounter(challenge.endDate).dDayByDash //디데이 설정
                    tvChallengeStartDate.text = challenge.startDate.substring(5) //시작 일자
                    tvChallengeEndDate.text = challenge.endDate.substring(5) //마감 일자
                    tvCtitle.text = challenge.title
                }
                //챌린지 아이템 선택 감지
                root.setOnClickListener {
                    val pos = bindingAdapterPosition

                }
            }
        }
    }

    /** 챌린지 아이템 로딩 시 로딩중을 표시해 주는 뷰홀더
     * */
    private inner class LoadingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    //로딩뷰를 보여주는 함수
    private fun showLoadingView(viewHolder: LoadingItemViewHolder, pos: Int) {
        LayoutItemLoadingBinding.bind(viewHolder.itemView).apply {
            progressBar.setIndeterminateDrawable(
                    Circle().apply {
                        animationDelay = 0
                    })
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].id == "") 0 //로딩 뷰홀더
        else 1 //챌린지 뷰 홀더
    }

    //각각의 아이템을 독자의 객체로 형성할 수 있도록 아이디를 세팅함.
    override fun getItemId(position: Int): Long {
        return currentList[position].hashCode().toLong()
    }

    /** 기존에 리사이클러 뷰에 있는 데이터와 비교하여, 새로운 데이터가 아닌 경우, 별다른 업데이트를 하지 않도록 함.
     * */
    companion object : DiffUtil.ItemCallback<Challenge>() {

        override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge) =
                oldItem == newItem

        override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
            return oldItem.id == newItem.id
        }
    }
}