package com.example.bookworm.Feed;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.example.bookworm.PostInfo;
import com.example.bookworm.R;


public class activity_createfeed extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createfeed);


        Button btn_submit;
        PostInfo postInfo = new PostInfo();


/*
        btn_submitibtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = editText.getText().toString(); // 게시글
                f = friends.getText().toString(); // 친구태그
                l = location.getText().toString(); // 위치

                final String uid = mAuth.getCurrentUser().getUid();
                final Uri file = Uri.fromFile(new File(Uripath)); // 절대경로uri를 file에 할당
                Log.d(TAG, "phto file : " + file);

                StorageReference storageReference = mStorage.getReference().child("userImages").child("uid/"+file.getLastPathSegment());
                storageReference.putFile(uriUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        final Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                        while (!imageUrl.isComplete()) ;

                        mDatabase.getReference().child("users").child(uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                        Log.d(TAG, "profileImageUrl" + userModel.profileImageUrl);
                                        Log.d(TAG, "userName" + userModel.userName);


                                        postInfo.postid = uid;
                                        postInfo.photo = imageUrl.getResult().toString();
                                        postInfo.profileimg = userModel.profileImageUrl;
                                        postInfo.photopath = file.getLastPathSegment();
                                        postInfo.contents = c;

                                        postInfo.username = userModel.userName;

                                        // 게시글 내용 저장
                                        mDatabase.getReference().child("contents").child("content").push()
                                                .setValue(postInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }
                });



 */
    }
}