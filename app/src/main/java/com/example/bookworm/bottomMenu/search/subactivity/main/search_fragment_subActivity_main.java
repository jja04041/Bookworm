package com.example.bookworm.bottomMenu.search.subactivity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.search.Modules.SearchFB;
import com.example.bookworm.databinding.SubactivitySearchMainBinding;

import java.util.HashMap;
import java.util.Map;

//검색 창을 누르거나 하는 경우 나타나는 화면
//검색 결과를 보여주는 리사이클러뷰가 위치할 예정
//검색 아이템의 세부 내용은 subActivity_result 에서 담당

public class search_fragment_subActivity_main extends AppCompatActivity {

    SubactivitySearchMainBinding binding;
    Context context;
    FragmentManager fm;
    SearchPageBookFragment searchPageBookFragment;
    SearchPageFeedFragment searchPageFeedFragment;
    SearchPageUserFragment searchPageUserFragment;
    SearchPageChallengeFragment searchPageChallengeFragment;

    SearchFB searchFB;

//    BookAdapter bookAdapter;
    private int option_idx = 0;//검색 옵션을 선택하기 위한 변수
    private String[] type = {"Keyword", "Title", "Author", "Publisher"};
    private String[] items = {"제목+저자", "제목", "저자", "출판사"};
    private Map<String, String> querys;
//    public ArrayList<Book> bookList;
//    Module module;
//    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
//    int count = 0, page = 0, check = 0;
//    final int CPP = 10; //Contents Per Page : 페이지당 보이는 컨텐츠의 개수
    Intent intent;
    int classindex = 0;
    SearchPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivitySearchMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        searchFB = new SearchFB(context);

        fm = getSupportFragmentManager();
         adapter = new SearchPageAdapter(fm);

//        fm.beginTransaction().replace(R.id.viewpager, new SearchPageBookFragment(), "0").commitAllowingStateLoss();
//        fm.beginTransaction().add(R.id.viewpager, new SearchPageFeedFragment(), "1").commitAllowingStateLoss();
//        fm.beginTransaction().add(R.id.viewpager, new SearchPageChallengeFragment(), "2").commitAllowingStateLoss();
//        fm.beginTransaction().add(R.id.viewpager, new SearchPageUserFragment(), "3").commitAllowingStateLoss();

        binding.viewpager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);

        binding.tabLayout.getTabAt(0).setText("도서");
        binding.tabLayout.getTabAt(0).setTag("0");
        binding.tabLayout.getTabAt(1).setText("피드");
        binding.tabLayout.getTabAt(2).setText("챌린지");
        binding.tabLayout.getTabAt(3).setText("유저");


//        binding.tabLayout.getTabAt(selected).select();
        //각 위젯 변수와 실제 위젯을 매칭
//        btnBefore = findViewById(R.id.btnBefore);
//        btnSearch = findViewById(R.id.btnSearch);
//        spinner1 = findViewById(R.id.spinner1);
//        edtSearch = findViewById(R.id.edtSearch);
//        mRecyclerView = findViewById(R.id.recyclerView);
//        bookList = new ArrayList<>();
        intent = getIntent();

        //spinner를 위한 adapter 생성
        ArrayAdapter<String> dap = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        binding.spinner1.setAdapter(dap); //Adapter 적용(Spinner에 값 세팅)

        //각 이벤트별 리스너
        //옵션 선택
        binding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int idx, long l) {
                option_idx = idx; //Spinner에서 아이템 선택시, 옵션 인덱스를 변경하도록 함.
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //이전으로 돌아가기
        binding.btnBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //이전으로 돌아감.
            }
        });

        //검색 기능
        //검색 버튼을 누른 이후에, 결과를 표출한다.(RecyclerView)
        binding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        setItems();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    setItems();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //리사이클러뷰 관련 설정


    }//onCreate End


    //아이템 세팅
    public void setItems() throws InterruptedException {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.edtSearch.getWindowToken(), 0);
        binding.edtSearch.clearFocus();


            if (binding.edtSearch.getText().toString().replaceAll(" ", "").equals("") != true) {

                String keyword = binding.edtSearch.getText().toString();

                // 도서 검색처리
                querys = new HashMap<>();
                querys.put("Query", binding.edtSearch.getText().toString());
                querys.put("QueryType", type[option_idx]);
                //기본값
                this.querys.put("ttbkey", getString(R.string.ttbKey));
                this.querys.put("MaxResults", "10"); //최대 길이
                this.querys.put("output", "js");
                this.querys.put("SearchTarget", "Book");
                this.querys.put("Version", "20131101");
                String url = "http://www.aladin.co.kr/ttb/api/"; //API 이용

//            module = new Module(this, url, querys);
//            module.connect(0);

                for(int i=0; i<4; ++i) {
                    adapter.instantiateItem(binding.viewpager ,i);
                }


                searchPageBookFragment = ((SearchPageBookFragment) ((SearchPageAdapter) binding.viewpager.getAdapter()).getItem(0));
                searchPageFeedFragment = ((SearchPageFeedFragment) ((SearchPageAdapter) binding.viewpager.getAdapter()).getItem(1));
                searchPageChallengeFragment = ((SearchPageChallengeFragment) ((SearchPageAdapter) binding.viewpager.getAdapter()).getItem(2));
                searchPageUserFragment = ((SearchPageUserFragment) ((SearchPageAdapter) binding.viewpager.getAdapter()).getItem(3));

                // 책 검색
                searchPageBookFragment.searchBook(context, url, querys);
                // 피드 검색
                searchFB.getFeed(keyword, searchPageFeedFragment);
                // 챌린지 검색
                searchFB.getChallenge(keyword, searchPageChallengeFragment);
                // 유저 검색
                searchFB.getUser(keyword, searchPageUserFragment);


            } else {
                Toast.makeText(getApplicationContext(), "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show();
            }
    }
}//subActivity End