package com.example.bookworm.Feed;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.bookworm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class subActivity_Feed_Create extends AppCompatActivity {
    EditText edtFeedContent;
    Button btnAdd, btnFinish;
    LinearLayout layout;
    ArrayList<Button> btn;
    int label = 0;
    String a = "가나다라마바아";
    //라벨은 알럿 다이어그램을 통해 입력을 받고, 선택한 값으로 라벨이 지정됨

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_feed_create);
        edtFeedContent = findViewById(R.id.edtFeedContent);
        btnAdd = findViewById(R.id.btnAdd);
        btnFinish = findViewById(R.id.btnFinish);
        layout = findViewById(R.id.llLabel);
        btn = new ArrayList<>();

        btn.add(btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ShowDialog 만들기
                saveData();
//                //라벨생성
//                setLabel(a,Color.parseColor("#EFDDDD"));
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                readData();
            }
        });


    }

    private void saveData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "john");
        user.put("age", 22);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + o.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

    private void readData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void labelCtrl(int mode, Button button) {

        switch (mode) {
            case 0: //ADD
                btn.add(button);
                if (btn.size() == 4) {
                    btn.remove(btnAdd);
                }
                break;
            case 1: //Remove
                btn.remove(button);
                if (!btn.contains(btnAdd)) {
                    btn.add(0, btnAdd);
                }
                label--;
                break;
        }
        layout.removeAllViews();
        for (Button i : btn) {
            layout.addView(i);
        }
        Log.d("arrayNow", btn.toString());
//        //레이아웃을 새로고침 함.

    }

    private void setLabel(String Text, int btnColor) {
        if (label < 3) {
            //버튼 세팅
            Button btn = new Button(getApplicationContext());
            btn.setClickable(true);
            //label 디자인
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, 150);
            layoutParams.setMargins(10, 10, 10, 10);
            btn.setLayoutParams(layoutParams);
            btn.setPadding(10, 0, 10, 0);
            Drawable unwrappedDrawable = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.label_design);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, btnColor);
            btn.setBackground(wrappedDrawable);

            btn.setText(Text + "\t\t\tX");
            //클릭 불가능하게 만들기
//            btn.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    labelCtrl(1,btn);
//                    return true;
//                }
//            });
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    labelCtrl(1, btn);
                }
            });
            //라벨 추가
            labelCtrl(0, btn);
            label++;
        }
    }

}