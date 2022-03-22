package com.example.bookworm.Feed;


import android.Manifest;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class subActivity_Feed_Create extends AppCompatActivity {

    private SubactivityFeedCreateBinding binding;
    FBModule fbModule;
    UserInfo userInfo;
    Context current_context;
    Module module;
    HashMap<String, String> data;

    TextView tvFinish, tvNickName, tvlabel1;
    Button btnAdd, btnUp;
    ImageView ivUpload, imgProfile;
    LinearLayout LLlabel;
    ArrayList<Button> btn;
    Dialog customDialog;
    Book selected_book; //선택한 책 객체
    int label = 0;

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

//        edtFeedContent = findViewById(R.id.edtFeedContent);
//        btnAdd = findViewById(R.id.btnAdd);
        btnUp = findViewById(R.id.btnImageUpload);
//        ivUpload = findViewById(R.id.ivUpload);
        imgProfile = findViewById(R.id.ivProfileImage);
        tvFinish = findViewById(R.id.tvFinish);
        tvNickName = findViewById(R.id.tvNickname);
        tvlabel1 = findViewById(R.id.tvlabel1);
        LLlabel = findViewById(R.id.llLabel);

        //피드 생성화면에 존재하는 라벨
        TextView feedCreateLabel[] = new TextView[5];
        int[] feedCreateLabelID = {R.id.tvlabel1, R.id.tvlabel2, R.id.tvlabel3, R.id.tvlabel4, R.id.tvlabel5,};

        //우선 빈 껍데기만 있는 라벨을 보이지 않게 설정해놓음
        for (int i = 0; i < feedCreateLabel.length; i++) {
            feedCreateLabel[i] = findViewById(feedCreateLabelID[i]);
            feedCreateLabel[i].setVisibility(View.INVISIBLE);
        }


        //tvlabel1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55ff0000"))); //자바로 BackgroundTint 설정

        btn = new ArrayList<>();

        fbModule = new FBModule(null);

        LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat now_date = new SimpleDateFormat();

        current_context = this;
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.


        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(imgProfile); //프로필사진 로딩후 삽입.
        binding.tvNickname.setText(userInfo.getUsername());

        customDialog = new Dialog(subActivity_Feed_Create.this);       // Dialog 초기화
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        customDialog.setContentView(R.layout.custom_dialog_label);

        LLlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showcustomDialog();
            }
        });


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

        binding.addLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showcustomDialog();
            }
        });

        binding.tvFeedBookTitle.setSingleLine(true);    // 책 제목 한줄로 표시하기
        binding.tvFeedBookTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        binding.tvFeedBookTitle.setSelected(true);      // 선택하기

        binding.tvFeedBookTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBook();
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

        private void launchGalleryIntent () {
            Intent intent = new Intent(subActivity_Feed_Create.this, ImagePicker.class);
            intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_GALLERY_IMAGE);

            // setting aspect ratio
            intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true);
            intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
            intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1);
            startActivityResult.launch(intent);
        }

        private void showSettingsDialog () {
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
        private void openSettings () {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityResult.launch(intent);

        }

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

                //라벨 리스트 초기화하고 기존의 라벨 칸마다 INVISIBLE 시키고 Text를 Null로 만듦
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

    }