package com.example.bookworm.bottomMenu.challenge.subactivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.bookworm.achievement.Achievement;
import com.example.bookworm.bottomMenu.bookworm.BookWorm;
import com.example.bookworm.bottomMenu.challenge.items.Challenge;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.bottomMenu.search.items.Book;

import com.example.bookworm.core.dataprocessing.image.ImageProcessing;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.internet.Module;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.SubactivityChallengeBoardCreateBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class subactivity_challenge_board_create extends AppCompatActivity {

    public static int CREATE_OK = 30;
    private SubactivityChallengeBoardCreateBinding binding;
    FBModule fbModule;
    UserInfo userInfo;
    Context current_context;
    Bitmap uploaded;
    Module module;
    UserInfoViewModel uv;
    ImageProcessing imageProcess;
    BookWorm userBw;
    Challenge challenge;
    String imgurl = null;
    Dialog customDialog;
    String BoardID;
    Book selected_book; //선택한 책 객체

    //사용자가 선택한 어플로 이어서 사진을 선택할 수 있게 함.
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int code = result.getResultCode();
                if (code == Activity.RESULT_OK) {
                    Uri uri = result.getData().getParcelableExtra("path");
                    try {
                        // You can update this bitmap to your server
                        uploaded = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                        // loading profile image from local cache
                        loadImage(uri.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivityChallengeBoardCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        //넘겨받은 값 챌린지 객체에 넣음
        challenge = (Challenge) intent.getSerializableExtra("challenge");

        selected_book = challenge.getBook();


        current_context = this;
        uv = new UserInfoViewModel(current_context);
        uv.getUser(null, false);
        fbModule = new FBModule(current_context);

        imageProcess = new ImageProcessing(current_context);


        //이미지 업로드 버튼
        binding.btnImageUpload.setOnClickListener(view -> imageProcess.initProcess());

        uv.getData().observe(this, userinfo -> {
            uv.getBookWorm(userinfo.getToken());
            userInfo = userinfo;
            Glide.with(this).load(userinfo.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필사진 로딩후 삽입.
            binding.tvNickname.setText(userinfo.getUsername());

            uv.getBwdata().observe(this,bookWorm -> {
                userBw=bookWorm;
                imageProcess.getBitmap().observe(this, bitmap -> {
                    //완료 버튼 (피드 올리기)
                    binding.tvFinish.setOnClickListener(view ->
                            new AlertDialog.Builder(current_context)
                                    .setMessage("인증글을 업로드하시겠습니까?")
                                    .setPositiveButton("네", (dialog, which) -> {
                                        dialog.dismiss();
                                        upload(bitmap, userInfo);
                                    })
                                    .setNegativeButton("아니요", (dialog, which)
                                            -> dialog.dismiss())
                                    .show()
                    );
                    imageProcess.getImgData().observe(this,imgurl->{
                        feedUpload(imgurl);
                    });
                });

                binding.tvFinish.setOnClickListener(view ->
                        new AlertDialog.Builder(current_context)
                                .setMessage("인증글을 업로드하시겠습니까?")
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

        if(getIntent() != null) {
//            intent = getIntent();
//            if((Book) intent.getSerializableExtra("data") != null){
//                selected_book = (Book) intent.getSerializableExtra("data");
//                binding.tvFeedBookTitle.setText(selected_book.getTitle()); //책 제목만 세팅한다.
//            }
        }
        imageProcess.getBitmapUri().observe(this, it -> {
            Glide.with(this).load(it)
                    .into(binding.ivpicture);
            binding.ivpicture.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        });

    }

    //메소드
    //이미지 처리 코드
    private void loadImage(String url) {
        Log.d("이미지 캐싱 ", "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(binding.ivpicture);
        binding.ivpicture.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }


    //서버에 이미지 업로드
    private void upload(Bitmap data, UserInfo userInfo) {

        BoardID = System.currentTimeMillis() + "_" + userInfo.getToken(); //현재 시각 + 사용자 토큰을 FeedID로 설정
        if (data != null) {
            String name="feed_"+BoardID+".jpg";
            imageProcess.uploadImage(data,name);
        } else {
            feedUpload(null);
        }
    }

    //피드 업로드
    public void feedUpload(String imgUrl) {

        if (binding.edtFeedText.getText().toString().equals("") || binding.ivpicture.getDrawable() == null) { //인증사진이나 피드 내용이 없으면 작성해달라는 알림 띄움
            new AlertDialog.Builder(current_context)
                    .setMessage("인증 사진과 인증글 내용을 작성해주세요")
                    .setPositiveButton("알겠습니다.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else { //필수 입력사항이 입력돼있다면 작성한 내용을 파이어베이스에 업로드
            HashMap<String, Object> map = new HashMap<>();



            //ArrayList<String> labelList = new ArrayList<String>(); //선택한 라벨 목록을 담을 리스트

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatTime = dateFormat.format(System.currentTimeMillis());

            map.put("UserToken", userInfo.getToken()); //유저 정보
            map.put("book", selected_book); //책 정보
            map.put("boardText", binding.edtFeedText.getText().toString()); //피드 내용
            //map.put("label", labelAdd(labelList)); //라벨 리스트
            map.put("date", formatTime); //현재 시간 millis로
            map.put("boardID", BoardID); //인증글 아이디
            map.put("challengeName", challenge.getTitle()); //챌린지 명
            map.put("commentsCount",0);
            map.put("likeCount", 0);
            if (imgUrl != null) map.put("imgurl", imgUrl); //이미지 url

            //챌린지 인증글 업로드
            fbModule.uploadChallengeBoard(2, challenge.getTitle(), BoardID, map);

            // 장르 처리
//            HashMap<String, Object> savegenremap = new HashMap<>();
            userInfo.setGenre(selected_book.getCategoryname(), current_context);

            int count = userBw.getReadcount();
            userBw.setReadcount(++count);
            uv.updateUser(userInfo);
            uv.updateBw(userInfo.getToken(), userBw);

            boolean exit = true;
            Achievement achievement = new Achievement(current_context, fbModule, userInfo, userBw);
            achievement.CompleteAchievement(userInfo, current_context);
            exit = achievement.canreturn();

            if(exit == true) {
                setResult(CREATE_OK);
                finish();
            }
        }
    }


}