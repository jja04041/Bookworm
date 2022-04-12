package com.example.bookworm.Achievement.Adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Achievement.Listener.OnViewHolderItemClickListener;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;

import java.util.HashMap;

public class ViewHolder extends  RecyclerView.ViewHolder  {

    TextView title;
    // 샘플 iv와 tap시 나오는 큰 iv
    ImageView iv_1,iv_2, iv_bookworm;
    Button btnsetworm;
    LinearLayout linearlayout;
    UserInfo userinfo;
    FBModule fbModule;

    OnViewHolderItemClickListener onViewHolderItemClickListener;


    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        iv_1 = itemView.findViewById(R.id.iv_1);
        title = itemView.findViewById(R.id.title);
        iv_2 = itemView.findViewById(R.id.iv_2);
        iv_bookworm = itemView.findViewById(R.id.iv_bookworm);
        btnsetworm = itemView.findViewById(R.id.btn_setworm);

        linearlayout = itemView.findViewById(R.id.linearlayout);

        linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewHolderItemClickListener.onViewHolderItemClick();
            }
        });
    }

    public void onBind(ItemData itemData, int position, SparseBooleanArray selectedItems){
        title.setText(itemData.getTitle());
        iv_1.setImageResource(itemData.getImage());
        iv_2.setImageResource(itemData.getImage());

        userinfo = new PersonalD(itemData.context).getUserInfo();

        btnsetworm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> map = new HashMap<>();

                userinfo.setWormtype(itemData.image);
                fbModule = new FBModule(itemData.context);

                map.put("userinfo_wormtype", userinfo.getWormtype());
                fbModule.readData(0, map, userinfo.getToken());
                new PersonalD(itemData.context).saveUserInfo(userinfo);
                ((Activity)itemData.context).finish();


            }
        });

        // 현재 item이 선택되어있는지 여부를 changeVisibility의 인자로 넣습니다.
        changeVisibility(selectedItems.get(position));
    }

    private void changeVisibility(final boolean isExpanded) {
        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
        ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, 600) : ValueAnimator.ofInt(600, 0);
        // Animation이 실행되는 시간, n/1000초
        va.setDuration(500);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // imageView의 높이 변경
                iv_2.getLayoutParams().height = (int) animation.getAnimatedValue();
                iv_2.requestLayout();
                // imageView가 실제로 사라지게하는 부분
                iv_2.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        });
        // Animation start
        va.start();
    }

    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
        this.onViewHolderItemClickListener = onViewHolderItemClickListener;
    }
}