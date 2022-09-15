package com.example.bookworm.bottomMenu.feed

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookworm.databinding.LayoutLabelCustomDialogBinding

class LabelCustomDialog(context: Context) : DialogFragment() {
    private var _dataBinding: LayoutLabelCustomDialogBinding? = null

    //   private val pager
    inner class EmotionPage : Fragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
    }

    inner class RecommendPage : Fragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
    }

    inner class PageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
        : FragmentStateAdapter(fragmentManager, lifecycle) {
        private var pageList = ArrayList<Fragment>()
        override fun getItemCount(): Int = pageList.size


        override fun createFragment(pos: Int): Fragment {
            when (pos) {
                0 ->
                    pageList.add(EmotionPage())
                else ->
                    pageList.add(RecommendPage())
            }
            return pageList[pos]
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _dataBinding = LayoutLabelCustomDialogBinding.inflate(layoutInflater)

    }

    override fun onDestroy() {
        super.onDestroy()
        _dataBinding = null
    }
}