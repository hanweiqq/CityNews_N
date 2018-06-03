package com.han.citynews_n.citynews_n.base.newscentermenu.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.han.citynews_n.citynews_n.R;
import com.han.citynews_n.citynews_n.base.NewsCenterMenuBasePager;
import com.han.citynews_n.citynews_n.domain.PhotosBean;
import com.han.citynews_n.citynews_n.utils.CacheUtils;
import com.han.citynews_n.citynews_n.utils.Constants;
import com.han.citynews_n.citynews_n.utils.ImageCacheUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by han on 2018/5/11.
 */

public class PhotosMenuPage extends NewsCenterMenuBasePager {

    @ViewInject(R.id.lv_photos_list)
    private ListView mListView;


    @ViewInject(R.id.gv_photos_grid)
    private GridView mGridView;
    private boolean isList = true;//当前显示的是网格
    private List<PhotosBean.PhotosItem> photosList;
    private ImageCacheUtils cacheUtils;//图片缓存工具类
    private PhotosAdapter mPhotosAdapter;


    /**
     * 接收消息的处理器
     */
    class InternalHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ImageCacheUtils.SUCCESS:
                    Bitmap bm = (Bitmap) msg.obj;
                    int tag = msg.arg1;

                    //找到含有当前tag的imageview，把bitmap设置给它
                    ImageView iv = mListView.findViewWithTag(tag);//设置三级图片的重要方法。 找到对应请求的imageview
                    iv.setImageBitmap(bm);
                    break;
                case ImageCacheUtils.FAILED:
                    Toast.makeText(mContext, "图片请求失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }

        }
    }

    public PhotosMenuPage(Context context) {
        super(context);
        InternalHandler mHandler = new InternalHandler();
        cacheUtils = new ImageCacheUtils(mHandler, mContext);
    }


    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.photos, null);
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    public void initData() {
        String json = CacheUtils.getString(mContext, Constants.PHOTOS_URL, null);

        if (!TextUtils.isEmpty(json)) {
            processData(json);
        }

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.PHOTOS_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("组图访问成功" + responseInfo.result);
                CacheUtils.putString(mContext, Constants.PHOTOS_URL, responseInfo.result);
                processData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                System.out.println("组图访问失败" + s);

            }
        });
    }

    /**
     * 解析处理数据
     *
     * @param result
     */
    private void processData(String result) {
        Gson gson = new Gson();
        PhotosBean bean = gson.fromJson(result, PhotosBean.class);
        photosList = bean.data.news;
        mPhotosAdapter = new PhotosAdapter();
        mListView.setAdapter(mPhotosAdapter);

    }

    /**
     * 切换当前页面
     */
    public void switchListOrGrid(ImageButton ib) {
        if (isList) {
            //切换到网格页面
            isList = false;
            ib.setImageResource(R.drawable.icon_pic_grid_type);
            mGridView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);

            mGridView.setAdapter(mPhotosAdapter);
        } else {
            isList = true;
            ib.setImageResource(R.drawable.icon_pic_list_type);

            mListView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mListView.setAdapter(mPhotosAdapter);

        }
    }

    class PhotosAdapter extends BaseAdapter {

        private PhotosBean.PhotosItem photosItem;

        @Override
        public int getCount() {
            return photosList.size();
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            PhotosViewHolder mphotosHolder = null;
            if (view == null) {
                mphotosHolder = new PhotosViewHolder();
                view = View.inflate(mContext, R.layout.photos_item, null);
                mphotosHolder.ivImage = view.findViewById(R.id.iv_photos_item_image);
                mphotosHolder.tvText = view.findViewById(R.id.tv_photos_items_text);
                view.setTag(mphotosHolder);
            } else {
                mphotosHolder = (PhotosViewHolder) view.getTag();
            }
            photosItem = photosList.get(i);
            mphotosHolder.tvText.setText(photosItem.title);
            //设置默认图片
            mphotosHolder.ivImage.setImageResource(R.drawable.pic_item_list_default);

            //给当前ivImage设置一个标识，为方便在后期找到它  多级缓存的核心
            mphotosHolder.ivImage.setTag(i);
            //请求网络抓取图片
            Bitmap bm = cacheUtils.getBitmapFromUrl(photosItem.listimage, i);
            if(bm!=null){
                mphotosHolder.ivImage.setImageBitmap(bm);
            }

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

    class PhotosViewHolder {
        ImageView ivImage;
        TextView tvText;
    }
}
