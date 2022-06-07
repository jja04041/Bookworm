package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.bookworm.databinding.FragmentProfileAlbumArtBinding

//앨범의 사진을 선택하는 화면
class FragmentAlbumArt : Fragment() {
    var binding: FragmentProfileAlbumArtBinding? = null
    lateinit var parentActivity: CreateAlbumActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileAlbumArtBinding.inflate(inflater)
        parentActivity = context as CreateAlbumActivity
        val imageProcessing = parentActivity.imageProcessing
        parentActivity.albumProcessViewModel.newAlbumData.observe(viewLifecycleOwner) { data ->
            binding!!.albumItem.albumName.setText(data.albumName)
            binding!!.ivSelectedImg.setOnClickListener({
                imageProcessing.initProcess()
                imageProcessing.bitmapUri.observe(viewLifecycleOwner, {
                    parentActivity.albumProcessViewModel.modifyThumb(it.toString())
                    Glide.with(binding!!.root).load(it).into(binding!!.ivSelectedImg)
                    Glide.with(binding!!.root).load(it)
                        .into(binding!!.albumItem.albumThumb)
                })

            })

        }


        binding!!.btnNext.setOnClickListener({
            parentActivity.switchTab(2)
        })
        binding!!.btnPrev.setOnClickListener({
            parentActivity.switchTab(0)
        })

        return binding!!.root
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

}