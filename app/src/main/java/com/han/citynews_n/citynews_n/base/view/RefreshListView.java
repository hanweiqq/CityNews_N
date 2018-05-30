package com.han.citynews_n.citynews_n.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.han.citynews_n.citynews_n.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 在原有的listview的基础上增加下拉刷新头和加载更多尾
 * Created by han on 2018/5/25.
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener {

    private LinearLayout handerView;
    private View mCustomHeaderView;//用户添加的自定义头
    private int downY = -1;
    private int downX;
    private int mPullDownHeaderViewHeight;
    private View mPullDownHeaderView;

    private final int PULL_DOWN_REFRESH = 0;//下拉刷新状态
    private final int RELEASE_REFRESH = 1;//松开状态
    private final int REFRESHING = 2;//正在刷新

    private int currentState = PULL_DOWN_REFRESH; //当前下拉箭头的状态  默认是下拉刷新状态
    private RotateAnimation upAnimation;  //向上旋转
    private RotateAnimation downAnimation;//向下旋转
    private ImageView ivArror; //头布局的箭头
    private ProgressBar mProgressBar;//头布局的进度圈
    private TextView tvState;//头布局的状态
    private TextView tvLastUpdataTime;//头布局的时间
    private int mListViewOnScreenY = -1; //当前listview的左上角在屏幕中y轴的坐标点，默认为-1

    private OnRefreshListener mOnRefreshListener;
    private View mFooterView;
    private int mFooterViewHeight;
    private boolean isLoadingMore = false; //是否正在加载更多中
    private boolean isEnablePullDownRefresh=false; //是否启用“下拉刷新”功能
    private boolean isEnableLoadMoreRefresh=false;//是否启用“加载更多”功能

    public RefreshListView(Context context) {
        super(context);
        initPullDownHeaderView();
        initLoadMoreFooterView();

    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPullDownHeaderView();
        initLoadMoreFooterView();
    }

    /**
     * 初始化加载更多的脚布局
     */
    private void initLoadMoreFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.refreshlistview_footer, null);
        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
        addFooterView(mFooterView);

        setOnScrollListener(this);
    }

    /**
     * 初始化下拉头
     */
    private void initPullDownHeaderView() {
        handerView = (LinearLayout) View.inflate(getContext(), R.layout.refreshlistview_header, null);
        mPullDownHeaderView = handerView.findViewById(R.id.ll_refreshlistView_pull_down_header);

        ivArror = handerView.findViewById(R.id.iv_refreshlistView_heaer_arrow);
        mProgressBar = handerView.findViewById(R.id.pb);
        tvState = handerView.findViewById(R.id.tv_refreshlistView_heaer_state);
        tvLastUpdataTime = handerView.findViewById(R.id.tv_refreshlistView_heaer_last_update_time);

        mPullDownHeaderView.measure(0, 0);

        mPullDownHeaderViewHeight = mPullDownHeaderView.getMeasuredHeight();
        mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
        this.addHeaderView(handerView);

        initAnimation();


    }

    private void initAnimation() {
        upAnimation = new RotateAnimation(
                0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(
                -180, -360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);

    }

    public void addListViewCustomHeaderView(View v) {
        mCustomHeaderView = v;
        handerView.addView(v);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (downY == -1) {
                    downY = (int) ev.getY();
                }

                if(!isEnablePullDownRefresh){
                    break;
                }


                //如果当前状态是正在刷新中，直接跳出switch
                if (currentState == REFRESHING) {
                    break;
                }


                if (mCustomHeaderView != null) {

                    //如果轮播图没有完全显示，不应该进行下拉头的操作，而是响应listview本身的touch事件,直接跳出switch
                    int[] location = new int[2];
                    if (mListViewOnScreenY == -1) {
                        this.getLocationOnScreen(location);
                        mListViewOnScreenY = location[1];
                    }
                    //取出轮播图在屏幕中y轴的值
                    mCustomHeaderView.getLocationOnScreen(location);
                    if (location[1] < mListViewOnScreenY) {//如果轮播图在屏幕中y轴的值小于当前listview在屏幕中y轴的值，
                        // 轮播图没有完全显示，不执行下拉头的操作，直接跳出
                        System.out.println("轮播图没有完全显示，直接跳出");
                        break;
                    }

                }
                int moveY = (int) ev.getY();
                int diffY = moveY - downY;
                if (diffY > 0 && getFirstVisiblePosition() == 0) {//当前是向下滑动,并且是在listview的顶部
                    int paddingTop = -mPullDownHeaderViewHeight + diffY;

                    if (paddingTop < 0 && currentState != PULL_DOWN_REFRESH) { //当前没有完全显示，并且当前状态属于松开刷新， 进入下拉刷新
                        System.out.println("下拉刷新");
                        currentState = PULL_DOWN_REFRESH;
                        refreshPullDownState();
                    } else if (paddingTop > 0 && currentState != RELEASE_REFRESH) {//当前完全显示，属于松开刷新
                        System.out.println("松开刷新");
                        currentState = RELEASE_REFRESH;
                        refreshPullDownState();
                    }
                    mPullDownHeaderView.setPadding(0, paddingTop, 0, 0);
                    return true;//自己处理，不响应父类的touch事件
                }
                break;
            case MotionEvent.ACTION_UP:
                downY = -1;
                if (currentState == PULL_DOWN_REFRESH) {
                    //当前是下拉刷新
                    mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
                } else if (currentState == RELEASE_REFRESH) {
                    //当前是松开刷新,进入正在刷新中状态
                    currentState = REFRESHING;
                    refreshPullDownState();
                    mPullDownHeaderView.setPadding(0, 0, 0, 0);
                    //调用使用者的监听事件
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onPullDownRefresh();
                    }

                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据当前的currentState来刷新头布局
     */
    private void refreshPullDownState() {
        switch (currentState) {
            case PULL_DOWN_REFRESH://下拉刷新
                //箭头向下旋转的动画，并且把状态旋转为 下拉刷新
                ivArror.startAnimation(downAnimation);
                tvState.setText("下拉刷新");

                break;
            case RELEASE_REFRESH: //释放刷新
                ivArror.startAnimation(upAnimation);
                tvState.setText("松开刷新");
                break;
            case REFRESHING://正在刷新
                ivArror.clearAnimation();
                ivArror.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                tvState.setText("正在刷新");
                break;
            default:
                break;

        }
    }

    /**
     * 当用户刷新数据完成时，回调此方法，把下拉刷新的头给隐藏
     */
    public void OnRefreshDataFinish() {

        if(isLoadingMore){
            //当前是加载更多，隐藏脚布局
            isLoadingMore = false;
            mFooterView.setPadding(0,-mFooterViewHeight,0,0);
        }else{
            //当前是下拉刷新，隐藏头布局

            ivArror.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            tvState.setText("下拉刷新");
            tvLastUpdataTime.setText("最后刷新时间:" + getCurrentTime());
            mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
            currentState = PULL_DOWN_REFRESH;
        }


    }

    /**
     * 获取当前系统的时间，格式为：2014-11-16 16：06：32
     *
     * @return
     */
    public String getCurrentTime() {
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formate.format(new Date());
    }

    /**
     * 设置listview刷新数据的监听事件
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(!isEnableLoadMoreRefresh){
            return ;
        }
        //当滚动停止时，当前listview是在底部
        if (i == SCROLL_STATE_IDLE || i == SCROLL_STATE_FLING) {
            if ((getLastVisiblePosition() == getCount() - 1) && !isLoadingMore) {
                //System.out.println("滑动到底部。");
                isLoadingMore = true;
                //显示脚布局
                mFooterView.setPadding(0, 0, 0, 0);
                setSelection(getCount());

                if(mOnRefreshListener != null){
                    mOnRefreshListener.onLoadingMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    /**
     * 自定义listview刷新的监听事件
     */
    public interface OnRefreshListener {
        /**
         * 当下拉刷新时回调此方法
         */
        public void onPullDownRefresh();

        public void onLoadingMore();
    }


    /**
     * 设置是否启用下拉刷新
     * @param b
     */
    public void setEnablePullDownRefresh(boolean b){
        isEnablePullDownRefresh = b;
    }

    /**
     * 设置是否启用加载更多
     * @param b
     */
    public void setEnableLoadMoreRefresh(boolean b){
        isEnableLoadMoreRefresh = b;
    }
}


