package org.crawler.controler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.crawler.model.Movie;

public class GoogleCrawler extends Crawler {

	public GoogleCrawler(String baseUrl) {
		super(baseUrl);
	}

	public List<String> getKeywords(Movie movie) throws Exception {
		ArrayList<String> result = new ArrayList<String>();
		String movieUrl = getMovieUrlFromGoogle(movie);
		System.out.println(movieUrl);
		if(!movieUrl.isEmpty()) {
			Source keyWordSite = readSite(movieUrl+"keywords");
			for (Element b : keyWordSite.getAllElementsByClass("keyword")) {
				Element anker = b.getFirstElement();
				if(anker != null) {
					result.add(anker.getTextExtractor().toString());
				}
			}
		}
		return result;
	}

	private String getMovieUrlFromGoogle(Movie movie) throws Exception {
		Source source = readSite("http://google.com/search?q="+movie.getTitle());
		for (Element link : source.getAllElements(HTMLElementName.A)) {
			String value = link.getAttributeValue("href");
			if (value != null && value.contains("imdb.com/title/")) {
				return value;
			}
		}
		return "";
	}

}
