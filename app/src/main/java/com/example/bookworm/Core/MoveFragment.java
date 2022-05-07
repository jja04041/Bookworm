package com.example.bookworm.Core;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.BottomMenu.Search.fragment_search;
import com.example.bookworm.R;
import com.example.bookworm.BottomMenu.Bookworm.fragment_bookworm;
import com.example.bookworm.BottomMenu.Challenge.fragment_challenge;
import com.example.bookworm.BottomMenu.Feed.Fragment_feed;
import com.example.bookworm.BottomMenu.Profile.fragment_profile;

public class MoveFragment {


    public void show_fragment(Fragment[] fragments, FragmentManager fragmentManager, int idx) {


        for (int i = 0; i < 5; i++) {
            if (i == idx) {
                if (fragments[i] == null) {
                    switch (idx) {
                        case 0:
                            fragments[i] = new Fragment_feed();
                            break;
                        case 1:
                            fragments[i] = new fragment_search();
                            break;
                        case 2:
                            fragments[i] = new fragment_bookworm();
                            break;
                        case 3:
                            fragments[i] = new fragment_challenge();
                            break;
                        case 4:
                            fragments[i] = new fragment_profile();
                            break;
                    }

                    fragmentManager.beginTransaction().add(R.id.container, fragments[i], String.valueOf(i)).commitAllowingStateLoss();

                }
                else
                    fragmentManager.beginTransaction().show(fragments[i]).commitAllowingStateLoss();
            }
            else {
                if (fragments[i] != null) {
                    if(i == 2 || i == 4)
                    {
                        fragmentManager.beginTransaction().remove(fragments[i]).commitAllowingStateLoss();
                        fragments[i] = null;
                    }

                    else
                        fragmentManager.beginTransaction().hide(fragments[i]).commitAllowingStateLoss();
                }

            }
            fragmentManager.executePendingTransactions();
        }
    }

}
