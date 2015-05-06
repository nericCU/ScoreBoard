package com.apigee.sdk.apm.android;

import android.net.http.AndroidHttpClient;

import com.apigee.sdk.AppIdentification;
import com.apigee.sdk.apm.http.impl.client.cache.CachingHttpClient;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Properties;

/**
 * @y.exclude
 */
public class HttpClientWrapper implements HttpClient {

	public static final String ATTR_DELEGATE_EXCEPTION          = "delegate_exception";
	public static final String ATTR_DELEGATE_EXCEPTION_OCCURRED = "delegate_exception_occurred";
	public static final String ATTR_OVERRIDDEN_RESPONSE         = "overridden_response";
	public static final String ATTR_SKIP_PROCESSING             = "skip_processing";
	
	
	private HttpClient delgatedHttpClientImpl;
	private AppIdentification appIdentification;
	private NetworkMetricsCollectorService metricsCollector;
	private ApigeeActiveSettings activeSettings;

	BasicHttpProcessor httpproc = new BasicHttpProcessor();
	CachingHttpClient cachingClient;

	public CachingHttpClient getCachingClient() {
		return cachingClient;
	}
	
	// Added constructor to make this look closer to Apache's HttpClient constructors
	public HttpClientWrapper(AppIdentification appIdentification,
			NetworkMetricsCollectorService metricsCollector,
            ApigeeActiveSettings activeSettings)
	{
		
		HttpClient delegateClient = AndroidHttpClient.newInstance(appIdentification.getApplicationId());
		initialize(appIdentification, metricsCollector, activeSettings,
				delegateClient);
	}

	public HttpClientWrapper(HttpClient delegateClient,
			AppIdentification appIdentification,
			NetworkMetricsCollectorService metricsCollector,
			ApigeeActiveSettings activeSettings) {
		initialize(appIdentification, metricsCollector, activeSettings,
				delegateClient);
	}

	/**
	 * 
	 * 
	 * @return HttpProcessor with WebManager specific clients
	 */
	protected BasicHttpProcessor createHttpProcessor() {
		BasicHttpProcessor httpproc = new BasicHttpProcessor();

		/**
		 * In this section, add interceptors
		 */

		PerformanceMonitoringInterceptor performanceMonitoringInterceptor = new PerformanceMonitoringInterceptor();

		performanceMonitoringInterceptor
				.setMetricsCollector(this.metricsCollector);

		httpproc.addInterceptor((HttpRequestInterceptor) performanceMonitoringInterceptor);

		httpproc.addInterceptor((HttpResponseInterceptor) performanceMonitoringInterceptor);

		return httpproc;
	}
	
	protected void initialize(AppIdentification appIdentification,
			NetworkMetricsCollectorService metricsCollector,
			ApigeeActiveSettings activeSettings,
			HttpClient delegateClient) {
		this.appIdentification = appIdentification;
		this.metricsCollector = metricsCollector;
		this.activeSettings = activeSettings;

		// Initialize the default http client
		// TODO: Need to read the props for default client connection parameter
		delgatedHttpClientImpl = delegateClient;

		if (activeSettings.getCachingEnabled()) {
			cachingClient = new CachingHttpClient(delegateClient,activeSettings.getCacheConfig());
			delgatedHttpClientImpl = cachingClient;
		}

		httpproc = createHttpProcessor();
	}

	public ApigeeActiveSettings getActiveSettings() {
		return activeSettings;
	}

	public NetworkMetricsCollectorService getMetricsCollector() {
		return metricsCollector;
	}

	public String getAppId() {
		return appIdentification.getApplicationId();
	}
	
	protected void captureRequest(HttpUriRequest request, HttpContext context) throws IOException {
		try {
			httpproc.process(request, context);
		} catch (HttpException e) {
			throw new ClientProtocolException(e);
		}
	}

	protected void captureRequest(HttpRequest request, HttpContext context) throws IOException {
		try {
			httpproc.process(request, context);
		} catch (HttpException e) {
			throw new ClientProtocolException(e);
		}
	}

	protected void captureResponse(HttpResponse response, HttpContext context) throws IOException {
		try {
			httpproc.process(response, context);
		} catch (HttpException e) {
			throw new ClientProtocolException(e);
		}
	}

	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException,
			ClientProtocolException {
		return execute(request, new BasicHttpContext());
	}

	@Override
	public HttpResponse execute(HttpUriRequest request, HttpContext context)
			throws IOException, ClientProtocolException {
		//TODO: should we be using the startTime?
		//long startTime = System.currentTimeMillis();
		
		boolean errorOccurred = false;

		// Pre-Process requests
		captureRequest(request, context);

		HttpResponse response = null;
		try {

			if ((context != null) && (context.getAttribute(ATTR_SKIP_PROCESSING) != null)
					&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
				response = (HttpResponse) context
						.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			} else {
				response = delgatedHttpClientImpl.execute(request);
			}

		} catch (ClientProtocolException e) {
			errorOccurred = true;
			if (context != null) {
				context.setAttribute(ATTR_DELEGATE_EXCEPTION_OCCURRED, errorOccurred);
				context.setAttribute(ATTR_DELEGATE_EXCEPTION, e);
			}
			throw e;
		} catch (IOException e) {
			errorOccurred = true;
			if (context != null) {
				context.setAttribute(ATTR_DELEGATE_EXCEPTION_OCCURRED, errorOccurred);
				context.setAttribute(ATTR_DELEGATE_EXCEPTION, e);
			}
			throw e;
		} finally {
			captureResponse(response, context);
		}

		return response;
	}

