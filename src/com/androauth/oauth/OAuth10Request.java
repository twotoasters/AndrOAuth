package com.androauth.oauth;

import java.io.UnsupportedEncodingException;


public class OAuth10Request extends OAuthRequest{

	private Token token;
	private OAuth10Service service;
	
	/**
	 * Constructs a new OAuth10Request
	 * @param accessToken an object containing an access_token and user_secret
	 * @param oAuthService an instance of OAuth10Service that has the apikey and apisecret set
	 */
	public OAuth10Request(Token accessToken, OAuth10Service oAuthService) {
		token = accessToken;
		service = oAuthService;
	}
	
	/**
	 * Builds a valid post request for OAuth10 
	 * @param onRequestCompleteListener
	 */
	public void post(OnRequestCompleteListener onRequestCompleteListener) {
		String authHeader = null;
		try {
			authHeader = service.signOAuthRequest(token, requestUrl, POST, getRequestParams());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post(onRequestCompleteListener, authHeader);
	}
	
	/**
	 * Builds a valid get request for OAuth10
	 * @param onRequestCompleteListener
	 */
	public void get(OnRequestCompleteListener onRequestCompleteListener){
		String authHeader = null;
		try {
			authHeader = service.signOAuthRequest(token, requestUrl, GET, getRequestParams());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		get(onRequestCompleteListener, authHeader);
	}

}
