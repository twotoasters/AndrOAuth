package com.androauth.api;

/**
 * An interface for apis that conform to the OAuth 1.0 spec
 * @author pfives
 * 
 */

public interface OAuth10Api {

	/**
	 * Gets the authorization url
	 * @return the authorize url
	 */
	public String getAuthorizeUrl();
	
	/**
	 * Gets the request token url
	 * @return the request token url
	 */
	public String getRequestTokenResource();
	
	/**
	 * Gets the access token url
	 * @return the access token url
	 */
	public String getAccessTokenResource();
	
	/**
	 * Gets the OAuth version
	 * @return the OAuth version
	 */
	public String getOauthVersion();
	
}
