package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.view

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout.LayoutParams

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.R

import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item.AlbumDisplayAdapter
import com.example.bookworm.databinding.FragmentProfileAlbumItemBinding


//앨범을 보여주는 뷰 홀더
class AlbumDisplayViewHolder(itemView: View, val context: Context,var listener: AlbumDisplayAdapter.OnViewHolderItemClickListener) :
    RecyclerView.ViewHolder(itemView) {
    var binding: FragmentProfileAlbumItemBinding? = FragmentProfileAlbumItemBinding.bind(itemView)
    var CARDVIEWWIDTH: Int = 0
    var CARDVIEWHEIGHT: Int = 0

    init {
        initUI()
    }

    fun setItem(album: AlbumData) {
        //앨범 이름 세팅
        binding!!.albumName.text = album.albumName
        //이미지 세팅
        if (album.thumbnail != null) Glide.with(itemView).load(album.thumbnail)
            .into(binding!!.albumThumb)
        else binding!!.albumThumb.setImageResource(R.color.black)
        itemView.setOnClickListener {
            val position = bindingAdapterPosition
            if(position!=RecyclerView.NO_POSITION){
                listener.onViewHolderItemClick(itemView,position)
            }
        }
    }

    //픽셀을 DP로 변환
    fun convertPX2DP(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        context.getResources().getDisplayMetrics()
    ).toInt()

    //UI를 일부 수정한다.
    fun initUI() {
        CARDVIEWWIDTH = convertPX2DP(150F)
        CARDVIEWHEIGHT = convertPX2DP(200F)
        var layoutParams = LayoutParams(CARDVIEWWIDTH, CARDVIEWHEIGHT)
        layoutParams.setMargins(convertPX2DP(25F), 0, convertPX2DP(50F), convertPX2DP(50F))
        binding!!.albumContainer.layoutParams = layoutParams
    }

}