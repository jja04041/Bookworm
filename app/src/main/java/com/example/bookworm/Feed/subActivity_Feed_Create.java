package com.example.bookworm.Feed;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.databinding.SubactivityFeedCreateBinding;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.Module;
import com.example.bookworm.modules.personalD.PersonalD;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    int label = 0;
    //라벨은 알럿 다이어그램을 통해 입력을 받고, 선택한 값으로 라벨이 지정됨 => 구현 예정

    //사용자가 선택한 어플로 이어서 사진을 선택할 수 있게 함.
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            Log.d("map", result.getData().toString()); // 카메라로 찍을 경우에는 인식이 안된다.
                            InputStream in = getContentResolver().openInputStream(result.getData().getData());
                            Bitmap img = BitmapFactory.decodeStream(in);
                            in.close();
                            ivUpload.setImageBitmap(img);
                        } catch (Exception e) {

                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
                    }
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
//        tvlabel1 = findViewById(R.id.tvlabel1);
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
        btn.add(btnAdd);
        LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat now_date = new SimpleDateFormat();

        current_context = this;
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.

        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(imgProfile); //프로필사진 로딩후 삽입.
        binding.tvNickname.setText(userInfo.getUsername());

        customDialog = new Dialog(subActivity_Feed_Create.this);       // Dialog 초기화
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        customDialog.setContentView(R.layout.custom_dialog_label);

        binding.addLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showcustomDialog();
            }
        });

//        LLlabel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });


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
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getExternalFilesDir(Environment.DIRECTORY_DCIM);
                Uri cameraOutputUri = Uri.fromFile(file);
                Intent intent = getPickIntent(cameraOutputUri);
                startActivityResult.launch(intent);
            }
        });

        tvFinish.setOnClickListener(new View.OnClickListener() {
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

    private Intent getPickIntent(Uri cameraOutputUri) {
        final List<Intent> intents = new ArrayList<Intent>();

        if (true) {
            intents.add(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        }

        if (true) {
            setCameraIntents(intents, cameraOutputUri);
        }

        if (intents.isEmpty()) return null;
        Intent result = Intent.createChooser(intents.remove(0), null);
        if (!intents.isEmpty()) {
            result.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[]{}));
        }
        return result;


    }

    private void setCameraIntents(List<Intent> cameraIntents, Uri output) {
        //인텐트 생성
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        //다양한 카메라 어플을 지원함.
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
            cameraIntents.add(intent);
        }
    }

    // 커스텀 다이얼로그(라벨)를 디자인하는 함수
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
        for (int i = 0; i < Recommend.length; i++) {
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
//                for (int i = 0; i < 5; i++) {
//                    feedCreateLabel[i].setVisibility(View.INVISIBLE);
//                    feedCreateLabel[i].setText(null);
//                }

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

}