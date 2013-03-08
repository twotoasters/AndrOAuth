package com.androauth.api;

/**
 * An interface for apis that conforms to the OAuth 2.0 spec
 * @author pfives
 *
 */
public interface OAuth20Api {
	
	/**
	 * Gets the authorize url with appended query parameters
	 * @return the authorize url
	 */
	public String getAuthorizeUrl();
	
	/**
	 * Gets the access token url
	 * @return the access token url
	 */
	public String getAccessTokenResource();
	
	
}
