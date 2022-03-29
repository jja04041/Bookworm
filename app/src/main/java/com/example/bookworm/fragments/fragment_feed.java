package com.example.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bookworm.Feed.items.FeedAdapter;
import com.example.bookworm.databinding.FragmentFeedBinding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Feed.subActivity_Feed_Create;
import com.example.bookworm.R;
import com.example.bookworm.modules.FBModule;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class fragment_feed extends Fragment {
    ImageView imgCreate;
    FragmentFeedBinding binding;
    FeedAdapter feedAdapter;
    Boolean isLoading = false;
    private DocumentSnapshot lastVisible; //마지막에 가져온 값 부터 추가로 가져올 수 있도록 함.
    private Map map;
    private FBModule fbModule;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        fbModule=new FBModule(getActivity());

//        //Create New Feed
        imgCreate=view.findViewById(R.id.img_createfeed);
        imgCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), subActivity_Feed_Create.class);
                startActivity(intent);
            }
        });
        //피드 초기 호출
        map = new HashMap();
//        fbModule.readData(2, map, null); //검색한 데이터를 조회


        // Inflate the layout for this fragment
        return view;
    }

    //뷰보다 프레그먼트의 생명주기가 길어서, 메모리 누수 발생가능
    //누수 방지를 위해 뷰가 Destroy될 때, binding값을 nullify 함.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    //리사이클러뷰를 초기화
    private void initRecyclerView() {
        binding.recyclerView.setAdapter(feedAdapter);
        initScrollListener(); //무한스크롤
    }

    //리사이클러뷰 스크롤 초기화
    private void initScrollListener() {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
//                if (!isLoading) {
//                    try {
//                        if (layoutManager != null && lastVisibleItemPosition == feedAdapter.getItemCount() - 1) {
//                            page = module.getPage();
//                            count = module.getCount();
//                            feedAdapter.deleteLoading();
//                            if ((page * CPP) < count) {
//                                module.connect(0);
//                            }
//                            isLoading = true;
//                        }
//                    } catch (NullPointerException e) {
//
//                    }
//                }
//
//            }
        });

    }
}