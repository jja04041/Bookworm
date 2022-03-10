package com.example.bookworm.Challenge.subActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Challenge.items.Challenge;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class subactivity_challenge_challengeinfo extends AppCompatActivity {
    UserInfo userInfo;
    Button btn_back, btn_join;
    TextView tv_bookname, tv_challenge_end, tv_Dday, tv_challenge_current, tv_challenge_max, tv_end_date, tv_creator, tv_current_participants, tv_challenge_description;
    FrameLayout board;
    ProgressBar progressBar;
    ImageView Thumbnail;
    Challenge challenge;
    Context mContext;

    private FBModule fbModule;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_challenge_challengeinfo);

        btn_back = findViewById(R.id.btnBack);
        btn_join = findViewById(R.id.btn_challenge_join); // 챌린지 참여 버튼

        Thumbnail = findViewById(R.id.ivThumbnail); // 썸네일

        tv_challenge_end = findViewById(R.id.tv_challenge_end); // ~~~~에 종료됨
        tv_Dday = findViewById(R.id.Dday); // #일 남음
        tv_challenge_current = findViewById(R.id.tv_Current_participants); // 현재 참여자 #/#
        tv_challenge_max = findViewById(R.id.tv_max_participants); // 최대 참여자 #/#
        tv_bookname = findViewById(R.id.tv_challengeinfo_bookname); // 책 제목
        tv_end_date = findViewById(R.id.tv_challengeinfo_end); // 마감일
        tv_current_participants = findViewById(R.id.tv_challengeinfo_Current_participants); // 참여자 ##명
        tv_creator = findViewById(R.id.tv_challengeinfo_creator); // 생성자 닉네임
        tv_challenge_description = findViewById(R.id.tv_challenge_Description); // 챌린지 설명

        mContext = this;
        fbModule = new FBModule(mContext);

        progressBar = findViewById(R.id.progress); // 현재 인원 프로그레스 바

        board = findViewById(R.id.FL_board); // 인증게시판

        //넘겨받은 값 챌린지 객체에 넣음
        Intent intent = getIntent();
        challenge = (Challenge) intent.getSerializableExtra("challengeInfo");

        userInfo = new PersonalD(mContext).getUserInfo(); //저장된 UserInfo값을 가져온다.

        //책 썸네일 설정
        Glide.with(this).load(challenge.getBookThumb()).into(Thumbnail);

        tv_challenge_end.setText(challenge.getEndDate());
        tv_Dday.setText(countdday(challenge.getEndDate()));
//        tv_challenge_current.setText(String.valueOf(challenge.getCurrentPart().size())); // 받아온 ArrayList 의 길이를 넣음 (현재 참여 인원 수 )
        tv_challenge_max.setText(String.valueOf(challenge.getMaxPart()));
        tv_end_date.setText(challenge.getEndDate());
//        tv_current_participants.setText(String.valueOf(challenge.getCurrentPart().size()));
        tv_creator.setText(challenge.getMaster());
        tv_challenge_description.setText(challenge.getChallengeDescription());

        tv_bookname.setText(challenge.getBookTitle()); // 책 제목
        tv_bookname.setSingleLine(true);    // 한줄로 표시하기
        tv_bookname.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        tv_bookname.setSelected(true);      // 선택하기

        //챌린지 상세 정보 화면에 들어갈때 현재참여인원 등등 설정
        fbModule.setParticipating(challenge.getTitle(), progressBar, tv_challenge_current, tv_current_participants);

        //사용자가 이 챌린지에 참여중인지 여부를 판단해서 챌린지 참여 버튼을 활성/비활성 함.
        fbModule.isParticipating(challenge.getTitle(), userInfo.getToken(), btn_join);

        //챌린지 참여 버튼을 눌렀을 때
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = db.collection("challenge").document(challenge.getTitle()); //챌린지 컬렉션에서 챌린지 제목으로 검색
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<String> CurrentParticipation = (ArrayList<String>) document.get("CurrentParticipation"); //받아온 문서의 값중에 CurrentParticipation 필드의 값을 리스트에 넣음

                                if (CurrentParticipation.size() >= Integer.parseInt(String.valueOf(document.get("MaxParticipation")))) { //참여인원 초과
                                    new AlertDialog.Builder(mContext)
                                            .setMessage("정원이 초과되었습니다.")
                                            .setPositiveButton("아쉬워요 ㅠㅠ", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                } else { //참여인원 미달
                                    new AlertDialog.Builder(mContext)
                                            .setMessage("챌린지에 참여하시겠습니까? (참여 후 탈퇴 불가)")
                                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //Firebase의 현재 참여자 배열에 토큰 추가
                                                    DocumentReference challengeJoin = db.collection("challenge").document(challenge.getTitle());
                                                    challengeJoin.update("CurrentParticipation", FieldValue.arrayUnion(userInfo.getToken()));

                                                    btn_join.setEnabled(false); // 이제 참여했으니 버튼 비활성화
                                                    btn_join.setText("참여중인 챌린지입니다.");

                                                    //참여했으니 현재 화면에서 참여인원, 프로그레스바 최신화
                                                    fbModule.setParticipating(challenge.getTitle(), progressBar, tv_challenge_current, tv_current_participants);

                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            } else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
    }

    public String countdday(String EndDate) { //D-day 계산
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar todaCal = Calendar.getInstance(); //오늘날짜 가져오기
            Calendar ddayCal = Calendar.getInstance(); //오늘날짜를 가져와 변경시킴

            int year, month, day;

            year = Integer.parseInt(EndDate.substring(0, 4));
            month = Integer.parseInt(EndDate.substring(5, 7));
            day = Integer.parseInt(EndDate.substring(8, 10));

            month -= 1; // 받아온날짜에서 -1을 해줘야함.
            ddayCal.set(year, month, day);// D-day의 날짜를 입력

            long today = todaCal.getTimeInMillis() / 86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
            long dday = ddayCal.getTimeInMillis() / 86400000;
            long count = dday - today; // 오늘 날짜에서 dday 날짜를 빼주게 됨.

            if (count < 0) {
                return "종료된 챌린지입니다.";
            } else {
                return String.valueOf(count) + "일 남음";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

}