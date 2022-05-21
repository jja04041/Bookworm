package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_statistics;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.FragmentStatisticsBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Random;


public class fragment_statistics extends Fragment {

    private FragmentStatisticsBinding binding;
    private UserInfoViewModel uv;
    UserInfoViewModel pv;
    UserInfo userinfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater);
        View view = binding.getRoot();
        pv = new UserInfoViewModel(getContext());
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(getContext())).get(UserInfoViewModel.class);


        pv.getUser(null, false);

        //데이터 수정을 감지함
        pv.getData().observe(getViewLifecycleOwner(), userinfo -> {

        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        uv.getUser(null, false);
        uv.getData().observe(this, userInfo -> {
            String[] genre = {
                    "자기계발", "소설", "육아", "어린이", "청소년", "사회", "과학", "인문", "생활", "공부", "만화"};
            TextView bookworm[] = { //권수 확인
                    binding.tvBookworm1, binding.tvBookworm2, binding.tvBookworm3, binding.tvBookworm4, binding.tvBookworm5, binding.tvBookworm6, binding.tvBookworm7, binding.tvBookworm8, binding.tvBookworm9, binding.tvBookworm10, binding.tvBookworm11};
            LinearLayout llbookworm[] = { //읽은 책만 Visible로 설정하기 위함
                    binding.llBookworm1, binding.llBookworm2, binding.llBookworm3, binding.llBookworm4, binding.llBookworm5, binding.llBookworm6, binding.llBookworm7, binding.llBookworm8, binding.llBookworm9, binding.llBookworm10, binding.llBookworm11};
            LinearLayout llbooks[] = { //책 ImageView가 추가될 레이아웃
                    binding.llBooks1, binding.llBooks2, binding.llBooks3, binding.llBooks4, binding.llBooks5, binding.llBooks6, binding.llBooks7, binding.llBooks8, binding.llBooks9, binding.llBooks10, binding.llBooks11
            };
            ImageView ivBookshelf[] = { // 선반
                    binding.ivBookshelf1, binding.ivBookshelf2, binding.ivBookshelf3, binding.ivBookshelf4, binding.ivBookshelf5, binding.ivBookshelf6, binding.ivBookshelf7, binding.ivBookshelf8, binding.ivBookshelf9, binding.ivBookshelf10, binding.ivBookshelf11};


            userinfo = userInfo;

            ArrayList<PieEntry> record = new ArrayList<>();

            for (int i = 0; i < genre.length; i++) {
                if (userInfo.getGenre().get(genre[i]) != null) {
                    bookworm[i].setText(genre[i] + " " + userInfo.getGenre().get(genre[i]) + "권");
                    record.add(new PieEntry(userInfo.getGenre().get(genre[i]), genre[i]));

                    Random random = new Random(); //책 길이, 색상을 랜덤으로 설정하기 위함

                    llbooks[i].removeAllViews(); //onResume() 작동시 계속 뷰가 늘어나는 현상 해결

                    for (int j = 0; j < userInfo.getGenre().get(genre[i]); j++) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT /* layout_width */, LinearLayout.LayoutParams.WRAP_CONTENT /* layout_height */ /* layout_weight */);
                        layoutParams.width = 25;
                        layoutParams.height = random.nextInt(20) + 90;
                        // layoutParams.setMargins(0,0,0,0); 이미지 마진(왼쪽, 위, 오른쪽, 아래)
                        ImageView iv = new ImageView(getContext());

                        iv.setBackgroundColor(ColorTemplate.JOYFUL_COLORS[random.nextInt(5)]);
                        iv.setLayoutParams(layoutParams);
                        llbooks[i].addView(iv);
                        llbookworm[i].setVisibility(View.VISIBLE);
                        ivBookshelf[i].setVisibility(View.VISIBLE);
                    }

                }

            }

//            record.add(new PieEntry(42,"자기계발"));

            PieDataSet pieDataSet = new PieDataSet(record, "");
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieDataSet.setValueTextColor(Color.BLACK);
            pieDataSet.setValueTextSize(0);


            PieData pieData = new PieData(pieDataSet);

            binding.pieChart.setData(pieData);

            Description description = new Description();
            description.setText("장르별 비율"); //라벨
            description.setTextSize(15);
            binding.pieChart.setDescription(description);

//        binding.pieChart.setCenterText("센터");
            binding.pieChart.animate();


            uv.getBookWorm(userInfo.getToken());
        });
        uv.getBwdata().observe(this, bw -> {
//            binding.tvBookwormBookcount.setText("읽은 권 수 : " + bw.getReadcount());
            binding.pieChart.setCenterText("총 " + bw.getReadcount() + "권");
            binding.pieChart.setCenterTextSize(20);

            if (bw.getReadcount() == 0) { //읽은 권수가 0권이라면 독서기록이 없다고 표기
                isEmptyRecord(true);
            } else {
                isEmptyRecord(false);
            }
        });


    }

    public void isEmptyRecord(boolean bool) {
        if (bool) {
            binding.scrollView.setVisibility(View.GONE);
            binding.llEmptyRecord.setVisibility(View.VISIBLE);
        } else {
            binding.scrollView.setVisibility(View.VISIBLE);
            binding.llEmptyRecord.setVisibility(View.GONE);
        }
    }
}