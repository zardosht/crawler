package org.crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.crawler.controller.Crawler;
import org.crawler.controller.DeepCrawler;
import org.crawler.controller.GoogleCrawler;
import org.crawler.model.CSVWriter;
import org.crawler.model.DataUtil;
import org.crawler.model.Movie;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class.getPackage()
			.getName());

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length > 0 && args[0].equals("--crawler")) {
			if(args.length == 3 && (args[1].equals("google") || args[1].equals("imdb")) && (args[2].equals("google") || args[2].equals("imdb")) ){
				configureLogger("results/c2.deep.log.txt");
				startDeepCrawler();
				configureLogger("results/c1.google.log.txt");
				startGoogleCrawler();
			}else if(args.length == 2 && args[1].equals("google")){
				configureLogger("results/c1.google.log.txt");
				startGoogleCrawler();
			}else if(args.length == 2 && args[1].equals("imdb")){
				configureLogger("results/c2.deep.log.txt");
				startDeepCrawler();
			}else{
				System.out.println("Usage: java -jar crawler.jar --crawl google imdb");
			}
		} else {
			configureLogger("results/gui.log.txt");
			openGui();
		}

	}


	private static void startDeepCrawler() throws Exception {
		logger.info("Deep Crawling started.");
		File resultsFile = new File("results/c2.deep.result");
		logger.info("Writing results to file: " + resultsFile.getName());
		CSVWriter csvWriter = new CSVWriter(resultsFile,
				Arrays.asList("id", "title", "date", "url", "keywords"));
		File itemsFile = new File("data/u.item");
		logger.info("Importing movies from file: " + itemsFile.getName());
		List<Movie> movies = DataUtil.importMoviesFromFile(itemsFile);

		DeepCrawler crawler = new DeepCrawler("http://www.imdb.com");

		for (Movie movie : movies) {
			List<String> keywords = crawler.getKeywords(movie);
			System.out.println("IMDB: Keywords for " + movie.getTitle() + " : " + keywords);
			for (String keyword : keywords) {
				movie.getKeywords().add(keyword);
			}
			logger.info("Keywords extarcted for: " + movie.getTitle());
			writeCrawlerResult(csvWriter, movie);
			logger.info("Wrote record to result file: " + movie.toString());
		}
		csvWriter.close();
		logger.info("Deep crawling finished.");
		
		writeEval(crawler, "results/c2.deep.eval", movies.size());
	}

	private static void openGui() throws IOException {
		logger.info("GUI started.");
		File file = new File("results/c1.google.result");
		List<Movie> googleMovies = DataUtil
				.importMoviesFromCrawlingResults(file);
		logger.info("Extracted movies form result file: "  + file.getName());
	
		file = new File("results/c2.deep.result");
		List<Movie> imdbMovies = DataUtil.importMoviesFromCrawlingResults(file);
		logger.info("Extracted movies form result file: "  + file.getName());
		
		new CrawlerGui().openGUI(googleMovies, imdbMovies);
	}

	private static void startGoogleCrawler() throws IOException, Exception {
		logger.info("Google Crawling started.");
		File resultsFile = new File("results/c1.google.result");
		logger.info("Writing results to file: " + resultsFile.getName());
		CSVWriter csvWriter = new CSVWriter(resultsFile,
				Arrays.asList("id", "title", "date", "url", "keywords"));
		File itemsFile = new File("data/u.item");
		logger.info("Importing movies from file: " + itemsFile.getName());
		List<Movie> movies = DataUtil.importMoviesFromFile(itemsFile);

		GoogleCrawler crawler = new GoogleCrawler("http://www.imdb.com");

		for (Movie movie : movies) {
			List<String> keywords = crawler.getKeywords(movie);
			System.out.println("GOOGLE: Keywords for " + movie.getTitle() + " : " + keywords);
			for (String keyword : keywords) {
				movie.getKeywords().add(keyword);
			}
			logger.info("Keywords extarcted for: " + movie.getTitle());
			writeCrawlerResult(csvWriter, movie);
			logger.info("Wrote record to result file: " + movie.toString());
		}
		csvWriter.close();
		logger.info("Google crawling finished.");
		
		writeEval(crawler, "results/c1.google.eval", movies.size());
	}

	private static void writeEval(Crawler crawler, String evalFile, int numMovies) throws IOException {
		int visited = crawler.getVisited();
		int visitedRelevant = crawler.getVisitedRelevant();
		double harvestRate =  (double)visitedRelevant / (double)visited;
		double coverage = (double)visitedRelevant / numMovies;
		
		
		FileWriter fw = new FileWriter(new File(evalFile));
		fw.write("Visted Pages: " + String.format("%d", visited));
		fw.write(System.getProperty("line.separator"));
		fw.write("Visted Relevant Pages: " + String.format("%d", visitedRelevant));
		fw.write(System.getProperty("line.separator"));
		fw.write("Coverage: " + String.format("%.3f (found keywords for %d items out of %d)", coverage, visitedRelevant, numMovies));
		fw.write(System.getProperty("line.separator"));
		fw.write("Harvest Rate: " + String.format("%.3f", harvestRate));
		fw.close();
	}

	public static void writeCrawlerResult(CSVWriter csvWriter, Movie movie)
			throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		HashMap<String, Object> record = new HashMap<String, Object>();
		record.put("id", movie.getId());
		record.put("title", movie.getTitle());
		try {
			record.put("date", dateFormat.format(movie.getDate()));
		} catch (Exception e) {
			e.printStackTrace();
			record.put("date", " ");
		}
		record.put("url", movie.getUrl());
		StringBuilder sb = new StringBuilder("");
		for (String keyword : movie.getKeywords()) {
			sb.append(keyword);
			sb.append(";");
		}
		record.put("keywords", sb.toString());
		csvWriter.writeRecord(record);

	}



	private static void configureLogger(String logFileName) {
		logger.setUseParentHandlers(false);
		try {
			FileHandler fileHandler = new FileHandler(logFileName);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.setLevel(Level.INFO);
	}

}
