package com.androauth.oauth;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import android.util.Log;

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

	private String requestUrl;
	private Map<String, String> queryParams;
	private Map<String, String> headersMap;
	private String token;
	private Token oAuth1token;
	private OAuth10Service oAuthService;
	private static final String AUTHORIZATION = "Authorization";
	private static final String BEARER = "Bearer ";
	private static final String POST = "POST";
	private static final String GET = "GET";

	/**
	 * An interface for notifying the caller when the hoot request completes
	 */
	public interface OnRequestCompleteListener {
		public void onSuccess(HootResult result);
		public void onFailure();
	}
	
	/**
	   * Constructs an OAuthRequest object
	   *
	   * @param requestUrl the url to execute the request on
	   */
	public OAuthRequest(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	   * Sets the queryParams for the request
	   *
	   * @param queryParams the queryParams for either a Post or Get
	   */
	public void setParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}

	/**
	   * Sets the access token used for the request
	   *
	   * @param token the oauth access token used to access protected resources
	   */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	   * Sets additional headers used for the request 
	   *
	   * @param headersMap the headers that will be added to the request
	   */
	public void setHeaders(Map<String, String> headersMap) {
		this.headersMap = headersMap;
	}
	
	public OAuth10Service getoAuthService() {
		return oAuthService;
	}

	public void setoAuthService(OAuth10Service oAuthService) {
		this.oAuthService = oAuthService;
	}
	
	
	
	public Token getoAuth1token() {
		return oAuth1token;
	}

	public void setoAuth1token(Token oAuth1token) {
		this.oAuth1token = oAuth1token;
	}

	/**
	   * Starts a Hoot Get
	   *
	   * @param onRequestCompleteListener an interface for notifying when this hoot request completes
	   */
	public void get(OnRequestCompleteListener onRequestCompleteListener) {
		HootRequest request = execute(onRequestCompleteListener, GET);
		request.get().execute();
	}

	/**
	   * Starts a Hoot Post
	   *
	   * @param onRequestCompleteListener an interface for notifying when this hoot request completes
	   */
	public void post(OnRequestCompleteListener onRequestCompleteListener) {
		HootRequest request = execute(onRequestCompleteListener, POST);
		if(queryParams != null) {
			request.post(queryParams).execute();
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
	private HootRequest execute(final OnRequestCompleteListener onRequestCompleteListener, String method) {
		Hoot hoot = Hoot.createInstanceWithBaseUrl(requestUrl);

		HootRequest request = hoot.createRequest();
		request.setStreamingMode(HootRequest.STREAMING_MODE_FIXED);

		Properties headers = new Properties();
		String authHeader = null;
		if(getoAuthService()==null){
			//OAuth 2.0 call
			authHeader = BEARER + token;
		}else{
			//OAuth 1.0 call
			try {
				authHeader = getoAuthService().signOAuthRequest(getoAuth1token(), requestUrl, method, queryParams);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		headers.setProperty(AUTHORIZATION, authHeader);
		

		if(headersMap != null) {
			for(Map.Entry<String, String> entry : headersMap.entrySet()) {
				headers.setProperty(entry.getKey(), entry.getValue());
			}
		}
		request.setHeaders(headers);
		if(method.equals(GET) && queryParams != null) {
			request.setQueryParameters(queryParams);
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
				Log.v("into","res fail: "+result.getResponseString());
				onRequestCompleteListener.onFailure();
			}

			@Override
			public void onCancelled(HootRequest request) {
			}
		});
		
		return request;
	}
}