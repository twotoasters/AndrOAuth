package com.androauth.oauth;

/**
 * An OAuth1.0 request class
 * @author pfives
 *
 */
public class OAuth10Request extends OAuthRequest{

	private OAuth10Token token;
	private OAuth10Service service;
	
	/**
	 * Constructs a new OAuth10Request
	 * @param accessToken an object containing an access_token and user_secret
	 * @param oAuthService an instance of OAuth10Service that has the apikey and apisecret set
	 */
	public OAuth10Request(OAuth10Token accessToken, OAuth10Service oAuthService) {
		token = accessToken;
		service = oAuthService;
	}
	
	/**
	 * Builds a valid post request for OAuth10 
	 * @param onRequestCompleteListener
	 */
	public void post() {
		post(getAuthHeader(POST));
	}
	
	/**
	 * Builds a valid get request for OAuth10
	 * @param onRequestCompleteListener
	 */
	public void get(){
		get(getAuthHeader(GET));
	}
	
	/**
	 * Gets a valid OAuth 1.0 header
	 * @param method (post or get)
	 * @return valid header
	 */
	private String getAuthHeader(String method){
		return service.signOAuthRequest(token, requestUrl, method, getRequestParams());
	}

}