package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FragmentBwItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HasBookwormViewHolder(itemView: View,val listener:BookwormImgAdapter.OnItemClickEventListener,val context:Context) : RecyclerView.ViewHolder(itemView) {
    //부모의 뷰를 이용하여 해당 뷰홀더가 선택되면, 해당 데이터로 사진을 바꾼다.
    var binding:FragmentBwItemBinding?=null

    lateinit var user: UserInfo
    var uv:UserInfoViewModel
    var bwData:BookWorm?=null

    init{

        binding= FragmentBwItemBinding.bind(itemView)
        uv= UserInfoViewModel(context)
        uv.getUser(null,false)
        uv.data.observe(context as MainActivity,{
            user=it
            uv.getBookWorm(user.token)

        })

    }
    fun setItems(data:BookwormData){
        binding!!.ivBwImage.setImageResource(data.id)
        binding!!.tvGenre.setText(data.name)
        uv.bwdata.observe(context as MainActivity) {
            bwData = it
//            if (bwData!!.wormtype == data.id) {
//                binding!!.itemContainer.setBackgroundColor(Color.BLACK)
//            }
        }


        //해당 데이터 선택시
        binding!!.ivBwImage.setOnClickListener({
            uv.getBookWorm(user.token)
            var position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(itemView, position)
            }
            uv.bwdata.observe(context as MainActivity,{
                if(it.wormtype!=data.id) {
                    it.wormtype = data.id
                    CoroutineScope(Dispatchers.IO).launch {
                        uv.updateBw(null, it)
                    }
                }
            })
        })
    }
}