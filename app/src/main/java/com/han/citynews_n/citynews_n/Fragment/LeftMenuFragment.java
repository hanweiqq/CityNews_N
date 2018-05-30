package com.han.citynews_n.citynews_n.Fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.han.citynews_n.citynews_n.MainActivity;
import com.han.citynews_n.citynews_n.R;
import com.han.citynews_n.citynews_n.base.BaseFragment;
import com.han.citynews_n.citynews_n.base.impl.NewsPager;
import com.han.citynews_n.citynews_n.domain.NewsCenterMenuBean;

import java.util.List;

/**
 * Created by han on 2018/5/11.
 */

public class LeftMenuFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private List<NewsCenterMenuBean.NewsCenterMenu> mLeftMenuList; //当前页面数据
    private ListView mListView;
    private int currentEnalbledPosition; //当前可用的选项索引
    private MenuAdapter menuAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.left_fragment_activity);

    }

    @Override
    public View initView() {
        mListView = new ListView(mActivity);
        mListView.setBackgroundColor(Color.BLACK);
        mListView.setPadding(0, 80, 0, 0);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setSelector(android.R.color.transparent);
        mListView.setOnItemClickListener(this);
        return mListView;
    }

    /**
     * 设置左侧菜单数据
     *
     * @param mLeftMenuList
     */
    public void setMenuDataList(List<NewsCenterMenuBean.NewsCenterMenu> mLeftMenuList) {
        this.mLeftMenuList = mLeftMenuList;
        currentEnalbledPosition = 0;

        menuAdapter = new MenuAdapter();
        mListView.setAdapter(menuAdapter);


        //设置主页面默认的布局
        switchNewsCenterPager();
    }

    /**
     * 根据索引切换新闻中心对应的菜单
     */
    private void switchNewsCenterPager() {
        MainActivity mainActivity = (MainActivity) mActivity;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsPager newsPager = contentFragment.getNewsPager();
        newsPager.switchPager(currentEnalbledPosition);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        currentEnalbledPosition = i;
        //刷新
        menuAdapter.notifyDataSetChanged();

        //把菜单关闭
        ((MainActivity) mActivity).getSlidingMenu().toggle();
        //把主界面切换成对应菜单的页面
        switchNewsCenterPager();
    }


    class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLeftMenuList.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView tv = null;
            if (view == null) {
                tv = (TextView) View.inflate(mActivity, R.layout.left_menu_item, null);
            } else {
                tv = (TextView) view;
            }
            tv.setText(mLeftMenuList.get(i).title);

            tv.setEnabled(currentEnalbledPosition == i);
            return tv;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


    }

}
