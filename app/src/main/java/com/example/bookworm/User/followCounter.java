package com.example.bookworm.User;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bookworm.Feed.Comments.Comment;
import com.example.bookworm.ProfileInfoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

public class followCounter {
    FirebaseFirestore db;
    Long current;//현재 팔로워 수
    Context context;
    public followCounter() {
        db = FirebaseFirestore.getInstance();
    }

    public void follow(UserInfo userInfo, UserInfo nowuserInfo, Context context) {
        initialize(userInfo, nowuserInfo, 1);
    }

    public void unfollow(UserInfo userInfo, UserInfo nowuserInfo, Context context) {
        initialize(userInfo, nowuserInfo, -1);
    }

    //팔로우중인지 판단
    public void isfollow(UserInfo userInfo, UserInfo nowuserInfo, Context context) {
        this.context=context;
        DocumentReference Myref = db.collection("users").document(nowuserInfo.getToken());
        DocumentReference refFollowing = Myref.collection("following").document(userInfo.getToken());
        refFollowing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ((ProfileInfoActivity) context).isFollowingTrue();
                    } else {
                        ((ProfileInfoActivity) context).isFollowingFalse();
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }


    public void initialize(UserInfo userInfo, UserInfo nowuserInfo, int count) {
        final DocumentReference ref = db.collection("users").document(userInfo.getToken());
        final DocumentReference Myref = db.collection("users").document(nowuserInfo.getToken());
        final DocumentReference refFollower = ref.collection("follower").document(nowuserInfo.getToken());
        final DocumentReference refFollowing = Myref.collection("following").document(userInfo.getToken());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                current=transaction.get(ref).getLong("UserInfo.followerCounts");
                current+=count;
                transaction.update(ref, "UserInfo.followerCounts",current);
                transaction.update(Myref, "UserInfo.followingCounts", FieldValue.increment(count));
                if (count == 1) {
                    transaction.set(refFollower, nowuserInfo);
                    transaction.set(refFollowing, userInfo);
                } else {
                    transaction.delete(refFollower);
                    transaction.delete(refFollowing);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ((ProfileInfoActivity)context).setFollowerCnt(current);
                Log.d("Success", "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Failed", "Transaction failure.", e);
            }
        });
    }
}
