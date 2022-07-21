package com.example.bookworm.bottomMenu.challenge.board;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookworm.bottomMenu.Feed.comments.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

public class BoardFB {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    private int LIMIT = 10;
    Task task = null;
    CollectionReference collectionReference;

    BoardFB(Context context) {
        this.context = context;
    }

    public void setLIMIT(int LIMIT) {
        this.LIMIT = LIMIT;
    }

    public void getData(Map map, String token) {//token = 챌린지 명
        collectionReference = db.collection("challenge").document(token).collection("feed");
        Query query = collectionReference.orderBy("boardID", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {//가져온 데이터가 존재할경우
                        ((subactivity_challenge_board) context).moduleUpdated(querySnapshot.getDocuments());
                    } else {//가져온 데이터가 존재하지 않을 경우
                        ((subactivity_challenge_board) context).isEmptyBoard(true);//인증글이 없습니다 라는 문구를 띄워줌
                    }
                }
            }
        });
    }


    //인증글 승인(트랜잭션)
    public void allowBoard(Board board) {
        final DocumentReference ref = db.collection("challenge").document(board.getChallengeName()).collection("feed").document(board.getBoardID()); //인증글
        final DocumentReference ref2 = db.collection("users").document(board.getUserToken()); //인증글 작성자

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(ref, "allowed", true); //인증완료 여부를 true로
                transaction.update(ref2, "UserInfo.completedChallenge", FieldValue.increment(1)); //완료한 챌린지 개수에 +1

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

    //인증글 삭제
    public void deleteBoard(Board board) {
        collectionReference = db.collection("challenge").document(board.getChallengeName()).collection("feed");
        //task 결정
        task = collectionReference.document(board.getBoardID()).delete();
        //task 실행
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("오류", "Error deleting " + e);
            }
        });
    }

    //인증글 댓글 삭제
    public void deleteComment(Board board, String commentID) {
        collectionReference = db.collection("challenge").document(board.getChallengeName()).collection("feed").document(board.getBoardID()).collection("comments");
        //task 결정
        task = collectionReference.document(commentID).delete();
        //task 실행
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("오류", "Error deleting " + e);
            }
        });
    }

    public void getCommentData(Map map, String token) {//token = 인증글 ID
        collectionReference = db.collection("challenge");
        Query query = collectionReference;
        if (map.get("BoardID") != null) {
            query = collectionReference.document((String) map.get("challengeName")).collection("feed").document(token).collection("comments").orderBy("commentID", Query.Direction.DESCENDING);
        }

        if (map.get("lastVisible") != null) {
            DocumentSnapshot snapshot = (DocumentSnapshot) map.get("lastVisible");
            query = query.startAfter(snapshot);
        }
        query = query.limit(LIMIT);
        task = query.get();

        //결과 확인
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    successRead((QuerySnapshot) task.getResult(), map);
                } else {
                    Log.d("TAG3", "get failed with ", task.getException());
                }
            }
        });
    }

    private void successRead(QuerySnapshot querySnapshot, Map map) {
        if (querySnapshot.isEmpty()) {
            //피드 조회
            if (map.get("BoardID") != null) {
                ((subactivity_challenge_board_comment) context).moduleUpdated(null);
            }

        } else {
            if (map.get("BoardID") != null) {
                ((subactivity_challenge_board_comment) context).moduleUpdated(querySnapshot.getDocuments());
            }

        }
    }
}
