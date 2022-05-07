package com.example.bookworm.extension.follow.modules;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.bookworm.extension.follow.view.FragmentFollowList;

import java.util.ArrayList;

public class FollowPagerAdapter extends FragmentStatePagerAdapter{

    private ArrayList<Fragment> arrayList = new ArrayList<>();

    public FollowPagerAdapter(Context context, @NonNull FragmentManager fm, String token) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        arrayList.add(new FragmentFollowList(token, 1
//                , this
                )); //팔로워탭
        arrayList.add(new FragmentFollowList(token, 0
//                , this
        )); //팔로잉탭



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

}
