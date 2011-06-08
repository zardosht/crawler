package org.crawler;


import java.io.File;
import java.util.List;

import org.crawler.controler.GoogleCrawler;
import org.crawler.model.ImportUtil;
import org.crawler.model.Movie;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
		Display display = new Display ();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		
		
		ListViewer lv = new ListViewer(shell, SWT.V_SCROLL | SWT.H_SCROLL);
		lv.getList().setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
		lv.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				if(element instanceof Movie){
					return ((Movie)element).getTitle();
				}
				return super.getText(element);
			}
			
		});
		
		lv.setContentProvider(new ArrayContentProvider());
		lv.setInput(movies);
		
	 
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}		

}
