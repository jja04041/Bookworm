package com.example.bookworm.Follow.Modules;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bookworm.Core.UserData.PersonalD;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.Follow.Interfaces.PagerInterface;
import com.example.bookworm.Follow.View.FragmentFollowList;

import java.util.ArrayList;

public class FollowPagerAdapter extends FragmentPagerAdapter implements PagerInterface.PageAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();

    public FollowPagerAdapter(Context context, @NonNull FragmentManager fm, String token) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        arrayList.add(new FragmentFollowList(token, 1,this)); //팔로워탭
        arrayList.add(new FragmentFollowList(token, 0,this)); //팔로잉탭
        UserInfo nowUser = new PersonalD(context).getUserInfo();
        name.add(nowUser.getFollowerCounts() + "팔로워");
        name.add(nowUser.getFollowingCounts() + "팔로잉");
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int positon) {
        return name.get(positon);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public void UpdateTapName(String newName,int page) {
        name.set(page,newName);

    }
}
