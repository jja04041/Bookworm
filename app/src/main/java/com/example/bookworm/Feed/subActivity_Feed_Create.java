package com.example.bookworm.Feed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.User.UserInfo;
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
    FBModule fbModule;
    UserInfo userInfo;
    Context current_context;
    Module module;
    HashMap<String, String> data;
    TextView tvFinish, tvNickName;
    Button btnAdd, btnUp;
    ImageView ivUpload, imgProfile;
    LinearLayout layout;
    ArrayList<Button> btn;
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
        setContentView(R.layout.subactivity_feed_create);
//        edtFeedContent = findViewById(R.id.edtFeedContent);
//        btnAdd = findViewById(R.id.btnAdd);
//        btnUp = findViewById(R.id.btnimgUp);
//        ivUpload = findViewById(R.id.ivUpload);
        imgProfile = findViewById(R.id.ivProfileImage);
        tvFinish = findViewById(R.id.tvFinish);
        tvNickName = findViewById(R.id.tvNickname);
        layout = findViewById(R.id.llLabel);

        btn = new ArrayList<>();
        fbModule = new FBModule(null);
        btn.add(btnAdd);
        LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat now_date = new SimpleDateFormat();

        current_context = this;
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.

        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(imgProfile); //프로필사진 로딩후 삽입.
        tvNickName.setText(userInfo.getUsername());

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
//        btnUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                File file = getExternalFilesDir(Environment.DIRECTORY_DCIM);
//                Uri cameraOutputUri = Uri.fromFile(file);
//                Intent intent = getPickIntent(cameraOutputUri);
//                startActivityResult.launch(intent);
//            }
//        });

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

    private void labelCtrl(int mode, Button button) {

        switch (mode) {
            case 0: //ADD
                btn.add(button);
                if (btn.size() == 4) {
                    btn.remove(btnAdd);
                }
                break;
            case 1: //Remove
                btn.remove(button);
                if (!btn.contains(btnAdd)) {
                    btn.add(0, btnAdd);
                }
                label--;
                break;
        }
        layout.removeAllViews();
        for (Button i : btn) {
            layout.addView(i);
        }
        Log.d("arrayNow", btn.toString());
//        //레이아웃을 새로고침 함.

    }

    private void setLabel(String Text, int btnColor) {
        if (label < 3) {
            //버튼 세팅
            Button btn = new Button(getApplicationContext());
            btn.setClickable(true);
            //label 디자인
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, 150);
            layoutParams.setMargins(10, 10, 10, 10);
            btn.setLayoutParams(layoutParams);
            btn.setPadding(10, 0, 10, 0);
            Drawable unwrappedDrawable = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.label_design);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, btnColor);
            btn.setBackground(wrappedDrawable);

            btn.setText(Text + "\t\t\tX");
            //클릭 불가능하게 만들기
//            btn.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    labelCtrl(1,btn);
//                    return true;
//                }
//            });
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    labelCtrl(1, btn);
                }
            });
            //라벨 추가
            labelCtrl(0, btn);
            label++;
        }
    }

}