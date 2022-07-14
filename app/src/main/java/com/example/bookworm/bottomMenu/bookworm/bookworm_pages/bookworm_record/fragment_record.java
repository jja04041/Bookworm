    package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_record;

    import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bookworm.bottomMenu.Feed.items.Feed;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.databinding.FragmentRecordBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


    public class fragment_record extends Fragment {

        private FragmentRecordBinding binding;
        private UserInfoViewModel uv;

        RecordFB recordFB;
        private Boolean canLoad = true; //더 로딩이 가능한지 확인하는 변수[자료의 끝을 판별한다.]
        private int page = 1;
        Feed feed;
        UserInfoViewModel pv;
        private final int LIMIT = 10;
        private ArrayList<Feed> feedList = null;
        private RecordAdapter recordAdapter;
        private Boolean isRefreshing = false;
        public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
        private DocumentSnapshot lastVisible;
        Map<String, Object> map;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            binding = FragmentRecordBinding.inflate(inflater);
            View view = binding.getRoot();
            pv = new UserInfoViewModel(getContext());
            uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(getContext())).get(UserInfoViewModel.class);

            showShimmer(true);

            recordFB = new RecordFB(getContext());


            pv.getUser(null, false);

            //데이터 수정을 감지함
            pv.getData().observe(getViewLifecycleOwner(), userinfo -> {

                recordFB.getData(map, userinfo.getToken());

            });


            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            uv.getUser(null, false);
            uv.getData().observe(this, userInfo -> {
                String[] genre = {
                        "자기계발",
                        "소설",
                        "육아",
                        "어린이",
                        "청소년",
                        "사회",
                        "과학",
                        "인문",
                        "생활",
                        "공부",
                        "만화"};
                TextView bookworm[] = {
                        binding.tvBookworm1,
                        binding.tvBookworm2,
                        binding.tvBookworm3,
                        binding.tvBookworm4,
                        binding.tvBookworm5,
                        binding.tvBookworm6,
                        binding.tvBookworm7,
                        binding.tvBookworm8,
                        binding.tvBookworm9,
                        binding.tvBookworm10,
                        binding.tvBookworm11};
                for (int i = 0; i < genre.length; i++) {
                    if (userInfo.getGenre().get(genre[i]) != null)
                        bookworm[i].setText(genre[i] + " : " + userInfo.getGenre().get(genre[i]));
                }
                uv.getBookWorm(userInfo.getToken());
            });
            uv.getBwdata().observe(this, bw -> {
                binding.tvBookwormBookcount.setText("읽은 권 수 : " + bw.getReadcount());
            });


        }

        private void initAdapter() {
            recordAdapter = new RecordAdapter(feedList, getContext());
            //어댑터 리스너
            recordAdapter.setListener((holder, view, position) -> {

            });
        }

        //RecordFB에서 사용할 함수
        public void moduleUpdated(List<DocumentSnapshot> a) {
            feedList = new ArrayList<>();
            try {
                for (DocumentSnapshot snapshot : a) {
                    Map data = snapshot.getData();
                    Feed feed = new Feed();
                    feed.setFeedData(data);
                    feedList.add(feed);
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
            showShimmer(false);
        }

        public void initRecyclerView() {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            binding.mRecyclerView.setLayoutManager(linearLayoutManager);
            binding.mRecyclerView.setAdapter(recordAdapter);
    //        initScrollListener(); //무한스크롤
        }

        //shimmer을 켜고 끄고 하는 메소드
        private void showShimmer(Boolean bool) {
            if (bool) {
                binding.llRecord.setVisibility(View.GONE);
                binding.SFLRecord.startShimmer();
                binding.SFLRecord.setVisibility(View.VISIBLE);
            } else {
                binding.llRecord.setVisibility(View.VISIBLE);
                binding.SFLRecord.stopShimmer();
                binding.SFLRecord.setVisibility(View.GONE);
            }
        }

        public void isEmptyRecord(boolean bool) {
            showShimmer(false);//시머 종료
            if (bool) {
                binding.mRecyclerView.setVisibility(View.GONE);
                binding.llEmptyRecord.setVisibility(View.VISIBLE);
            } else {
                binding.mRecyclerView.setVisibility(View.VISIBLE);
                binding.llEmptyRecord.setVisibility(View.GONE);
            }
        }
    }