package com.example.bookworm.core.internet.interfaces

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface GetDataInterface {
    //추천 도서
    @GET("ItemList.aspx")
    fun getRecom(
        @QueryMap querys: Map<String?, String?>?
    ): Call<String?>?

    //도서 검색
    @GET("ItemSearch.aspx")
    fun getResult(
        @QueryMap querys: Map<String?, String?>?
    ): Call<String?>?

    //도서별 상세 정보
    @GET("ItemLookUp.aspx")
    fun getItem(
        @QueryMap querys: Map<String?, String?>?
    ): Call<String?>?

    //이미지 업로드
    @Multipart
    @POST("/upload")
    fun postImage(
        @Part image: MultipartBody.Part?,
        @Part("upload") name: RequestBody?
    ): Call<String?>?

    @Multipart
    @POST("/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part?,
        @Part("upload") name: RequestBody?
    ): Response<String?>?


}