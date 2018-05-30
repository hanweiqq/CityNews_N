package com.han.citynews_n.citynews_n.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.han.citynews_n.citynews_n.R;
import com.han.citynews_n.citynews_n.base.BaseFragment;
import com.han.citynews_n.citynews_n.base.TabBasePager;
import com.han.citynews_n.citynews_n.base.impl.GoraffoirsPager;
import com.han.citynews_n.citynews_n.base.impl.HomePager;
import com.han.citynews_n.citynews_n.base.impl.NewsPager;
import com.han.citynews_n.citynews_n.base.impl.SettingPager;
import com.han.citynews_n.citynews_n.base.impl.SmartServicePager;
import com.han.citynews_n.citynews_n.base.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2018/5/11.
 */

public class ContentFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.rg_content_fragment)
    private RadioGroup mRadioGroup;

    @ViewInject(R.id.vp_content_fragment)
    private NoScrollViewPager mViewPager;
    private ContentViewPagerAdapter contentViewPagerAdapter;
    private List<TabBasePager> pagerList;

    @Override
    public View initView() {

        View inflate = View.inflate(mActivity, R.layout.content_fragment_activity, null);
        ViewUtils.inject(this,inflate);

        return inflate;
    }

    @Override
    public void initData() {

        pagerList = new ArrayList<>();
        pagerList.add(new HomePager(mActivity));
        pagerList.add(new NewsPager(mActivity));
        pagerList.add(new SmartServicePager(mActivity));
        pagerList.add(new GoraffoirsPager(mActivity));
        pagerList.add(new SettingPager(mActivity));


        contentViewPagerAdapter = new ContentViewPagerAdapter();
        mViewPager.setAdapter(contentViewPagerAdapter);
        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.check(R.id.rb_home);

    }



    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {

            case R.id.rb_home:
                Toast.makeText(mActivity, "首页", Toast.LENGTH_SHORT).show();
                mViewPager.setCurrentItem(0);
                isEnableMenu(false);
                break;

            case R.id.rb_newscenter:
                Toast.makeText(mActivity, "新闻", Toast.LENGTH_SHORT).show();
                // 当前选中的是新闻中心
                mViewPager.setCurrentItem(1);
                isEnableMenu(true);
                break;

            case R.id.rb_smartserver:
                Toast.makeText(mActivity, "智慧", Toast.LENGTH_SHORT).show();
                // 当前选中的是新闻中心
                mViewPager.setCurrentItem(2);
                isEnableMenu(true);
                break;

            case R.id.rb_groaffoirs:
                Toast.makeText(mActivity, "政务", Toast.LENGTH_SHORT).show();
                // 当前选中的是新闻中心
                mViewPager.setCurrentItem(3);
                isEnableMenu(true);
                break;

            case R.id.rb_setting:
                Toast.makeText(mActivity, "设置", Toast.LENGTH_SHORT).show();
                // 当前选中的是新闻中心
                mViewPager.setCurrentItem(4);
                isEnableMenu(false);
                break;

            default:
                break;
        }
    }

    private void isEnableMenu(boolean b) {
        SlidingFragmentActivity slidingFragmentActivity = (SlidingFragmentActivity) mActivity;
        SlidingMenu slidingMenu = slidingFragmentActivity.getSlidingMenu();
        if(b){
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }else{
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    /**
     * 获取新闻中心页面
     * @return
     */
    public NewsPager getNewsPager() {
        return (NewsPager) pagerList.get(1);
    }

    class ContentViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return pagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabBasePager tabBasePager = pagerList.get(position);
            View rootView = tabBasePager.getRootView();
            container.addView(rootView);
            tabBasePager.initData();
            return  rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
