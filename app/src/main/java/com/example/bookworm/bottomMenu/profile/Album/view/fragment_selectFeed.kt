package com.example.bookworm.bottomMenu.profile.Album.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.profile.Album.item.AlbumAdapter
import com.example.bookworm.databinding.FragmentSelectFeedBinding

//앨범에 담을 피드를 선택하는 화면
class fragment_selectFeed : Fragment() {
    var binding: FragmentSelectFeedBinding? = null
    var tracker: SelectionTracker<Long>? = null
    var actionMode: ActionMode? = null
    var albumAdapter: AlbumAdapter? = null
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
            parentActivity.finish()
        })
        initRecyclerView()
        if (savedInstanceState != null) tracker!!.onRestoreInstanceState(savedInstanceState)
        return binding!!.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker!!.onSaveInstanceState(outState)
    }

    fun initRecyclerView() {
        this.albumAdapter = AlbumAdapter(requireContext())
        binding!!.mRecyclerView.adapter = albumAdapter
        binding!!.mRecyclerView.setLayoutManager(LinearLayoutManager(requireContext()));
        setTracker()
        albumAdapter!!.tracker = tracker
        albumAdapter!!.submitList(parentActivity.feedList.toList())
    }

    fun setTracker() {
        tracker = SelectionTracker.Builder(
            "selectionItem",
            binding!!.mRecyclerView,
            StableIdKeyProvider(binding!!.mRecyclerView),
            AlbumAdapter.ItemsDetailsLookup(binding!!.mRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
//        tracker!!.addObserver(
//            object : SelectionTracker.SelectionObserver<Long>() {
//                override fun onSelectionChanged() {
//                    super.onSelectionChanged()
//                    if (actionMode == null && tracker!!.hasSelection()) {
//                        actionMode = parentActivity.startActionMode(object :
//                            ActionModeController(context!!, tracker!!) {})
//                    } else {
//                        actionMode!!.finish()
//                        actionMode = null
//                    }
//                    val iter: MutableIterator<Long> = tracker!!.selection.iterator()
//                    while (iter.hasNext()) {
//                        Log.i("테스트 중 ", iter.next().toString())
//                    }
//                }
//            })
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}