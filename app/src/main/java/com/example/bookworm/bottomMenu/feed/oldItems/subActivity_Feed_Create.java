package com.example.bookworm.bottomMenu.feed.oldItems;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.achievement.Achievement;
import com.example.bookworm.bottomMenu.bookworm.BookWorm;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;

import com.example.bookworm.bottomMenu.search.bookitems.Book;
import com.example.bookworm.core.dataprocessing.image.ImageProcessing;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.SubactivityFeedCreateBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

//사용하지 않지만, 참고용으로 둔 상태
public class subActivity_Feed_Create extends AppCompatActivity {


    private SubactivityFeedCreateBinding binding;
    FBModule fbModule;
    UserInfo userInfo;
    Context current_context;
    Dialog customDialog;
    ImageProcessing imageProcess;
    String FeedID;
    UserInfoViewModel uv;
    BookWorm userBw;
    Book selected_book; //선택한 책 객체
    Intent intent;
    public static int CREATE_OK = 30;


    //액티비티 간 데이터 전달 핸들러(검색한 데이터의 값을 전달받는 매개체가 된다.)
    ActivityResultLauncher<Intent> bookResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    selected_book = new Book();
//                    this.selected_book = (Book) intent.getSerializableExtra("data");
                    binding.tvFeedBookTitle.setText(selected_book.getTitle()); //책 제목만 세팅한다.
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = SubactivityFeedCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        current_context = this;
        uv = new UserInfoViewModel(current_context);
        uv.getUser(null, false);
        fbModule = new FBModule(current_context);

        imageProcess = new ImageProcessing(current_context);


        customDialog = new Dialog(subActivity_Feed_Create.this);       // Dialog 초기화
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        customDialog.setContentView(R.layout.custom_dialog_label);

        //뒤로가기
        binding.btnBack.setOnClickListener(view -> finish());

        //이미지 업로드 버튼
        binding.btnImageUpload.setOnClickListener(view -> imageProcess.initProcess());

        uv.getUserInfoLiveData().observe(this, userinfo -> {
            uv.getBookWorm(userinfo.getToken());
            userInfo = userinfo;
            Glide.with(this).load(userinfo.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필사진 로딩후 삽입.
            binding.tvNickname.setText(userinfo.getUsername());

            uv.getBwdata().observe(this, bookWorm -> {
                userBw = bookWorm;
                imageProcess.getBitmap().observe(this, bitmap -> {
                    //완료 버튼 (피드 올리기)
                    binding.tvFinish.setOnClickListener(view ->
                            new AlertDialog.Builder(current_context)
                                    .setMessage("피드를 업로드하시겠습니까?")
                                    .setPositiveButton("네", (dialog, which) -> {
                                        dialog.dismiss();
                                        upload(bitmap, userInfo);
                                    })
                                    .setNegativeButton("아니요", (dialog, which)
                                            -> dialog.dismiss())
                                    .show()
                    );
                    imageProcess.getImgData().observe(this, imgurl -> {
                        feedUpload(imgurl);
                    });
                });

                binding.tvFinish.setOnClickListener(view ->
                        new AlertDialog.Builder(current_context)
                                .setMessage("피드를 업로드하시겠습니까?")
                                .setPositiveButton("네", (dialog, which) -> {
                                    dialog.dismiss();
                                    upload(null, userInfo);
                                })
                                .setNegativeButton("아니요", (dialog, which)
                                        -> dialog.dismiss())
                                .show()
                );
            });
        });

        if (selected_book != null)
            binding.tvFeedBookTitle.setText(selected_book.getTitle()); //책 제목만 세팅한다.

        if (getIntent() != null) {
            intent = getIntent();
            if (intent.getSerializableExtra("data") != null) {
//                selected_book = (Book) intent.getSerializableExtra("data");
                selected_book = new Book();
                binding.tvFeedBookTitle.setText(selected_book.getTitle()); //책 제목만 세팅한다.
            }
        }
        imageProcess.getBitmapUri().observe(this, it -> {
            Glide.with(this).load(it)
                    .into(binding.ivpicture);
            binding.ivpicture.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        });

        binding.tvFeedBookTitle.setSingleLine(true);    // 책 제목 한줄로 표시하기
        binding.tvFeedBookTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        binding.tvFeedBookTitle.setSelected(true);      // 선택하기

        //책 고르기
        binding.tvFeedBookTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBook();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //서버에 이미지 업로드
    private void upload(Bitmap data, UserInfo userInfo) {
        FeedID = System.currentTimeMillis() + "_" + userInfo.getToken(); //현재 시각 + 사용자 토큰을 FeedID로 설정
        if (data != null) {
            String name = "feed_" + FeedID + ".jpg";
            imageProcess.uploadImage(data, name);
        } else {
            feedUpload(null);
        }
    }

    //책 검색해서 선택하는 함수
    public void getBook() {

//        intent = new Intent(this, search_fragment_subActivity_main.class);
//        intent.putExtra("classindex", 2);
//        bookResult.launch(intent); //검색 결과를 받는 핸들러를 작동한다.
    }

    //피드 업로드
    public void feedUpload(String imgUrl) {

        if (binding.tvFeedBookTitle.getText().toString().equals("") || binding.edtFeedText.getText().toString().equals("")) { //책 선택이나 피드 내용이 없으면 작성해달라는 알림 띄움
            new AlertDialog.Builder(current_context)
                    .setMessage("책 제목과 피드 내용을 작성해주세요")
                    .setPositiveButton("알겠습니다.", (dialog, which) -> dialog.dismiss()).show();
        } else { //필수 입력사항이 입력돼있다면 작성한 내용을 파이어베이스에 업로드
            HashMap<String, Object> map = new HashMap<>();


            ArrayList<String> labelList = new ArrayList<String>(); //선택한 라벨 목록을 담을 리스트

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatTime = dateFormat.format(System.currentTimeMillis());

//            selected_book.setCategoryname(userInfo.setGenre(selected_book.getCategoryname(), current_context));

            map.put("UserToken", userInfo.getToken()); //유저 정보
            map.put("book", selected_book); //책 정보
            map.put("feedText", binding.edtFeedText.getText().toString()); //피드 내용
            map.put("date", formatTime); //현재 시간 millis로
            map.put("FeedID", FeedID); //피드 아이디
            map.put("commentsCount", 0);
            map.put("likeCount", 0);
            if (imgUrl != null) map.put("imgurl", imgUrl); //이미지 url

            fbModule.readData(1, map, FeedID);

            int count = userBw.getReadCount();
            userBw.setReadCount(++count);
            uv.updateUser(userInfo);
            uv.updateBw(userInfo.getToken(), userBw);

            boolean exit = true;
            Achievement achievement = new Achievement(current_context, fbModule, userInfo, userBw);
            achievement.CompleteAchievement(userInfo, current_context);
            exit = achievement.canreturn();

            if (exit == true) {
                setResult(CREATE_OK);
                finish();
            }
        }
    }


}
