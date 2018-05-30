package com.han.citynews_n.citynews_n.base;

import android.content.Context;
import android.view.View;

/**
 * Created by han on 2018/5/11.
 */

public abstract class NewsCenterMenuBasePager {
    public Context mContext;

    private View rootView;
    public NewsCenterMenuBasePager(Context context) {
        this.mContext = context;
        rootView = initView();
    }

    public abstract View initView();

    public View getRootView(){
        return rootView;
    }

    /**
     * 初始化数据，子类需要覆盖此方法，去实现自己的数据初始化
     */
    public void initData(){

    }
}
