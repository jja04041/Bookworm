package com.example.bookworm.Challenge.Board;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.bookworm.Achievement.Achievement;
import com.example.bookworm.Bw.BookWorm;
import com.example.bookworm.Challenge.items.Challenge;
import com.example.bookworm.Core.Internet.FBModule;
import com.example.bookworm.Core.Internet.Module;
import com.example.bookworm.Core.UserData.PersonalD;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.Feed.ImagePicker;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.databinding.SubactivityChallengeBoardCreateBinding;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class subactivity_challenge_board_create extends AppCompatActivity {

    public static int CREATE_OK = 30;
    private SubactivityChallengeBoardCreateBinding binding;
    FBModule fbModule;
    UserInfo userInfo;
    Context current_context;
    Bitmap uploaded;
    Module module;
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
        fbModule = new FBModule(current_context);
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.


        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필사진 로딩후 삽입.
        binding.tvNickname.setText(userInfo.getUsername());


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
        Intent intent = new Intent(subactivity_challenge_board_create.this, ImagePicker.class);
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
        Intent intent = new Intent(subactivity_challenge_board_create.this, ImagePicker.class);
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1);
        startActivityResult.launch(intent);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(subactivity_challenge_board_create.this);
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

        BoardID = String.valueOf(System.currentTimeMillis()) + "_" + userInfo.getToken(); //현재 시각 + 사용자 토큰을 BoardID로 설정

        if (uploaded != null) {
            try {
                File filesDir = getApplicationContext().getFilesDir();
                File file = new File(filesDir, "feed_" + BoardID + ".jpg"); //파일명 설정
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

    //피드 업로드
    public void feedUpload(String imgUrl) {

        if (binding.edtFeedText.getText().toString().equals("")) { //피드 내용이 없으면 작성해달라는 알림 띄움
            new AlertDialog.Builder(current_context)
                    .setMessage("인증글 내용을 작성해주세요")
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
            map.put("commentsCount",0);
            map.put("likeCount", 0);
            if (imgUrl != null) map.put("imgurl", imgUrl); //이미지 url

            //챌린지 인증글 업로드
            fbModule.uploadChallengeBoard(2, challenge.getTitle(), BoardID, map);

            // 장르 처리
            HashMap<String, Object> savegenremap = new HashMap<>();

            userInfo.setGenre(selected_book.getCategoryname(), current_context);
            savegenremap.put("userinfo_genre", userInfo.getGenre());
            fbModule.readData(0, savegenremap, userInfo.getToken());

            BookWorm bookworm = new PersonalD(current_context).getBookworm();
            Achievement achievement = new Achievement(current_context, fbModule, userInfo, bookworm);
            achievement.CompleteAchievement(userInfo, current_context);

            new PersonalD(current_context).saveUserInfo(userInfo);
            setResult(CREATE_OK);
            finish();
        }
    }


}