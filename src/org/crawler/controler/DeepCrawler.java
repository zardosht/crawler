package org.crawler.controler;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

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
		String normalTitle = getNormalTitle(movie.getTitle());
		String year = getYear(movie.getDate());
		String encodedParam = URLEncoder.encode(
				String.format("release_date=%s,%s&title=%s", year, year, normalTitle)
				, "UTF-8");
		String url =  "http://www.imdb.com/search/title?" + encodedParam;
		Source source = readSite(url);
		String movieUrl = findMovieUrl(source);
		Source movieSite = readSite(movieUrl);
		List<String> keywords = extractKeywords(movieSite);
		return keywords;
	}

	private List<String> extractKeywords(Source movieSite) {
		// TODO Auto-generated method stub
		return null;
	}

	private String findMovieUrl(Source source) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getYear(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getNormalTitle(String title) {
		// replace the, a, an, ...
		return null;
	}

}
