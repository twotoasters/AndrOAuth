package com.androauth.oauth;


public class OAuth20Request extends OAuthRequest{

	private String token;
	
	/**
	 * Constructs an OAuth20Request
	 * @param accessToken a string containing the access_token
	 */
	public OAuth20Request(String accessToken) {
		token = accessToken;
	}
	
	/**
	 * Builds a valid post request for OAuth20
	 * @param onRequestCompleteListener
	 */
	public void post(OnRequestCompleteListener onRequestCompleteListener) {
		String authHeader = BEARER + token;
		post(onRequestCompleteListener, authHeader);
	}
	
	/**
	 * Builds a valid get request for OAuth20
	 * @param onRequestCompleteListener
	 */
	public void get(OnRequestCompleteListener onRequestCompleteListener){
		String authHeader = BEARER + token;
		get(onRequestCompleteListener, authHeader);
	}

	
	
}
