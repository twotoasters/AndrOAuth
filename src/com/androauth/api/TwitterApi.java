package com.androauth.api;

import com.androauth.oauth.OAuthUtils.HttpMethod;

/**
 * A class to use OAuth on the Twitter.com api
 * @author pfives
 *
 */
public class TwitterApi implements OAuth10Api{

	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	private static final String REQUEST_TOKEN_RESOURCE = "https://api.twitter.com/oauth/request_token";
	private static final String ACCESS_TOKEN_RESOURCE = "https://api.twitter.com/oauth/access_token";
	private static final String OAUTH_VERSION = "1.0";
	
	@Override
	public String getAuthorizeUrl() {
		return AUTHORIZE_URL;
	}
	@Override
	public String getRequestTokenResource() {
		return REQUEST_TOKEN_RESOURCE;
	}
	@Override
	public String getAccessTokenResource() {
		return ACCESS_TOKEN_RESOURCE;
	}
	@Override
	public String getOauthVersion() {
		return OAUTH_VERSION;
	}
	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

}
