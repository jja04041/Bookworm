package com.example.bookworm.bottomMenu.search.subactivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class SearchPageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();
    FragmentManager fragmentManager;


    public SearchPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
        arrayList.add(new SearchPageBookFragment());
        arrayList.add(new SearchPageFeedFragment());
        arrayList.add(new SearchPageChallengeFragment());
        arrayList.add(new SearchPageUserFragment());
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