	@Override
	public <T> T execute(final HttpUriRequest request,
			final ResponseHandler<? extends T> responseHandler) throws IOException,
			ClientProtocolException {
		final HttpContext context = new BasicHttpContext();
		return execute(request, responseHandler, context);
	}

	@Override
	public <T> T execute(HttpUriRequest request,
			final ResponseHandler<? extends T> responseHandler, final HttpContext context)
			throws IOException, ClientProtocolException {
		// Pre-Process requests

		ResponseHandler<? extends T> wrappedHandler = new ResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse theResponse)
					throws ClientProtocolException, IOException {
				captureResponse(theResponse, context);
				return responseHandler.handleResponse(theResponse);
			}
		};

		captureRequest(request, context);

		HttpResponse response;
		if ((context != null) && (context.getAttribute(ATTR_SKIP_PROCESSING) != null)
				&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
			response = (HttpResponse) context
					.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			T result;
			result = wrappedHandler.handleResponse(response);
			return result;
		} else {
			// TODO: Need to add proper error handling. Really need to put in
			// the correct wrapper
			T result = delgatedHttpClientImpl.execute(request, wrappedHandler);
			return result;
		}
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request)
			throws IOException, ClientProtocolException {
		HttpContext context = null;
		return execute(target, request, context);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request,
			HttpContext context) throws IOException, ClientProtocolException {
		
		boolean errorOccurred = false;
		
		if (context == null) {
			context = new BasicHttpContext();
		}

		// Pre-Process requests
		captureRequest(request, context);

		HttpResponse response = null;
		try {
			if ((context.getAttribute(ATTR_SKIP_PROCESSING) != null)
					&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
				response = (HttpResponse) context
						.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			} else {
				response = delgatedHttpClientImpl.execute(target, request, context);
			}

		} catch (ClientProtocolException e) {
			errorOccurred = true;
			context.setAttribute(ATTR_DELEGATE_EXCEPTION_OCCURRED, errorOccurred);
			context.setAttribute(ATTR_DELEGATE_EXCEPTION, e);
			throw e;
		} catch (IOException e) {
			errorOccurred = true;
			context.setAttribute(ATTR_DELEGATE_EXCEPTION_OCCURRED, errorOccurred);
			context.setAttribute(ATTR_DELEGATE_EXCEPTION, e);
			throw e;
		} finally {
			captureResponse(response, context);
		}

		return response;
	}


	@Override
	public <T> T execute(HttpHost target, HttpRequest request,
			ResponseHandler<? extends T> responseHandler) throws IOException,
			ClientProtocolException {
		HttpContext context = new BasicHttpContext();
		return execute(target, request, responseHandler, context);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request,
			final ResponseHandler<? extends T> responseHandler, final HttpContext context)
			throws IOException, ClientProtocolException {
		ResponseHandler<? extends T> wrappedHandler = new ResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse theResponse)
					throws ClientProtocolException, IOException {
				captureResponse(theResponse, context);
				return responseHandler.handleResponse(theResponse);
			}
		};

		captureRequest(request, context);

		HttpResponse response;
		if ((context != null) && (context.getAttribute(ATTR_SKIP_PROCESSING) != null)
				&& (Boolean) context.getAttribute(ATTR_SKIP_PROCESSING)) {
			response = (HttpResponse) context
					.getAttribute(ATTR_OVERRIDDEN_RESPONSE);
			T result;
			result = wrappedHandler.handleResponse(response);
			return result;
		} else {
			// TODO: Need to add proper error handling. Really need to put in
			// the correct wrapper
			T result = delgatedHttpClientImpl.execute(target, request, wrappedHandler, context);
			return result;
		}
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		return delgatedHttpClientImpl.getConnectionManager();
	}

	@Override
	public HttpParams getParams() {
		return delgatedHttpClientImpl.getParams();
	}

	/**
	 * @return the delgatedHttpClientImpl
	 */
	public HttpClient getDelgatedHttpClientImpl() {
		return delgatedHttpClientImpl;
	}

	/**
	 * Still in progress
	 */
	/**
	 * 
	 * This function basically kills the existing HTTP Client and initializes
	 * the HTTP Client with a new set of parameters.
	 * 
	 * 
	 * 
	 * 
	 * @param properties
	 *            -
	 */
	public void initializeHttpClient(Properties properties) {

		// Shutdown existing httpClient
		ClientConnectionManager connectionManager = this.delgatedHttpClientImpl
				.getConnectionManager();

		connectionManager.shutdown();
	}

}
