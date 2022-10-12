package com.example.bookworm.bottomMenu.search.searchtest.modules

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface SearchRetrofitInterface {
    //추천 도서
    @GET("ItemList.aspx")
    suspend fun getRecom(
            @QueryMap querys: Map<String, String>,
    ): Response<String>

    //도서 검색
    @GET("ItemSearch.aspx")
    suspend fun getResult(
            @QueryMap querys: Map<String, String>,
    ): Response<String>

    //도서별 상세 정보
    @GET("ItemLookUp.aspx")
    suspend fun getItem(
            @QueryMap querys: Map<String, String>,
    ): Response<String>
}