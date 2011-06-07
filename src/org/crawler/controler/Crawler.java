package org.crawler.controler;

import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Crawler {

	
	public static long TIMEOUT = 500; //1 second
	
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
		
		HttpGet get = new HttpGet(url);
		HttpResponse response = getClient().execute(get);
		System.out.println(response);
		
		Source source = new Source(response.getEntity().getContent());
		source.setLogger(null);
		return source;
	}
	
}
