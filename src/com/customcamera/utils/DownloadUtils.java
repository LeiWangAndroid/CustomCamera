package com.customcamera.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class DownloadUtils {
	
	/**
	 * Get InputStream From HttpClient
	 */
	public static InputStream GetInputStreamFromHttpClient(String strURL, DefaultHttpClient httpClient) {
		InputStream inputStream = null;
		try {			
	        HttpGet request = new HttpGet(strURL);
	        HttpResponse response = httpClient.execute(request);
	        
	        int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					inputStream = entity.getContent(); 				
				}		        	
			}	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return inputStream;
	}	
	
	/**
	 * Get InputStream From URLConnection
	 */
	public static InputStream GetInputStreamFromURLConnection(String strURL) {
		InputStream inputStream = null;
		try {
			URL mUrl = new URL(strURL);
			URLConnection c = mUrl.openConnection();
			c.setConnectTimeout(5000);
			c.setReadTimeout(20000);
			inputStream = c.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}	

	/**
	 * Convert from Stream to String
	 */
	public static String ConvertStreamToString(InputStream is) throws UnsupportedEncodingException, IOException {
		
	    BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	    StringBuilder sb = new StringBuilder();
	    String line = null;

	    while ((line = reader.readLine()) != null) {
	        sb.append(line); // sb.append(line + "\n");
	    }
	    is.close();
	    return sb.toString();
	}

}
