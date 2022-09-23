package com.example.bookworm.bottomMenu.challenge.subactivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.bottomMenu.challenge.NumberPickerDialog;
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.R;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.SubactivityChallengeCreatechallengeBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class subactivity_challenge_createchallenge extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    SubactivityChallengeCreatechallengeBinding binding;
    UserInfo userInfo;
    Button btn_back;
    TextView tv_bookname;
    EditText et_challenge_name, et_challenge_max, et_challenge_info;
    Button btn_start_challenge;
    ImageView Thumbnail;
    String strBookname, strChallengeName, strChallengeInfo, strChallengeStartDate, strChallengeEndDate, strCurrentParticipation, strMaxParticipation;
    Book selected_book; //선택한 책 객체
    private FBModule fbModule;
    Context mContext;
    String challengeStartDay; // 챌린지 시작일 기록

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    //액티비티 간 데이터 전달 핸들러(검색한 데이터의 값을 전달받는 매개체가 된다.)
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    this.selected_book = intent.getParcelableExtra("data");
                    tv_bookname.setText(selected_book.getTitle()); //책 제목만 세팅한다.
                    Glide.with(this).load(selected_book.getImgUrl()).into(Thumbnail); //책 표지 로딩후 삽입.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivityChallengeCreatechallengeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btn_start_challenge = findViewById(R.id.btn_start_challenge);
        btn_back = findViewById(R.id.btnBack);
        tv_bookname = findViewById(R.id.tv_createchallenge_bookname);

        tv_bookname.setSingleLine(true);    // 한줄로 표시하기
        tv_bookname.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        tv_bookname.setSelected(true);      // 선택하기

        et_challenge_name = findViewById(R.id.et_createchallenge_challengename);
//        et_challenge_max = findViewById(R.id.etMax);
        et_challenge_info = findViewById(R.id.et_createchallenge_challengeinfo);
        Thumbnail = findViewById(R.id.ivThumbnail);

        mContext = this;
        fbModule = new FBModule(mContext);

        userInfo = new PersonalD(mContext).getUserInfo(); //저장된 UserInfo값을 가져온다.

        //챌린지 시작일에 오늘 날짜가 나오게 함
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        challengeStartDay = df.format(cal.getTime());

        //날짜 고르기
        binding.datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000 + (1000L * 60 * 60 * 24 * 30)); //최대 30일로 설정
                datePickerDialog.show();
            }
        });

        //인원수 고르기
        binding.numberPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumberPicker(view, "인원 선택", "", 30, 2, 1, 10);
            }
        });


        //챌린지 기간설정 EditText의 내용이 바뀔때 이벤트
//        et_challenge_date.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                //바뀌기 전 이벤트
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                //바뀌는 동시에 이벤트
//                String addDate = et_challenge_date.getText().toString();
//
//                if (!addDate.equals("")) { //EditText가 공백이 아니면 적힌 값만큼 날짜를 더해서 출력
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(new Date());
//                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                    if (Integer.parseInt(addDate) == 0) { //챌린지 기간을 0일로 설정하려 했을 때
//                        Toast.makeText(getApplicationContext(), "챌린지 기간은 최소 1일 입니다.", Toast.LENGTH_SHORT).show();
//                        et_challenge_date.setText(null); // 챌린지 기간 항목 비우기
//                    } else {
//                        cal.add(Calendar.DATE, (Integer.parseInt(addDate) - 1));
//                    }
//                    tv_challenge_end.setText(df.format(cal.getTime()));
//                } else { //EditText가 공백이면 종료일이라고 출력
//                    tv_challenge_end.setText("종료일");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                //바뀌고 나서 이벤트
//            }
//        });

        tv_bookname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBook();
            }
        });

        //챌린지 시작 버튼
        btn_start_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChallenge();
            }
        });

        //뒤로가기 버튼
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //검색창을 열어서 책을 검색한다.
    public void getBook() {
//        Intent intent = new Intent(this, search_fragment_subActivity_main.class);
//        intent.putExtra("classindex", 2);
//        startActivityResult.launch(intent); //검색 결과를 받는 핸들러를 작동한다.
    }

    private void updateLabel(TextView tv, Calendar calendar) {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tv.setText(sdf.format(calendar.getTime()));
    }

    private void createChallenge() {
        //파이어베이스에 올릴것들
        strBookname = tv_bookname.getText().toString();
        strChallengeName = et_challenge_name.getText().toString().trim();
        strChallengeInfo = et_challenge_info.getText().toString();
        strChallengeStartDate = challengeStartDay;
        strChallengeEndDate = binding.tvEndDate.getText().toString();
        strCurrentParticipation = "0";
        strMaxParticipation = binding.tvMax.getText().toString();
//        strChallengeDate = et_challenge_date.getText().toString();

        //입력 안한 항목 있는지 찾기
        if (strBookname.equals("") || strChallengeName.equals("") || strChallengeInfo.equals("") || strMaxParticipation.equals("")) {
            Toast.makeText(this, "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
        } else {//다 입력 했다면
            HashMap<String, Object> map = new HashMap<>();

            String[] strCurrentParticipation = {userInfo.getToken()};

            //기간 지났는지 확인하기 위한 값
            boolean outDated = false;

            map.put("Profileimg", userInfo.getProfileimg()); //프로필 이미지
            map.put("Username", userInfo.getUsername()); //닉네임
            map.put("masterToken", userInfo.getToken()); //토큰
            map.put("book", selected_book);
            map.put("strChallengeName", strChallengeName); //챌린지 명
            map.put("challengeInfo", strChallengeInfo); //챌린지 설명
            map.put("challengeStartDate", strChallengeStartDate); //챌린지 시작일
            map.put("ChallengeEndDate", strChallengeEndDate); //챌린지 종료일
            map.put("CurrentParticipation", Arrays.asList(strCurrentParticipation)); //현재 참가자
            map.put("MaxParticipation", Integer.parseInt(strMaxParticipation)); //최대 참가자 수
            map.put("outDated", outDated); //기간 종료 확인 값

            //파이어베이스에 해당 챌린지명이 등록돼있지 않다면

            fbModule.readData(2, map, strChallengeName);


        }
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";    // 출력형식
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        binding.tvEndDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        binding.tvMax.setText(String.valueOf(numberPicker.getValue() + 2));
    }

    //Dialog를 시작하는 함수, 특정 버튼을 눌렀을 때 이 함수를 실행 시키면 된다
    public void showNumberPicker(View view, String title, String subtitle, int maxvalue, int minvalue, int step, int defvalue) {
        NumberPickerDialog newFragment = new NumberPickerDialog();

        //Dialog에는 bundle을 이용해서 파라미터를 전달한다
        Bundle bundle = new Bundle(6); // 파라미터는 전달할 데이터 개수
        bundle.putString("title", title); // key , value
        bundle.putString("subtitle", subtitle); // key , value
        bundle.putInt("maxvalue", maxvalue); // key , value
        bundle.putInt("minvalue", minvalue); // key , value
        bundle.putInt("step", step); // key , value
        bundle.putInt("defvalue", defvalue); // key , value
        newFragment.setArguments(bundle);
        //class 자신을 Listener로 설정한다
        newFragment.setValueChangeListener(this);
        newFragment.show(getFragmentManager(), "number picker");
    }
}
