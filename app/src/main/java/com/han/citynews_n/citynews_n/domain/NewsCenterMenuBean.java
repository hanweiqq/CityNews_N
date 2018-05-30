package com.han.citynews_n.citynews_n.domain;

import java.util.List;

/**
 * Created by han on 2018/5/13.
 */

public class NewsCenterMenuBean {
    public int retcode;
    public List<NewsCenterMenu> data;
    public List<String> extend;

    public class NewsCenterMenu {
        public int id;
        public String title;
        public String type;
        public List<NewsCenterTabBean> children;
        public String url;
        public String url1;
        public String excurl;
        public String dayurl;
        public String weekurl;
    }

    public class NewsCenterTabBean {
        public int id;
        public String title;
        public int type;
        public String url;
    }
}
