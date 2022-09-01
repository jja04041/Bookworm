package com.example.bookworm.bottomMenu.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.bookworm.BookWorm;
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity;
import com.example.bookworm.bottomMenu.search.items.book.Book;
import com.example.bookworm.bottomMenu.search.items.book.BookAdapter;
import com.example.bookworm.bottomMenu.search.items.book.OnBookItemClickListener;
import com.example.bookworm.bottomMenu.search.items.book.RecomBookAdapter;
import com.example.bookworm.bottomMenu.search.subactivity.main.search_fragment_subActivity_main;
import com.example.bookworm.bottomMenu.search.subactivity.result.search_fragment_subActivity_result;
import com.example.bookworm.core.internet.Module;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.FragmentSearchBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//탐색 탭

public class fragment_search extends Fragment {
    private ArrayList<UserInfo> userInfoList = null;
    private ArrayList<BookWorm> bookWormList = null;
    Module favmodule;
    RankFB rankFB;
    FragmentSearchBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(getLayoutInflater());

        rankFB = new RankFB(getContext());
        rankFB.getData();

        showShimmer(true);

        binding.edtSearchBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
                    Intent intent = new Intent(getActivity(), search_fragment_subActivity_main.class);
                    startActivity(intent);
                    binding.edtSearchBtn.clearFocus();
                }
            }
        });

        setItems();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void setItems() {
        Map querys = new HashMap<>();
        //기본값
        querys.put("ttbkey", getString(R.string.ttbKey));
        querys.put("QueryType", "Bestseller");
        querys.put("MaxResults", "10"); //최대 길이
        querys.put("output", "js");
        querys.put("SearchTarget", "Book");
        querys.put("Version", "20131101");
        String url = "http://www.aladin.co.kr/ttb/api/"; //API 이용
        favmodule = new Module(getContext(), url, querys);
        favmodule.connect(1);
    }

    public void updateRecom(JSONArray json) throws JSONException {
        ArrayList<Book> bookList = new ArrayList<>();
        //책 입력
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            Book book = new Book(obj.getString("title"), obj.getString("categoryName"), obj.getString("description"), obj.getString("publisher"), obj.getString("author"), obj.getString("cover"), obj.getString("itemId"));
            bookList.add(book);
        }
        RecomBookAdapter bookAdapter = new RecomBookAdapter(bookList, getContext());
        bookAdapter.setListener(new OnBookItemClickListener() {
            @Override
            public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position) {

            }

            @Override
            public void onItemClick(RecomBookAdapter.ItemViewHolder holder, View view, int position) {
                Intent intent = new Intent(getContext(), search_fragment_subActivity_result.class);
                intent.putExtra("itemid", bookList.get(position).getItemId());
                intent.putExtra("data", bookList.get(position));
                startActivity(intent);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        binding.favRecyclerView.setLayoutManager(gridLayoutManager);//그리드 뷰로 보이게 함.
        binding.favRecyclerView.setAdapter(bookAdapter);
        showShimmer(false);
    }

    //RankFB에서 사용할 함수
    public void setRanking(List<DocumentSnapshot> a) {
        userInfoList = new ArrayList<>();
        bookWormList = new ArrayList<>();
        ImageView rankProfile[] = {binding.img1stProfile, binding.img2ndProfile, binding.img3rdProfile};
        TextView rankNickname[] = {binding.tv1stNickname, binding.tv2ndNickname, binding.tv3rdNickname};
        TextView rankReadCount[] = {binding.tv1stReadCount, binding.tv2ndReadCount, binding.tv3rdReadCount};
        try {
            for (DocumentSnapshot snapshot : a) {
                Map data = snapshot.getData();

                UserInfo userInfo = new UserInfo();
                BookWorm bookWorm = new BookWorm();

                userInfo.add((Map) data.get("UserInfo"));
                bookWorm.add((Map) data.get("BookWorm"));

                userInfoList.add(userInfo);
                bookWormList.add(bookWorm);
            }
        } catch (NullPointerException e) {
            System.out.print(e);
        }
        for (int i = 0; i < userInfoList.size(); i++) {
            Glide.with(this).load(userInfoList.get(i).getProfileimg()).circleCrop().into(rankProfile[i]); //등수 별 프로필 사진 세팅
            rankNickname[i].setText(userInfoList.get(i).getUsername()); //등수 별 닉네임 세팅
            rankReadCount[i].setText(String.valueOf(bookWormList.get(i).getReadcount()) + "권"); //등수 별 읽은 권 수 세팅

            int index = i;
            rankProfile[i].setOnClickListener(new View.OnClickListener() { //프로필 클릭시 프로필 화면으로 이동
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProfileInfoActivity.class);
                    intent.putExtra("userID", userInfoList.get(index).getToken());
                    startActivity(intent);
                }
            });
        }

        showShimmer(false);
    }

    //shimmer을 켜고 끄고 하는 메소드
    private void showShimmer(Boolean bool) {
        if (bool) {
            binding.llSearchbook.setVisibility(View.GONE);
            binding.SFLSearchbook.startShimmer();
            binding.SFLSearchbook.setVisibility(View.VISIBLE);
        } else {
            binding.llSearchbook.setVisibility(View.VISIBLE);
            binding.SFLSearchbook.stopShimmer();
            binding.SFLSearchbook.setVisibility(View.GONE);
        }
    }
}