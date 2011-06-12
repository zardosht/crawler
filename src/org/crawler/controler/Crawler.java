package org.crawler.controler;

import java.util.logging.Logger;

import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Crawler {

	private static Logger logger = Logger.getLogger(Crawler.class.getPackage().getName());
	
	public static long TIMEOUT = 3000; //1 second
	
	private long lastCrawl = 0;

	private DefaultHttpClient client;
	
	public Crawler(String baseUrl) {
		client = new DefaultHttpClient();
		intRobotsTxtFilter(baseUrl);
	}

	private void intRobotsTxtFilter(String baseUrl) {
	}

	protected void timeOut() {
		long timeDifference = System.currentTimeMillis()-lastCrawl;
		if(timeDifference < TIMEOUT) {
			try {
				Thread.sleep(TIMEOUT-timeDifference);
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
		
		Source source = new Source(response.getEntity().getContent());
		source.setLogger(null);
		return source;
	}
	
}
