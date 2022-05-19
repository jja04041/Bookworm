package com.example.bookworm.bottomMenu.bookworm;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.Feed.items.Story;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.FragmentBookwormBinding;
import com.example.bookworm.notification.MyFirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;

public class fragment_bookworm extends Fragment {

    private FragmentBookwormBinding binding;

    private ImageView iv_bookworm;
    private ImageView iv_bg;
    private Button btn_Achievement;
    private Button btn_Achievement_bg;
    private Button btn_sendpush;

    private TextView tv_bookcount;


    private TextView tv_genrecount;

    private TextView tv_bookworm1, tv_bookworm2, tv_bookworm3, tv_bookworm4, tv_bookworm5, tv_bookworm6, tv_bookworm7,
            tv_bookworm8, tv_bookworm9, tv_bookworm10, tv_bookworm11;

    private MyFirebaseMessagingService myFirebaseMessagingService;

    UserInfoViewModel uv;

    private UserInfo userinfo;
    private BookWorm bookworm;

    public static Context current_context;
//    private FBModule fbModule;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        current_context = getContext();

        List<Story> stories = new ArrayList<>();
        for(int i=0; i<10; ++i)
            stories.add(new Story(false));

        // 알림 보낼때 해놔야댐
        binding = FragmentBookwormBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(getContext())).get(UserInfoViewModel.class);
        myFirebaseMessagingService = new MyFirebaseMessagingService();

        iv_bookworm = view.findViewById(R.id.iv_bookworm);
        iv_bg = view.findViewById(R.id.iv_bg);

//        btn_Achievement = view.findViewById(R.id.btn_achievement);
//        btn_Achievement_bg = view.findViewById(R.id.btn_achievement_bg);
//        btn_sendpush = view.findViewById(R.id.btn_sendpush);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        myFirebaseMessagingService=null;
        binding = null;
        super.onDestroyView();
    }
}



