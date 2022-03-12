//SharedPreference를 좀 더 편하게 사용하기 위함
package com.example.bookworm.modules.personalD;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.bookworm.User.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonalD {
    Context context;
    public PersonalD(Context context){
        this.context=context;
    }

    //데이터 저장을 위해서
    public void saveUserInfo(UserInfo userInfo){
        SharedPreferences pref = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String userinfo = gson.toJson(userInfo, UserInfo.class);
        editor.putString("key_user", userinfo);
        editor.commit();
    }

    //데이터 출력을 위해서
    public UserInfo getUserInfo(){
        SharedPreferences pref = context.getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new GsonBuilder().create();
        String key_user = pref.getString("key_user", null);
        UserInfo userInfo=null;
        try {
            JSONObject json = new JSONObject(key_user);
            userInfo = gson.fromJson(json.toString(), UserInfo.class);
        } catch (JSONException e) {
        }
        return userInfo;
    }
}
