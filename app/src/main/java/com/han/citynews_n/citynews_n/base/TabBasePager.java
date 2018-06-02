package com.han.citynews_n.citynews_n.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.han.citynews_n.citynews_n.MainActivity;
import com.han.citynews_n.citynews_n.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by han on 2018/5/11.
 */

public class TabBasePager implements View.OnClickListener {

    public Context mContext;
    private View rootView;
    public ImageButton ibMenu;
    public TextView tvTitle;
    public View view;
    public FrameLayout flTabBasePager;
    public ImageButton ibListAndGrid;


    public TabBasePager(Context context) {
        this.mContext = context;
        rootView = initView();
    }

    private View initView() {

        View view = View.inflate(mContext, R.layout.tab_base_pager, null);
        ibMenu = view.findViewById(R.id.ib_title_bar_menu);
        tvTitle = view.findViewById(R.id.tv_title_bar);
        flTabBasePager = view.findViewById(R.id.fl_tab_base_pager);
        ibMenu.setOnClickListener(this);

        ibListAndGrid = view.findViewById(R.id.ib_title_bar_list_and_grid);
        return view;
    }
    
    public View getRootView(){
        return rootView;
    }


    public void initData() {

    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(mContext, "打开左侧菜单", Toast.LENGTH_SHORT).show();
        MainActivity mContext = (MainActivity) this.mContext;
        mContext.getSlidingMenu().toggle();
    }
}
