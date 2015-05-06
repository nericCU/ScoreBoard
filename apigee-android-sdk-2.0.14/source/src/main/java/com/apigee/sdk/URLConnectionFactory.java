package com.apigee.sdk;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;

/**
 * @y.exclude
 */
public interface URLConnectionFactory {
	public URLConnection openConnection(String urlAsString) throws MalformedURLException, IOException;
}
