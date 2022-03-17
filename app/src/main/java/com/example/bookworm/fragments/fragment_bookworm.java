package com.example.bookworm.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.personalD.PersonalD;

import java.util.Vector;

public class fragment_bookworm extends Fragment {

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private String[] images;
    private UserInfo userinfo;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookworm, container, false);

        PersonalD personalD = new PersonalD(getContext());
        userinfo = personalD.getUserInfo();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = getView();
        String[] images;

//        set_wormtype(userinfo);
//        setBookworm(userinfo);
//
//        // 유저 책볼레 벡터를 이미지 슬라이더에 넣을 String[]에 넣는다.
//        images = new String[userinfo.getWormimgvec().size()];
//        userinfo.getWormimgvec().copyInto(images);

        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        layoutIndicator = view.findViewById(R.id.layoutIndicators);

        sliderViewPager.setOffscreenPageLimit(1);
//        sliderViewPager.setAdapter(new ImageSliderAdapter(getActivity(), images));

        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

//        setupIndicators(images.length);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private String getURLForResource(int resId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resId).toString();
    }


    // 이미지 넘길때 밑에 페이지로 동그란 원 나오는거
    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getActivity(),
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getActivity(),
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }

//
//    private void setBookworm(UserInfo userinfo) {
//        // 현재시간 얻고
//        GregorianCalendar today = new GregorianCalendar();
//
//        int index = today.get(today.YEAR) % userinfo.getRegister_year();
//        int path = R.drawable.ex_default;
//
//        // 현재년도 % 가입년도 연산으로 현재의 책볼레타입으로 채움
//        userinfo.getWormvec().set(index, userinfo.getWormtype().value());
//
//        switch (userinfo.getWormvec().get(index))
//        {
//            case 0:
//                path = R.drawable.ex_default;
//                break;
//            case 1:
//                path = R.drawable.ex_horror;
//                break;
//            case 2:
//                path = R.drawable.ex_detective;
//                break;
//            case 3:
//                path = R.drawable.ex_romance;
//                break;
//            default:
//                break;
//        }
//
//        userinfo.getWormimgvec().set(index, Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + path).toString());
//    }


    // userinfo의 genre 배열의 어떤 인덱스(장르)가 가장 큰 값을 가지고 있는지 찾은 후
    // 그 인덱스를 반환
    private int favorgenre(Vector<Integer> genre) {
        int max = 0;

        for(int i=1; i<genre.size(); ++i)
            if(genre.get(max) < genre.get(i))
                max = i;

        return max;
    }

//    private void set_wormtype(UserInfo userinfo) {
//        switch (favorgenre(userinfo.getGenre()))
//        {
//            case 0:
//                userinfo.setWormtype(enum_wormtype.디폴트);
//                break;
//            case 1:
//                userinfo.setWormtype(enum_wormtype.공포);
//                break;
//            case 2:
//                userinfo.setWormtype(enum_wormtype.추리);
//                break;
//            case 3:
//                userinfo.setWormtype(enum_wormtype.로맨스);
//                break;
//            default:
//                break;
//        }
//    }


}