package com.example.bookworm.bottomMenu.profile.Album.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.databinding.FragmentAlbumArtBinding

//앨범의 사진을 선택하는 화면
class fragment_albumArt : Fragment() {
    var binding: FragmentAlbumArtBinding? = null
    lateinit var parentActivity: CreateAlbumActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumArtBinding.inflate(inflater)
        parentActivity = context as CreateAlbumActivity
        var imageProcessing = ImageProcessing(parentActivity)
        parentActivity.albumViewModel.newAlbumData.observe(viewLifecycleOwner, { data ->
            binding!!.albumName.setText(data.albumName)
            binding!!.ivSelectedImg.setOnClickListener({
                imageProcessing.initProcess()
                imageProcessing.bitmapUri.observe(viewLifecycleOwner, {
                    parentActivity.albumViewModel.modifyThumb(it.toString())
                    Glide.with(binding!!.root).load(it).into(binding!!.ivSelectedImg)
                    Glide.with(binding!!.root).load(it)
                        .into(binding!!.albumThumb)
                })
            })

        })


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