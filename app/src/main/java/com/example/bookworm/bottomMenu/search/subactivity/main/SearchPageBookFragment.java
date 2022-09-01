package com.example.bookworm.bottomMenu.search.subactivity.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.bottomMenu.search.items.book.Book;
import com.example.bookworm.bottomMenu.search.items.book.BookAdapter;
import com.example.bookworm.bottomMenu.search.items.book.OnBookItemClickListener;
import com.example.bookworm.bottomMenu.search.items.book.RecomBookAdapter;
import com.example.bookworm.bottomMenu.search.subactivity.result.search_fragment_subActivity_result;
import com.example.bookworm.core.internet.Module;
import com.example.bookworm.databinding.FragmentSearchPageBookBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SearchPageBookFragment extends Fragment {

    FragmentSearchPageBookBinding binding;
    FragmentManager fm;

    BookAdapter bookAdapter;
//    private int option_idx = 0;//검색 옵션을 선택하기 위한 변수
//    private String[] type = {"Keyword", "Title", "Author", "Publisher"};
//    private String[] items = {"제목+저자", "제목", "저자", "출판사"};
    private Map<String, String> querys;
    public ArrayList<Book> bookList;
    Module module;
    Context mContext;
    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    int count = 0, page = 0, check = 0;
    final int CPP = 10; //Contents Per Page : 페이지당 보이는 컨텐츠의 개수
    Intent intent;
    int classindex = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchPageBookBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

//        ((search_fragment_subActivity_main)getActivity()).testss();

        bookList = new ArrayList<>();
//        ((search_fragment_subActivity_main) context).setItems();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //@@@@@@@@@@@@@@@@@@

    //@@@@@@@@@@@@@@@@@@@@@


    public void searchBook(Context context, String url, Map querys) {
        mContext = context;
        module = new Module(mContext, url, querys);
        module.connect(0);
//        test();
    }

    //리사이클러뷰를 초기화
    private void initRecyclerView() {
        binding.recyclerView.setAdapter(bookAdapter);
        initScrollListener(); //무한스크롤
    }

    //    리사이클러뷰 스크롤 초기화
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
        int beforeSize = bookList.size();
        if (page == 1) {
            check = count;
            Log.d("cje", String.valueOf(check));
            bookList = new ArrayList<>(); //book을 담는 리스트 생성
        }
        if (count == 0) {
            //검색결과가 없을 경우엔 리사이클러 뷰를 비움.
            bookList = new ArrayList<>();
            bookAdapter = new BookAdapter(bookList, mContext);
            initRecyclerView();
            Toast.makeText(mContext, "검색결과가 없습니다", Toast.LENGTH_SHORT).show();
        } else {
            //booklist에 책을 담음
            //이미 한번 검색한 경우 추가 페이지 로딩하도록 설계해야 함.
            //아마 북리스트에 아이템을 계속 추가하면 되지 않을까,,
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Book book = new Book(obj.getString("title"), obj.getString("categoryName"), obj.getString("description"), obj.getString("publisher"), obj.getString("author"), obj.getString("cover"), obj.getString("itemId"));
                bookList.add(book);
            }
            if (check > 20 && page < 20) {
                bookList.add(new Book("", "", "", "", "", ""));
                this.check = count - bookList.size();
            } else isLoading = true;

            if (page != 1 && page < 20) {
                isLoading = false;
                bookAdapter.notifyItemRangeChanged(beforeSize, bookList.size() - beforeSize);
            } else {
                bookAdapter = new BookAdapter(bookList, mContext);
                bookAdapter.setListener(new OnBookItemClickListener() {
                    @Override
                    public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position) {

                        if (classindex == 0) {
                            Intent intent = new Intent(mContext, search_fragment_subActivity_result.class);

                            intent.putExtra("itemid", bookList.get(position).getItemId());
                            intent.putExtra("data", bookList.get(position));
                            getActivity().setResult(Activity.RESULT_OK, intent);

                            startActivity(intent);
                        } else if (classindex == 1) {
                            getActivity().finish();
                        } else if (classindex == 2) {
                            Intent intent = new Intent();
                            intent.putExtra("data", bookList.get(position));
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
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



}