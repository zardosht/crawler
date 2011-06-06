package org.crawler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.crawler.model.ImportUtil;
import org.crawler.model.Movie;

public class Main {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		
		List<Movie> movies = ImportUtil.importMoviesFromFile(new File("data/u.item"));
		for(Movie movie : movies){
			System.out.println(movie.toString());
		}
		
	}

}
