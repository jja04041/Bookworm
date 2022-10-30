package com.example.bookworm.notification;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SupportFCMService {

    @POST("/fcm/send")
    Call<String> createPost(@Body RequestBody body);

}
