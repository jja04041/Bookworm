package com.example.bookworm.Feed.Comments;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bookworm.Feed.Comments.Comment;
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



public class CommentsCounter {
    FirebaseFirestore db;
    public CommentsCounter() {
        db = FirebaseFirestore.getInstance();
    }


    public void addCounter(Map map, Context context, String feedID) {
       initialize(map,feedID,1);
    }
    public void removeCounter(Map map, Context context, String feedID){
        initialize(map,feedID,-1);
    }


    public void initialize(Map map,String feedID,int count){
        final DocumentReference ref = db.collection("feed").document(feedID);
        Comment comment=(Comment) map.get("comment");
        final DocumentReference ref2 = ref.collection("comments").document(comment.getCommentID());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(ref,"commentsCount", FieldValue.increment(count));
                if (count==1){
                    transaction.set(ref2,comment);
                }else transaction.delete(ref2);
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



