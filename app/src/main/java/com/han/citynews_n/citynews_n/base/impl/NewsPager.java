package com.han.citynews_n.citynews_n.base.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.han.citynews_n.citynews_n.Fragment.LeftMenuFragment;
import com.han.citynews_n.citynews_n.MainActivity;
import com.han.citynews_n.citynews_n.base.NewsCenterMenuBasePager;
import com.han.citynews_n.citynews_n.base.TabBasePager;
import com.han.citynews_n.citynews_n.base.newscentermenu.impl.InteractMenuPager;
import com.han.citynews_n.citynews_n.base.newscentermenu.impl.NewsMenuPager;
import com.han.citynews_n.citynews_n.base.newscentermenu.impl.PhotosMenuPage;
import com.han.citynews_n.citynews_n.base.newscentermenu.impl.TopicMenuPager;
import com.han.citynews_n.citynews_n.domain.NewsCenterMenuBean;
import com.han.citynews_n.citynews_n.utils.CacheUtils;
import com.han.citynews_n.citynews_n.utils.Constants;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2018/5/11.
 */

public class NewsPager extends TabBasePager {


    private List<NewsCenterMenuBean.NewsCenterMenu> mLeftMenuList;//左侧菜单的数据列表
    private List<NewsCenterMenuBasePager> pagerList;

    public NewsPager(Context context) {
        super(context);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("新闻页面");
        ibMenu.setVisibility(View.INVISIBLE);
        getDataFromNet();
    }

    /**
     * 获取数据
     */
    public void getDataFromNet() {
        String json = CacheUtils.getString(mContext, Constants.NEWSCENTER_URL, null);
        if(!TextUtils.isEmpty(json)){

            processData(json);
        }

        System.out.println("NewsPager的GetDataFromNet方法解析数据");

        HttpUtils httpUtils = new HttpUtils();
        System.out.println(Constants.NEWSCENTER_URL+"测试Newspager中的Constants.NewsCenter_url");
        httpUtils.send(com.lidroid.xutils.http.client.HttpRequest.HttpMethod.GET, Constants.NEWSCENTER_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("访问成功"+responseInfo.result);

                CacheUtils.putString(mContext,Constants.NEWSCENTER_URL,responseInfo.result);

                processData(responseInfo.result);
            }

            @Override
            public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {
                System.out.println("访问失败"+s);
            }


        });
    }

    /**
     * 解析和绑定数据
     * @param json
     */
    private void processData(String json) {
        Gson gson = new Gson();
        NewsCenterMenuBean bean = gson.fromJson(json, NewsCenterMenuBean.class);


        //初始化对应页面
        pagerList = new ArrayList<>();
        pagerList.add(new NewsMenuPager(mContext,bean.data.get(0)));
        pagerList.add(new TopicMenuPager(mContext));
        pagerList.add(new PhotosMenuPage(mContext));
        pagerList.add(new InteractMenuPager(mContext));

        //初始化左侧菜单的数据
        mLeftMenuList = bean.data;
        LeftMenuFragment mLeftMenuFragment = ((MainActivity)mContext).getLeftMenuFragment();
        mLeftMenuFragment.setMenuDataList(mLeftMenuList);

    }
    /**
     * 切换页面
     * @param position 需要切换到页面的数值
     */
    public void switchPager(int position) {
        NewsCenterMenuBasePager pager = pagerList.get(position);
        View rootView = pager.getRootView();
        flTabBasePager.removeAllViews();
        flTabBasePager.addView(rootView);
        tvTitle.setText(mLeftMenuList.get(position).title);
        pager.initData();


        if(position == 2){
            ibListAndGrid.setTag(pager);
            ibListAndGrid.setVisibility(View.VISIBLE);
            ibListAndGrid.setOnClickListener(new OnPhotoListAndGridClickListener());
        }else{
            ibListAndGrid.setVisibility(View.GONE);
        }
    }

    class OnPhotoListAndGridClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            PhotosMenuPage pager= (PhotosMenuPage) view.getTag();
            pager.switchListOrGrid((ImageButton) view);
        }
    }
}
