package com.example.bookworm.Achievement;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Achievement.Adapter.ItemData;
import com.example.bookworm.Achievement.Adapter.RecyclerViewAdapter;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;

public class activity_achievement extends AppCompatActivity {

        protected Context current_context;
        protected FBModule fbModule;
        protected UserInfo userinfo;
        protected RecyclerViewAdapter adapter;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_achievement);


                current_context = this;
                fbModule = new FBModule(current_context);
                userinfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.

                init();
                CheckAchievement();
        }

        private void init() {
                RecyclerView recyclerView = findViewById(R.id.recyclerView);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(linearLayoutManager);

                adapter = new RecyclerViewAdapter();
                recyclerView.setAdapter(adapter);

        }

        private void getData(int iData) {
                ItemData itemData = new ItemData(iData, "디폴트");
                adapter.addItem(itemData);
        }


        public void CheckAchievement() {
                int size = userinfo.getWormvec().size();

                for (int i = 0; i < size; ++i) {
                        if (null != userinfo.getWormvec().get(i)) {
                                getData(userinfo.getWormvec().get(i));

                        }
                }


        }
}