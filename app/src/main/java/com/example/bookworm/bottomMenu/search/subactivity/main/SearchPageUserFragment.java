package com.example.bookworm.bottomMenu.search.subactivity.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bookworm.bottomMenu.search.items.user.UserAdapter;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.FragmentSearchPageUserBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchPageUserFragment extends Fragment {

    FragmentSearchPageUserBinding binding;


    private Boolean canLoad = true; //더 로딩이 가능한지 확인하는 변수[자료의 끝을 판별한다.]
    private int page = 1;
    UserInfo users;
    private ArrayList<UserInfo> userlist = null;
    private UserAdapter userAdapter;
    private final int LIMIT = 10;

    private Boolean isRefreshing = false;
    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    private DocumentSnapshot lastVisible;
    Map<String, Object> map;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchPageUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initAdapter() {
        userAdapter = new UserAdapter(userlist, getContext());
        //어댑터 리스너
        userAdapter.setListener((holder, view, position) -> {

        });
    }

    public void moduleUpdated(List<DocumentSnapshot> a) {
        userlist = new ArrayList<>();
        try {
            for (DocumentSnapshot snapshot : a) {
                Map data = snapshot.getData();
                UserInfo user = new UserInfo();
                user.setUserData(data);
                userlist.add(users);
            }
            //가져온 값의 마지막 snapshot부터 이어서 가져올 수 있도록 하기 위함.
            lastVisible = a.get(a.size() - 1);
            //리사이클러뷰에서 튕김현상이 발생하여 넣어준 코드
            //현재 불러오는 값의 크기(a.size())가 페이징 제한 값(LIMIT)보다 작은 경우 => 더이상 불러오지 않게 함.
            if (a.size() < LIMIT) {
                canLoad = false;
            }
        } catch (NullPointerException e) {
            canLoad = false;
        }
        initAdapter();
        initRecyclerView();
        isEmptyRecord(false);
    }

    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.mRecyclerView.setLayoutManager(linearLayoutManager);
        binding.mRecyclerView.setAdapter(userAdapter);
        //        initScrollListener(); //무한스크롤
    }

    public void isEmptyRecord(boolean bool) {

        if (bool) {
            binding.mRecyclerView.setVisibility(View.GONE);
            binding.llEmptySearchUser.setVisibility(View.VISIBLE);
        } else {
            binding.mRecyclerView.setVisibility(View.VISIBLE);
            binding.llEmptySearchUser.setVisibility(View.GONE);
        }
    }
}