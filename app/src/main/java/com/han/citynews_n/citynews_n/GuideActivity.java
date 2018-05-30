package com.han.citynews_n.citynews_n;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.han.citynews_n.citynews_n.utils.CacheUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2018/5/10.
 */

public class GuideActivity extends Activity implements View.OnClickListener {

    private ViewPager viewPager;
    private List<ImageView> imageList;
    private Button button;
    private LinearLayout llPoint;
    private float basicWidth;
    private View selectPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);
        init();

    }

    private void init() {
        button = findViewById(R.id.bt_start_splash);
        viewPager = findViewById(R.id.vp_splash);
        llPoint = findViewById(R.id.ll_point);
        selectPoint = findViewById(R.id.select_point);
        button.setOnClickListener(this);
        initData();
        viewPager.setAdapter(new MViewPagerAdapter());
        selectPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                selectPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                basicWidth = llPoint.getChildAt(1).getLeft()-llPoint.getChildAt(0).getLeft();
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float leftMargin = basicWidth * (position + positionOffset);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) selectPoint.getLayoutParams();
                layoutParams.leftMargin = (int) leftMargin;
                selectPoint.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == imageList.size() - 1) {
                    button.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        int[] imageResIDs = {
                R.drawable.guide_1,
                R.drawable.guide_2,
                R.drawable.guide_3,
        };
        imageList = new ArrayList();
        ImageView iv;
        View v;
        for (int i = 0; i < imageResIDs.length; i++) {
            iv = new ImageView(this);
            iv.setBackgroundResource(imageResIDs[i]);
            imageList.add(iv);

            v = new View(this);
            v.setBackgroundResource(R.drawable.point_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);


            if (i != 0) {
                params.leftMargin = 10;
            }

            v.setLayoutParams(params);
            llPoint.addView(v);
        }

    }

    @Override
    public void onClick(View view) {
       // Toast.makeText(this, "开始体验", Toast.LENGTH_SHORT).show();
        CacheUtils.putBoolean(GuideActivity.this,SplashActivity.IS_FIRST_OPEN,false);
        startActivity(new Intent(this, MainActivity.class));
    }

    class MViewPagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageList.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
