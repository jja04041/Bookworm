package com.example.bookworm.Feed;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bookworm.Core.UserData.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

public class likeCounter {
    FirebaseFirestore db;
    public likeCounter() {
        db = FirebaseFirestore.getInstance();
    }

    public void updateCounter( Map map, String feedID) {
        final DocumentReference ref = db.collection("feed").document(feedID);
        final DocumentReference ref2 = db.collection("users").document(((UserInfo) map.get("nowUser")).getToken());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot =transaction.get(ref);
                long likecount = snapshot.getLong("likeCount");
                if ((Boolean) map.get("liked")) likecount += 1;
                else likecount -= 1;
                transaction.update(ref, "likeCount", likecount).update(ref2, "UserInfo.likedPost", ((UserInfo) map.get("nowUser")).getLikedPost());
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
