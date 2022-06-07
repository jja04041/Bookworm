package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item.AlbumProcessAdapter
import com.example.bookworm.databinding.FragmentSelectFeedBinding

//앨범에 담을 피드를 선택하는 화면
class FragmentSelectFeed : Fragment() {
    var binding: FragmentSelectFeedBinding? = null
    var albumProcessAdapter: AlbumProcessAdapter? = null
    lateinit var parentActivity: CreateAlbumActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = context as CreateAlbumActivity
        binding = FragmentSelectFeedBinding.inflate(inflater)
        binding!!.btnPrev.setOnClickListener({
            parentActivity.switchTab(1)
        })
        binding!!.btnNext.setOnClickListener({
            parentActivity.albumProcessViewModel.modifyFeedList(albumProcessAdapter!!.selectedFeed)
            //이곳에 서버에 앨범을 업로드 하는 로직을 작성해야 함.
            parentActivity.albumProcessViewModel.uploadAlbum()
        })
        initRecyclerView()
        return binding!!.root
    }

    fun initRecyclerView() {
        this.albumProcessAdapter = AlbumProcessAdapter(requireContext())
        binding!!.mRecyclerView.adapter = albumProcessAdapter
        binding!!.mRecyclerView.setLayoutManager(LinearLayoutManager(requireContext()));
        //만약 피드가 없는 경우에는 선택할 피드가 없다고 나오게 하도록
        if (parentActivity.feedList.size < 1) {
            var theme = getResources().newTheme()
            theme.applyStyle(R.style.ThemeOverlay_AppCompat_Dark, true)
            binding!!.mRecyclerView.visibility = View.GONE
            binding!!.tvAlertNoPost.visibility = View.VISIBLE
            binding!!.btnNext.isEnabled = false
            binding!!.btnNext.setBackgroundTintList(
                requireContext().getResources().getColorStateList(
                    R.color.gray,
                    theme
                )
            )
        } else {
            binding!!.mRecyclerView.visibility = View.VISIBLE
            binding!!.tvAlertNoPost.visibility = View.GONE
            albumProcessAdapter!!.setItemClickListener {
                var theme = getResources().newTheme()
                theme.applyStyle(R.style.ThemeOverlay_AppCompat_Dark, true)
                val a = albumProcessAdapter!!.selectedFeed.size >= 1
                if (!a) {
                    binding!!.btnNext.setBackgroundTintList(
                        requireContext().getResources().getColorStateList(
                            R.color.gray,
                            theme
                        )
                    )

                } else {
                    binding!!.btnNext.setBackgroundTintList(
                        requireContext().getResources().getColorStateList(
                            R.color.subcolor_1,
                            theme
                        )
                    )
                }
                binding!!.btnNext.isEnabled = a
            }
            albumProcessAdapter!!.submitList(parentActivity.feedList.toList())

        }

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}