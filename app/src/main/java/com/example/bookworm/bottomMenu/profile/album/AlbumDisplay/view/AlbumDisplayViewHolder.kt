package com.example.bookworm.bottomMenu.profile.album.AlbumDisplay.view

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.profile.album.AlbumData
import com.example.bookworm.databinding.FragmentAlbumItemBinding


//앨범을 보여주는 뷰 홀더
class AlbumDisplayViewHolder(itemView: View, val context: Context) :
    RecyclerView.ViewHolder(itemView) {
    var binding: FragmentAlbumItemBinding? = FragmentAlbumItemBinding.bind(itemView)
    fun setItem(album: AlbumData) {
        //앨범 이름 세팅
        binding!!.albumName.text = album.albumName
        //이미지 세팅
        if (album.thumbnail != null) Glide.with(itemView).load(album.thumbnail)
            .into(binding!!.albumThumb)
        else binding!!.albumThumb.setImageResource(R.color.black)
    }
}