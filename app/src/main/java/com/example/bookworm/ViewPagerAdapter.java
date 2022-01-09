package com.example.bookworm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    // 프래그먼트 교체를 보여주는 처리를 구현한 곳
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragment_feed.newInstance();
            case 1:
                return fragment_search.newInstance();
            case 2:
                return fragment_bookworm.newInstance();
            case 3:
                return fragment_challenge.newInstance();
            case 4:
                return fragment_profile.newInstance();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 5;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "피드";
            case 1:
                return "탐색";
            case 2:
                return "책볼레";
            case 3:
                return "챌린지";
            case 4:
                return "프로필";
            default:
                return null;
        }
    }
}
