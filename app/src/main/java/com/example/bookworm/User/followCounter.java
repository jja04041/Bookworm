package com.example.bookworm.User;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bookworm.Feed.Comments.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

public class followCounter {
    FirebaseFirestore db;

    public followCounter() {
        db = FirebaseFirestore.getInstance();
    }

    public void follow(UserInfo userInfo, UserInfo nowuserInfo, Context context) {
        initialize(userInfo, nowuserInfo, 1);
    }

    public void unfollow(UserInfo userInfo, UserInfo nowuserInfo, Context context) {
        initialize(userInfo, nowuserInfo, -1);
    }


    public void initialize(UserInfo userInfo, UserInfo nowuserInfo, int count) {
        final DocumentReference ref = db.collection("users").document(userInfo.getToken());
        final DocumentReference Myref = db.collection("users").document(nowuserInfo.getToken());
        final DocumentReference refFollower = ref.collection("follower").document(nowuserInfo.getToken());
        final DocumentReference refFollowing = Myref.collection("following").document(userInfo.getToken());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
//                transaction.update(ref, "commentsCount", FieldValue.increment(count));
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
