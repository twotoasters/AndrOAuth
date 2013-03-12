package com.androauth.api;

public class InstagramApi implements OAuth20Api{
	
	public static final String AUTHORIZE_URL = "https://api.instagram.com/oauth/authorize";
	public static final String ACCESS_TOKEN_RESOURCE = "https://api.instagram.com/oauth/access_token";
	
	@Override
	public String getAuthorizeUrl() {
		return AUTHORIZE_URL;
	}

	@Override
	public String getAccessTokenResource() {
		return ACCESS_TOKEN_RESOURCE;
	}

}
