package com.example.bookworm.modules;

import android.app.Activity;
import android.content.Context;

import android.util.Log;


import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_result;
import com.example.bookworm.fragments.fragment_search;
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
                Log.d("검색페이지","페이지 : "+page);
                querys.put("Start", String.valueOf(page));
                call = mainInterface.getResult(querys);
                break;
            case 1: //추천 책
                call = mainInterface.getRecom(querys);
                break;
            case 2:
                call=mainInterface.getItem(querys);
        }
        //이곳에 로딩중이라는 alertDialog 넣어도 좋을듯
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        //이곳에서 alertDialog 지우면 됨.
                        Log.d("result",response.body());
                        JSONObject json = new JSONObject(response.body());
                        parseResult(idx, json);
                    } catch (JSONException | InterruptedException e) {
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
    private void parseResult(int idx, JSONObject json) throws JSONException, InterruptedException {
        switch (idx) {
            case 0://검색 결과 표시
                setResult(json);
                break;
            case 1: //추천 책(카테고리 별)
                JSONArray jsonArray = json.getJSONArray("item");
                ((fragment_search)((MainActivity)context).getSupportFragmentManager().findFragmentByTag("1")).updateRecom(jsonArray);
                break;
            case 2: //책 상세 내용
                JSONObject jsonObject= json.getJSONArray("item").getJSONObject(0);
                ((search_fragment_subActivity_result)context).putItem(jsonObject);
                break;

        }
    }

    private void setResult(JSONObject json) throws JSONException, InterruptedException //리사이클러 뷰에 결과를 표시하는 함수
    {
        count = Integer.parseInt(json.get("totalResults").toString());
        JSONArray jsonArray = json.getJSONArray("item");
        ((search_fragment_subActivity_main)context).moduleUpdated(jsonArray); //이후의 작업은 서브액티비티의 메소드에서 진행
    }

    public void setPage(int page) {
        this.page = page;
    }


    public int getCount() {
        return count;
    }

    public int getPage() {
        return page;
    }


}
