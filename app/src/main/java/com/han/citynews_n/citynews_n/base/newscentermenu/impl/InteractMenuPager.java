package com.han.citynews_n.citynews_n.base.newscentermenu.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.han.citynews_n.citynews_n.base.NewsCenterMenuBasePager;

/**
 * Created by han on 2018/5/11.
 */

public class InteractMenuPager extends NewsCenterMenuBasePager {
    public InteractMenuPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        TextView tv = new TextView(mContext);
        tv.setText("菜单");
        tv.setTextSize(23);
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }
}
