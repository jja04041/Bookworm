package com.example.bookworm.modules.Interface;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;


public interface GetDataInterface {
    @GET("ItemList.aspx")
    Call<String> bestSeller(
            @QueryMap Map<String,String> querys

    );
    @GET("ItemSearch.aspx")
    Call<String> string_call(
            @QueryMap Map<String,String> querys
    );

    @GET("ItemLookUp.aspx")
    Call<String> getItem(
            @QueryMap Map<String,String> querys
    );

}
