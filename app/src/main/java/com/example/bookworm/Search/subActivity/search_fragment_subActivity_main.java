package com.example.bookworm.Search.subActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.items.BookAdapter;
import com.example.bookworm.Search.items.OnBookItemClickListener;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_result;
import com.example.bookworm.modules.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//검색 창을 누르거나 하는 경우 나타나는 화면
//검색 결과를 보여주는 리사이클러뷰가 위치할 예정
//검색 아이템의 세부 내용은 subActivity_result 에서 담당

public class search_fragment_subActivity_main extends AppCompatActivity {
    Button btnBefore, btnSearch;
    Spinner spinner1;
    EditText edtSearch;
    RecyclerView mRecyclerView;
    BookAdapter bookAdapter;
    private int option_idx = 0;//검색 옵션을 선택하기 위한 변수
    private String[] type = {"Keyword", "Title", "Author", "Publisher"};
    private String[] items = {"제목+저자", "제목", "저자", "출판사"};
    private Map<String, String> querys;
    public ArrayList<Book> bookList;
    Module module;
    public boolean isLoading = false;
    int count = 0, page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_search_main);

        //각 위젯 변수와 실제 위젯을 매칭
        btnBefore = findViewById(R.id.btnBefore);
        btnSearch = findViewById(R.id.btnSearch);
        spinner1 = findViewById(R.id.spinner1);
        edtSearch = findViewById(R.id.edtSearch);
        mRecyclerView = findViewById(R.id.recyclerView);
        bookList=new ArrayList<>();

        //spinner를 위한 adapter 생성
        ArrayAdapter<String> dap = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner1.setAdapter(dap); //Adapter 적용(Spinner에 값 세팅)

        //각 이벤트별 리스너
        //옵션 선택
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int idx, long l) {
                option_idx = idx; //Spinner에서 아이템 선택시, 옵션 인덱스를 변경하도록 함.
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //이전으로 돌아가기
        btnBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //이전으로 돌아감.
            }
        });

        //검색 기능
        //검색 버튼을 누른 이후에, 결과를 표출한다.(RecyclerView)
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        btnSearch.setOnClickListener(new View.OnClickListener() {
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

    private void setItems() throws InterruptedException {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        edtSearch.clearFocus();

        if (edtSearch.getText().toString().replaceAll(" ","").equals("")!=true) {
            querys = new HashMap<>();
            querys.put("Query", edtSearch.getText().toString());
            querys.put("QueryType", type[option_idx]);
            //기본값
            this.querys.put("ttbkey", getString(R.string.ttbKey));
            this.querys.put("MaxResults", "10"); //최대 길이
            this.querys.put("output", "js");
            this.querys.put("SearchTarget", "Book");
            this.querys.put("Version", "20131101");
            String url = "http://www.aladin.co.kr/ttb/api/"; //API 이용

            module = new Module(this, url, querys);
            module.connect(0);
            moduleUpdated();
        }else{
            Toast.makeText(getApplicationContext(),"검색어를 입력해 주세요",Toast.LENGTH_SHORT).show();
        }

    }

    //리사이클러뷰를 초기화
    private void initRecyclerView() {
        mRecyclerView.setAdapter(bookAdapter);
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

                        if (layoutManager != null && lastVisibleItemPosition == bookAdapter.getItemCount() - 1) {
                            page=module.getPage();
                            count=module.getCount();

                            bookAdapter.deleteLoading();
                            if ((page * 10) < count) {
                                Log.d("얼", "우울ㄴㄹ" + page + count);
                                //리스트 마지막
                                //여기서 로딩바를 보여주고, 새로운 아이템을 로딩해야 함.
                                //무한 스크롤 가능 => 로딩바 구현
                                module.connect(0);
                                moduleUpdated();
                            }
                            isLoading = true;
                        }
                    } catch (NullPointerException | InterruptedException ex) {

                    }
                }

            }
        });

    }

    private void moduleUpdated() throws InterruptedException {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    count = module.getCount();
                    page= module.getPage();

                    if (count == 0) {
                        bookList = new ArrayList<>();
                        bookAdapter = new BookAdapter(bookList, getApplicationContext());
                        initRecyclerView();
                        Toast.makeText(getApplicationContext(),"검색 결과가 없습니다",Toast.LENGTH_SHORT).show();
                    } else {
                        bookList=module.getBookList();
                        if (page != 2) {
                            isLoading = false;
                            bookAdapter.notifyItemRangeChanged(0, bookList.size(), null);
                        } else {
                            bookAdapter = new BookAdapter(bookList, getApplicationContext());
                            bookAdapter.setListener(new OnBookItemClickListener() {
                                @Override
                                public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position) {
                                    Intent intent = new Intent(getApplicationContext(), search_fragment_subActivity_result.class); //선택한 아이템에 대한 상세 정보를 표가
                                    intent.putExtra("itemid", bookList.get(position).getItemId());
                                    startActivity(intent);
                                }
                            });
                            initRecyclerView(); //initialize RecyclerView
                        }
                    }
                } catch (NullPointerException e){
                    Log.d("null","ㅇㅇ");

                }
            }
        }, 1000);

    }
}//subActivity End