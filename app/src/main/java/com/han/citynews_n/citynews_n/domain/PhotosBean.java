package com.han.citynews_n.citynews_n.domain;
import java.util.List;



/**
 * Created by han on 2018/6/1.
 */
public class PhotosBean {

    public int retcode;
    public Photos data;

    public class Photos {

        public String countcommenturl;
        public String more;
        public List<PhotosItem> news;
        public String title;
        public List topic;
    }

    public class PhotosItem {

        public String comment;
        public String commentlist;
        public String commenturl;
        public String id;
        public String largeimage;
        public String listimage;
        public String pubdate;
        public String smallimage;
        public String title;
        public String type;
        public String url;

    }
}

