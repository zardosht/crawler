package org.crawler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

public class CrawlerGui {
	
	private ListViewer lvMovies;

	public void openGUI(final List<Movie> googleMovies, List<Movie> imdbMovies) {
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
		cmbCrawler.select(0);
		cmbCrawler.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = ((Combo)e.widget).getSelectionIndex();
				if(selectionIndex == 0){
					lvMovies.setInput(googleMovies);
				}else if(selectionIndex == 1){
					lvMovies.setInput(googleMovies);
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

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
		
		Label label = new Label(shell, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		

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

		lvMovies = new ListViewer(shell, SWT.BORDER
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
		lvMovies.setInput(googleMovies);

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


}
