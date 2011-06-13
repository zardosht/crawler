package org.crawler.controler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Crawler {

	private static Logger logger = Logger.getLogger(Crawler.class.getPackage()
			.getName());

	public long TIMEOUT = 1000; // 1 second

	private long lastCrawl = 0;

	private DefaultHttpClient client;

	protected ArrayList<String> disallow;

	private int totalSites;

	private int relevantSites;

	protected final String baseUrl;

	public Crawler(String baseUrl) throws Exception {
		this.baseUrl = baseUrl;
		client = new DefaultHttpClient();
		intRobotsTxtFilter(baseUrl);
		totalSites = 0;
		relevantSites = 0;
	}

	private void intRobotsTxtFilter(String baseUrl) throws Exception {
		disallow = new ArrayList<String>();
		HttpGet get = new HttpGet(baseUrl + "/robots.txt");
		HttpResponse response = getClient().execute(get);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		String line = "";
		boolean concerningUs = false;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("#")) {
				continue;
			}
			String[] split = line.split(":");
			if (split.length == 2) {
				if (split[0].trim().equalsIgnoreCase("user-agent")) {
					concerningUs = split[1].trim().equals("*") ? true : false;
				}
				if (!concerningUs)
					continue;
				if (split[0].trim().equalsIgnoreCase("disallow")) {
					disallow.add(split[1].trim());
				}
			}

		}
	}

	protected void timeOut() {
		long timeDifference = System.currentTimeMillis() - lastCrawl;
		if (timeDifference < TIMEOUT) {
			try {
				Thread.sleep(TIMEOUT - timeDifference);
			} catch (InterruptedException e) {
			}
		}
		lastCrawl = System.currentTimeMillis();
	}

	public DefaultHttpClient getClient() {
		return client;
	}

	protected Source readSite(String url) throws Exception {
		timeOut();
		logger.info("Retrieving: " + url);
		HttpGet get = new HttpGet(url);
		HttpResponse response = getClient().execute(get);
		System.out.println(response);
		logger.info("Response: " + response);

		totalSites++;

		Source source = new Source(response.getEntity().getContent());
		source.setLogger(null);
		return source;
	}

	protected ArrayList<String> findKeyWords(Source keyWordSite) {
		ArrayList<String> result = new ArrayList<String>();
		for (Element b : keyWordSite.getAllElementsByClass("keyword")) {
			Element anker = b.getFirstElement();
			if (anker != null) {
				result.add(anker.getTextExtractor().toString());
			}
		}
		if (result.size() > 0)
			relevantSites++;
		return result;
	}

	protected boolean allowed(String value) {
		for (String disallowed : disallow) {
			if (value.startsWith(baseUrl + disallowed)
					|| value.startsWith(disallowed)) {
				return false;
			}
		}
		return true;
	}

	public int getVisited() {
		return totalSites;
	}

	public int getVisitedRelevant() {
		return relevantSites;
	}
}
