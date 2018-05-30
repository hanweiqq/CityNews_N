package com.han.citynews_n.citynews_n.domain;

import java.util.List;

public class TabDetailBean {
	
	public int retcode;
	public TabDetailData data;

	public class TabDetailData {
		
		public String countcommenturl;
		public String more;
		public List<NewsBean> news;
		public String title;
		public List<Topic> topic;
		public List<TopNew> topnews;
	}
	
	public class NewsBean {

		public String comment;
		public String commentlist;
		public String commenturl;
		public String id;
		public String listimage;
		public String pubdate;
		public String title;
		public String type;
		public String url;
	}
	
	public class Topic {

		public String description;
		public String id;
		public String listimage;
		public String sort;
		public String title;
		public String url;
	}
	
	public class TopNew {
		public String comment;
		public String commentlist;
		public String commenturl;
		public String id;
		public String pubdate;
		public String title;
		public String topimage;
		public String type;
		public String url;
	}
}
