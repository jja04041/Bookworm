package com.example.bookworm.bottomMenu.challenge.board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.bookworm.bottomMenu.Feed.comments.Comment;
import com.example.bookworm.bottomMenu.challenge.items.Challenge;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.SubactivityChallengeBoardCommentBinding;
import com.example.bookworm.extension.DiffUtilCallback;
import com.example.bookworm.notification.MyFCMService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//인증 게시판에서 사진을 누르면 댓글이 보이도록 한다.

public class subactivity_challenge_board_comment extends AppCompatActivity {

    SubactivityChallengeBoardCommentBinding binding;
    Context context;
    Board item;
    Challenge challenge;
    UserInfo nowUser;//현재 사용자 계정
    UserInfo creatorUser;
    final int LIMIT = 10;
    int page = 1;
    int position;
    BoardFB boardFB;
    MyFCMService myFCMService;
    UserInfoViewModel uv;
    private Map map;
    Board_CommentAdapter boardCommentAdapter;
    public ArrayList commentList;
    private Boolean isLoading = false, canLoad = true;
    DocumentSnapshot lastVisible = null;

//            });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivityChallengeBoardCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        item = (Board) getIntent().getSerializableExtra("board");
        challenge = (Challenge) getIntent().getSerializableExtra("challenge");
        nowUser = new PersonalD(this).getUserInfo();
        context = this;
        boardFB = new BoardFB(context);
        myFCMService = new MyFCMService();
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);
        setItems();
        binding.mRecyclerView.setNestedScrollingEnabled(false);


        binding.btnWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addComment();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setItems() {
        initComment();
        loadData();
        uv.getUser(item.getUserToken(), true);
        uv.getData().observe(this, userInfo -> {
            creatorUser = userInfo;
        });
    }

    //피드 초기화
    private void initComment() {
        isLoading = true;
        page = 1;
        canLoad = false;
        lastVisible = null;
        commentList = new ArrayList(); //댓글을 담는 리스트 생성
//        item.setPosition(position);
        commentList.add(item);
    }

    //리사이클러뷰 스크롤 초기화
    private void initScrollListener() {
        binding.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (!isLoading) {
                    try {
                        if (layoutManager != null && lastVisibleItemPosition == boardCommentAdapter.getItemCount() - 1) {
                            deleteLoading();
                            //이전에 가져왔던 자료를 인자로 보내주어 그 다음 자료부터 조회한다.
                            if (lastVisible != null) {
                                map.put("lastVisible", lastVisible);
                                boardFB.getCommentData(map, item.getBoardID());   //쿼리를 보내어, 데이터를 조회한다.
                            }
                            isLoading = true;//현재 로딩을 끝냄을 알린다.
                        }
                    } catch (NullPointerException e) {

                    }
                }
            }
        });

    }

    // 로딩이 완료되면 프로그레스바를 지움
    public void deleteLoading() {
        ArrayList arr = new ArrayList(commentList);
        arr.remove(arr.size() - 1);
        replaceItem(arr); //데이터가 삭제됨을 알림.
    }

    //리사이클러뷰를 초기화
    private void initRecyclerView() {
        binding.mRecyclerView.setAdapter(boardCommentAdapter);
        initScrollListener(); //무한스크롤
    }

    private void initAdapter() {
        boardCommentAdapter = new Board_CommentAdapter(commentList, context);
    }

    public void replaceItem(ArrayList newthings) {
        DiffUtilCallback callback = new DiffUtilCallback(commentList, newthings);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback, true);
        commentList.clear();
        commentList.addAll(newthings);
        boardCommentAdapter.setData(commentList);
        diffResult.dispatchUpdatesTo(boardCommentAdapter);
    }

    private void addComment() throws MalformedURLException {
        Map<String, Object> data = new HashMap<>();
        String string = binding.edtComment.getText().toString();

        if (!string.equals("") && !string.equals(null)) {
            //유저정보, 댓글내용, 작성시간
            Comment comment = new Comment();
            comment.getData(nowUser.getToken(), string, System.currentTimeMillis());
            data.put("comment", comment);
            //입력한 댓글 화면에 표시하기
            ArrayList a = new ArrayList(commentList);
            a.add(1, comment);
            replaceItem(a);
            new Board_CommentsCounter().addCounter(data, context, challenge.getTitle(), item.getBoardID());

            //키보드 내리기
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.edtComment.getWindowToken(), 0);
            binding.edtComment.clearFocus();
            binding.edtComment.setText(null);
            binding.mRecyclerView.smoothScrollToPosition(0); //맨 위로 포커스를 이동 (본인 댓글 확인을 위함)

            myFCMService.sendPostToFCM(context, creatorUser.getFCMtoken(), nowUser.getUsername() + "님이 댓글을 남겼습니다. " + "\"" + string + "\"");
        }
    }


    private void loadData() {
        map = new HashMap();
        if (map.get("lastVisible") != null) map.remove("lastVisible");
        map.put("BoardID", item.getBoardID());
        map.put("challengeName", challenge.getTitle());
        boardFB.setLIMIT(LIMIT);
        boardFB.getCommentData(map, item.getBoardID());
    }

    public void moduleUpdated(List<DocumentSnapshot> a) {
        ArrayList newList = new ArrayList(commentList);
        if (a == null) {
            if (page == 1) {
                initComment();
                initAdapter();
            } else {
                canLoad = false;
                isLoading = true;
            }
        } else {
            if (page == 1) {
                isLoading = false;
                if (commentList.size() < 1) {
                    commentList = new ArrayList(); //챌린지를 담는 리스트 생성
                    commentList.add(item);
                }
            }
            //가져온 데이터를 for문을 이용하여, feed리스트에 차곡차곡 담는다.
            try {

                for (DocumentSnapshot snapshot : a) {
                    Map data = snapshot.getData();
                    Comment item = new Comment();
                    item.setData(data);
                    newList.add(item);
                }
                //가져온 값의 마지막 snapshot부터 이어서 가져올 수 있도록 하기 위함.
                lastVisible = a.get(a.size() - 1);
                //리사이클러뷰에서 튕김현상이 발생하여 넣어준 코드
                //현재 불러오는 값의 크기(a.size())가 페이징 제한 값(LIMIT)보다 작은 경우 => 더이상 불러오지 않게 함.
                canLoad = true;
                if (a.size() < LIMIT) {
                    canLoad = false;
                }
            } catch (NullPointerException e) {
                canLoad = false;
            }
        }
        //만약 더이상 불러오지 못 할 경우
        if (canLoad == false) {
            isLoading = true;
            if (page > 1) replaceItem(newList); //이미 불러온 데이터가 있는 경우엔 가져온 데이터 만큼의 범위를 늘려준다.
            else { //없는 경우엔 새로운 어댑터에 데이터를 담아서 띄워준다.
                initAdapter(); //어댑터 초기화
                replaceItem(newList);//데이터 범위 변경
                initRecyclerView(); //리사이클러뷰에 띄워주기
            }
        }
        //더 불러올 데이터가 있는 경우
        else {
            isLoading = false;
            newList.add(new Comment()); //로딩바 표시를 위한 빈 값
            if (page > 1) replaceItem(newList);//데이터 범위 변경
            else {
                initAdapter();//어댑터 초기화
                replaceItem(newList);//데이터 범위 변경
                initRecyclerView(); //리사이클러뷰에 띄워주기
            }
            page++; //로딩을 다하면 그다음 페이지로 넘어간다.
        }
    }
}
