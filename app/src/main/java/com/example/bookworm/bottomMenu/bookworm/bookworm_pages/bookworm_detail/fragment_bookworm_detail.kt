package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FragmentBwLayoutBinding


class fragment_bookworm_detail : Fragment() {
    var binding: FragmentBwLayoutBinding? = null
    lateinit var uv: UserInfoViewModel
    lateinit var bwAdapter: BookwormImgAdapter
    lateinit var userInfo: UserInfo
    lateinit var bwDataList: ArrayList<BookwormData>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBwLayoutBinding.inflate(layoutInflater)
        uv = ViewModelProvider(
            this, UserInfoViewModel.Factory(requireContext())
        ).get(
            UserInfoViewModel::class.java
        )
        bwDataList = ArrayList()
        initAdapter()
        val bwImgList = resources.obtainTypedArray(R.array.bookworm_char)
        val bwNameList = resources.getStringArray(R.array.bookworm_name) //전체 책볼레 리스트 생성
        for (i: Int in 0..bwImgList.length() - 1) {
            var bookwormData = BookwormData()
            bookwormData.setBwData(bwImgList.getResourceId(i, -1), bwNameList[i], false)
            bwDataList.add(bookwormData)
        }
        uv.getUser(null, false)
        //사용자 데이터를 추적
        uv.data.observe(this.viewLifecycleOwner) { userInfo: UserInfo ->
            this.userInfo = userInfo
            uv.getBookWorm(userInfo.token) //책볼레 데이터를 불러오도록 한다.
        }
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()


        uv.getBookWorm(userInfo.token) //책볼레 데이터를 불러오도록 한다.
        //책벌레 데이터의 도착 여부 추적
        uv.bwdata.observe(this) { bw: BookWorm ->
            for (i: Int in 0..bwDataList.size - 1) {
                bwDataList[i].hasBw = bw.wormvec.contains(bwDataList[i].id)
            }
            bwAdapter.submitList(bwDataList)
//            //데이터가 도착한 경우 , 사용자가 뷰홀더에서 선택한 경우에만 세팅을 한다.
            binding!!.ivBookworm.setImageResource(bw.wormtype)
            binding!!.ivBg.setImageResource(bw.bgtype)
        }
        bwAdapter.submitList(bwDataList)
        binding!!.ivBg.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
        binding!!.ivBg.setAdjustViewBounds(true)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun initAdapter() {
        bwAdapter = context?.let { BookwormImgAdapter(binding!!.root, it) }!!
        bwAdapter.setOnItemClickListener(object : BookwormImgAdapter.OnItemClickEventListener {
            override fun onItemClick(a_view: View?, a_position: Int) {
                var item = bwDataList[a_position]
                binding!!.ivBookworm.setImageResource(item.id)
            }
        })
        val gridLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false)
        binding!!.bwRecyclerView.layoutManager = gridLayoutManager
        binding!!.bwRecyclerView.adapter = bwAdapter
    }

    //뷰보다 프레그먼트의 생명주기가 길어서, 메모리 누수 발생가능
    //누수 방지를 위해 뷰가 Destroy될 때, binding값을 nullify 함.
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}