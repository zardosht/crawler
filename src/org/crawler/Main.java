package org.crawler;

import org.crawler.controler.GoogleCrawler;
import org.crawler.model.Movie;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length > 0 && args[0].equals("--gui")) {
			openGUI();
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

	private static void openGUI() {
		Display display = new Display ();
		Shell shell = new Shell(display);
	 
		Text helloWorldTest = new Text(shell, SWT.NONE);
		helloWorldTest.setText("Hello World SWT");
		helloWorldTest.pack();
	 
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}		

}
