package com.example.bookworm.fragments.functions;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.R;
import com.example.bookworm.fragments.fragment_bookworm;
import com.example.bookworm.fragments.fragment_challenge;
import com.example.bookworm.fragments.fragment_feed;
import com.example.bookworm.fragments.fragment_profile;
import com.example.bookworm.fragments.fragment_search;

public class frag_functions {



    public void show_fragment(Fragment[] fragments, FragmentManager fragmentManager, int idx) {
        for (int i = 0; i < 5; i++) {
            if (i == idx) {
                if (fragments[i] == null) {
                    switch (idx)
                    {
                        case 0:
                            fragments[i] = new fragment_feed();
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

                    fragmentManager.beginTransaction().add(R.id.container, fragments[i],String.valueOf(i)).commitAllowingStateLoss();
                } else
                    fragmentManager.beginTransaction().show(fragments[i]).commitAllowingStateLoss();
            } else {
                if (fragments[i] != null)
                    fragmentManager.beginTransaction().hide(fragments[i]).commitAllowingStateLoss();
            }
            fragmentManager.executePendingTransactions();
        }
    }
}
