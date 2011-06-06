package org.crawler.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Movie {

	private String id;
	private String title; 
	private Date date;
	private String url;
	private List<String> keywords;
	
	public Movie(String id, String title, Date date, String url) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.url = url;
		keywords = new ArrayList<String>();
	}
	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Date getDate() {
		return date;
	}

	public String getUrl() {
		return url;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		return title + "; " + dateFormat.format(date) + "; " + url;
	}


}
