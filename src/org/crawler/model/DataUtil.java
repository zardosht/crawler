package org.crawler.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataUtil {
	

	private DataUtil(){
	}

	public static List<Movie> importMoviesFromFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<Movie> result = new ArrayList<Movie>();
		String line = null;
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		int lineNumber = 0;
		while((line = reader.readLine()) != null) {
			lineNumber++;
			String[] split = line.split("\t");
			String id = split[0];
			String title = split[1];
			Date date;
			try {
				date = dateFormat.parse(split[2]);
			} catch (ParseException e) {
				System.out.println("WARNING: Could not parse date! " + file.getName() + ":" + lineNumber);
				date = new Date();
			}
			String url = split[4];
			result.add(new Movie(id, title, date, url));
		}
		
		return result;
	}

	public static List<Movie> importMoviesFromCrawlingResults(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<Movie> result = new ArrayList<Movie>();
		String line = null;
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		int lineNumber = 0;
		while((line = reader.readLine()) != null) {
			lineNumber++;
			String[] split = line.split(";");
			String id = split[0];
			String title = split[1];
			Date date;
			try {
				date = dateFormat.parse(split[2]);
			} catch (ParseException e) {
				System.out.println("WARNING: Could not parse date! " + file.getName() + ":" + lineNumber);
				date = new Date();
			}
			String url = split[3];
			Movie movie = new Movie(id, title, date, url);
			for(int i = 4; i < split.length; i++){
				movie.getKeywords().add(split[i]);
			}
			result.add(movie);
		}
		
		return result;
	}

	
}
