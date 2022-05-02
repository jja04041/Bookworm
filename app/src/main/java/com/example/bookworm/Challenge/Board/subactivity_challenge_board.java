package com.example.bookworm.Challenge.Board;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.bookworm.Challenge.items.Challenge;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_result;
import com.example.bookworm.databinding.SubactivityChallengeBoardBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//인증 게시판

public class subactivity_challenge_board extends AppCompatActivity {

    SubactivityChallengeBoardBinding binding;
    Context context;
    BoardFB boardFB;
    private Boolean canLoad = true; //더 로딩이 가능한지 확인하는 변수[자료의 끝을 판별한다.]
    private int page = 1;
    Challenge challenge;
    private final int LIMIT = 10;
    private ArrayList<Board> boardList = null;
    private BoardAdapter boardAdapter;
    private Boolean isRefreshing = false;
    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    private DocumentSnapshot lastVisible;
    Map<String, Object> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivityChallengeBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        boardFB = new BoardFB(context);


        Intent intent = getIntent();
        //넘겨받은 값 챌린지 객체에 넣음
        challenge = (Challenge) intent.getSerializableExtra("challenge");

//        Book book = item.getBook();
        binding.feedBookAuthor.setText(challenge.getBook().getAuthor());
        Glide.with(this).load(challenge.getBook().getImg_url()).into(binding.feedBookThumb); //책 썸네일 설정
        binding.feedBookTitle.setText(challenge.getBook().getTitle());
        binding.llbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, search_fragment_subActivity_result.class);
                intent.putExtra("itemid", challenge.getBook().getItemId());
                context.startActivity(intent);
            }
        });

        //인증글 작성 버튼
        binding.btnCreateChallengeBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, subactivity_challenge_board_create.class);
                intent.putExtra("challenge", challenge);
                context.startActivity(intent);
            }
        });

        boardFB.getData(map, challenge.getTitle());
    }

    private void setItems() {

    }

    //BoardFB에서 사용할 함수
    public void moduleUpdated(List<DocumentSnapshot> a) {
        boardList = new ArrayList<>();
        try {
            for (DocumentSnapshot snapshot : a) {
                Map data = snapshot.getData();
                Board board = new Board(data);
                boardList.add(board);
            }
            //가져온 값의 마지막 snapshot부터 이어서 가져올 수 있도록 하기 위함.
            lastVisible = a.get(a.size() - 1);
            //리사이클러뷰에서 튕김현상이 발생하여 넣어준 코드
            //현재 불러오는 값의 크기(a.size())가 페이징 제한 값(LIMIT)보다 작은 경우 => 더이상 불러오지 않게 함.
            if (a.size() < LIMIT) {
                canLoad = false;
            }
        } catch (NullPointerException e) {
            canLoad = false;
        }
        initRecyclerView();
    }

    //리사이클러뷰 초기화
    public void initRecyclerView() {
        BoardAdapter boardAdapter = new BoardAdapter(boardList, context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false);
        binding.mRecyclerView.setLayoutManager(gridLayoutManager);
        binding.mRecyclerView.setAdapter(boardAdapter);
//        initScrollListener(); //무한스크롤
    }

}