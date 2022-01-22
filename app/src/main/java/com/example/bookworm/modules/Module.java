package com.example.bookworm.modules;

import android.app.Activity;
import android.content.Context;

import android.util.Log;


import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;
import com.example.bookworm.modules.Interface.GetDataInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Module {
    //connection을 위한 변수들 (기본 세팅 값)
    Context context;
    Retrofit retrofit;
    GetDataInterface mainInterface;
    String url; //생성자로 전달받을 url
    Call<String> call = null; //레트로핏이 실제로 실행되는 call객체
    Map<String, String> querys;
    int count = 0;
    int page = 1;
    int check = 0;
    ArrayList<Book> bookList;

    //Constructor
    public Module(Context context, String url, Map<String, String> querys) {
        this.context = context;
        this.url = url;
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        mainInterface = retrofit.create(GetDataInterface.class);
        this.querys = querys;

    }

    //Methods
    public void connect(int idx) {

        switch (idx) {
            case 0:
                querys.put("Start", String.valueOf(page));
                call = mainInterface.string_call(querys);
                break;
            case 1: //추천 책
                break;
        }
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject json = new JSONObject(response.body());
                        Log.d("get", response.body());
                        parseResult(idx, json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("에러 : ", t.getMessage());
            }
        });
    }


    //결과를 보여주는 곳
    private void parseResult(int idx, JSONObject json) throws JSONException {
        switch (idx) {
            case 0://검색 결과 표시
                setResult(json);
                break;
            case 1: //추천 책
                break;

        }
    }

    private void setResult(JSONObject json) throws JSONException //리사이클러 뷰에 결과를 표시하는 함수
    {
        count = Integer.parseInt(json.get("totalResults").toString());
        JSONArray jsonArray = json.getJSONArray("item");
        //리스트만
        if (page == 1) {
            Log.d("count", count + "ehla");
            check = count;
            bookList = new ArrayList<>(); //book을 담는 리스트 생성
        }
        //booklist에 책을 담음
        //이미 한번 검색한 경우 추가 페이지 로딩하도록 설계해야 함.
        //아마 북리스트에 아이템을 계속 추가하면 되지 않을까,,
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Book book = new Book(obj.getString("title"), obj.getString("description"), obj.getString("publisher"), obj.getString("author"), obj.getString("cover"), obj.getString("itemId"));
            bookList.add(book);
        }
        if (check > 20) {
            Log.d("check", check + "개" + bookList.size());
            bookList.add(new Book("", "", "", "", ""));
            check = count - bookList.size();
        } else {
            Log.d("count", count + "개");
//                    ((search_fragment_subActivity_main)context).isLoading=true;
        }
        page++;
    }

    public int getCount() {
        return count;
    }

    public int getPage() {
        return page;
    }

    public ArrayList<Book> getBookList() {
        return bookList;
    }

}
