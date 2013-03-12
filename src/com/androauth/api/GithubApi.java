package com.androauth.api;

public class GithubApi implements OAuth20Api{

	private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";
	private static final String ACCESS_TOKEN_RESOURCE = "https://github.com/login/oauth/access_token";
	
	@Override
	public String getAuthorizeUrl() {
		return AUTHORIZE_URL;
	}

	@Override
	public String getAccessTokenResource() {
		return ACCESS_TOKEN_RESOURCE;
	}

}
