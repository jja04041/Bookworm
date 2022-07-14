package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.databinding.FragmentProfileAlbumNameBinding

class FragmentAlbumName : Fragment() {
    var binding: FragmentProfileAlbumNameBinding? = null
    lateinit var parentActivity: CreateAlbumActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileAlbumNameBinding.inflate(inflater)
        parentActivity = context as CreateAlbumActivity
        parentActivity.albumProcessViewModel.newAlbumData
            .observe(viewLifecycleOwner, { data: AlbumData ->
                if (data.thumbnail != null)
                    Glide.with(binding!!.root).load(data.thumbnail)
                        .into(binding!!.albumItem.albumThumb)
            })
        binding!!.btnNext.setOnClickListener {
            val albumName = binding!!.edtAlbumName.text.toString()
            parentActivity.albumProcessViewModel.isOkayToUse(albumName)
        }
        binding!!.btnCancel.setOnClickListener(
            {
                parentActivity.finish()
            })


        //앨범명 입력 감지
        binding!!.edtAlbumName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
        binding!!.edtAlbumName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    binding!!.albumItem.albumName.setText(p0.toString())
                }

            })
        binding!!.edtAlbumName.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, keyEvent: KeyEvent): Boolean {
                //엔터키를 눌렀을 경우 처리
                if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    val imm =
                        context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding!!.edtAlbumName.getWindowToken(), 0)
                    binding!!.btnNext.performClick()
                }
                return false
            }

        })
        return binding!!.root
    }
}