package com.example.bookworm.bottomMenu.search.subactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.search.items.Book;
import com.example.bookworm.bottomMenu.search.items.BookAdapter;
import com.example.bookworm.bottomMenu.search.items.OnBookItemClickListener;
import com.example.bookworm.bottomMenu.search.items.RecomBookAdapter;
import com.example.bookworm.core.internet.Module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    int count = 0, page = 0, check = 0;
    final int CPP = 10; //Contents Per Page : 페이지당 보이는 컨텐츠의 개수
    Intent intent;
    int classindex = 0;


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
        bookList = new ArrayList<>();
        intent = getIntent();
        classindex = intent.getIntExtra("classindex", 0);

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

    //아이템 세팅
    private void setItems() throws InterruptedException {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        edtSearch.clearFocus();

        if (edtSearch.getText().toString().replaceAll(" ", "").equals("") != true) {
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
        } else {
            Toast.makeText(getApplicationContext(), "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show();
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
                            page = module.getPage();
                            count = module.getCount();
                            bookAdapter.deleteLoading();
                            if ((page * CPP) < count) {
                                module.connect(0);
                            }
                            isLoading = true;
                        }
                    } catch (NullPointerException e) {

                    }
                }

            }
        });

    }


    //module에서 사용하는 함수
    public void moduleUpdated(JSONArray jsonArray) throws JSONException {
        page = module.getPage();
        count = module.getCount();
        int beforeSize=bookList.size();
        if (page == 1) {
            check = count;
            Log.d("cje",String.valueOf(check));
            bookList = new ArrayList<>(); //book을 담는 리스트 생성
        }
        if (count == 0) {
            //검색결과가 없을 경우엔 리사이클러 뷰를 비움.
            bookList = new ArrayList<>();
            bookAdapter = new BookAdapter(bookList, this);
            initRecyclerView();
            Toast.makeText(this, "검색결과가 없습니다", Toast.LENGTH_SHORT).show();
        } else {
            //booklist에 책을 담음
            //이미 한번 검색한 경우 추가 페이지 로딩하도록 설계해야 함.
            //아마 북리스트에 아이템을 계속 추가하면 되지 않을까,,
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Book book = new Book(obj.getString("title"), obj.getString("categoryName") ,obj.getString("description"), obj.getString("publisher"), obj.getString("author"), obj.getString("cover"), obj.getString("itemId"));
                bookList.add(book);
            }
            if (check > 20 && page < 20) {
                bookList.add(new Book("","", "", "", "", ""));
                this.check = count - bookList.size();
            } else isLoading = true;

            if (page != 1 && page < 20) {
                isLoading = false;
                bookAdapter.notifyItemRangeChanged(beforeSize, bookList.size() -beforeSize);
            } else {
                bookAdapter = new BookAdapter(bookList, this);
                bookAdapter.setListener(new OnBookItemClickListener() {
                    @Override
                    public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position) {

                        if (classindex == 0) {
                            Intent intent = new Intent(getApplicationContext(), search_fragment_subActivity_result.class);
                            intent.putExtra("itemid", bookList.get(position).getItemId());
                            startActivity(intent);
                        } else if (classindex == 1) {
                            finish();
                        } else if (classindex == 2) {
                            Intent intent=new Intent();
                            intent.putExtra("data",bookList.get(position));
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }
                    }

                    @Override
                    public void onItemClick(RecomBookAdapter.ItemViewHolder holder, View view, int position) {

                    }
                });
                initRecyclerView(); //initialize RecyclerView
            }
            this.page++;
            module.setPage(page);
        }

    }
}//subActivity End