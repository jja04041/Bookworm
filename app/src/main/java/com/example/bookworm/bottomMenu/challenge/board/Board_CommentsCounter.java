package com.example.bookworm.bottomMenu.challenge.board;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bookworm.bottomMenu.Feed.comments.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Map;


public class Board_CommentsCounter {
    FirebaseFirestore db;

    public Board_CommentsCounter() {
        db = FirebaseFirestore.getInstance();
    }


    public void addCounter(Map map, Context context, String challengeName, String BoardID) {
        initialize(map, challengeName, BoardID, 1);
    }

    public void removeCounter(Map map, Context context, String challengeName, String BoardID) {
        initialize(map, challengeName, BoardID, -1);
    }


    public void initialize(Map map, String challengeName, String BoardID, int count) {
        final DocumentReference ref = db.collection("challenge").document(challengeName).collection("feed").document(BoardID);
        Comment comment = (Comment) map.get("comment");
        final DocumentReference ref2 = ref.collection("comments").document(comment.getCommentID());

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(ref, "commentsCount", FieldValue.increment(count));
                if (count == 1) {
                    transaction.set(ref2, comment);
                } else transaction.delete(ref2);
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



