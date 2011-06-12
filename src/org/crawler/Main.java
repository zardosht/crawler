package org.crawler;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.crawler.controler.GoogleCrawler;
import org.crawler.model.CSVWriter;
import org.crawler.model.DataUtil;
import org.crawler.model.Movie;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length > 0 && args[0].equals("--gui")) {
			List<Movie> movies = DataUtil
					.importMoviesFromCrawlingResults(new File(
							"results/c1.result"));
			openGUI(movies);
		} else {
			CSVWriter csvWriter = new CSVWriter(new File("results/c1.result"),
					Arrays.asList("id", "title", "date", "url", "keywords"));
			List<Movie> movies = DataUtil.importMoviesFromFile(new File(
					"data/u.item"));

			GoogleCrawler crawler = new GoogleCrawler("http://imdb.com");

			for (Movie movie : movies) {
				List<String> keywords = crawler.getKeywords(movie);
				for (String keyword : keywords) {
					movie.getKeywords().add(keyword);
				}
				System.out.println(movie.toString());
				writeCrawlerResult(csvWriter, movie);
			}
			csvWriter.close();
		}

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
			record.put("date", "");
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
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(4, false));

		Label lblCrawler = new Label(shell, SWT.NONE);
		lblCrawler.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		lblCrawler.setText("Crawler: ");

		Combo cmbCrawler = new Combo(shell, SWT.DROP_DOWN);
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

}
