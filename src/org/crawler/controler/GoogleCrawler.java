package org.crawler.controler;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.crawler.model.Movie;

public class GoogleCrawler extends Crawler {

	private static Logger logger = Logger.getLogger(GoogleCrawler.class.getPackage().getName());
	
	public GoogleCrawler(String baseUrl) throws Exception {
		super(baseUrl);
		TIMEOUT = 3000;
	}

	public List<String> getKeywords(Movie movie) throws Exception {
		logger.info("Querying Google for movie: " + movie.getTitle());
		String movieUrl = getMovieUrlFromGoogle(movie);
		System.out.println(movieUrl);
		logger.info("Retrieved movei URL from Google: " + movieUrl);
		ArrayList<String> result;
		if (!movieUrl.isEmpty()) {
			Source keyWordSite = readSite(movieUrl + "keywords");
			result = findKeyWords(keyWordSite);
		} else {
			result = new ArrayList<String>();
		}
		logger.info("Retrieved keywords: " + result);
		return result;
	}

	private String getMovieUrlFromGoogle(Movie movie) throws Exception {
		String title = movie.getTitle();
		String encodedTitle = URLEncoder.encode(title, "UTF-8");
		Source source = readSite("http://google.com/search?q=" + encodedTitle);
		for (Element link : source.getAllElements(HTMLElementName.A)) {
			String value = link.getAttributeValue("href");
			if (value != null && Pattern.matches("http:\\/\\/www\\.imdb\\.com\\/title\\/[a-zA-Z0-9]*\\/", value)) {
				return value;
			}
		}
		return "";
	}

}
