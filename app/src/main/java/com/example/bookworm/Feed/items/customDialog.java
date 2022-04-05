package com.example.bookworm.Feed.items;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.databinding.LayoutCommentItemBinding;
import com.example.bookworm.modules.FBModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class customDialog extends Dialog {
    Context current_context;
    final int LIMIT=10;
    FBModule fbModule;

    public customDialog(@NonNull Context context) {
        super(context);
        current_context = context;
        fbModule= new FBModule(current_context);
    }

    public void setView(int index) {
        if (index == 0) {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
            this.setContentView(R.layout.custom_dialog_comment);
        }
    }

    public void showcustomDialog(Feed item, UserInfo nowUser) {
        this.show(); // 다이얼로그 띄우기
        //Initialize
        EditText edtComment = findViewById(R.id.edtComment);
        TextView tvCommentCount = findViewById(R.id.tvCommentCount);
        RecyclerView mRecyclerview = findViewById(R.id.mRecyclerView);
        Button btnWriteComment = findViewById(R.id.btnWriteComment);

        ArrayList<Comment> comments =new ArrayList<>(); //댓글을 담을 Arraylist 생성
//        CommentAdapter commentAdapter;
        tvCommentCount.setText(item.getFeedID());


        //화면에 댓글을 보여주는 리사이클러뷰가 필요
        //LoadData();

        //댓글 작성 완료 버튼
        btnWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                //유저정보, 댓글내용, 작성시간
                Comment comment = new Comment(nowUser, edtComment.getText().toString(), System.currentTimeMillis());
                map.put("comment", comment);
                map.put("FeedID", item.getFeedID());
                fbModule.readData(1, map, item.getFeedID());
                InputMethodManager imm = (InputMethodManager) current_context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
                edtComment.clearFocus();
                edtComment.setText(null);
            }
        });
    }

    public void LoadData(Feed item) {
        Map map= new HashMap();
        map.put("FeedID",item.getFeedID());
        fbModule.setLIMIT(LIMIT);
        fbModule.readData(1,map,null);
    }

    public void UpdateUI(){

    }


}
