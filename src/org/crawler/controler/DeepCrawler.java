package org.crawler.controler;

import java.util.List;

import org.crawler.model.Movie;

/**
 * 1. put "The, A, An, etc. " to begining of title.
 * 2. goto http://www.imdb.com/search/title?release_date=1993,1993&title=the%20program
 * 3. extract Genre, 
 * 4. compute relvance based on extracted Genre and existing Genre (from movie database)
 * 5. take the most relvant page (movie!)
 * 6. extract keywords
 *  
 * 
 * 
 * @author zardosht
 *
 */
public class DeepCrawler extends Crawler {

	
	
	
	public DeepCrawler(String baseUrl) {
		super(baseUrl);
	}

	public List<String> getKeywords(Movie movie) {
		
		
		return null;
	}
	
	

}
