package com.example.bookworm.Follow.Modules;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bookworm.Follow.View.FragmentFollowList;
import java.util.ArrayList;

public class FollowPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();

    public FollowPagerAdapter(@NonNull FragmentManager fm, String token)
    {
        super(fm);
        arrayList.add(new FragmentFollowList(token,1)); //팔로워탭
        arrayList.add(new FragmentFollowList(token,0)); //팔로잉탭

        name.add("팔로워 탭");
        name.add("팔로잉 탭");
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int positon)
    {
        return name.get(positon);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        return arrayList.get(position);
    }

    @Override
    public int getCount()
    {
        return arrayList.size();
    }

}
