package com.apigee.sdk.apm.android;

/**
 * Convenience methods for configuration functionality in Apigee App Monitoring.
 *
 * @see <a href="http://apigee.com/docs/app-services/content/app-monitoring">App Monitoring documentation</a>
 */
public class AppMonConfig {

	/**
	 * Retrieves the value for the specified category and key
	 * @param category the category for parameter whose value is requested
	 * @param key the key for the parameter whose value is requested
	 * @return the value for the specified category and key or null on error
	 */
	public static String getValue(String category, String key)
	{
		String value = null;
		ApigeeMonitoringClient client = ApigeeMonitoringClient.getInstance();
		if( null != client && AppMon.isInitialized() ) {
			ApigeeActiveSettings activeSettings = client.getActiveSettings();
			if (null != activeSettings ) {
				value = activeSettings.getAppConfigCustomParameter(category, key);
			}
		}
		
		return value;
	}
}
