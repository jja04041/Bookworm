package com.example.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
//import com.example.bookworm.modules.module_search;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.items.BookAdapter;
import com.example.bookworm.Search.items.OnBookItemClickListener;
import com.example.bookworm.Search.items.RecomBookAdapter;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_result;
import com.example.bookworm.modules.Module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import com.example.bookworm.modules.module_search;
//탐색 탭

public class fragment_search extends Fragment {
    EditText edtSearchBtn;
    RecyclerView favRecyclerView;
    Module favmodule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        edtSearchBtn = v.findViewById(R.id.edtSearchBtn);
        favRecyclerView = v.findViewById(R.id.favRecyclerView);
        edtSearchBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
                    Intent intent = new Intent(getActivity(), search_fragment_subActivity_main.class);
                    startActivity(intent);
                    edtSearchBtn.clearFocus();
                }
            }
        });

        setItems();

        return v;
    }

    private void setItems() {
        Map querys = new HashMap<>();
        //기본값
        querys.put("ttbkey", getString(R.string.ttbKey));
        querys.put("QueryType", "Bestseller");
        querys.put("MaxResults", "10"); //최대 길이
        querys.put("output", "js");
        querys.put("SearchTarget", "Book");
        querys.put("Version", "20131101");
        String url = "http://www.aladin.co.kr/ttb/api/"; //API 이용
        favmodule = new Module(getContext(), url, querys);
        favmodule.connect(1);
    }

    public void updateRecom(JSONArray json) throws JSONException {
        ArrayList<Book> bookList=new ArrayList<>();
        //책 입력
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            Book book = new Book(obj.getString("title"), obj.getString("categoryName") ,obj.getString("description"), obj.getString("publisher"), obj.getString("author"), obj.getString("cover"), obj.getString("itemId"));
            bookList.add(book);
        }
        RecomBookAdapter bookAdapter = new RecomBookAdapter(bookList, getContext());
        bookAdapter.setListener(new OnBookItemClickListener() {
            @Override
            public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position) {

            }
            @Override
            public void onItemClick(RecomBookAdapter.ItemViewHolder holder, View view, int position) {
                Intent intent = new Intent(getContext(), search_fragment_subActivity_result.class);
                intent.putExtra("itemid", bookList.get(position).getItemId());
                startActivity(intent);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1,GridLayoutManager.HORIZONTAL, false);
        favRecyclerView.setLayoutManager(gridLayoutManager);//그리드 뷰로 보이게 함.
        favRecyclerView.setAdapter(bookAdapter);
    }
}