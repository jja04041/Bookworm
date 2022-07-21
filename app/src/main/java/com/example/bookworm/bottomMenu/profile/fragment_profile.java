package com.example.bookworm.bottomMenu.profile;


import static android.app.Activity.RESULT_CANCELED;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookworm.R;
import com.example.bookworm.achievement.Achievement;
import com.example.bookworm.bottomMenu.bookworm.BookWorm;
import com.example.bookworm.bottomMenu.profile.submenu.SubMenuPagerAdapter;
import com.example.bookworm.bottomMenu.profile.views.ProfileSettingActivity;
import com.example.bookworm.chat.newchat.Activity_chatlist;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.FragmentProfileBinding;
import com.example.bookworm.extension.follow.view.FollowViewModelImpl;
import com.example.bookworm.extension.follow.view.FollowerActivity;

public class fragment_profile extends Fragment implements LifecycleObserver {

    private BookWorm bookworm;
    private Achievement achievement;
    private FragmentProfileBinding binding;
    private Context current_context;
    private FBModule fbModule;
    UserInfoViewModel pv;
    FollowViewModelImpl fv;
    private UserInfo NowUser;
    SubMenuPagerAdapter menuPagerAdapter;
    public ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_CANCELED)
                    fv.WithoutSuspendgetUser(NowUser.token);
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        current_context = getActivity();
        pv = new ViewModelProvider(this, new UserInfoViewModel.Factory(current_context)).get(UserInfoViewModel.class);
        fv = new ViewModelProvider(this, new FollowViewModelImpl.Factory(current_context)).get(FollowViewModelImpl.class);
        fbModule = new FBModule(current_context);
        //서브 메뉴를 보여주기위한 어댑터
        menuPagerAdapter = new SubMenuPagerAdapter(null, getChildFragmentManager());


