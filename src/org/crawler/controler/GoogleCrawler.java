package org.crawler.controler;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.crawler.model.Movie;

public class GoogleCrawler extends Crawler {

	public GoogleCrawler(String baseUrl) {
		super(baseUrl);
	}

	public List<String> getKeywords(Movie movie) throws Exception {
		ArrayList<String> result = new ArrayList<String>();
		String movieUrl = getMovieUrlFromGoogle(movie);
		System.out.println(movieUrl);
		if (!movieUrl.isEmpty()) {
			Source keyWordSite = readSite(movieUrl + "keywords");
			for (Element b : keyWordSite.getAllElementsByClass("keyword")) {
				Element anker = b.getFirstElement();
				if (anker != null) {
					result.add(anker.getTextExtractor().toString());
				}
			}
		}
		return result;
	}

	private String getMovieUrlFromGoogle(Movie movie) throws Exception {
		String title = movie.getTitle();
		String encodedTitle = URLEncoder.encode(title, "UTF-8");
		Source source = readSite("http://google.com/search?q=" + encodedTitle);
		for (Element link : source.getAllElements(HTMLElementName.A)) {
			String value = link.getAttributeValue("href");
			if (value != null && value.contains("imdb.com/title/")) {
				return value;
			}
		}
		return "";
	}

}
