package com.androauth.api;

/**
 * A class to use OAuth on the Reddit.com api
 * @author pfives
 *
 */
public class RedditApi implements OAuth20Api {
	private static final String AUTHORIZE_URL = "https://ssl.reddit.com/api/v1/authorize";
	private static final String ACCESS_TOKEN_RESOURCE = "https://ssl.reddit.com/api/v1/access_token";
	
	@Override
	public String getAuthorizeUrl() {
		return AUTHORIZE_URL;
	}

	@Override
	public String getAccessTokenResource() {
		return ACCESS_TOKEN_RESOURCE;
	}

}
