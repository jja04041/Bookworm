package com.example.bookworm.bottomMenu.challenge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookworm.bottomMenu.challenge.items.Challenge;
import com.example.bookworm.bottomMenu.challenge.subactivity.subactivity_challenge_challengeinfo;
import com.example.bookworm.bottomMenu.challenge.subactivity.subactivity_challenge_createchallenge;
import com.example.bookworm.bottomMenu.challenge.items.ChallengeAdapter;
import com.example.bookworm.bottomMenu.challenge.items.OnChallengeItemClickListener;
import com.example.bookworm.databinding.FragmentChallengeBinding;
import com.example.bookworm.core.internet.FBModule;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_challenge extends Fragment {

    //Variable
    private FBModule fbModule;
    private Boolean canLoad = true; //더 로딩이 가능한지 확인하는 변수[자료의 끝을 판별한다.]
    private int page = 1;
    private ArrayList<Challenge> challengeList = null;
    private ChallengeAdapter challengeAdapter;
    private final int LIMIT = 10;
    private Boolean isRefreshing = false;
    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    private DocumentSnapshot lastVisible;
    private FragmentChallengeBinding binding;
    Map<String, Object> map;
    //액티비티의 결과를 받아오기 위함.
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    pageRefresh();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChallengeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        showShimmer(true);

        //fbmodule을 이용하여 자료를 가져옴
        fbModule = new FBModule(getActivity());//파이어베이스를 통해서 챌린지를 가져와야함.
        fbModule.setLIMIT(LIMIT);
        map = new HashMap();
        fbModule.readData(2, map, null); //검색한 데이터를 조회


        //리스너 설정
        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageRefresh();
                isRefreshing = true;
            }
        });
        binding.btnCreateChallenge.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), subactivity_challenge_createchallenge.class);
                startActivityResult.launch(intent);
                binding.btnCreateChallenge.clearFocus();
            }
        });
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItems();
            }
        });
        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    //뷰보다 프레그먼트의 생명주기가 길어서, 메모리 누수 발생가능
    //누수 방지를 위해 뷰가 Destroy될 때, binding값을 nullify 함.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //functions

    //검색 키워드를 쿼리에 싣는 함수
    private void setItems() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
        binding.etSearch.clearFocus();
        map = new HashMap();
        //입력한 데이터를 Map 객체 내의 "like"의 값으로 설정한다.
        map.put("like", binding.etSearch.getText().toString());
        //새로운 데이터를 받기 위해 초기화 한다.
        initChallenge();
        //검색한 데이터를 조회
        fbModule.readData(2, map, null);
    }

    //리사이클러뷰 스크롤 초기화
    private void initScrollListener() {
        binding.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                //로딩이 필요할 때만 데이터를 추가적으로 불러온다
                if (!isLoading) {
                    try {
                        if (layoutManager != null && lastVisibleItemPosition == challengeAdapter.getItemCount() - 1) {
                            challengeAdapter.deleteLoading();
                            map = new HashMap();
                            //이전에 가져왔던 자료를 인자로 보내주어 그 다음 자료부터 조회한다.
                            map.put("lastVisible", lastVisible);
                            //쿼리를 보내어, 데이터를 조회한다.
                            fbModule.readData(2, map, null);
                            //현재 로딩을 끝냄을 알린다.
                            isLoading = true;
                        }
                    } catch (NullPointerException e) {

                    }
                }

            }
        });
    }

    //리사이클러뷰 초기화
    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.mRecyclerView.setLayoutManager(gridLayoutManager);
        binding.mRecyclerView.setAdapter(challengeAdapter);
        if (map.get("like") != null) {
            if (map.get("like").equals("")) {
                binding.llSearchR.setVisibility(View.INVISIBLE);
            } else {
                binding.llSearchR.setVisibility(View.VISIBLE);
                binding.tvSearchR.setText('"' + (String) map.get("like") + '"');
            }
        } else {
            binding.llSearchR.setVisibility(View.INVISIBLE);
        }
        initScrollListener(); //무한스크롤
    }

    //페이지 새로고침 시 사용하는 메소드
    private void pageRefresh() {
        initChallenge();
        if (map.get("lastVisible") != null) map.remove("lastVisible");
        fbModule.readData(2, map, null);
    }

    private void initChallenge() {
        isLoading = true;
        page = 1;
        canLoad = true;
        lastVisible = null;
        initRecyclerView();
    }

    private void initAdapter() {
        challengeAdapter = new ChallengeAdapter(challengeList, getContext());
        //어댑터 리스너
        challengeAdapter.setListener(new OnChallengeItemClickListener() {
            @Override
            public void onItemClick(ChallengeAdapter.ItemViewHolder holder, View view, int position) {
                //닫힌 챌린지 인경우 표시할 코드 등을 입력해야함.
                //아이템 선택시 실행할 코드를 입력
                Intent intent = new Intent(getActivity(), subactivity_challenge_challengeinfo.class);
                intent.putExtra("challengeInfo", challengeList.get(position));
                startActivityResult.launch(intent);
            }
        });
    }

    //fbmodule에서 사용하는 함수 -> 검색한 데이터를 전달하여, 화면에 띄워준다. (비동기 처리)
    public void moduleUpdated(List<DocumentSnapshot> a) {
        int beforesize = 0;
        if (isRefreshing) {
            binding.swiperefresh.setRefreshing(false);
            isRefreshing = false;
        }
        if (page == 1) {
            isLoading = false;
            challengeList = new ArrayList<>(); //챌린지를 담는 리스트 생성
        }
        beforesize = challengeList.size();
        if (a == null && lastVisible == null) {
            challengeList = new ArrayList<>(); //챌린지를 담는 리스트 생성
            initAdapter();
            initChallenge();
            Toast.makeText(getContext(), "검색결과가 없습니다", Toast.LENGTH_SHORT).show();
        } else {
            //가져온 데이터를 for문을 이용하여, challenge리스트에 차곡차곡 담는다.
            try {
                for (DocumentSnapshot snapshot : a) {
                    Map data = snapshot.getData();
                    Challenge challenge = new Challenge(data);
                    challengeList.add(challenge);
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
            //만약 더이상 불러오지 못 할 경우
            if (canLoad == false) {
                isLoading = true;
                if (page > 1) {
                    challengeAdapter.notifyItemRangeChanged(beforesize, challengeList.size() - beforesize);
                }//이미 불러온 데이터가 있는 경우엔 가져온 데이터 만큼의 범위를 늘려준다.
                else { //없는 경우엔 새로운 어댑터에 데이터를 담아서 띄워준다.
                    initAdapter(); //어댑터 초기화
                    initRecyclerView(); //리사이클러뷰에 띄워주기
                }
            }
            //더 불러올 데이터가 있는 경우
            else {
                challengeList.add(new Challenge(null)); //로딩바 표시를 위한 빈 값
                if (page > 1) {
                    isLoading = false;
                    challengeAdapter.notifyItemRangeChanged(beforesize, challengeList.size() - beforesize);
                } else {
                    initAdapter();//어댑터 초기화
                    initRecyclerView(); //리사이클러뷰에 띄워주기
                }
                page++; //로딩을 다하면 그다음 페이지로 넘어간다.
            }
        }
        showShimmer(false);
    }

    //shimmer을 켜고 끄고 하는 메소드
    private void showShimmer(Boolean bool) {
        if (bool) {
            binding.llChallenge.setVisibility(View.GONE);
            binding.SFLChallenge.startShimmer();
            binding.SFLChallenge.setVisibility(View.VISIBLE);
        } else {
            binding.llChallenge.setVisibility(View.VISIBLE);
            binding.SFLChallenge.stopShimmer();
            binding.SFLChallenge.setVisibility(View.GONE);
        }
    }


}
