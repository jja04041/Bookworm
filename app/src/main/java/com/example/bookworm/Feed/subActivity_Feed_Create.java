package com.example.bookworm.Feed;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
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
        tvlabel1 = findViewById(R.id.tvlabel1);
        LLlabel = findViewById(R.id.llLabel);

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

        LLlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showcustomDialog();
            }
        });


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
    public void showcustomDialog () {
        customDialog.show(); // 다이얼로그 띄우기

        Button noBtn = customDialog.findViewById(R.id.btnCancle);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                customDialog.dismiss(); // 다이얼로그 닫기
            }
        });
        // 네 버튼
        customDialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                customDialog.dismiss(); // 다이얼로그 닫기
            }
        });
    }

}