package com.example.bookworm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.bookworm.Follow.View.FollowerActivity;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.Follow.Modules.followCounter;
import com.example.bookworm.databinding.ActivityProfileInfoBinding;
import com.example.bookworm.Core.UserData.PersonalD;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ProfileInfoActivity extends AppCompatActivity {

    ActivityProfileInfoBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserInfo userInfo, nowUser; //타인 userInfo, 현재 사용자 nowUser
    Context context;
    String userID;
    followCounter followCounter = new followCounter();

    //자신이나 타인의 프로필을 클릭했을때 나오는 화면
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        //일단 안보였다가 파이어베이스에서 값을 모두 받아오면 보여주는게 UX면에서 좋을거같음
//        binding.tvNickname.setVisibility(View.INVISIBLE);
//        binding.ivProfileImage.setVisibility(View.INVISIBLE);
//        binding.tvFollow.setVisibility(View.INVISIBLE);

        //shimmer 적용을 위해 기존 뷰는 일단 안보이게, shimmer는 보이게
        binding.llResult.setVisibility(View.GONE);
        binding.SFLoading.startShimmer();
        binding.SFLoading.setVisibility(View.VISIBLE);

        //작성자 UserInfo (userID를 사용해 파이어베이스에서 받아옴)
        userID = getIntent().getStringExtra("userID");
        userInfo = new UserInfo();
        setUserInfo(userInfo, userID);

        //현재 사용자 UserInfo
        nowUser = new PersonalD(this).getUserInfo();

        //팔로우 버튼을 클릭했을때 버튼 모양, 상태 변경
        binding.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.tvFollow.isSelected()) {
                    binding.tvFollow.setSelected(false);
                    binding.tvFollow.setText("팔로우");
                    followCounter.unfollow(userInfo, nowUser, context);
                } else {
                    binding.tvFollow.setSelected(true);
                    binding.tvFollow.setText("팔로잉");
                    followCounter.follow(userInfo, nowUser, context);
                }
            }
        });

        //뒤로가기
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnFollower.setOnClickListener((view)-> {
            Intent intent=new Intent(context, FollowerActivity.class);
            intent.putExtra("token",userInfo.getToken());
            intent.putExtra("page",0);
            context.startActivity(intent);
        });
        binding.btnFollowing.setOnClickListener((view)-> {
            Intent intent=new Intent(context, FollowerActivity.class);
            intent.putExtra("token",userInfo.getToken());
            intent.putExtra("page",1);
            context.startActivity(intent);
        });
    }

    //이미 팔로잉 중
    public void isFollowingTrue() {
        binding.tvFollow.setSelected(true);
        binding.tvFollow.setText("팔로잉");
        Log.d("TAG", "로그값");
    }

    //팔로잉 중이 아님
    public void isFollowingFalse() {
        binding.tvFollow.setSelected(false);
        binding.tvFollow.setText("팔로우");
        Log.d("TAG", "로그값2");
    }

    //usedID만으로 파이어베이스에서 UserInfo 가져와서 세팅하기
    public void setUserInfo(UserInfo UserInfo, String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserInfo.add((Map) document.get("UserInfo"));
                        setUI();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    //UI설정
    public void setUI() {
        binding.tvNickname.setText(userInfo.getUsername()); //닉네임 설정
        binding.tvNickname.setVisibility(View.VISIBLE);

        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필이미지 설정
        binding.ivProfileImage.setVisibility(View.VISIBLE);

        //지금 팔로우중인지 판단하고 팔로우 버튼 상태를 변경
        followCounter.isfollow(userInfo, nowUser, context);
        binding.tvFollow.setVisibility(View.VISIBLE);

        //내 프로필 화면이라면 팔로우 버튼 안보이게
        if (userInfo.getToken().equals(nowUser.getToken())) {
            binding.tvFollow.setVisibility(View.GONE);
        }
        binding.tvFollowCount.setText(String.valueOf(userInfo.getFollowerCounts()));

        //shimmer 적용 끝내고 shimmer는 안보이게, 기존 뷰는 보이게
        binding.llResult.setVisibility(View.VISIBLE);
        binding.SFLoading.stopShimmer();
        binding.SFLoading.setVisibility(View.GONE);
    }

    public void setFollowerCnt(Long count) {
        binding.tvFollowCount.setText(String.valueOf(count));
    }


}