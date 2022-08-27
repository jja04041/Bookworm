package com.example.bookworm.bottomMenu.search.subactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.feed.items.Feed;
import com.example.bookworm.bottomMenu.feed.subActivity_Feed_Create;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.bottomMenu.search.items.Book;
import com.example.bookworm.core.internet.Module;
import com.example.bookworm.databinding.SubactivitySearchFragmentResultBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class search_fragment_subActivity_result extends AppCompatActivity {
    SubactivitySearchFragmentResultBinding binding;
    ImageView iv_selectedItem;
    final int textViewCount = 8;
    String itemId;
    Button btnBack, btnFeedCreate;
    int textViewID[] = {R.id.tvResTitle, R.id.tvResAuthor, R.id.tvLink, R.id.tvResPublisher, R.id.tvResDescription, R.id.tvResPriceSales, R.id.tvResPriceStandard, R.id.tvResRatingscore};
    String getContent[] = {"title", "author", "link", "publisher", "description", "priceSales", "priceStandard", "customerReviewRank"};
    TextView[] textViews = new TextView[textViewCount];
    TextView tvViewMore;
    RatingBar customerReviewRank;
    Context context, thisContext;
    Book book;

    SearchResultFB searchResultFB;
    private Boolean canLoad = true; //더 로딩이 가능한지 확인하는 변수[자료의 끝을 판별한다.]
    private int page = 1;
    UserInfoViewModel pv;
    private UserInfoViewModel uv;
    private final int LIMIT = 10;
    private ArrayList<Feed> feedList = null;
    private SearchResultAdapter searchResultAdapter;
    private Boolean isRefreshing = false;
    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    private DocumentSnapshot lastVisible;
    Map<String, Object> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivitySearchFragmentResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        book = (Book) intent.getSerializableExtra("data");
        itemId = intent.getExtras().getString("itemid");
        iv_selectedItem = findViewById(R.id.iv_selectedItem);
        tvViewMore = findViewById(R.id.tvViewMore);
        btnBack = findViewById(R.id.btnBack);
        btnFeedCreate = findViewById(R.id.btnFeedCreate);
        customerReviewRank = findViewById(R.id.customerReviewRank);
        context = getApplicationContext();
        for (int i = 0; i < textViewCount; i++) {
            textViews[i] = findViewById(textViewID[i]);
        }
        setItem();

        thisContext = this;

        searchResultFB = new SearchResultFB(thisContext);
        searchResultFB.getData(map, itemId);

        pv = new UserInfoViewModel(thisContext);
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(thisContext)).get(UserInfoViewModel.class);


        ScrollView ScrParents = (ScrollView) findViewById(R.id.ScrParents);
        ScrollView ScrChild = (ScrollView) findViewById(R.id.ScrChild);

        showShimmer(true);

        //제목,저자,출판사가 나오는 스크롤뷰를 터치해도 부모 스크롤뷰가 반응하지 않게 함
        ScrChild.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ScrParents.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //뒤로가기 버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnFeedCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), subActivity_Feed_Create.class);
                intent.putExtra("data", book);
                startActivity(intent);
            }
        });


//        showShimmer(false);


    }


    private void setItem() {
        Map querys = new HashMap<String, String>();
        querys.put("ItemId", itemId);
        querys.put("ItemIdType", "ItemId");
        //기본값
        querys.put("ttbkey", getString(R.string.ttbKey));
        querys.put("output", "js");
        querys.put("SearchTarget", "Book");
        querys.put("Version", "20131101");
        String url = "http://www.aladin.co.kr/ttb/api/"; //API 이용
        Module module = new Module(this, url, querys);
        module.connect(2);
    }

    public void putItem(JSONObject json) throws JSONException {
        Glide.with(this).load(json.getString("cover").replace("coversum", "cover500")).into(iv_selectedItem);
        for (int i = 0; i < textViewCount; i++) {
            String text = getContent[i];
            if (text.equals("link")) {
                textViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //구매하기 누르면 글씨색 보라색으로 바뀌게
                        textViews[2].setTextColor(Color.parseColor("#660099"));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            intent.setData(Uri.parse(json.getString(text)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
            } else if (text.equals("customerReviewRank")) {
                float rank = Float.parseFloat(json.getString(text)) / 2;
                customerReviewRank.setRating(rank);
                textViews[i].setText(String.valueOf(rank));
            } else if (text.equals("description")) {
                String description = json.getString(text);
                if (description.equals("")) description = "책 소개가 없습니다.";
                textViews[i].setText(replace_ltgt(description));
            } else textViews[i].setText(replace_ltgt(json.getString(getContent[i])));
        }

        textViews[1].post(new Runnable() {
            @Override
            public void run() {
                int lineCount = textViews[1].getLineCount();
                // Use lineCount here
                if (lineCount > 0) {
                    if (textViews[1].getLayout().getEllipsisCount(lineCount - 1) > 0) {
                        Log.d("count", textViews[1].getLayout().getEllipsisCount(lineCount - 1) + "dd");
                        tvViewMore.setVisibility(View.VISIBLE);
                        tvViewMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                textViews[1].setMaxLines(Integer.MAX_VALUE);
                                tvViewMore.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        });
    }

    private void initAdapter() {
        searchResultAdapter = new SearchResultAdapter(feedList, thisContext, this);
        //어댑터 리스너
        searchResultAdapter.setListener((holder, view, position) -> {
            //닫힌 챌린지 인경우 표시할 코드 등을 입력해야함.
            //아이템 선택시 실행할 코드를 입력
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
        isEmptyReview(false);
        showShimmer(false);
    }

    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(thisContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.mRecyclerView.setLayoutManager(linearLayoutManager);
        binding.mRecyclerView.setAdapter(searchResultAdapter);
//        initScrollListener(); //무한스크롤
    }


    //shimmer을 켜고 끄고 하는 메소드
    public void showShimmer(Boolean bool) {
        if (bool) {
            binding.llSearch.setVisibility(View.GONE);
            binding.SFLSearch.startShimmer();
            binding.SFLSearch.setVisibility(View.VISIBLE);
        } else {
            binding.llSearch.setVisibility(View.VISIBLE);
            binding.SFLSearch.stopShimmer();
            binding.SFLSearch.setVisibility(View.GONE);
        }
    }

    public void isEmptyReview(boolean bool) {
        showShimmer(false);//시머 종료
        if (bool) {
            binding.mRecyclerView.setVisibility(View.GONE);
            binding.llEmptyReview.setVisibility(View.VISIBLE);
        } else {
            binding.mRecyclerView.setVisibility(View.VISIBLE);
            binding.llEmptyReview.setVisibility(View.GONE);
        }
    }

    private String replace_ltgt(String text) {
        text = text.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&lt", "<")
                .replace("&gt", ">");
        return text;
    }

}