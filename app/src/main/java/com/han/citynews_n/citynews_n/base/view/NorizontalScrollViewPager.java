package com.han.citynews_n.citynews_n.base.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by han on 2018/5/22.
 */

public class NorizontalScrollViewPager extends ViewPager {

    private int downX;
    private int downY;

    public NorizontalScrollViewPager(Context context) {
        super(context);
    }

    public NorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();

                int diffX = moveX - downX;
                int diffY = moveY - downY;
                if (Math.abs(diffX) > Math.abs(diffY)) {

                    if (getCurrentItem() == 0 && diffX > 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);

                    } else if (getCurrentItem() == (getAdapter().getCount() - 1) && diffX < 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);

                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);

                }
                break;

        }
        return super.dispatchTouchEvent(ev);
    }
}
