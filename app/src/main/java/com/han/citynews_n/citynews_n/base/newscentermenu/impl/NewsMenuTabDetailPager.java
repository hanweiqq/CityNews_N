package com.han.citynews_n.citynews_n.base.newscentermenu.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.han.citynews_n.citynews_n.R;
import com.han.citynews_n.citynews_n.base.NewsCenterMenuBasePager;
import com.han.citynews_n.citynews_n.base.view.RefreshListView;
import com.han.citynews_n.citynews_n.domain.NewsCenterMenuBean;
import com.han.citynews_n.citynews_n.domain.TabDetailBean;
import com.han.citynews_n.citynews_n.utils.CacheUtils;
import com.han.citynews_n.citynews_n.utils.Constants;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;


/**
 * 新闻中心页签详情页面
 * Created by han on 2018/5/18.
 */

public class NewsMenuTabDetailPager extends NewsCenterMenuBasePager implements ViewPager.OnPageChangeListener, RefreshListView.OnRefreshListener {

    @ViewInject(R.id.nsvp_tab_detail_topnews)
    private ViewPager topNewsViewPager;

    @ViewInject(R.id.tv_tab_detail_description)
    private TextView tvDescription;

    @ViewInject(R.id.ll_tab_detail_point_group)
    private LinearLayout llPointGroup;


    @ViewInject(R.id.rlv_tab_detail_news)
    private RefreshListView newsListView;

    private InternalHandler mHandler;

    private NewsCenterMenuBean.NewsCenterTabBean mNewsCenterTabBean;
    private String moreUrl;
    private List<TabDetailBean.TopNew> topNewsList;
    private BitmapUtils bitmapUtils;
    private int previousEnabledPostion;
    private List<TabDetailBean.NewsBean> newsList; //新闻列表的数据
    private String url;
    private TopNewsAdapter topNewsAdapter;
    private NewsAdapter newsAdapter;


    public NewsMenuTabDetailPager(Context context) {
        super(context);
    }

