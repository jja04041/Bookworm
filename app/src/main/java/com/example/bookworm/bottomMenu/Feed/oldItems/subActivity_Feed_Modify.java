package com.example.bookworm.bottomMenu.feed.oldItems;


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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;

import com.example.bookworm.bottomMenu.feed.Feed;
import com.example.bookworm.bottomMenu.feed.ImagePicker;
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.internet.Module;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.databinding.SubactivityFeedModifyBinding;
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
//사용하지 않지만, 참고용으로 둔 상태
public class subActivity_Feed_Modify extends AppCompatActivity {

    public static int MODIFY_OK = 26;
    private SubactivityFeedModifyBinding binding;
    FBModule fbModule;
    UserInfo userInfo;
    Context current_context;
    Bitmap uploaded;
    Module module;
    String imgurl = null;
    Dialog customDialog;
    String FeedID;
    Feed feed;
    Book selected_book; //선택한 책 객체
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
                    this.selected_book = (Book) intent.getParcelableExtra("data");
                    binding.tvFeedBookTitle.setText(selected_book.getTitle()); //책 제목만 세팅한다.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivityFeedModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnImageUpload.setVisibility(View.INVISIBLE); //피드 수정 화면에서는 사진 수정 불가능

        feed = getIntent().getParcelableExtra("Feed");
        this.selected_book = feed.getBook();
        binding.tvFeedBookTitle.setText(selected_book.getTitle()); //책 제목 세팅한다.
        binding.edtFeedText.setText(feed.getFeedText()); //피드 내용 세팅
        if (!feed.getImgurl().equals("")) Glide.with(this).load(feed.getImgurl()).into(binding.ivpicture); //피드 사진 세팅(수정 불가)

        //프로필 세팅


//        ArrayList<String> modifyLabel; //수정화면의 라벨 세팅
//        modifyLabel = feed.getLabel();

        //뒤로가기
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        current_context = this;
        fbModule = new FBModule(current_context);
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.

        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필사진 로딩후 삽입.
        binding.tvNickname.setText(userInfo.getUsername());

        customDialog = new Dialog(subActivity_Feed_Modify.this);       // Dialog 초기화
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        customDialog.setContentView(R.layout.custom_dialog_label);


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
                        .setMessage("피드를 수정하시겠습니까?")
                        .setPositiveButton("네", (dialog, which) -> {
                                    dialog.dismiss();
                                    upload();
                                }
                        )
                        .setNegativeButton("아니요", (dialog, which) -> dialog.dismiss()
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
        Intent intent = new Intent(subActivity_Feed_Modify.this, ImagePicker.class);
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
        Intent intent = new Intent(subActivity_Feed_Modify.this, ImagePicker.class);
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1);
        startActivityResult.launch(intent);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(subActivity_Feed_Modify.this);
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

    //책 검색해서 선택하는 함수
    public void getBook() {
//        Intent intent = new Intent(this, search_fragment_subActivity_main.class);
//        intent.putExtra("classindex", 2);
//        bookResult.launch(intent); //검색 결과를 받는 핸들러를 작동한다.
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

            map.put("userToken", userInfo.getToken()); //사용자 토큰
            map.put("book", selected_book); //책 정보
            map.put("feedText", binding.edtFeedText.getText().toString()); //피드 내용
            map.put("date", feed.getDate()); //현재 시간 millis로
            map.put("feedID", feed.getFeedID()); //피드 아이디
            map.put("commentsCount", feed.getCommentsCount());
            map.put("likeCount", feed.getLikeCount());
            if (feed.getImgurl() != null) map.put("imgurl", feed.getImgurl()); //이미지 url


            Intent intent = new Intent();
            intent.putExtra("modifiedFeed", feed);
            fbModule.readData(1, map, FeedID);
            setResult(MODIFY_OK, intent);

            finish();
        }
    }

}
