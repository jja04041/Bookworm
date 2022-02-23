package com.example.bookworm.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Challenge.Challenge;
import com.example.bookworm.Challenge.activity_createchallenge;
import com.example.bookworm.Challenge.items.ChallengeAdapter;
import com.example.bookworm.Challenge.items.OnChallengeItemClickListener;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;

import java.util.Map;

public class fragment_challenge extends Fragment {

    private String strNickname, strProfile, strEmail;
    private RecyclerView mRecyclerView;
    private ChallengeAdapter challengeAdapter;
    private Spinner spinnerC;
    private FBModule fbModule;
    private PersonalD personalD;
    private UserInfo userInfo;
    private int page = 0, count = 0,check=0;
    private ArrayList<Challenge> challengeList;
    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        Button btn_create_challenge = view.findViewById(R.id.btn_create_challenge);
        spinnerC = view.findViewById(R.id.spinnerC);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        fbModule = new FBModule(getActivity());//파이어베이스를 통해서 챌린지를 가져와야함.
        personalD = new PersonalD(getContext()); //UserInfo 값을 가져옴
        //UserInfo값 가져오기
        userInfo = personalD.getUserInfo();
        strNickname = userInfo.getUsername();
        strProfile = userInfo.getProfileimg();
        strEmail = userInfo.getEmail();

        btn_create_challenge.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), activity_createchallenge.class);
                //파이어베이스 챌린지 컬렉션에 유저이름과 프로필 URL을 올리기 위해 activity_login에서 받은 값을 activity_createchallenge.java로 넘겨줌
                intent.putExtra("strNickname", strNickname);
                intent.putExtra("strProfile", strProfile);
                startActivity(intent);
                btn_create_challenge.clearFocus();
            }
        });


        return view;
    }

    //리사이클러뷰 초기화
    private void initRecyclerView() {
        mRecyclerView.setAdapter(challengeAdapter);
        initScrollListener(); //무한스크롤
    }

    //리사이클러뷰 스크롤 초기화
    private void initScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (!isLoading) {
                    try {
                        if (layoutManager != null && lastVisibleItemPosition == challengeAdapter.getItemCount() - 1) {
//                            page = module.getPage();
//                            count = module.getCount();
//                            challengeAdapter.deleteLoading();
//                            if ((page * CPP) < count) {
//                            fbModule.readData(2, );
//                            }
                            isLoading = true;
                        }
                    } catch (NullPointerException e) {

                    }
                }

            }
        });
    }


    //fbmodule에서 사용하는 함수
    public void moduleUpdated(Object[] a) {
//        page = module.getPage();
//        count = module.getCount();

        if (page == 1) {
            check = count;
            challengeList = new ArrayList<>(); //book을 담는 리스트 생성
        }
        if (a == null) {
            //검색결과가 없을 경우엔 리사이클러 뷰를 비움.
            challengeList = new ArrayList<>();
            challengeAdapter = new ChallengeAdapter(challengeList, getActivity());
            initRecyclerView();
            Toast.makeText(getContext(), "검색결과가 없습니다", Toast.LENGTH_SHORT).show();
        } else {
            for (DocumentSnapshot snapshot : (DocumentSnapshot[]) a) {
                Map data = snapshot.getData();
                //등록 순서: 참가자 목록, 책 ID, 시작일자,마감일자, 챌린지명, 최대 수용가능 인원
                Challenge challenge = new Challenge((String[]) data.get(""), data.get("").toString());
                challengeList.add(challenge);
            }
            if (check > 20 && page < 20) {
                challengeList.add(new Challenge(null, ""));
//                this.check = count - bookList.size();
            } else isLoading = true;

            if (page != 1 && page < 20) {
                isLoading = false;
                challengeAdapter.notifyItemRangeChanged(0, challengeList.size() - 1, null);
            } else {
                challengeAdapter = new ChallengeAdapter(challengeList, getActivity());
                challengeAdapter.setListener(new OnChallengeItemClickListener() {
                    @Override
                    public void onItemClick(ChallengeAdapter.ItemViewHolder holder, View view, int position) {

                    }
                });
                initRecyclerView(); //initialize RecyclerView
            }
            this.page++;
//            module.setPage(page);
        }

    }
}
