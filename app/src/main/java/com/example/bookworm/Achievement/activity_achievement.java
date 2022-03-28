package com.example.bookworm.Achievement;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Achievement.Adapter.ItemData;
import com.example.bookworm.Achievement.Adapter.RecyclerViewAdapter;
import com.example.bookworm.R;

public class activity_achievement extends AppCompatActivity {
        RecyclerViewAdapter adapter;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                init();
                getData();

        }

        private void init(){
                RecyclerView recyclerView = findViewById(R.id.recyclerView);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(linearLayoutManager);

                adapter = new RecyclerViewAdapter();
                recyclerView.setAdapter(adapter);

        }

        private void getData(){
                ItemData itemData = new ItemData(R.drawable.bw_default, "디폴트");
                adapter.addItem(itemData);
                itemData = new ItemData(R.drawable.bw_horror, "호러");
                adapter.addItem(itemData);
                itemData = new ItemData(R.drawable.bw_detective, "추리");
                adapter.addItem(itemData);
        }
}