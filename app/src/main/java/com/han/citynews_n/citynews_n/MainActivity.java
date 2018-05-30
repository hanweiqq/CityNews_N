package com.han.citynews_n.citynews_n;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.han.citynews_n.citynews_n.Fragment.ContentFragment;
import com.han.citynews_n.citynews_n.Fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * Created by han on 2018/5/11.
 */

public class MainActivity extends SlidingFragmentActivity {
    private static final String CONTENT_FRAGMENT_TAG = "content_fragment";
    private static final String LEFT_FRAGMENT_TAG = "left_fragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setBehindContentView(R.layout.left_fragment_activity);
        SlidingMenu mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setBehindOffset(300);
        initFragment();
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fl_main_content,new ContentFragment(),CONTENT_FRAGMENT_TAG);
        fragmentTransaction.replace(R.id.fl_main_left_menu,new LeftMenuFragment(),LEFT_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    /**
     * 输出左侧的菜单
     * @return
     */
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager fm =getSupportFragmentManager();
        LeftMenuFragment leftMenufragment = (LeftMenuFragment)fm.findFragmentByTag(LEFT_FRAGMENT_TAG);
        return leftMenufragment;
    }

    /**
     * 输出右侧内容页面
     * @return
     */
    public ContentFragment getContentFragment() {
        FragmentManager fm =getSupportFragmentManager();
        ContentFragment contentFragment = (ContentFragment)fm.findFragmentByTag(CONTENT_FRAGMENT_TAG);
        return contentFragment;
    }
}
