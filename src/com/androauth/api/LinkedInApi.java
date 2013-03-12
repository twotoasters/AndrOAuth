package com.androauth.api;

public class LinkedInApi implements OAuth20Api{

	private static final String AUTHORIZE_URL = "https://www.linkedin.com/uas/oauth2/authorization";
	private static final String ACCESS_TOKEN_RESOURCE = "https://api.linkedin.com/uas/oauth2/accessToken";
	
	@Override
	public String getAuthorizeUrl() {
		return AUTHORIZE_URL;
	}

	@Override
	public String getAccessTokenResource() {
		return ACCESS_TOKEN_RESOURCE;
	}

}
