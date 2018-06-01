package com.han.citynews_n.citynews_n;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 新闻详情页面
 * Created by han on 2018/5/30.
 */

public class NewsDetailUi extends Activity implements View.OnClickListener {

    private String url;
    private int currentTextSizeIndex=2;
    private WebSettings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsdetail_activity);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        System.out.println("url" + url);
        initView();

    }

    private void initView() {

        findViewById(R.id.tv_title_bar).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_title_bar_menu).setVisibility(View.INVISIBLE);
        findViewById(R.id.ib_title_bar_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_title_bar_textSize).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_title_bar_share).setVisibility(View.VISIBLE);


        findViewById(R.id.ib_title_bar_back).setOnClickListener(this);
        findViewById(R.id.ib_title_bar_textSize).setOnClickListener(this);
        findViewById(R.id.ib_title_bar_share).setOnClickListener(this);


        final ProgressBar mPreProgressBar = findViewById(R.id.pb_news_detail);

        WebView mWebView = findViewById(R.id.wv_news_detail);
        settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);//启用javaScript脚本


        settings.setBuiltInZoomControls(true);//屏幕上显示放大和缩小按钮
        settings.setUseWideViewPort(true);//设置双击可以放大或者缩小
        mWebView.setWebViewClient(new WebViewClient() {
            /**
             * 页面数据加载完成 回调
             * @param view
             * @param url
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                mPreProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        mWebView.loadUrl(url);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_title_bar_back:
                finish();
                break;
            case R.id.ib_title_bar_textSize:
                showSelectTextSizeDialog();
                break;

            case R.id.ib_title_bar_share:

                showShare();
                break;

        }
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("这是一个标题");
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网使用
        oks.setComment("我是测试评论文本");
        // 启动分享GUI
        oks.show(this);

    }


    /**
     * 弹出选择字体大小的对话框
     */
    private void showSelectTextSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择字体大小");
        String[] items = {"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};
        builder.setSingleChoiceItems(items, currentTextSizeIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                currentTextSizeIndex = i;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switchTextSize();
            }
        }).setNegativeButton("取消", null);

        builder.show();
    }

    /**
     * 切换字体
     */
    private void switchTextSize() {
        switch (currentTextSizeIndex) {
            case 0:
                settings.setTextSize(WebSettings.TextSize.LARGEST);
                break;
            case 1:
                settings.setTextSize(WebSettings.TextSize.LARGER);
                break;
            case 2:
                settings.setTextSize(WebSettings.TextSize.NORMAL);
                break;
            case 3:
                settings.setTextSize(WebSettings.TextSize.SMALLER);
                break;
            case 4:
                settings.setTextSize(WebSettings.TextSize.SMALLEST);
                break;
            default:
                break;

        }
    }
}
