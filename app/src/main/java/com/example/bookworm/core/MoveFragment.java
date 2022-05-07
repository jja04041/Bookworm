package com.example.bookworm.core;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.bottomMenu.search.fragment_search;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.bookworm.fragment_bookworm;
import com.example.bookworm.bottomMenu.challenge.fragment_challenge;
import com.example.bookworm.bottomMenu.Feed.Fragment_feed;
import com.example.bookworm.bottomMenu.profile.fragment_profile;

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
