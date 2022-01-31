package com.example.bookworm.Challenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bookworm.R;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;

public class activity_createchallenge extends Activity {

    String bookid = "default";
    Button btn_search, btn_dupli;
    TextView tv_bookname, tv_selectdate_start, tv_selectdate_end;
    Button btn_confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createchallenge);

        activity_createchallenge activity_createchallenge = new activity_createchallenge();


        btn_search = findViewById(R.id.btn_createchallenge_search);
        btn_dupli = findViewById(R.id.btn_createchallenge_duplicheck);
        tv_bookname= findViewById(R.id.tv_createchallenge_bookname);
        tv_selectdate_start =  findViewById(R.id.tv_createchallenge_selectdate_start);
        tv_selectdate_start =  findViewById(R.id.tv_createchallenge_selectdate_end);

        btn_search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), search_fragment_subActivity_main.class);
                intent.putExtra("classindex", 2);
                startActivity(intent);
                btn_search.clearFocus();
            }
        });



    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public void get_Bookid(String bookname)
    {
        //this.id = bookname;
    }
}
