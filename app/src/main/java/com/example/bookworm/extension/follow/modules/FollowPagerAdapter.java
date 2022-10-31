package com.example.bookworm.extension.follow.modules;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.extension.follow.view.FragmentFollowList;

import java.util.ArrayList;

public class FollowPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();

    public FollowPagerAdapter(@NonNull FragmentManager fm, Lifecycle lifecycle, UserInfo targetUserInfo,UserInfo nowUserInfo) {
        super(fm, lifecycle);
        arrayList.add(new FragmentFollowList(targetUserInfo, nowUserInfo,0)); //팔로워탭
        arrayList.add(new FragmentFollowList(targetUserInfo, nowUserInfo,1)); //팔로잉탭
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return arrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
