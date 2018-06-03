package com.han.citynews_n.citynews_n.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片工具类
 * 用来抓取并且缓存图片
 * Created by han on 2018/6/1.
 */

public class ImageCacheUtils {

    private Handler handler;
    public static final int SUCCESS = 0;//请求成功
    public static final int FAILED = 1;//请求失败
    private final LruCache<String, Bitmap> mMemoryCache;
    private Context context;
    private final File cacheDir;
    private FileOutputStream fos;
    private final ExecutorService mExecutorService;

    public ImageCacheUtils(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;

        cacheDir = context.getCacheDir();
        System.out.println("cacheDir"+cacheDir.toString());

        //获取运行时可以使用的内存大小/8
        long maxMemory = Runtime.getRuntime().maxMemory() / 8;
        mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //android3.0之前不能用  return value.getByteCount();  使用getbytecount方法中的代码替换
                return value.getRowBytes() * value.getHeight();
            }
        };

        //获得一个线程为5个的线程池
        mExecutorService = Executors.newFixedThreadPool(5);

    }

    /**
     * 根据url请求图片
     * 请求图片的顺序
     * 1、内存
     * 2、本地
     * 3、网络
     *
     * @param url
     * @param tag 当前请求的标志
     */
    public Bitmap getBitmapFromUrl(String url, int tag) {
        //1、内存从内存中取出
        Bitmap bm = mMemoryCache.get(url);
        if (bm != null) {
            System.out.println("从内存中取出bm");
            return bm;
        }

        //2、从本地中取出
        bm = getBitmapFromLocal(url);
        if(bm!=null){
            System.out.println("从本地取出");
            return bm;
        }
        //3、网络
        System.out.println("从网络中取出bm");
        getBitmapFromNet(url, tag);
        return null;
    }

    /**
     * 根据url从本地中取图片
     * @param url
     * @return
     */
    private Bitmap getBitmapFromLocal(String url) {
        try {
            String fileName = MD5Encoder.encode(url).substring(0, 10);
            File file = new File(cacheDir,fileName);
            if(file.exists()){
                return BitmapFactory.decodeFile(file.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 把图片缓存在本地
     *
     * @param url
     * @param bm
     */
    private void writeToLocal(String url, Bitmap bm) {

        System.out.println("存入本地");

        try {
            String fileName = MD5Encoder.encode(url).substring(0,10);
            fos = new FileOutputStream(new File(cacheDir,fileName));
            bm.compress(Bitmap.CompressFormat.JPEG,100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally{
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 根据url请求网络得到图片
     *
     * @param url
     * @param tag
     */
    private void getBitmapFromNet(String url, int tag) {

        //new Thread(new RequestNetRunnable(url, tag)).start();因单线程 所以不用
        //一般采用线程池 在ImageCacheUtils初始化时就创建
        mExecutorService.execute(new RequestNetRunnable(url,tag));


    }






    /**
     * 请求网络的任务类
     */
    class RequestNetRunnable implements Runnable {

        private String url;
        private int tag;
        private HttpURLConnection conn;

        public RequestNetRunnable(String url, int tag) {
            this.url = url;
            this.tag = tag;
        }

        @Override
        public void run() {
            try {
                URL mUrl = new URL(url);
                conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);//设置连接超时时间
                conn.setReadTimeout(5000);//读取超时时间
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    Bitmap bm = BitmapFactory.decodeStream(is);

                    //把图片发送给主线程
                    Message msg = handler.obtainMessage();
                    msg.obj = bm;
                    msg.arg1 = tag;
                    msg.what = SUCCESS;
                    msg.sendToTarget();//把消息发送给PhotoMenuPager中的消息处理器

                    //向内存中存储一个
                    mMemoryCache.put(url, bm);


                    //向本地中存储
                    writeToLocal(url, bm);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();//断开连接
                }
            }

            handler.obtainMessage(FAILED).sendToTarget();
        }
    }


}
