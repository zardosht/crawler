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
import java.util.logging.Logger;

public class DataUtil {
	
	private static Logger logger = Logger.getLogger(DataUtil.class.getPackage().getName());

	private DataUtil() {
	}

	public static List<Movie> importMoviesFromFile(File file)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<Movie> result = new ArrayList<Movie>();
		String line = null;
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		int lineNumber = 0;
		while ((line = reader.readLine()) != null) {
			lineNumber++;
			String[] split = line.split("\t");
			String id = split[0];
			String title = split[1];
			Date date;
			try {
				date = dateFormat.parse(split[2]);
			} catch (ParseException e) {
				logger.warning("WARNING: Could not parse date! "
						+ file.getName() + ":" + lineNumber);
				date = new Date();
			}
			String url = split[4];
			List<String> genres = new ArrayList<String>();

			String[] genre = new String[] { "Action", "Adventure", "Animation",
					"Children's", "Comedy", "Crime", "Documentary", "Drama",
					"Fantasy", "Film-Noir", "Horror", "Musical", "Mystery",
					"Romance", "Sci-Fi", "Thriller", "War", "Western" };
			int offset = 6;
			for (int i = offset; i < split.length; i++) {
				int has = Integer.valueOf(split[i]).intValue();
				if (has == 1) {
					genres.add(genre[i - offset]);
				}
			}
			result.add(new Movie(id, title, date, url, genres));
		}

		return result;
	}

	public static List<Movie> importMoviesFromCrawlingResults(File file)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<Movie> result = new ArrayList<Movie>();
		String line = null;
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		int lineNumber = 0;
		while ((line = reader.readLine()) != null) {
			lineNumber++;
			String[] split = line.split(";");
			String id = " ";
			if(split.length >= 1) id = split[0];
			String title = " ";
			if(split.length >= 2) title = split[1];
			Date date = new Date();
			try {
				if(split.length >= 3) date = dateFormat.parse(split[2]);
			} catch (ParseException e) {
				logger.warning("WARNING: Could not parse date! "
						+ file.getName() + ":" + lineNumber);
			}
			String url = " ";
			if(split.length >= 4) url = split[3];
			Movie movie = new Movie(id, title, date, url, null);
			for (int i = 4; i < split.length; i++) {
				movie.getKeywords().add(split[i]);
			}
			result.add(movie);
		}

		return result;
	}

}
