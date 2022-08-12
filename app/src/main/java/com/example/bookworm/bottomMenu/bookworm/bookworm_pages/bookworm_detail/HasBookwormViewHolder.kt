package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FragmentBwItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HasBookwormViewHolder(
    itemView: View,
    val listener: BookwormImgAdapter.OnItemClickEventListener,
    val context: Context
) : RecyclerView.ViewHolder(itemView) {
    //부모의 뷰를 이용하여 해당 뷰홀더가 선택되면, 해당 데이터로 사진을 바꾼다.
    var binding: FragmentBwItemBinding? = null
    var enabled: Boolean = false
    lateinit var user: UserInfo
    var uv: UserInfoViewModel
    var bwData: BookWorm? = null
    var liveData = MutableLiveData<Int>()

    init {
        binding = FragmentBwItemBinding.bind(itemView)
        uv = UserInfoViewModel(context)
        uv.getUser(null, false)
        uv.data.observe(context as MainActivity, {
            user = it
            uv.getBookWorm(user.token)
        })

    }

    fun setItems(data: BookwormData) {

        binding!!.ivBwImage.setImageResource(
            context.resources.getIdentifier(
                "bw_${data.id}",
                "drawable",
                context.packageName
            )
        )
        binding!!.tvGenre.setText(data.name)
        uv.bwdata.observe(context as MainActivity) {
            bwData = it
            if(it.wormtype == data.id) liveData.value=bindingAdapterPosition
        }


    }
    // userinfo의 wormtype데이터와
    // 리사이클러뷰의 position의 data.id 값이 일치하면
    // binding!!.itemContainer.setBackgroundColor(Color.rgb(204, 204, 204))
    // binding!!.ivBwImage.setBackgroundColor(Color.rgb(170, 170, 170))
    // 이것이 상시로 돌아가야함 그래야 click했을때나 항시에 적용이됨


    fun setWorks(data: BookwormData) {
        uv.getBookWorm(user.token)
        var position = bindingAdapterPosition
        listener.onItemClick(itemView, position)
        uv.bwdata.observe(context as AppCompatActivity, {
            if (it.wormtype != data.id) {
                it.wormtype = data.id
                CoroutineScope(Dispatchers.IO).launch {
                    uv.updateBw(null, it)
                }
            }
        })

//        liveData.observe(context, {
//            Log.d("볼레 값", it.toString() + "--" + bindingAdapterPosition)
//            if (it == bindingAdapterPosition) {
//                itemView.isSelected = true
//                binding!!.tvGenre.setTextColor(Color.parseColor("#FF0000"))
//            } else {
//                itemView.isSelected = false
//                binding!!.tvGenre.setTextColor(Color.parseColor("#000000"))
//            }
//        })
    }
}