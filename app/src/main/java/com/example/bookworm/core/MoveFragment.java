package com.example.bookworm.core;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.bookworm.fragment_bookworm;
import com.example.bookworm.bottomMenu.challenge.FragmentChallenge;
import com.example.bookworm.bottomMenu.feed.FragmentFeed;
import com.example.bookworm.bottomMenu.profile.FragmentProfile;
import com.example.bookworm.bottomMenu.search.views.FragmentSearch;

public class MoveFragment {


    public void show_fragment(Fragment[] fragments, FragmentManager fragmentManager, int idx) {


        for (int i = 0; i < 5; i++) {
            if (i == idx) {
                if (fragments[i] == null) {
                    switch (idx) {
                        case 0:
//                            fragments[i] = new Fragment_feed();
                            fragments[i] = new FragmentFeed();
                            break;
                        case 1:
//                            fragments[i] = new fragment_search();
                            fragments[i] = new FragmentSearch();
                            break;
                        case 2:
                            fragments[i] = new fragment_bookworm();
                            break;
                        case 3:
//                            fragments[i] = new fragment_challenge();
                            fragments[i] = new FragmentChallenge();
                            break;
                        case 4:
                            fragments[i] = new FragmentProfile();
                            break;
                    }

                    fragmentManager.beginTransaction().add(R.id.container, fragments[i], String.valueOf(i)).commitAllowingStateLoss();

                } else
                    fragmentManager.beginTransaction().show(fragments[i]).commitAllowingStateLoss();
            } else {
                if (fragments[i] != null) {
                    if (i == 2) {
                        fragmentManager.beginTransaction().remove(fragments[i]).commitAllowingStateLoss();
                        fragments[i] = null;
                    } else
                        fragmentManager.beginTransaction().hide(fragments[i]).commitAllowingStateLoss();
                }

            }
            fragmentManager.executePendingTransactions();
        }
    }

}
