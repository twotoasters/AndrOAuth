package com.androauth.api;

import com.androauth.oauth.OAuthUtils.HttpMethod;

public class TumblrApi implements OAuth10Api{

	private static final String AUTHORIZE_URL = "http://www.tumblr.com/oauth/authorize";
	private static final String REQUEST_TOKEN_RESOURCE = "http://www.tumblr.com/oauth/request_token";
	private static final String ACCESS_TOKEN_RESOURCE = "http://www.tumblr.com/oauth/access_token";
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