    public NewsMenuTabDetailPager(Context mContext, NewsCenterMenuBean.NewsCenterTabBean newsCenterTabBean) {
        super(mContext);

        this.mNewsCenterTabBean = newsCenterTabBean;
        bitmapUtils = new BitmapUtils(mContext);
        bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.ARGB_4444);


    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.tab_detail, null);
        ViewUtils.inject(this, view);
        View topNewsView = View.inflate(mContext, R.layout.tab_detail_topnews, null);
        ViewUtils.inject(this, topNewsView);

        //newsListView.addHeaderView(topNewsView);
        newsListView.addListViewCustomHeaderView(topNewsView);
        newsListView.setEnablePullDownRefresh(true);
        newsListView.setEnableLoadMoreRefresh(true);
        newsListView.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void initData() {
        url = Constants.SERVER_URL + mNewsCenterTabBean.url;
        getDataFromNet(url);
    }

    private void getDataFromNet(final String url) {
        String json = CacheUtils.getString(mContext, url, null);
        if (!TextUtils.isEmpty(json)) {
            processData(json);
        }
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println(mNewsCenterTabBean.title + "请求成功：" + responseInfo.result);
                CacheUtils.putString(mContext, url, responseInfo.result);
                processData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                System.out.println(mNewsCenterTabBean.title + "请求失败：" + s);
            }
        });
    }


    /**
     * 解析json数据
     *
     * @param json
     * @return
     */
    private TabDetailBean parserJson(String json) {
        Gson gson = new Gson();
        TabDetailBean bean = gson.fromJson(json, TabDetailBean.class);
        System.out.println(bean.data.topnews.get(0).title);
        moreUrl = bean.data.more;
        if (!TextUtils.isEmpty(moreUrl)) {
            moreUrl = Constants.SERVER_URL + moreUrl;
        }
        return bean;
    }

    /**
     * 接收json数据进行解析并展示
     *
     * @param json
     */
    private void processData(String json) {
        TabDetailBean bean = parserJson(json);
        if (!TextUtils.isEmpty(moreUrl)) {
            moreUrl = Constants.SERVER_URL + moreUrl;
        }
        //初始化顶部轮播新闻的数据
        topNewsList = bean.data.topnews;
        if (topNewsAdapter == null) {
            topNewsAdapter = new TopNewsAdapter();
            topNewsViewPager.setAdapter(topNewsAdapter);
            topNewsViewPager.setOnPageChangeListener(this);
        } else {
            topNewsAdapter.notifyDataSetChanged();
        }


        //初始化轮播图对应的点
        llPointGroup.removeAllViews();
        View view;
        LinearLayout.LayoutParams params;
        for (int i = 0; i < topNewsList.size(); i++) {
            view = new View(mContext);
            view.setBackgroundResource(R.drawable.tab_detail_topnews_point_bg);
            params = new LinearLayout.LayoutParams(5, 5);
            if (i != 0) {
                params.leftMargin = 10;
            }
            view.setLayoutParams(params);
            view.setEnabled(false);
            llPointGroup.addView(view);
        }

        //设置默认选中的点
        previousEnabledPostion = 0;
        tvDescription.setText(topNewsList.get(previousEnabledPostion).title);
        llPointGroup.getChildAt(previousEnabledPostion).setEnabled(true);

        //开始循环播放
        if (mHandler == null) {
            mHandler = new InternalHandler();
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new AutoSwitchRunnable(), 3000);

        //新闻列表数据初始化

        newsList = bean.data.news;
        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter();
            newsListView.setAdapter(newsAdapter);
        } else {
            newsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        llPointGroup.getChildAt(previousEnabledPostion).setEnabled(false);
        previousEnabledPostion = position;
        llPointGroup.getChildAt(previousEnabledPostion).setEnabled(true);

        tvDescription.setText(topNewsList.get(previousEnabledPostion).title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPullDownRefresh() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                newsListView.OnRefreshDataFinish();
                Toast.makeText(mContext, "刷新数据成功", Toast.LENGTH_SHORT).show();

                CacheUtils.putString(mContext, url, responseInfo.result);
                processData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                newsListView.OnRefreshDataFinish();
                Toast.makeText(mContext, "刷新数据失败", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onLoadingMore() {
        //加载更多数据，去刷新更多的数据，并且把脚布局隐藏
        if(TextUtils.isEmpty(moreUrl)){
            Toast.makeText(mContext,"没有更多数据", Toast.LENGTH_SHORT).show();
            newsListView.OnRefreshDataFinish();

        }else{
            //有更多的数据，去请求
            HttpUtils utils = new HttpUtils();
            utils.send(HttpRequest.HttpMethod.GET, moreUrl, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    newsListView.OnRefreshDataFinish();
                    Toast.makeText(mContext, "加载更多数据成功", Toast.LENGTH_SHORT).show();

                    TabDetailBean bean = parserJson(responseInfo.result);
                    //把新闻列表数据取出来，并且在原有集合的基础上加上
                    newsList.addAll(bean.data.news);
                    newsAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(HttpException e, String s) {
                    newsListView.OnRefreshDataFinish();
                    Toast.makeText(mContext, "加载更多数据失败", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    class NewsViewHolder {
        TextView tvTitle;
        ImageView ivImage;
        TextView tvTime;
    }

    class NewsAdapter extends BaseAdapter {

        private View v;

        @Override
        public int getCount() {
            return newsList.size();
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            NewsViewHolder newsViewHolder = null;
            if (view == null) {
                view = View.inflate(mContext, R.layout.tab_detail_news_item, null);
                newsViewHolder = new NewsViewHolder();
                newsViewHolder.ivImage = view.findViewById(R.id.iv_tab_detail_news_item_image);
                newsViewHolder.tvTitle = view.findViewById(R.id.tv_tab_detail_news_item_title);
                newsViewHolder.tvTime = view.findViewById(R.id.tv_tab_detail_news_item_time);
                view.setTag(newsViewHolder);
            } else {
                newsViewHolder = (NewsViewHolder) view.getTag();
            }

            TabDetailBean.NewsBean newsBean = newsList.get(i);
            bitmapUtils.display(newsViewHolder.ivImage, newsBean.listimage);
            newsViewHolder.tvTitle.setText(newsBean.title);
            newsViewHolder.tvTime.setText(newsBean.pubdate);

            return view;
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

    class TopNewsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return topNewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = new ImageView(mContext);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            //设置默认的图片
            iv.setBackgroundResource(R.drawable.home_scroll_default);
            iv.setOnTouchListener(new TopNewsItemTouchListener());
            container.addView(iv);
            //请求网络图片，把图片展示给ImageView
            bitmapUtils.display(iv, topNewsList.get(position).topimage);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    class TopNewsItemTouchListener implements View.OnTouchListener {

        private int downX;
        private int downY;
        private long downTime;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    System.out.println("停止播放");
                    mHandler.removeCallbacksAndMessages(null);
                    downX = (int) motionEvent.getX();
                    downY = (int) motionEvent.getY();
                    downTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("开始播放");
                    mHandler.postDelayed(new AutoSwitchRunnable(), 3000);
                    int upX = (int) motionEvent.getX();
                    int upY = (int) motionEvent.getY();

                    if (downX == upX && downY == upY) {
                        long upTime = System.currentTimeMillis();
                        long time = upTime - downTime;
                        if (time < 500) {
                            topNewsItemClick(view);
                        }
                    }

                    break;

                default:
                    break;

            }
            return true;
        }

        private void topNewsItemClick(View view) {
            System.out.println("轮播图的图片被点击了");
        }
    }

    class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int currentItem = (topNewsViewPager.getCurrentItem() + 1) % topNewsList.size();
            topNewsViewPager.setCurrentItem(currentItem);
            postDelayed(new AutoSwitchRunnable(), 3000);
        }
    }

    class AutoSwitchRunnable implements Runnable {

        @Override
        public void run() {
            mHandler.obtainMessage().sendToTarget();
            //System.out.println("发送一个消息:");
        }
    }
}
