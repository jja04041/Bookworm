package com.example.bookworm.notification;

import android.content.Context;
import android.util.Log;

import com.example.bookworm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SupportFCMServiceImpl {
    Context context;
    OkHttpClient.Builder httpClient;
    Retrofit retrofit;
    SupportFCMService mainInterface;
    URL Url;

    //init
    SupportFCMServiceImpl(Context context) throws MalformedURLException {
        this.context = context;
        Url = new URL(context.getString(R.string.FCM_MESSAGE_URL));
        httpClient = new OkHttpClient().newBuilder();
        httpClient.addInterceptor(
                chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    String[] headers = {"Authorization", "key=" + context.getString(R.string.SERVER_KEY), "Accept", "application/json", "Content-type", "application/json"};
                    requestBuilder.headers(Headers.of(headers));
                    return chain.proceed(requestBuilder.build());
                }
        );
    }

    //메시지 보내기
    public void sendMessage(final String fcmtoken, final String message) {
        JSONObject root = new JSONObject();
        JSONObject notification = new JSONObject();
        try {
            notification.put("body", message);
            notification.put("title", "BOOKWORM");
            root.put("notification", notification);
            root.put("to", fcmtoken);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), root.toString());
            retrofit = new Retrofit.Builder()
                    .baseUrl(Url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(httpClient.build())
                    .build();
            mainInterface = retrofit.create(SupportFCMService.class);
            mainInterface.createPost(body)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d("메시지 전송 완료", String.valueOf(response.isSuccessful()));
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
