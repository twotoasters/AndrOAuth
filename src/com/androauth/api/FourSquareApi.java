package com.androauth.api;

public class FourSquareApi implements OAuth20Api {

	private static final String AUTHORIZE_URL = "https://foursquare.com/oauth2/authorize";
	private static final String ACCESS_TOKEN_RESOURCE = "https://foursquare.com/oauth2/access_token";
	
	@Override
	public String getAuthorizeUrl() {
		return AUTHORIZE_URL;
	}

	@Override
	public String getAccessTokenResource() {
		return ACCESS_TOKEN_RESOURCE;
	}

}
