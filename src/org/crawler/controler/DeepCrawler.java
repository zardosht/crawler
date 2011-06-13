package org.crawler.controler;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.crawler.model.Movie;

/**
 * 1. put "The, A, An, etc. " to begining of title. 2. goto
 * http://www.imdb.com/search/title?release_date=1993,1993&title=the%20program
 * 3. extract Genre, 4. compute relvance based on extracted Genre and existing
 * Genre (from movie database) 5. take the most relvant page (movie!) 6. extract
 * keywords
 * 
 * 
 * 
 * @author zardosht
 * 
 */
public class DeepCrawler extends Crawler {

	public DeepCrawler(String baseUrl) throws Exception {
		super(baseUrl);
	}

	public List<String> getKeywords(Movie movie) throws Exception {
		List<String> keywords = new ArrayList<String>();
		String normalTitle = getNormalTitle(movie.getTitle());
		String year = getYear(movie.getDate());
		String encodedParam = String.format("release_date=%s,%s&title=%s", year, year, URLEncoder.encode(normalTitle, "UTF-8"));
		String url = "http://www.imdb.com/search/title?" + encodedParam;
		Source source = readSite(url);
		String movieUrl = findMovieUrl(source, movie);
		if(movieUrl.isEmpty()){
			return keywords;
		}
		Source movieSite = readSite(movieUrl);
		keywords = extractKeywords(movieSite);
		return keywords;
	}

	private List<String> extractKeywords(Source movieSite) {
		List<String> keywords = new ArrayList<String>();
		return keywords;
	}

	private String findMovieUrl(Source source, Movie movie) {
		String url = "";
		List<String> genres = movie.getGenres();
		//get all links with href.contains(/title/)
		List<String> titleUrls = new ArrayList<String>();
		for (Element link : source.getAllElements(HTMLElementName.A)) {
			String value = link.getAttributeValue("href");
			if (value != null && value.contains("imdb.com/title/")) {
				titleUrls.add(value);
			}
		}
		
		double lastRelevance = 0.0;
		for(String foundTitle : titleUrls){
			List<String> extractedGenres = extractGenres(foundTitle);
			double relevance = getRelevance(genres, extractedGenres);
			if(relevance > lastRelevance){
				url = foundTitle;
				lastRelevance = relevance;
			}
			
		}
		//go into link
		return url;
	}

	private String getYear(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.US);
		String result = dateFormat.format(date);
		return result;
	}

	private String getNormalTitle(String title) {
		// replace the, a, an, ...
		String result = title;
		result = result.replaceAll("\\(.*\\)", "");
		String[] putToFront = new String[] { ", the", ", a", ", an" };
		for (int i = 0; i < putToFront.length; i++) {
			if (title.toLowerCase().contains(putToFront[i])) {
				result = result.toLowerCase().replace(putToFront[i], "");
				result = putToFront[i].replace(", ", "") + " " + result;
			}
		}

		return result.trim();
	}

}
