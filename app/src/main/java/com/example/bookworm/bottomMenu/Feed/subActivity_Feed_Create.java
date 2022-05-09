package com.example.bookworm.bottomMenu.Feed;


import android.Manifest;
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
import android.text.TextUtils;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.view.Window;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.search.items.Book;
import com.example.bookworm.bottomMenu.search.subactivity.search_fragment_subActivity_main;
import com.example.bookworm.core.userdata.UserInfo;

import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.internet.Module;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.databinding.SubactivityFeedCreateBinding;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class subActivity_Feed_Create extends AppCompatActivity {


    private SubactivityFeedCreateBinding binding;
    FBModule fbModule;
    UserInfo userInfo;
    Context current_context;
    Bitmap uploaded;
    Module module;
    String imgurl = null;
    Dialog customDialog;
    String FeedID;
    Book selected_book; //선택한 책 객체
    public static int CREATE_OK=30;
    //라벨은 알럿 다이어그램을 통해 입력을 받고, 선택한 값으로 라벨이 지정됨 => 구현 예정

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

    //액티비티 간 데이터 전달 핸들러(검색한 데이터의 값을 전달받는 매개체가 된다.)
    ActivityResultLauncher<Intent> bookResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    this.selected_book = (Book) intent.getSerializableExtra("data");
                    binding.tvFeedBookTitle.setText(selected_book.getTitle()); //책 제목만 세팅한다.

                    //Glide.with(this).load(selected_book.getImg_url()).into(Thumbnail); //책 표지 로딩후 삽입.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivityFeedCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //피드 생성화면에 존재하는 라벨
        TextView feedCreateLabel[] = new TextView[5];
        int[] feedCreateLabelID = {R.id.tvlabel1, R.id.tvlabel2, R.id.tvlabel3, R.id.tvlabel4, R.id.tvlabel5,};

        //우선 빈 껍데기만 있는 라벨을 보이지 않게 설정해놓음
        for (int i = 0; i < feedCreateLabel.length; i++) {
            feedCreateLabel[i] = findViewById(feedCreateLabelID[i]);
            feedCreateLabel[i].setVisibility(View.INVISIBLE);
        }


        //tvlabel1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55ff0000"))); //자바로 BackgroundTint 설정


        current_context = this;
        fbModule = new FBModule(current_context);
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.


        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필사진 로딩후 삽입.
        binding.tvNickname.setText(userInfo.getUsername());

        customDialog = new Dialog(subActivity_Feed_Create.this);       // Dialog 초기화
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        customDialog.setContentView(R.layout.custom_dialog_label);

        //뒤로가기
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //이미지 업로드 버튼
        binding.btnImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withActivity((Activity) current_context)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        //라벨 추가/수정 버튼
        binding.addLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showcustomDialog();
            }
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

        //완료 버튼 (피드 올리기)
        binding.tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(current_context)
                        .setMessage("피드를 업로드하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        upload();
                                    }
                                }
                        )
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }
                        ).show();
            }
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

    private void showImagePickerOptions() {
        ImagePicker.showImagePickerOptions(this, new ImagePicker.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(subActivity_Feed_Create.this, ImagePicker.class);
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePicker.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePicker.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePicker.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityResult.launch(intent);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(subActivity_Feed_Create.this, ImagePicker.class);
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1);
        startActivityResult.launch(intent);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(subActivity_Feed_Create.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    //권한 설정
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityResult.launch(intent);

    }

    //서버에 이미지 업로드
    private void upload() {

        FeedID = String.valueOf(System.currentTimeMillis()) + "_" + userInfo.getToken(); //현재 시각 + 사용자 토큰을 FeedID로 설정

        if (uploaded != null) {
            try {
                File filesDir = getApplicationContext().getFilesDir();
                File file = new File(filesDir, "feed_" + FeedID + ".jpg"); //파일명 설정
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                uploaded.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();
                //파일에 바이트배열로 담겨진 비트맵파일을 쓴다.
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");
                Map map = new HashMap();
                map.put("rqbody", body);
                map.put("rqname", name);
                imgurl = getString(R.string.serverUrl); //이미지 서버의 주소
                module = new Module(current_context, imgurl, map);
                module.connect(3);

            } catch (IOException e) {

            }
        } else {
            feedUpload(null);
        }
    }


    //라벨 선택하는 커스텀 다이얼로그
    public void showcustomDialog() {
        customDialog.show(); // 다이얼로그 띄우기

        //감정에 해당하는 라벨
        TextView Emotion[] = new TextView[7];
        int[] EmotionID = {R.id.tvEmotion0, R.id.tvEmotion1, R.id.tvEmotion2, R.id.tvEmotion3, R.id.tvEmotion4, R.id.tvEmotion5, R.id.tvEmotion6,};

        //추천에 해당하는 라벨
        TextView Recommend[] = new TextView[5];
        int[] RecommendID = {R.id.tvRecommend0, R.id.tvRecommend1, R.id.tvRecommend2, R.id.tvRecommend3, R.id.tvRecommend4};

        //피드 생성화면에 존재하는 라벨
        TextView feedCreateLabel[] = new TextView[5];
        int[] feedCreateLabelID = {R.id.tvlabel1, R.id.tvlabel2, R.id.tvlabel3, R.id.tvlabel4, R.id.tvlabel5,};

        //피드 생성화면의 라벨 findViewById 연결
        for (int i = 0; i < feedCreateLabel.length; i++) {
            feedCreateLabel[i] = findViewById(feedCreateLabelID[i]);
        }

        ArrayList<String> label = new ArrayList<String>(); //선택한 라벨 목록을 담을 리스트


        //감정 라벨 선택시 선택되면서 배경색 변경
        for (int i = 0; i < Emotion.length; i++) {
            final int index = i;
            Emotion[index] = customDialog.findViewById(EmotionID[index]);
            Emotion[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Emotion[index].isSelected()) {
                        Emotion[index].setSelected(false);
                    } else {
                        Emotion[index].setSelected(true);
                    }
                }
            });
        }

        //추천 라벨 선택시 선택되면서 배경색 변경
        for (
                int i = 0;
                i < Recommend.length; i++) {
            final int index = i;
            Recommend[index] = customDialog.findViewById(RecommendID[index]);
            Recommend[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Recommend[index].isSelected()) {
                        Recommend[index].setSelected(false);
                    } else {
                        Recommend[index].setSelected(true);
                    }
                }
            });
        }

        // 완료 버튼
        customDialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현

                TextView tvlabelHint = findViewById(R.id.tvlabelHint); //라벨을 추가해보세요 라는 문구
                int labelCount = 0; //라벨은 최대 5개까지만 설정하게끔 작동하게 하는 변수

                //라벨 리스트 초기화
                label.clear();

                //감정 라벨이 선택돼있으면 라벨 리스트에 선택된 항목 추가
                for (int i = 0; i < Emotion.length; i++) {
                    if (Emotion[i].isSelected()) {
                        label.add(Emotion[i].getText().toString());
                        labelCount += 1;
                    }
                }

                //추천 라벨이 선택돼있으면 라벨 리스트에 선택된 항목 추가
                for (int i = 0; i < Recommend.length; i++) {
                    if (Recommend[i].isSelected()) {
                        label.add(Recommend[i].getText().toString());
                        labelCount += 1;
                    }
                }

                //선택된 라벨의 갯수가 5개 이하라면 피드 작성 화면의 라벨을 선택한 라벨로 채워넣고 VISIBLE 시킴
                if (labelCount <= 5) {
                    for (int i = 0; i < labelCount; i++) {
                        feedCreateLabel[i].setVisibility(View.VISIBLE);
                        feedCreateLabel[i].setText(label.get(i));
                    }
                    for (int i = 4; i >= labelCount; i--) {
                        feedCreateLabel[i].setVisibility(View.INVISIBLE);
                        feedCreateLabel[i].setText(null);
                    }
                    customDialog.dismiss(); // 다이얼로그 닫기

                    //라벨이 없으면 버튼의 텍스트를 "라벨 추가", 하나라도 있으면 "라벨 수정" 으로 설정
                    if (TextUtils.isEmpty(feedCreateLabel[0].getText())) {
                        binding.addLabel.setText("라벨 추가");
                    } else {
                        binding.addLabel.setText("라벨 수정");
                    }

                } else {
                    //라벨을 6개 이상 선택했다면 AlertDialog를 띄워줌
                    new AlertDialog.Builder(current_context)
                            .setMessage("라벨은 최대 5개까지 선택이 가능합니다.")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

                //라벨을 1개 이상 5개 이하로 선택했다면 "라벨을 추가해보세요" 라는 문구를 안보이게 함.
                if (labelCount > 0 && labelCount <= 5) {
                    tvlabelHint.setVisibility(View.INVISIBLE);
                } else if (labelCount == 0) { //라벨을 하나도 선택하지 않았다면 다시 문구를보이게 함. 6개 이상 선택했다면 아무런 작동 없음 (이전의 상태를 따라감)
                    tvlabelHint.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    //책 검색해서 선택하는 함수
    public void getBook() {
        Intent intent = new Intent(this, search_fragment_subActivity_main.class);
        intent.putExtra("classindex", 2);
        bookResult.launch(intent); //검색 결과를 받는 핸들러를 작동한다.
    }

    //피드 업로드
    public void feedUpload(String imgUrl) {

        if (binding.tvFeedBookTitle.getText().toString().equals("") || binding.edtFeedText.getText().toString().equals("")) { //책 선택이나 피드 내용이 없으면 작성해달라는 알림 띄움
            new AlertDialog.Builder(current_context)
                    .setMessage("책 제목과 피드 내용을 작성해주세요")
                    .setPositiveButton("알겠습니다.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else { //필수 입력사항이 입력돼있다면 작성한 내용을 파이어베이스에 업로드
            HashMap<String, Object> map = new HashMap<>();



            ArrayList<String> labelList = new ArrayList<String>(); //선택한 라벨 목록을 담을 리스트

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatTime = dateFormat.format(System.currentTimeMillis());

            map.put("UserToken", userInfo.getToken()); //유저 정보
            map.put("book", selected_book); //책 정보
            map.put("feedText", binding.edtFeedText.getText().toString()); //피드 내용
            map.put("label", labelAdd(labelList)); //라벨 리스트
            map.put("date", formatTime); //현재 시간 millis로
            map.put("FeedID", FeedID); //피드 아이디
            map.put("commentsCount",0);
            map.put("likeCount", 0);
            if (imgUrl != null) map.put("imgurl", imgUrl); //이미지 url

            fbModule.readData(1, map, FeedID);

            // 장르 처리
//            HashMap<String, Object> AfterCreatemap = new HashMap<>(); //피드 만들면 장르 up
//            HashMap<String, Object> countmap = new HashMap<>(); // 피드 만들면
//
//
//            userInfo.setGenre(selected_book.getCategoryname(), current_context);
//            AfterCreatemap.put("userinfo_genre", userInfo.getGenre());
//            fbModule.readData(0, AfterCreatemap, userInfo.getToken());
//
//            BookWorm bookworm = new PersonalD(current_context).getBookworm();
//            int count = bookworm.getReadcount();
//            ++count;
//            bookworm.setReadcount(count);
//
//            countmap.put("bookworm_readcount", bookworm.getReadcount());
//            fbModule.readData(0, countmap, bookworm.getToken());
//
//
//            Achievement achievement = new Achievement(current_context, fbModule, userInfo, bookworm);
//            achievement.CompleteAchievement(userInfo, current_context);
//
//            new PersonalD(current_context).saveUserInfo(userInfo);
//            new PersonalD(current_context).saveBookworm(bookworm);
//            setResult(CREATE_OK);
            finish();
        }
    }

    //현재 화면의 라벨을 라벨 리스트에 추가하는 함수
    public ArrayList<String> labelAdd(ArrayList<String> label) {
        //피드 생성화면에 존재하는 라벨
        TextView feedCreateLabel[] = new TextView[5];
        int[] feedCreateLabelID = {R.id.tvlabel1, R.id.tvlabel2, R.id.tvlabel3, R.id.tvlabel4, R.id.tvlabel5,};
        //라벨 리스트에 현재 선택된 라벨들을 추가
        for (int i = 0; i < feedCreateLabel.length; i++) {
            feedCreateLabel[i] = findViewById(feedCreateLabelID[i]);
            label.add(feedCreateLabel[i].getText().toString());
        }
        return label;
    }

}
