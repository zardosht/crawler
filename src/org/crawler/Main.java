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

import org.apache.commons.io.FileUtils;
import org.crawler.controller.Crawler;
import org.crawler.controller.DeepCrawler;
import org.crawler.controller.GoogleCrawler;
import org.crawler.model.CSVWriter;
import org.crawler.model.DataUtil;
import org.crawler.model.Movie;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
			for (String keyword : keywords) {
				movie.getKeywords().add(keyword);
			}
			logger.info("Keywords extarcted for: " + movie.getTitle());
			writeCrawlerResult(csvWriter, movie);
			System.out.println(movie.toString());
			logger.info("Wrote record to result file: " + movie.toString());
		}
		csvWriter.close();
		logger.info("Deep crawling finished.");
		
		writeEval(crawler, "results/c2.deep.eval", movies.size());
	}

	private static void openGui() throws IOException {
		logger.info("GUI started.");
		File file = new File("results/c1.google.result");
		List<Movie> movies = DataUtil
				.importMoviesFromCrawlingResults(file);
		logger.info("Extracted movies form result file: "  + file.getName());
		openGUI(movies);
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
			for (String keyword : keywords) {
				movie.getKeywords().add(keyword);
			}
			logger.info("Keywords extarcted for: " + movie.getTitle());
			writeCrawlerResult(csvWriter, movie);
			System.out.println(movie.toString());
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
		fw.write("Coverage: " + String.format("%.3f", coverage));
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

	private static void openGUI(List<Movie> movies) {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(4, false));

		Label lblCrawler = new Label(shell, SWT.NONE);
		lblCrawler.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		lblCrawler.setText("Crawler: ");

		final Combo cmbCrawler = new Combo(shell, SWT.DROP_DOWN);
		cmbCrawler
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		cmbCrawler.setItems(new String[] { "Google (C1)",
				"IMDB, deep crawler (C2)" });

		Button btnRefresh = new Button(shell, SWT.PUSH);
		btnRefresh.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		btnRefresh.setText("Refresh");

		Button btnInfo = new Button(shell, SWT.PUSH);
		btnInfo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		btnInfo.setText("Crawler Info");
		btnInfo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = cmbCrawler.getSelectionIndex();
				String title = "";
				String eval = "";
				try{
					if(index == 0){
						//google
						eval = FileUtils.readFileToString(new File("results/c1.google.eval"));
						title = "Google Form Completion Crawler - Evaluation";
					}else if(index == 1){
						//imdb
						eval = FileUtils.readFileToString(new File("results/c2.deep.eval"));
						title = "IMDB Deep Crawler - Evaluation";
					}
				}catch (IOException exception){
					exception.printStackTrace();
				}
				if(!title.isEmpty()){
					MessageDialog.openInformation(shell, title, eval);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label lblKeywords = new Label(shell, SWT.NONE);
		lblKeywords.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
		lblKeywords.setText("Keywords Filter: ");
		lblKeywords.setToolTipText("Use quotes for exact match.");

		final Text txtKeywords = new Text(shell, SWT.SINGLE | SWT.LEAD
				| SWT.BORDER);
		txtKeywords.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1));
		txtKeywords.setText("");
		txtKeywords.setToolTipText("Use quotes for exact match.");

		final ListViewer lvMovies = new ListViewer(shell, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		lvMovies.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		lvMovies.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Movie) {
					return ((Movie) element).getTitle();
				}
				return super.getText(element);
			}
		});
		lvMovies.setContentProvider(new ArrayContentProvider());
		final ViewerFilter filter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				String text = txtKeywords.getText();
				if (element instanceof Movie) {
					return ((Movie) element).containsKeywords(text);
				}
				return false;
			}
		};
		lvMovies.setInput(movies);

		final ListViewer lvKeywords = new ListViewer(shell, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		lvKeywords.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		lvKeywords.setLabelProvider(new LabelProvider());
		lvKeywords.setContentProvider(new ArrayContentProvider());
		lvKeywords.setSorter(new ViewerSorter());

		lvMovies.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ssel = (IStructuredSelection) event
						.getSelection();
				Movie movie = (Movie) ssel.getFirstElement();
				if (movie != null) {
					lvKeywords.setInput(movie.getKeywords());
				}
			}
		});

		txtKeywords.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (txtKeywords.getText() != null
						&& !"".equals(txtKeywords.getText())) {
					lvMovies.addFilter(filter);
				} else {
					lvMovies.removeFilter(filter);
				}
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
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
