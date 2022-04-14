package com.example.bookworm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.databinding.FragmentProfileFollowerBinding

class fragment_profile_follower : Fragment() {
    var binding: FragmentProfileFollowerBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProfileFollowerBinding.inflate(layoutInflater)
        var v: View? = binding!!.root



        return v
    }
    fun moduleUpdated(){

    }

    //리사이클러뷰 스크롤 초기화
//    private fun initScrollListener() {
//        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
//                val lastVisibleItemPosition = layoutManager!!.findLastCompletelyVisibleItemPosition()
//                if (!isLoading) {
//                    try {
//                        if (layoutManager != null && lastVisibleItemPosition == feedAdapter.getItemCount() - 1) {
//                            deleteLoading()
//                            //이전에 가져왔던 자료를 인자로 보내주어 그 다음 자료부터 조회한다.
//                            map.put("lastVisible", lastVisible)
//                            //쿼리를 보내어, 데이터를 조회한다.
//                            fbModule.readData(1, map, null)
//                            //현재 로딩을 끝냄을 알린다.
//                            isLoading = true
//                        }
//                    } catch (e: NullPointerException) {
//                    }
//                }
//            }
//        })
//    }
}