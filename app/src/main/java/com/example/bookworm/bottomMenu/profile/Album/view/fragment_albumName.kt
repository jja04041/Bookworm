package com.example.bookworm.bottomMenu.profile.Album.view

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.profile.Album.item.AlbumData
import com.example.bookworm.databinding.FragmentAlbumNameBinding

class fragment_albumName : Fragment() {
    var binding: FragmentAlbumNameBinding? = null
    lateinit var parentActivity: CreateAlbumActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumNameBinding.inflate(inflater)
        parentActivity = context as CreateAlbumActivity
        parentActivity.albumViewModel.newAlbumData
            .observe(viewLifecycleOwner, { data: AlbumData ->
                if (data.thumbnail != null)
                    Glide.with(binding!!.root).load(data.thumbnail)
                        .into(binding!!.albumThumb)
            })
        binding!!.btnNext.setOnClickListener {
            val string = binding!!.edtAlbumName.text.toString()
            if (string.equals("") || string.contains(" ")) {
                Toast.makeText(
                    context,
                    "앨범명에는 공백을 포함할 수 없습니다. 다시 시도해 주세요.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                parentActivity.albumViewModel.modifyName(binding!!.edtAlbumName.text.toString())
                //데이터 삽입
                parentActivity.switchTab(1)
            }
        }
        binding!!.btnCancel.setOnClickListener(
            {
                parentActivity.finish()
            })

        binding!!.edtAlbumName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
        binding!!.edtAlbumName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    binding!!.albumName.setText(p0.toString())
                }

            })
        return binding!!.root
    }
}