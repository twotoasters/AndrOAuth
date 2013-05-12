package com.androauth.oauth;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.entity.mime.MultipartEntity;

import com.twotoasters.android.hoot.Hoot;
import com.twotoasters.android.hoot.HootRequest;
import com.twotoasters.android.hoot.HootRequest.HootRequestListener;
import com.twotoasters.android.hoot.HootResult;

/**
 * 
 * OAuth request class 
 * @author pfives
 */
public class OAuthRequest {

	protected String requestUrl;
	private Map<String, String> requestParams;
	private MultipartEntity multipartEntity;
	private Map<String, String> headersMap;
	private static final String AUTHORIZATION = "Authorization";
	private boolean shouldUseDefaultAuthorizationHeader = true;
	protected static final String BEARER = "Bearer ";
	protected static final String POST = "POST";
	protected static final String GET = "GET";
	protected OnRequestCompleteListener onRequestCompleteListener;
	
	/**
	 * An interface for notifying the caller when the hoot request completes
	 */
	public interface OnRequestCompleteListener {
		/**
		 * Returns the result from a successful OAuth request
		 * @param result
		 */
		public void onSuccess(HootResult result);
		/**
		 * Returns a new access token if an OAuth request initially failed
		 * OAuth 2.0 only
		 * @param token
		 */
		public void onNewAccessTokenReceived(OAuth20Token token);
		/**
		 * Callback if OAuth request fails
		 */
		public void onFailure(HootResult result);
	}
	
	/**
	 * Creates a new OAuth1.0 request
	 * @param oAuthRequestUrl the url that the oauth request will be executed on
	 * @param token an OAuth1 token containing an access token user secret
	 * @param service an OAuth1 service instance with consumer secret and consumer key
	 * @param onCompleteListner an interface to notify the caller when the request completes
	 * @return an OAuth1.0 request
	 */
	public static OAuth10Request newInstance(String oAuthRequestUrl, OAuth10Token token, OAuth10Service service, OnRequestCompleteListener onCompleteListener){
		OAuth10Request request = new OAuth10Request(token, service);
		request.setUrlAndListener(oAuthRequestUrl, onCompleteListener);
		return request;
	}
	
	/**
	 * Creates a new OAuth1.0 request
	 * @param oAuthRequestUrl the url that the oauth request will be executed on
	 * @param accessToken a string containing the access token
	 * @param userSecret a string containing the user secret
	 * @param service an OAuth1 service instance with consumer secret and consumer key
	 * @param onCompleteListner an interface to notify the caller when the request completes
	 * @return an OAuth1.0 request
	 */
	public static OAuth10Request newInstance(String oAuthRequestUrl, String accessToken, String userSecret, OAuth10Service service, OnRequestCompleteListener onCompleteListener){
		OAuth10Request request = new OAuth10Request(new OAuth10Token(accessToken, userSecret), service);
		request.setUrlAndListener(oAuthRequestUrl, onCompleteListener);
		return request;
	}
	
	/**
	 * Creates a new OAuth2.0 request
	 * @param oAuthRequestUrl the url that the oauth request will be executed on
	 * @param token an OAuth2 token containing an access token and refresh token if available
	 * @param onCompleteListner an inteface to notify the caller when the request completes
	 * @return an OAuth2.0 request
	 */
	public static OAuth20Request newInstance(String oAuthRequestUrl, OAuth20Token token, OnRequestCompleteListener onCompleteListener){
		OAuth20Request request = new OAuth20Request(token);
		request.setUrlAndListener(oAuthRequestUrl, onCompleteListener);
		return request;
	}
	
	/**
	 * Creates a new OAuth2.0 request
	 * @param oAuthRequestUrl the url that the oauth request will be executed on
	 * @param token an OAuth2 token containing an access token and refresh token if available
	 * @param service an OAuth2 service instance with consumer secret and consumer key (used for refreshing access token)
	 * @param onCompleteListner an interface to notify the caller when the request completes
	 * @return an OAuth2.0 request
	 */
	public static OAuth20Request newInstance(String oAuthRequestUrl, OAuth20Token token, OAuth20Service service, OnRequestCompleteListener onCompleteListener){
		OAuth20Request request = new OAuth20Request(token, service);
		request.setUrlAndListener(oAuthRequestUrl, onCompleteListener);
		return request;
	}
	
