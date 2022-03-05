package com.example.bookworm.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Challenge.items.Challenge;
import com.example.bookworm.Challenge.subActivity.subactivity_challenge_challengeinfo;
import com.example.bookworm.Challenge.subActivity.subactivity_challenge_createchallenge;
import com.example.bookworm.Challenge.items.ChallengeAdapter;
import com.example.bookworm.Challenge.items.OnChallengeItemClickListener;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_challenge extends Fragment {

    private RecyclerView mRecyclerView;
    private ChallengeAdapter challengeAdapter;
    private Spinner spinnerC;
    private EditText etSearch;
    private FBModule fbModule;
    private PersonalD personalD;
    private Button btn_create_challenge, btnSearch;
    private UserInfo userInfo;
    private Boolean canLoad = true; //더 로딩이 가능한지 확인하는 변수
    private int page = 1;
    private ArrayList<Challenge> challengeList = null;
    private final int LIMIT = fbModule.LIMIT;
    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    private DocumentSnapshot lastVisible;
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    initChallenge();
                    Map<String, String> map = new HashMap();
                    fbModule.readData(2, map, null); //검색한 데이터를 조회
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        btn_create_challenge = view.findViewById(R.id.btn_create_challenge);
        spinnerC = view.findViewById(R.id.spinnerC);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        //fbmodule을 이용하여 자료를 가져옴
        fbModule = new FBModule(getActivity());//파이어베이스를 통해서 챌린지를 가져와야함.
        Map<String, String> map = new HashMap();
        fbModule.readData(2, map, null); //검색한 데이터를 조회
        //사용자 데이터 가져옴
        personalD = new PersonalD(getContext()); //UserInfo 값을 가져옴
        userInfo = personalD.getUserInfo(); //UserInfo값 가져오기

        btn_create_challenge.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), subactivity_challenge_createchallenge.class);
                startActivityResult.launch(intent);
                btn_create_challenge.clearFocus();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setItems();
            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH) {
                    setItems();
                }
                return false;
            }
        });

        return view;
    }

    private void setItems(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        etSearch.clearFocus();
        Map<String, String> map = new HashMap();
        //입력한 데이터를 Map 객체 내의 "like"의 값으로 설정한다.
        map.put("like", etSearch.getText().toString());
        //새로운 데이터를 받기 위해 초기화 한다.
        initChallenge();
        //검색한 데이터를 조회
        fbModule.readData(2, map, null);
    }
    //리사이클러뷰 초기화
    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(challengeAdapter);
        initScrollListener(); //무한스크롤
    }

    //리사이클러뷰 스크롤 초기화
    private void initScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            //스크롤 감지
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            //스크롤 했을 때 작동하는 메소드
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (!isLoading) {
                    try {
                        if (layoutManager != null && lastVisibleItemPosition == challengeAdapter.getItemCount() - 1) {
                            challengeAdapter.deleteLoading();
                            Map map = new HashMap();
                            map.put("lastVisible", lastVisible);
                            fbModule.readData(2, map, null);
                            isLoading = true;
                        }
                    } catch (NullPointerException e) {

                    }
                }

            }
        });
    }

    public void initChallenge() {
        isLoading = true;
        page = 1;
        canLoad = true;
        lastVisible = null;
        initRecyclerView();
    }

    //fbmodule에서 사용하는 함수
    public void moduleUpdated(List<DocumentSnapshot> a) {

        //페이지 관리를 어떻게 하여야 할지..
        if (page == 1) {
            isLoading = false;
            challengeList = new ArrayList<>(); //챌린지를 담는 리스트 생성
        }
        if (a == null && lastVisible == null) {
            initChallenge();
            Toast.makeText(getContext(), "검색결과가 없습니다", Toast.LENGTH_SHORT).show();
        } else {
            try {
                for (DocumentSnapshot snapshot : a) {
                    Map data = snapshot.getData();
                    Challenge challenge = new Challenge(data);
                    challengeList.add(challenge);
                }
                lastVisible = a.get(a.size() - 1); //가져온 값의 마지막 snapshot부터 이어서 가져올 수 있도록 하기 위함.
                //리사이클러뷰에서 튕김현상이 발생하여 넣어준 코드
                //현재 불러오는 값의 크기(a.size())가 페이징 제한 값(LIMIT)보다 작은 경우 => 더이상 불러오지 않게 함.
                if (a.size() < LIMIT) {
                    canLoad = false;
                }
            } catch (NullPointerException e) {
                canLoad = false;
            }
            //만약 더이상 불러오지 못 할 경우
            if (canLoad == false) {
                isLoading = true;
                if (page>1) challengeAdapter.notifyDataSetChanged();
                else {
                    initAdapter();
                    initRecyclerView(); //리사이클러뷰에 띄워주기
                }

                Log.d("challengeList", Arrays.toString(challengeList.toArray()));
                Log.d("challengeListSize", String.valueOf(challengeList.size()));
//                challengeAdapter.notifyItemRangeChanged(0, challengeList.size()-1, null);
            } else {
                challengeList.add(new Challenge(null));
                Log.d("challengeList", Arrays.toString(challengeList.toArray()));
                Log.d("challengeListSize", String.valueOf(challengeList.size()));
                if (page > 1) {
                    isLoading = false;
                    challengeAdapter.notifyItemRangeChanged(0, challengeList.size() - 1, null);
                } else {
                    initAdapter();
                    initRecyclerView(); //리사이클러뷰에 띄워주기
                }
                page++;
            }

        }

    }
    private void initAdapter(){
        challengeAdapter = new ChallengeAdapter(challengeList, getContext());
        challengeAdapter.setListener(new OnChallengeItemClickListener() {
            @Override
            public void onItemClick(ChallengeAdapter.ItemViewHolder holder, View view, int position) {
                //아이템 선택시 실행할 코드를 입력
                Intent intent = new Intent(getActivity(), subactivity_challenge_challengeinfo.class);
                intent.putExtra("challengeInfo", challengeList.get(position));
                startActivity(intent);
            }
        });
    }
}
