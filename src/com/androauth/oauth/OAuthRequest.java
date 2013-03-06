package com.androauth.oauth;

import java.util.Map;
import java.util.Properties;

import com.twotoasters.android.hoot.Hoot;
import com.twotoasters.android.hoot.HootRequest;
import com.twotoasters.android.hoot.HootResult;
import com.twotoasters.android.hoot.HootRequest.HootRequestListener;

/**
 * 
 * OAuth request class for apis that conform to the OAuth 2.0 spec
 * @author pfives
 */
public class OAuthRequest {

	protected static String requestUrl;
	private Map<String, String> requestParams;
	private Map<String, String> headersMap;
	private static final String AUTHORIZATION = "Authorization";
	protected static final String BEARER = "Bearer ";
	protected static final String POST = "POST";
	protected static final String GET = "GET";

	/**
	 * An interface for notifying the caller when the hoot request completes
	 */
	public interface OnRequestCompleteListener {
		public void onSuccess(HootResult result);
		public void onFailure();
	}
	
	/**
	 * Creates a new OAuth10Request 
	 * @param oAuthRequestUrl the url that the oauth request will be executed on
	 * @param token an object containing the access_token and user_secret
	 * @param service an instance of OAuth10Service with key and secret set
	 * @return a new OAuth10Request
	 */
	public static OAuth10Request newInstance(String oAuthRequestUrl, OAuth10Token token, OAuth10Service service){
		requestUrl = oAuthRequestUrl;
		return new OAuth10Request(token, service);	
	}
	
	/**
	 * Creates a new OAuth20Request
	 * @param oAuthRequestUrl the url that the oauth request will be executed on
	 * @param token a string containing the access_token
	 * @return a new OAuth20Request
	 */
	public static OAuth20Request newInstance(String oAuthRequestUrl, OAuth20Token token){
		requestUrl = oAuthRequestUrl;
		return new OAuth20Request(token.getAccessToken());
	}

	/**
	   * Sets additional headers used for the request 
	   *
	   * @param headersMap the headers that will be added to the request
	   */
	public void setHeaders(Map<String, String> headersMap) {
		this.headersMap = headersMap;
	}

	/**
	 * Gets request parameters for either post or get
	 * @return request parameters
	 */
	public Map<String, String> getRequestParams() {
		return requestParams;
	}

	/**
	 * Sets request parameters for either post or get
	 * @param requestParams
	 */
	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}

	/**
	   * Starts a Hoot Get
	   *
	   * @param onRequestCompleteListener an interface for notifying when this hoot request completes
	   */
	public void get(OnRequestCompleteListener onRequestCompleteListener, String authHeader) {
		HootRequest request = execute(onRequestCompleteListener, GET, authHeader);
		request.get().execute();
	}

	/**
	   * Starts a Hoot Post
	   *
	   * @param onRequestCompleteListener an interface for notifying when this hoot request completes
	   */
	public void post(OnRequestCompleteListener onRequestCompleteListener, String authHeader) {
		HootRequest request = execute(onRequestCompleteListener, POST, authHeader);
		if(getRequestParams() != null) {
			request.post(getRequestParams()).execute();
		} else {
			request.post().execute();
		}
	}

	/**
	   * Constructs a HootRequest object for OAuth 2.0 calls
	   * Sets Bearer header and other headers,queryparameters set by OAuthRequest
	   *
	   * @param onRequestCompleteListener an interface for notifying when this hoot request completes
	   * @param method a get(1) or post(2)
	   * 
	   * @return a HootRequest instance
	   */
	private HootRequest execute(final OnRequestCompleteListener onRequestCompleteListener, String method, String authHeader) {
		Hoot hoot = Hoot.createInstanceWithBaseUrl(requestUrl);

		HootRequest request = hoot.createRequest();
		request.setStreamingMode(HootRequest.STREAMING_MODE_FIXED);

		Properties headers = new Properties();
		headers.setProperty(AUTHORIZATION, authHeader);
		
		if(headersMap != null) {
			for(Map.Entry<String, String> entry : headersMap.entrySet()) {
				headers.setProperty(entry.getKey(), entry.getValue());
			}
		}
		request.setHeaders(headers);
		if(method.equals(GET) && getRequestParams() != null) {
			request.setQueryParameters(getRequestParams());
		}

		request.bindListener(new HootRequestListener() {

			@Override
			public void onSuccess(HootRequest request, HootResult result) {
				onRequestCompleteListener.onSuccess(result);
			}

			@Override
			public void onRequestStarted(HootRequest request) {
			}

			@Override
			public void onRequestCompleted(HootRequest request) {
			}

			@Override
			public void onFailure(HootRequest request, HootResult result) {
				onRequestCompleteListener.onFailure();
			}

			@Override
			public void onCancelled(HootRequest request) {
			}
		});
		
		return request;
	}
}