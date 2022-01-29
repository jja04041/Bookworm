package com.example.bookworm.modules.Interface;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;


public interface GetDataInterface {
    //추천 도서
    @GET("ItemList.aspx")
    Call<String> getRecom(
            @QueryMap Map<String, String> querys

    );
    //도서 검색
    @GET("ItemSearch.aspx")
    Call<String> getResult(
            @QueryMap Map<String, String> querys
    );

    //도서별 상세 정보
    @GET("ItemLookUp.aspx")
    Call<String> getItem(
            @QueryMap Map<String, String> querys
    );

}
