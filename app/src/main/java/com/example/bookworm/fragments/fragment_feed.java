package com.example.bookworm.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.bookworm.Feed.Comments.DiffUtilCallback;
import com.example.bookworm.Feed.items.Feed;
import com.example.bookworm.Feed.items.FeedAdapter;
import com.example.bookworm.databinding.FragmentFeedBinding;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookworm.Feed.subActivity_Feed_Create;
import com.example.bookworm.databinding.LayoutTopbarBinding;
import com.example.bookworm.modules.FBModule;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firestore.v1.Precondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_feed extends Fragment {
    FragmentFeedBinding binding;
    public FeedAdapter feedAdapter;
    private final int LIMIT = 5;
    public ArrayList<Feed> feedList;
    public  static Context mContext;
    private DocumentSnapshot lastVisible; //마지막에 가져온 값 부터 추가로 가져올 수 있도록 함.
    private Map map;
    private FBModule fbModule;
    //isLoading:스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    //canLoad:더 로딩이 가능한지 확인하는 변수[자료의 끝을 판별한다.]
    private Boolean isRefreshing = false, isLoading = false, canLoad = true;
    private int page = 1;
    public ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == subActivity_Feed_Create.CREATE_OK) {
                    pageRefresh();
                }
                if (result.getResultCode() == 26) {
                    ArrayList newList = new ArrayList(feedList);
                    Feed item = (Feed) result.getData().getSerializableExtra("modifiedFeed");
                    newList.remove(item.getPosition());
                    newList.add(item.getPosition(),item);
                    replaceItem(newList);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();

        binding = FragmentFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        binding.recyclerView.setNestedScrollingEnabled(false);
        fbModule = new FBModule(getContext());
        fbModule.setLIMIT(LIMIT); //한번에 보여줄 피드의 최대치를 설정
        //Create New Feed
        LayoutTopbarBinding.bind(binding.getRoot()).imgCreatefeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), subActivity_Feed_Create.class);
                startActivityResult.launch(intent);
            }
        });
        //스와이프하여 새로고침
        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageRefresh();
                isRefreshing = true;
            }
        });
        binding.recyclerView.setItemViewCacheSize(3);
        //피드 초기 호출
        pageRefresh();

        // Inflate the layout for this fragment
        return view;
    }

    //뷰보다 프레그먼트의 생명주기가 길어서, 메모리 누수 발생가능
    //누수 방지를 위해 뷰가 Destroy될 때, binding값을 nullify 함.
    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    //리사이클러뷰를 초기화
    private void initRecyclerView() {
        binding.recyclerView.setAdapter(feedAdapter);
        initScrollListener(); //무한스크롤
    }

    private void initAdapter() {
        feedAdapter = new FeedAdapter(feedList, getContext());
    }

    //리사이클러뷰 스크롤 초기화
    private void initScrollListener() {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        if (layoutManager != null && lastVisibleItemPosition == feedAdapter.getItemCount() - 1) {
                            deleteLoading();
                            //이전에 가져왔던 자료를 인자로 보내주어 그 다음 자료부터 조회한다.
                            map.put("lastVisible", lastVisible);
                            //쿼리를 보내어, 데이터를 조회한다.
                            fbModule.readData(1, map, null);
                            //현재 로딩을 끝냄을 알린다.
                            isLoading = true;
                        }
                    } catch (NullPointerException e) {

                    }
                }

            }
        });

    }

    //페이지 새로고침 시 사용하는 메소드
    private void pageRefresh() {
        initFeed();
        map = new HashMap();
//        String[] followers={"113371714153801966916","2091385654"};
//        map.put("followers",Arrays.asList(followers));
        if (map.get("lastVisible") != null) map.remove("lastVisible");
        feedList = new ArrayList<>(); //챌린지를 담는 리스트 생성
        isLoading = true;
        fbModule.readData(1, map, null);

    }

    //피드 초기화
    private void initFeed() {
        isLoading = true;
        page = 1;
        canLoad = true;
        lastVisible = null;
        initRecyclerView();
    }

    // 로딩이 완료되면 프로그레스바를 지움
    public void deleteLoading() {
        ArrayList arr = new ArrayList(feedList);
        arr.remove(arr.size() - 1);
        replaceItem(arr); //데이터가 삭제됨을 알림.
    }

    public void replaceItem(ArrayList newthings) {
        DiffUtilCallback callback = new DiffUtilCallback(feedList, newthings);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback, true);
        feedList.clear();
        feedList.addAll(newthings);
        feedAdapter.setData(feedList);
        diffResult.dispatchUpdatesTo(feedAdapter);
    }

    //fb모듈을 통해 전달받은 값을 세팅
    public void moduleUpdated(List<DocumentSnapshot> a, ArrayList<DocumentSnapshot> b) {
        ArrayList newList = new ArrayList(feedList); //새로 들어오는 데이터와 기존 데이터의 길이를 비교하기 위해서
        if (isRefreshing) {
            binding.swiperefresh.setRefreshing(false);
            isRefreshing = false;
        }
        if (page == 1) {
            isLoading = false;
            feedList = new ArrayList<>(); //챌린지를 담는 리스트 생성
        }
        if (a == null) {
            if (page == 1) {
                Toast.makeText(getContext(), "화면에 표시할 포스트가 없습니다.", Toast.LENGTH_SHORT).show();
                feedList = new ArrayList<>(); //챌린지를 담는 리스트 생성
                initFeed();
                initAdapter();
            } else {
                canLoad = false;
                isLoading = true;
            }

        } else {
            //가져온 데이터를 for문을 이용하여, feed리스트에 차곡차곡 담는다.
            try {
                for (int i = 0; i < a.size(); i++) {
                    Map Adata = a.get(i).getData();//피드 데이터
                    Map Bdata = b.get(i) == null ? null : b.get(i).getData(); //상단의 댓글 데이터
                    Feed feed = new Feed();
                    feed.setData(Adata, Bdata);
                    newList.add(feed);
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
        }
        //만약 더이상 불러오지 못 할 경우
        if (canLoad == false) {
            isLoading = true;
            if (page > 1) replaceItem(newList);//이미 불러온 데이터가 있는 경우엔 가져온 데이터 만큼의 범위를 늘려준다.
            else { //없는 경우엔 새로운 어댑터에 데이터를 담아서 띄워준다.
                initAdapter(); //어댑터 초기화
                replaceItem(newList);
                initRecyclerView(); //리사이클러뷰에 띄워주기
            }
        }
        //더 불러올 데이터가 있는 경우
        else {
            isLoading = false;
            newList.add(new Feed());//로딩바 표시를 위한 빈 값
            if (page > 1) replaceItem(newList);
            else {
                initAdapter();//어댑터 초기화
                replaceItem(newList);
                initRecyclerView(); //리사이클러뷰에 띄워주기
            }
            page++; //로딩을 다하면 그다음 페이지로 넘어간다.
        }
    }
}