//뷰모델 안에서 데이터가 배치된다.
        binding.btnSetting.setOnClickListener(view1 -> {
            Intent intent = new Intent(current_context, ProfileSettingActivity.class);
            startActivity(intent);
        });


        binding.btnChatlist.setOnClickListener(v -> {
            Intent intent = new Intent (getActivity(), Activity_chatlist.class);
            startActivity(intent);
        });


        binding.subMenuViewPager.setAdapter(menuPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.subMenuViewPager);
        binding.tabLayout.getTabAt(1).setText("앨범");
        binding.tabLayout.getTabAt(0).setText("포스트");
        binding.tabLayout.getTabAt(0).select();
        //데이터를 가져옴
        pv.getUser(null, false);
        fv.WithoutSuspendgetUser(null);

        //데이터 수정을 감지함
        pv.getData().observe(getViewLifecycleOwner(), userinfo -> {
            NowUser = userinfo;
            pv.getBookWorm(NowUser.token);
            achievement = new Achievement(current_context, fbModule, NowUser, bookworm);
            binding.tvFollowerCount.setText(String.valueOf(userinfo.getFollowerCounts()));
            binding.tvFollowingCount.setText(String.valueOf(userinfo.getFollowingCounts()));
            setUI(NowUser);
        });
        fv.getData().observe(getViewLifecycleOwner(), userInfo -> {
            binding.tvFollowerCount.setText(String.valueOf(userInfo.getFollowerCounts()));
            binding.tvFollowingCount.setText(String.valueOf(userInfo.getFollowingCounts()));
        });
        pv.getBwdata().observe(getViewLifecycleOwner(), bookWorm -> {
            binding.tvReadBookCount.setText(String.valueOf(bookWorm.getReadcount()));
//            binding.ivBookworm.setImageResource(bookWorm.getWormtype());
            binding.ivBookworm.setImageResource(R.drawable.bw_confidence);
        });


        return view;
    }

    private void setUI(UserInfo user) {
        Glide.with(current_context)
                .load(user.getProfileimg())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(binding.imgFragProfileProfile);
       //프로필사진 로딩후 삽입.
        binding.tvUserName.setText(user.getUsername());
        binding.edtIntroduce.setText(user.getIntroduce());



        //팔로워액티비티 실행하기
        binding.btnFollower.setOnClickListener(view -> {
            Intent intent = new Intent(current_context, FollowerActivity.class);
            intent.putExtra("token", user.getToken());
            intent.putExtra("page", 0);
            startActivityResult.launch(intent);
        });

        //팔로잉액티비티 실행하기
        binding.btnFollowing.setOnClickListener(view -> {
            Intent intent = new Intent(current_context, FollowerActivity.class);
            intent.putExtra("token", user.getToken());
            intent.putExtra("page", 1);
            startActivityResult.launch(intent);
        });

        binding.btnIntroModify.setOnClickListener(new View.OnClickListener() { //자기소개 수정
            @Override
            public void onClick(View view) {
                if (binding.btnIntroModify.getText().toString().equals("수정")) { //수정하기 버튼을 눌렀을 때
                    binding.btnIntroModify.setText("완료");
                    binding.edtIntroduce.setBackgroundColor(Color.parseColor("#2200ff00")); //배경색 변경
                    binding.edtIntroduce.setEnabled(true); //EditText를 수정 가능한 상태로 만듦
                    binding.btnModifyCancle.setVisibility(View.VISIBLE); //수정 취소 버튼 노출

                    //키보드 띄우기
                    InputMethodManager imm = (InputMethodManager) current_context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    binding.edtIntroduce.requestFocus(); //초점 이동
                    binding.edtIntroduce.setSelection(binding.edtIntroduce.length()); //가장 마지막으로 커서 이동
                } else { //완료 버튼을 눌렀을 때
                    new AlertDialog.Builder(current_context) //변경하
                            .setMessage("변경하시겠습니까?")
                            .setPositiveButton("네", (dialog, which) -> {
                                user.setIntroduce(binding.edtIntroduce.getText().toString()); //userinfo의 자기소개를 변경
                                pv.updateUser(user); // 사용자 정보 업데이트
                                binding.btnIntroModify.setText("수정"); //완료 버튼에서 다시 수정 버튼으로 변경
                                binding.edtIntroduce.setBackgroundColor(Color.WHITE); //배경색 변경
                                binding.edtIntroduce.setEnabled(false); //수정 불가능하게 변경
                                binding.btnModifyCancle.setVisibility(View.INVISIBLE); //수정 취소 버튼 숨김

                                //키보드 내리기
                                InputMethodManager imm = (InputMethodManager) current_context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(binding.edtIntroduce.getWindowToken(), 0);
                                binding.edtIntroduce.clearFocus(); //초점 제거
                                dialog.dismiss();

                            })
                            .setNegativeButton("아니요", (dialog, which)
                                    -> dialog.dismiss())
                            .show();
                }
            }
        });

        binding.btnModifyCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnIntroModify.setText("수정"); //완료 버튼에서 다시 수정 버튼으로 변경
                binding.edtIntroduce.setBackgroundColor(Color.WHITE); //배경색 변경
                binding.edtIntroduce.setEnabled(false); //수정 불가능하게 변경
                binding.edtIntroduce.setText(user.getIntroduce()); //기존의 자기소개로 변경
                binding.btnModifyCancle.setVisibility(View.INVISIBLE); //수정 취소 버튼 숨김

                //키보드 내리기
                InputMethodManager imm = (InputMethodManager) current_context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.edtIntroduce.getWindowToken(), 0);
                binding.edtIntroduce.clearFocus(); //초점 제거
            }
        });

    }

    //뷰보다 프레그먼트의 생명주기가 길어서, 메모리 누수 발생가능
    //누수 방지를 위해 뷰가 Destroy될 때, binding값을 nullify 함.
    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        pv.getUser(null,false);
        fv.WithoutSuspendgetUser(null);
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            menuPagerAdapter.getItem(0).onResume();
            fv.WithoutSuspendgetUser(null);
            pv.getBookWorm(NowUser.token);
        }
        super.onHiddenChanged(hidden);
    }
}