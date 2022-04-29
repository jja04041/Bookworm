package com.example.bookworm.Challenge.subActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookworm.Challenge.items.Challenge;
import com.example.bookworm.Core.UserData.PersonalD;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.databinding.SubactivityChallengeChallengeinfoBinding;
import com.example.bookworm.Core.Internet.FBModule;
import com.example.bookworm.Core.UserData.PersonalD;
import com.example.bookworm.subactivity_challenge_board;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class subactivity_challenge_challengeinfo extends AppCompatActivity {
    UserInfo userInfo;
    SubactivityChallengeChallengeinfoBinding binding;
    Challenge challenge;
    Context mContext;
    private Map sendMap;
    private FBModule fbModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=SubactivityChallengeChallengeinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityInit(); //변수 초기화
        UpdateUI(); // 화면 갱신

        //shimmer 적용을 위해 기존 뷰는 일단 안보이게, shimmer는 보이게
        binding.llChallinfo.setVisibility(View.GONE);
        binding.SFLChallinfo.startShimmer();
        binding.SFLChallinfo.setVisibility(View.VISIBLE);

        //챌린지 참여 버튼을 눌렀을 때
        binding.btnChallengeJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkChallenge();
            }
        });

        //원래 화면으로 돌아갈 때 새로고침함.
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        binding.FLBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, subactivity_challenge_board.class);
                intent.putExtra("challenge", challenge);
                mContext.startActivity(intent);
            }
        });

        //shimmer 적용 끝내고 shimmer는 안보이게, 기존 뷰는 보이게
        binding.llChallinfo.setVisibility(View.VISIBLE);
        binding.SFLChallinfo.stopShimmer();
        binding.SFLChallinfo.setVisibility(View.GONE);
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
        mContext = this;
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
        Glide.with(this).load(challenge.getBookThumb()).into(binding.ivThumbnail);
        binding.tvChallengeEnd.setText(challenge.getEndDate());
        binding.tvDday.setText(countdday(challenge.getEndDate()));
        binding.tvChallengeinfoCurrentParticipants.setText(String.valueOf(challenge.getCurrentPart().size())); // 받아온 ArrayList 의 길이를 넣음 (현재 참여 인원 수 )
        binding.tvMaxParticipants.setText(String.valueOf(challenge.getMaxPart()));
        binding.tvChallengeinfoEnd.setText(challenge.getEndDate());
        binding.tvCurrentParticipants.setText(String.valueOf(challenge.getCurrentPart().size()));
        binding.tvChallengeinfoCreator.setText(challenge.getMaster());
        binding.tvChallengeDescription.setText(challenge.getChallengeDescription());

        binding.tvChallengeinfoBookname.setText(challenge.getBookTitle()); // 책 제목
        binding.tvChallengeinfoBookname.setSingleLine(true);    // 한줄로 표시하기
        binding.tvChallengeinfoBookname.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        binding.tvChallengeinfoBookname.setSelected(true);      // 선택하기

        binding.progress.setProgress(challenge.getCurrentPart().size());//현재인원 설정
        binding.progress.setMax(Integer.parseInt(String.valueOf(challenge.getMaxPart()))); //최대인원 설정

        if (binding.tvDday.getText() == "종료된 챌린지입니다.") {
            binding.btnChallengeJoin.setEnabled(false); // 종료된 챌린지는 참여 버튼 비활성화
            binding.btnChallengeJoin.setText("이미 종료된 챌린지입니다.");
        } else {
            //사용자가 이 챌린지에 참여중인지 여부를 판단해서 챌린지 참여 버튼을 활성/비활성 함.
            sendMap.put("check", 0); //fb모듈 함수 중 successRead()에서 분기하기 위함.
            fbModule.readData(2, sendMap, challenge.getTitle());
        }

    }

    //D-day 계산
    public String countdday(String EndDate) {
        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

    //
    public void checkChallenge() {
        new AlertDialog.Builder(mContext)
                .setMessage("챌린지에 참여하시겠습니까? (참여 후 탈퇴 불가)")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendMap.put("dialog", dialog);
                        sendMap.put("check", 1);
                        fbModule.readData(2, sendMap, challenge.getTitle());
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
        binding.progress.setProgress(CurrentParticipation.size());
        binding.progress.setMax(Integer.parseInt(String.valueOf(challenge.getMaxPart()))); //최대인원 설정
        binding.tvCurrentParticipants.setText(String.valueOf(CurrentParticipation.size())); // 프로그레스 바 상단의 현재 참여자 #/#명 설정
        binding.tvChallengeinfoCurrentParticipants.setText(String.valueOf(CurrentParticipation.size())); // 챌린지 상세 정보의 참여자 #명 설정
        //인원이 가득찬 경우 정원이 찬 챌린지임을 알림 .
        if (challenge.getMaxPart() <= CurrentParticipation.size()) {
            //해당 챌린지가 내가 참여한 챌린지인 경우
            if (CurrentParticipation.contains(userInfo.getToken())) {
                partJoin(); //참여중인 챌린지임을 표시
            } else partFull();
        }
    }

    //현재 참여중인지 확인
    public void isParticipating(DocumentSnapshot document) {
        ArrayList<String> CurrentParticipation = (ArrayList<String>) document.get("CurrentParticipation");
        setParticipating(CurrentParticipation);
        if (CurrentParticipation.contains(userInfo.getToken())) { //Firebase의 CurrentParticipation 필드에 사용자의 토큰이 있는지 확인
            partJoin();
        }
    }

    //참여가능한지 확인
    public void checkParticipating(DocumentSnapshot document, Dialog d) {
        ArrayList<String> CurrentParticipation = (ArrayList<String>) document.get("CurrentParticipation");
        if (CurrentParticipation.size() >= Integer.parseInt(String.valueOf(document.get("MaxParticipation")))) { //참여인원 초과
            new AlertDialog.Builder(mContext)
                    .setMessage("정원이 초과되었습니다.")
                    .setPositiveButton("아쉬워요 ㅠㅠ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setParticipating(CurrentParticipation);
                            dialog.dismiss();
                        }
                    }).show();
        } else { //정원이 가득차지 않은 경우(참여 가능한 경우)
            document.getReference().update("CurrentParticipation", FieldValue.arrayUnion(userInfo.getToken()));
            partJoin();
            //참여했으니 현재 화면에서 참여인원, 프로그레스바 최신화
            sendMap.put("check", 2);
            fbModule.readData(2, sendMap, challenge.getTitle());
            //열었던 다이어로그 닫음
            d.dismiss();
        }
    }

    //정원이 가득찬 경우 세팅
    private void partFull() {
        binding.btnChallengeJoin.setEnabled(false);
        binding.btnChallengeJoin.setText("정원이 초과된 챌린지입니다.");
    }
    //참여한 경우 세팅
    private void partJoin() {
        binding.btnChallengeJoin.setEnabled(false); //이미 사용자의 토큰이 있다면 (이미 참여한 챌린지라면) 챌린지 참여 버튼 비활성화
        binding.btnChallengeJoin.setText("참여중인 챌린지입니다.");
    }
}
