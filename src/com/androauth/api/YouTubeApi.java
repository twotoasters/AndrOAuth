package com.androauth.api;

public class YouTubeApi implements OAuth20Api {

	private static final String AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/auth";
	private static final String REQUEST_TOKEN_RESOURCE = "https://accounts.google.com/o/oauth2/token";
	private static final String ACCESS_TOKEN_RESOURCE = "https://accounts.google.com/o/oauth2/token";
	
	@Override
	public String getAuthorizeUrl() {
		return AUTHORIZE_URL;
	}

	@Override
	public String getAccessTokenResource() {
		return ACCESS_TOKEN_RESOURCE;
	}

}
