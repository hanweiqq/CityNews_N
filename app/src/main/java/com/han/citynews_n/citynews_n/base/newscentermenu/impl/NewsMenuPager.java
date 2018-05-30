package com.han.citynews_n.citynews_n.base.newscentermenu.impl;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.han.citynews_n.citynews_n.MainActivity;
import com.han.citynews_n.citynews_n.R;
import com.han.citynews_n.citynews_n.base.NewsCenterMenuBasePager;
import com.han.citynews_n.citynews_n.base.impl.NewsPager;
import com.han.citynews_n.citynews_n.domain.NewsCenterMenuBean;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻菜单 绑定数据
 * Created by han on 2018/5/11.
 */

public class NewsMenuPager extends NewsCenterMenuBasePager implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.tpi_news_menu)
    private TabPageIndicator mIndicator;

    @ViewInject(R.id.vp_news_menu)
    private ViewPager mViewPager;


    private List<NewsCenterMenuBean.NewsCenterTabBean> tabBeanList;//页签的数据
    private List<NewsMenuTabDetailPager> tabDetailPagersList;//页签对应的页面


    public NewsMenuPager(Context context) {
        super(context);
    }

    public NewsMenuPager(Context context, NewsCenterMenuBean.NewsCenterMenu newsCenterMenu) {
        super(context);
        tabBeanList = newsCenterMenu.children;
        System.out.println("NewsMenuPager新闻菜单页面接收到的数据" + newsCenterMenu.children.get(0).title);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.news_menu, null);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        //初始化ViewPager数据
        tabDetailPagersList = new ArrayList<>();

        for (int i = 0; i < tabBeanList.size(); i++) {
            tabDetailPagersList.add(new NewsMenuTabDetailPager(mContext, tabBeanList.get(i)));
        }
        NewsMenuAdapter mNewsMenuAdapter = new NewsMenuAdapter();
        mViewPager.setAdapter(mNewsMenuAdapter);

        mIndicator.setViewPager(mViewPager);

        //监听页面的改变
        mIndicator.setOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        //如果postion是0，菜单设置为可用
        if (position == 0) {
            ((MainActivity) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            ((MainActivity) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class NewsMenuAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return tabDetailPagersList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabBeanList.get(position).title;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            NewsMenuTabDetailPager newsMenuTabDetailPager = tabDetailPagersList.get(position);
            View rootView = newsMenuTabDetailPager.getRootView();
            container.addView(rootView);
            newsMenuTabDetailPager.initData();
            return rootView;
        }
    }

    @OnClick(R.id.ib_news_menu_text_tab)
    public void nextTab(View view){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
    }

}
