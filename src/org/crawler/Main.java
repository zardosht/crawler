package org.crawler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.crawler.controler.GoogleCrawler;
import org.crawler.model.ImportUtil;
import org.crawler.model.Movie;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
//		List<Movie> movies = ImportUtil.importMoviesFromFile(new File("data/u.item"));
	
		GoogleCrawler crawler = new GoogleCrawler("http://imdb.com");
		
//		for(Movie movie : movies){
//			crawler.getKeywords(movie);
//		}
		
		for(String str : crawler.getKeywords(new Movie(null, "Goldeneye", null, null))) {
			System.out.print(str+", ");
		}
		System.out.println("");
		
	}

}
