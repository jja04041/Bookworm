package com.example.bookworm.Feed;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.bookworm.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class subActivity_Feed_Create extends AppCompatActivity {
    EditText edtFeedContent;
    Button btnAdd;
    LinearLayout layout;
    ArrayList<Button> btn;
    int label = 0;
    //라벨은 알럿 다이어그램을 통해 입력을 받고, 선택한 값으로 라벨이 지정됨

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_feed_create);
        edtFeedContent = findViewById(R.id.edtFeedContent);
        btnAdd = findViewById(R.id.btnAdd);
        layout = findViewById(R.id.llLabel);
        btn = new ArrayList<>();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (label < 3) {
                    Button btn = new Button(getApplicationContext());
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    btn.setBackground(getDrawable(R.drawable.label_design));
                    btn.setText("안녕");
                    Log.d("label", label + "개");
                    btn.setEnabled(false);
                    AddBtn(btn);
                    if (label == 2) {
                        btnAdd.setVisibility(View.INVISIBLE);
                    }
                    label++;
                }
            }
        });


    }

    public void AddBtn(Button button) {
        int count = layout.getChildCount(); //현재 버튼 개수
        btn.add(button);
        for (int i = 0; i < count; i++) {
            btn.add((Button) layout.getChildAt(i));
        }
        layout.removeAllViews();
        for (Button i : btn) {
            layout.addView(i);
        }
        btn.clear();
    }

    public void RemoveBtn(int idx) {
    }
}