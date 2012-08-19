package com.mntripclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class HTTPTask implements Runnable {

	private static String MOUNTPOINT = "http://mntripcaster.appspot.com/mntripstr0";

	private static String basicAuthString = "bU5UUklQVXNlcjptTlRSSVBQYXNzd29yZA==";

	private AndroidHttpClient httpClient;
	private URI uri;

	private HttpGet request;
	private HttpResponse response;

	private byte[] content;

	private ArrayBlockingQueue<byte[]> httpBluetoothQueue;

	private Timer timer;

	public HTTPTask(ArrayBlockingQueue<byte[]> httpBluetoothQueue) {
		this.httpBluetoothQueue = httpBluetoothQueue;
		
		httpClient = AndroidHttpClient.newInstance("mNTRIPClient");
				
		content = new byte[1000];

		try {
			uri = new URI(MOUNTPOINT);
		} catch (URISyntaxException e) {
			Log.e("mNTRIPClient", e.getMessage());
		}

		request = new HttpGet();
		request.setURI(uri);
		request.addHeader("Authorization", "Basic " + basicAuthString);
		request.addHeader("Ntrip-Version", "Ntrip/2.0");
		request.addHeader("Connection", "Close");
		
		new Thread(new RequestTask()).start();		
	}

	public void run() {
		
	}

	private class RequestTask extends TimerTask {

		public void run() {
			while (true) {
				try {
					if (httpBluetoothQueue.remainingCapacity() > 0) {
						response = httpClient.execute(request);
						content = EntityUtils.toByteArray(response.getEntity());						
						httpBluetoothQueue.add(content);
					}					
					Thread.sleep(800);
				} catch (Exception e) {
					Log.e("mNTRIPClient", e.getMessage());
				}
			}
		}

	}

}
