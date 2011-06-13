package org.crawler.controler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

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
		Source source = readSite(createSearchQuery(movie));
		String movieUrl = findMovieUrl(source, movie);
		if(movieUrl.isEmpty()){
			return keywords;
		}
		return findSitesWithKeywords(readSite(movieUrl));
	}

	private String createSearchQuery(Movie movie)
			throws UnsupportedEncodingException {
		String normalTitle = getNormalTitle(movie.getTitle());
		String year = getYear(movie.getDate());
		String encodedParam = String.format("release_date=%s,%s&title=%s", year, year, URLEncoder.encode(normalTitle, "UTF-8"));
		String url = "http://www.imdb.com/search/title?" + encodedParam;
		return url;
	}

	private List<String> findSitesWithKeywords(Source movieSite) throws Exception {
		List<String> keywords = new ArrayList<String>();
		List<String> queue = new ArrayList<String>();
		for (Element link : movieSite.getAllElements(HTMLElementName.A)) {
			String value = link.getAttributeValue("href");
			//filter by robots txt and title prefix
			if(value != null && (value.contains("imdb.com/title") || value.contains("/title") && allowed(value))) {
				queue.add(value);
			}
		}
		
		prioritizeUrls(queue);

		for(String url : queue) {
			ArrayList<String> words = findKeyWords(readSite(baseUrl+url));
			if(words.size()>0) {
				return words;
			}
			
		}
		
		return keywords;
	}

	/**
	 * URLs containing "keyword" should be searched first
	 */
	private void prioritizeUrls(List<String> queue) {
		Collections.sort(queue, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				boolean k1 = hasKeyword(o1);
				boolean k2 = hasKeyword(o2);
				if(k1 && !k2) {
					return 1;
				} else if(!k1 && k2) {
					return -1;
				}
				return 0;
			}
			
			private boolean hasKeyword(String o) {
				return o.contains("keyword");
			}
			
		});
	}

	private String findMovieUrl(Source source, Movie movie) {
		String url = "";
		List<String> genres = movie.getGenres();
		//get all links with href.contains(/title/)
		List<Element> searchResults = new ArrayList<Element>();
		for (Element td : source.getAllElements(HTMLElementName.TD)) {
			String classAtt = td.getAttributeValue("calss");
			if (classAtt != null && classAtt.contains("detailed")) {
				searchResults.add(td);
			}
		}
		
		double lastRelevance = 0.0;
		for(Element resultRow : searchResults){
			List<String> extractedGenres = extractGenres(resultRow);
			double relevance = getRelevance(genres, extractedGenres);
			if(relevance > lastRelevance){
				url = getTileUrl(resultRow);
				lastRelevance = relevance;
			}
			
		}
		//go into link
		return url;
	}

	private String getTileUrl(Element resultRow) {
		String result = "";
		for (Element link : resultRow.getAllElements(HTMLElementName.A)) {
			String value = link.getAttributeValue("href");
			if (value != null && value.contains("imdb.com/title/")) {
				result = value;
			}
		}
		return result;
	}

	private double getRelevance(List<String> movieGenres,
			List<String> extractedGenres) {
		int positive = 0;
		int count = 0;
		for(String movieGenre : movieGenres){
			for(String extractedGenre : extractedGenres){
				count++;
				if(movieGenre.trim().equalsIgnoreCase(extractedGenre.trim())){
					positive++;
				}
			}
		}
		
		return (double)positive / (double)count;
	}

	private List<String> extractGenres(Element resultRow) {
		List<String> extractedGenres = new ArrayList<String>();
		for (Element link : resultRow.getAllElements(HTMLElementName.A)) {
			String value = link.getAttributeValue("href");
			if (value != null && value.contains("imdb.com/genre/")) {
				String genre = value.substring(value.lastIndexOf("genre/"));
				extractedGenres.add(genre);
			}
		}
		return extractedGenres;
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
