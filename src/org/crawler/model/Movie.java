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
	private List<String> genres;
	

	private String keywordsString;
	
	public Movie(String id, String title, Date date, String url, List<String> genres) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.url = url;
		this.genres = genres;
		keywords = new ArrayList<String>();
	}
	
	public String getId() {
		return id != null ? id : " ";
	}

	public String getTitle() {
		return title != null ? title : " ";
	}

	public Date getDate() {
		return date;
	}

	public String getUrl() {
		return url != null ? url : " ";
	}

	public List<String> getKeywords() {
		return keywords;
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		StringBuilder sb = new StringBuilder("");
		for(String keyword : keywords){
			sb.append(keyword);
			sb.append(";");
		}
		return id + "; " + title + "; " + dateFormat.format(date) + "; " + url + "; KEYWORDS: " + sb.toString();
	}

	public boolean containsKeywords(String text) {
		String[] split = text.split(" ");
		for(int i = 0; i < split.length; i++){
			if(!getKeywordString().contains(split[i].trim().toLowerCase())){
				return false;
			}
		}
		return true;
	}

	private String getKeywordString() {
		if(keywordsString == null){
			StringBuilder sb = new StringBuilder("");
			for(String keyword : keywords){
				sb.append(keyword.toLowerCase());
				sb.append(";");
			}
			keywordsString = sb.toString();
		}
		return keywordsString;
	}

	public List<String> getGenres() {
		return genres;
	}


}
