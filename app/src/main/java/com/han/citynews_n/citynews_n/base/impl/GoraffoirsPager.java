package com.han.citynews_n.citynews_n.base.impl;

import android.content.Context;
import android.view.View;

import com.han.citynews_n.citynews_n.base.TabBasePager;

/**
 * Created by han on 2018/5/11.
 */

public class GoraffoirsPager extends TabBasePager {


    public GoraffoirsPager(Context context) {
        super(context);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("Home页的标题");
        ibMenu.setVisibility(View.INVISIBLE);
    }
}
