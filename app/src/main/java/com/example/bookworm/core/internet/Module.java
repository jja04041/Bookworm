package com.example.bookworm.core.internet;

import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import com.example.bookworm.bottomMenu.profile.views.ProfileModifyActivity;
import com.example.bookworm.core.internet.interfaces.GetDataInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    Call call = null; //레트로핏이 실제로 실행되는 call객체
    Map<String, String> querys;
    Map<String, Object> query;
    int count = 0;
    int page = 1;

    //Constructor
    public Module(Context context, String url, Map querys) {
        this.context = context;
        this.url = url;
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        mainInterface = retrofit.create(GetDataInterface.class);
        if (querys.get("rqbody") != null) this.query = (Map<String, Object>) querys;
        else this.querys = (Map<String, String>) querys;
    }

    //Methods
    public void connect(int idx) {

        switch (idx) {
            case 0: //검색한 책 목록
                querys.put("Start", String.valueOf(page));
                call = mainInterface.getResult(querys);
                break;
            case 1: //추천 책목록
                call = mainInterface.getRecom(querys);
                break;
            case 2://개별 도서 검색
                call = mainInterface.getItem(querys);
                break;
            case 3://피드 이미지 업로드
                RequestBody name = (RequestBody) query.get("rqname");
                MultipartBody.Part body = (MultipartBody.Part) query.get("rqbody");
                call = mainInterface.postImage(body, name);
                break;
            case 4://피드 이미지 삭제
                break;
        }


    }


    //결과를 보여주는 곳
    private void parseResult(int idx, JSONObject json) throws JSONException, InterruptedException {
        switch (idx) {
            case 0://검색 결과 표시
                setResult(json);
                break;
            case 1: //추천 책(카테고리 별)
                JSONArray jsonArray = json.getJSONArray("item");
                if (jsonArray != null)
//                    ((fragment_search) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("1")).updateRecom(jsonArray);
                    break;
            case 2: //책 상세 내용
                JSONObject jsonObject = json.getJSONArray("item").getJSONObject(0);

                break;

        }
    }

    private void setResult(JSONObject json) throws JSONException, InterruptedException //리사이클러 뷰에 결과를 표시하는 함수
    {
        count = Integer.parseInt(json.get("totalResults").toString());
        JSONArray jsonArray = json.getJSONArray("item");

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