	/**
	 * Creates a new OAuth2.0 request
	 * @param oAuthRequestUrl the url that the oauth request will be executed on
	 * @param accessToken a string containing an access token
	 * @param refreshToken a string containing a refresh token
	 * @param service an OAuth2 service instance with consumer secret and consumer key (used for refreshing access token)
	 * @param onCompleteListner an interface to notify the caller when the request completes
	 * @return an OAuth2.0 request
	 */
	public static OAuth20Request newInstance(String oAuthRequestUrl, String accessToken, String refreshToken, OAuth20Service service, OnRequestCompleteListener onCompleteListener){
		OAuth20Request request = new OAuth20Request(new OAuth20Token(accessToken, refreshToken), service); 
		request.setUrlAndListener(oAuthRequestUrl, onCompleteListener);
		return request;
	}
	
	/**
	 * Creates a new OAuth2.0 request
	 * @param oAuthRequestUrl the url that the oauth request will be executed on
	 * @param accessToken a string containing an access token
	 * @param onCompleteListner an interface to notify the caller when the request completes
	 * @return an OAuth2.0 request
	 */
	public static OAuth20Request newInstance(String oAuthRequestUrl, String accessToken, OnRequestCompleteListener onCompleteListener){
		OAuth20Request request = new OAuth20Request(new OAuth20Token(accessToken));
		request.setUrlAndListener(oAuthRequestUrl, onCompleteListener);
		return request;
	}
	
	protected void setUrlAndListener(String url, OnRequestCompleteListener onCompleteListener){
		requestUrl = url;
		onRequestCompleteListener = onCompleteListener;
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
	 * 
	 */
	public Map<String,String> getPercentEncodedRequestParams(){
		Map<String,String> percentEncodedParams = new HashMap<String,String>();
		if(getRequestParams() != null && !getRequestParams().isEmpty()){
			for (Map.Entry<String, String> entry : getRequestParams().entrySet())
			{
				percentEncodedParams.put(entry.getKey(), OAuthUtils.percentEncode(entry.getValue()));
			}
		}
		return percentEncodedParams;
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
	 * Gets MultipartEntity for a post 
	 * @return previously set MultipartEntity
	 */
	public MultipartEntity getMultipartEntity() {
		return multipartEntity;
	}
	
	/**
	 * Sets the MultipartEntity for a post
	 * @param multipartEntity
	 */
	public void setMultipartEntity(MultipartEntity multipartEntity) {
		this.multipartEntity = multipartEntity;
	}
	
	public boolean isShouldUseDefaultAuthorizationHeader() {
		return shouldUseDefaultAuthorizationHeader;
	}

	/**
	 * Sets whether to use the default Authorization header for access to protected resources
	 * Default is true, set to false if the API follows different authorization process
	 * @param shouldUseDefaultAuthorizationHeader
	 */
	public void setShouldUseDefaultAuthorizationHeader(boolean shouldUseDefaultAuthorizationHeader) {
		this.shouldUseDefaultAuthorizationHeader = shouldUseDefaultAuthorizationHeader;
	}
	
	/**
	 * Attempts to refresh the access token if a request failed OAuth 2.0 only
	 * @param method post or get
	 * @param result why the request failed
	 */
	public void refreshAccessToken(String method, HootResult result){
		onRequestCompleteListener.onFailure(result);
	}

	/**
	   * Starts a Hoot Get
	   *
	   * @param authHeader a valid authorization header for OAuth1.0 or 2.0 
	   */
	protected void get(String authHeader) {
		HootRequest request = execute(GET, authHeader);
		request.get().execute();
	}

	/**
	   * Starts a Hoot Post
	   *
	   * @param authHeader a valid authorization header for OAuth1.0 or 2.0 
	   */
	protected void post(String authHeader) {
		HootRequest request = execute(POST, authHeader);
		
		if(getMultipartEntity() != null){
			request.post(getMultipartEntity()).execute();
		} else if(getPercentEncodedRequestParams() != null) {
			request.post(getPercentEncodedRequestParams()).execute();
		} else {
			request.post().execute();
		}
	}

	/**
	 * Constructs a HootRequest object for OAuth calls
	 * Sets Bearer header and other headers,queryparameters set by OAuthRequest
	 * @param method post or get
	 * @param authHeader a valid authorization header for OAuth1.0 or 2.0
	 * @return a hoot request instance
	 */
	private HootRequest execute(final String method, String authHeader) {
		Hoot hoot = Hoot.createInstanceWithBaseUrl(requestUrl);
		HootRequest request = hoot.createRequest();
		request.setStreamingMode(HootRequest.STREAMING_MODE_FIXED);

		Properties headers = new Properties();
		if(shouldUseDefaultAuthorizationHeader){
			headers.setProperty(AUTHORIZATION, authHeader);
		}
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
				
				refreshAccessToken(method, result);
			}

			@Override
			public void onCancelled(HootRequest request) {
			}
		});
		
		return request;
	}
	
	private String extractErrorResponse(String response){
		return OAuthUtils.extract(response, OAuthService.ERROR_JSON_REGEX);
	}
}