package com.example.bookworm.Search.subActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.databinding.SubactivitySearchFragmentResultBinding;
import com.example.bookworm.modules.Module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class search_fragment_subActivity_result extends AppCompatActivity {
    ImageView iv_selectedItem;
    final int textViewCount = 8;
    String itemId;
    int textViewID[] = {R.id.tvResTitle, R.id.tvResAuthor, R.id.tvLink, R.id.tvResPublisher, R.id.tvResDescription, R.id.tvResPriceSales, R.id.tvResPriceStandard, R.id.tvResRatingscore};
    String getContent[] = {"title", "author", "link", "publisher", "description", "priceSales", "priceStandard", "customerReviewRank"};
    TextView[] textViews = new TextView[textViewCount];
    TextView tvViewMore;
    RatingBar customerReviewRank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_search_fragment_result);
        Intent intent = getIntent();
        itemId = intent.getExtras().getString("itemid");
        iv_selectedItem = findViewById(R.id.iv_selectedItem);
        tvViewMore = findViewById(R.id.tvViewMore);
        customerReviewRank = findViewById(R.id.customerReviewRank);
        for (int i = 0; i < textViewCount; i++) {
            textViews[i] = findViewById(textViewID[i]);
        }
        setItem();

        ScrollView ScrParents = (ScrollView) findViewById(R.id.ScrParents);
        ScrollView ScrChild = (ScrollView) findViewById(R.id.ScrChild);
//제목,저자,출판사가 나오는 스크롤뷰를 터치해도 부모 스크롤뷰가 반응하지 않게 함
        ScrChild.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ScrParents.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void setItem() {
        Map querys = new HashMap<String, String>();
        querys.put("ItemId", itemId);
        querys.put("ItemIdType", "ItemId");
        //기본값
        querys.put("ttbkey", getString(R.string.ttbKey));
        querys.put("output", "js");
        querys.put("SearchTarget", "Book");
        querys.put("Version", "20131101");
        String url = "http://www.aladin.co.kr/ttb/api/"; //API 이용
        Module module = new Module(this, url, querys);
        module.connect(2);
    }

    public void putItem(JSONObject json) throws JSONException {
        Glide.with(this).load(json.getString("cover").replace("coversum","cover500")).into(iv_selectedItem);
        for (int i = 0; i < textViewCount; i++) {
            String text = getContent[i];
            if (text.equals("link")) {
                textViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //구매하기 누르면 글씨색 보라색으로 바뀌게
                        textViews[2].setTextColor(Color.parseColor("#660099"));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            intent.setData(Uri.parse(json.getString(text)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
            } else if (text.equals("customerReviewRank")) {
                float rank = Float.parseFloat(json.getString(text)) / 2;
                customerReviewRank.setRating(rank);
                textViews[i].setText(String.valueOf(rank));
            } else textViews[i].setText(json.getString(getContent[i]));
        }

        textViews[1].post(new Runnable() {
            @Override
            public void run() {
                int lineCount = textViews[1].getLineCount();
                // Use lineCount here
                if (lineCount > 0) {
                    if (textViews[1].getLayout().getEllipsisCount(lineCount - 1) > 0) {
                        Log.d("count", textViews[1].getLayout().getEllipsisCount(lineCount - 1) + "dd");
                        tvViewMore.setVisibility(View.VISIBLE);
                        tvViewMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                textViews[1].setMaxLines(Integer.MAX_VALUE);
                                tvViewMore.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        });
    }

}