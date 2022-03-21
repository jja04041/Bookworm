package com.example.bookworm.Feed;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.databinding.SubactivityFeedCreateBinding;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.Module;
import com.example.bookworm.modules.personalD.PersonalD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;

public class subActivity_Feed_Create extends AppCompatActivity {


    private SubactivityFeedCreateBinding binding;
    FBModule fbModule;
    UserInfo userInfo;
    Context current_context;
    Module module;
    HashMap<String, String> data;


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
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

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
        binding = SubactivityFeedCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fbModule = new FBModule(null);

        LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat now_date = new SimpleDateFormat();

        current_context = this;
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.

        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필사진 로딩후 삽입.
        binding.tvNickname.setText(userInfo.getUsername());

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //ShowDialog 만들기
//
//                data = new HashMap();
//                data.put("feed_content", edtFeedContent.getText().toString());
//                data.put("upload_date", now_date.toString());
//
//                //data.put()을 이용하여, data에 값을 넣는다
//
//                fbModule.saveData(1, data);
////                //라벨생성
////                setLabel(a,Color.parseColor("#EFDDDD"));
//            }
//        });
        binding.btnImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withActivity((Activity) current_context)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
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

        binding.tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                Map map = new HashMap();
//                Book book= new Book("a","b","c","d","e");
//                Book book2= new Book("a1","b1","c1","d1","e1");
//                ArrayList<Book> books=new ArrayList<>();//{ book,book2 }
//                books.add(book);
//                books.add(book2);
//                map.put("book",books);
//                db.collection("test").document("abc").set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        finish();
//                    }
//                });
                finish();
            }
        });
    }


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

}