package org.crawler;

import java.io.File;
import java.util.List;

import org.crawler.controler.GoogleCrawler;
import org.crawler.model.ImportUtil;
import org.crawler.model.Movie;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
			List<Movie> movies = ImportUtil.importMoviesFromFile(new File(
					"data/u.item"));

			openGUI(movies);
		} else {

			// List<Movie> movies = ImportUtil.importMoviesFromFile(new
			// File("data/u.item"));

			GoogleCrawler crawler = new GoogleCrawler("http://imdb.com");

			// for(Movie movie : movies){
			// crawler.getKeywords(movie);
			// }

			for (String str : crawler.getKeywords(new Movie(null, "Goldeneye",
					null, null))) {
				System.out.print(str + ", ");
			}
			System.out.println("");
		}

	}

	private static void openGUI(List<Movie> movies) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));

		Label lblKeywordFilter = new Label(shell, SWT.NONE);
		lblKeywordFilter.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
		lblKeywordFilter.setText("Keywords Filter: ");

		final Text txtKeywordsFilter = new Text(shell, SWT.SINGLE | SWT.LEAD
				| SWT.BORDER);
		txtKeywordsFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
		txtKeywordsFilter.setText("");

		final ListViewer lv = new ListViewer(shell, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		lv.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.TOP, true, true, 2, 1));
		lv.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Movie) {
					return ((Movie) element).getTitle();
				}
				return super.getText(element);
			}

		});

		lv.setContentProvider(new ArrayContentProvider());
		final ViewerFilter filter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof Movie) {
					return ((Movie) element).containsKeyword(txtKeywordsFilter
							.getText());
				}
				return false;
			}
		};
		lv.setInput(movies);

		txtKeywordsFilter.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (txtKeywordsFilter.getText() != null
						&& !"".equals(txtKeywordsFilter.getText())) {
					lv.addFilter(filter);
				}else{
					lv.removeFilter(filter);
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
