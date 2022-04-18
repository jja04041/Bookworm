package com.example.bookworm.Achievement;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Achievement.Adapter.ItemData;
import com.example.bookworm.Achievement.Adapter.RecyclerViewAdapter;
import com.example.bookworm.Bw.BookWorm;
import com.example.bookworm.R;
import com.example.bookworm.Core.UserData.PersonalD;

public class activity_achievement extends AppCompatActivity {

    private Context context;
    private BookWorm bookworm;
    private RecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        context = this;
        bookworm = new PersonalD(context).getBookworm();


        init();
        ShowView();
    }


    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData(int iData) {

        String wormname = "디폴트";

        switch(iData)
        {
            case R.drawable.bw_default:
                wormname = "디폴트";
                break;
            case R.drawable.bw_horror:
                wormname = "공포";
                break;
            case R.drawable.bw_detective:
                wormname = "추리";
                break;

        }

        ItemData itemData = new ItemData(iData, wormname, context);
        adapter.addItem(itemData);

    }


    public void ShowView ()
    {
        int size = bookworm.getWormvec().size();

        for (int i = 0; i < size; ++i) {
            if (null != bookworm.getWormvec().get(i)) {
                getData(bookworm.getWormvec().get(i));

            }
        }
    }
}