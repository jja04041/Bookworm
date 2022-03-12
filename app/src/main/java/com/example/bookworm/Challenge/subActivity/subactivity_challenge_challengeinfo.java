package com.example.bookworm.Challenge.subActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
    private Map sendMap;
    private FBModule fbModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_challenge_challengeinfo);

        ActivityInit(); //변수 초기화
        UpdateUI(); // 화면 갱신

        //챌린지 참여 버튼을 눌렀을 때
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fb모듈 함수 중 successRead()에서 분기하기 위함.
                checkChallenge();
            }
        });

        //원래 화면으로 돌아갈 때 새로고침함.
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    //뒤로가기 키를 누를 떄에도 반응할 수 있도록 함.
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK); //챌린지 프레그먼트에서 새로고침을 진행하도록 함.
        finish();
        super.onBackPressed();
    }

    //변수 초기화
    private void ActivityInit() {
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
        progressBar = findViewById(R.id.progress); // 현재 인원 프로그레스 바
        mContext = this;
        board = findViewById(R.id.FL_board); // 인증게시판
        fbModule = new FBModule(mContext);
        sendMap = new HashMap();//fb모듈에 전달할 맵 값
        Intent intent = getIntent();
        //넘겨받은 값 챌린지 객체에 넣음
        challenge = (Challenge) intent.getSerializableExtra("challengeInfo");
        userInfo = new PersonalD(mContext).getUserInfo(); //저장된 UserInfo값을 가져온다.


    }

    //화면 갱신
    private void UpdateUI() {
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
        if (tv_Dday.getText() == "종료된 챌린지입니다.") {
            btn_join.setEnabled(false); // 종료된 챌린지는 참여 버튼 비활성화
            btn_join.setText("이미 종료된 챌린지입니다.");
        } else {
            //사용자가 이 챌린지에 참여중인지 여부를 판단해서 챌린지 참여 버튼을 활성/비활성 함.
            sendMap.put("check", 0); //fb모듈 함수 중 successRead()에서 분기하기 위함.
            fbModule.readData(2, sendMap, challenge.getTitle());
        }
    }

    //D-day 계산
    public String countdday(String EndDate) {
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
    private void partFull(){
        btn_join.setEnabled(false);
        btn_join.setText("정원이 초과된 챌린지입니다.");
    }

    public void checkChallenge() {
            new AlertDialog.Builder(mContext)
                    .setMessage("챌린지에 참여하시겠습니까? (참여 후 탈퇴 불가)")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendMap.put("dialog",dialog);
                            sendMap.put("check",1);
                            fbModule.readData(2,sendMap,challenge.getTitle());
                            //Firebase의 현재 참여자 배열에 토큰 추가
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
    }

    public void setParticipating(ArrayList<String> CurrentParticipation) {
        //현재 인원 프로그레스 바 설정
        progressBar.setProgress(CurrentParticipation.size());
        progressBar.setMax(Integer.parseInt(String.valueOf(challenge.getMaxPart()))); //최대인원 설정
        tv_challenge_current.setText(String.valueOf(CurrentParticipation.size())); // 프로그레스 바 상단의 현재 참여자 #/#명 설정
        tv_current_participants.setText(String.valueOf(CurrentParticipation.size())); // 챌린지 상세 정보의 참여자 #명 설정
        //인원이 가득찬 경우 정원이 찬 챌린지임을 알림 .
        if (challenge.getMaxPart() <=CurrentParticipation.size()){
            partFull();
        }
    }

    //현재 참여중인지 확인
    public void isParticipating(DocumentSnapshot document) {
        ArrayList<String> CurrentParticipation = (ArrayList<String>) document.get("CurrentParticipation");
        setParticipating(CurrentParticipation);
        if (CurrentParticipation.contains(userInfo.getToken())) { //Firebase의 CurrentParticipation 필드에 사용자의 토큰이 있는지 확인
            btn_join.setEnabled(false); //이미 사용자의 토큰이 있다면 (이미 참여한 챌린지라면) 챌린지 참여 버튼 비활성화
            btn_join.setText("참여중인 챌린지입니다.");
        }
    }

    public void checkParticipating(DocumentSnapshot document,Dialog d) {
        ArrayList<String> CurrentParticipation = (ArrayList<String>) document.get("CurrentParticipation");
        if (CurrentParticipation.size() >= Integer.parseInt(String.valueOf(document.get("MaxParticipation")))) { //참여인원 초과
            new AlertDialog.Builder(mContext)
                    .setMessage("정원이 초과되었습니다.")
                    .setPositiveButton("아쉬워요 ㅠㅠ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            partFull();
                            dialog.dismiss();
                        }
                    }).show();
        }else{
            document.getReference().update("CurrentParticipation", FieldValue.arrayUnion(userInfo.getToken()));
            btn_join.setEnabled(false); // 이제 참여했으니 버튼 비활성화
            btn_join.setText("참여중인 챌린지입니다.");
            //참여했으니 현재 화면에서 참여인원, 프로그레스바 최신화
            sendMap.put("check", 2);
            fbModule.readData(2, sendMap, challenge.getTitle());
            //열었던 다이어로그 닫음
            d.dismiss();
        }
    }
}
