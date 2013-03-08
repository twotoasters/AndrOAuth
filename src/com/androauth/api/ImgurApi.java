package com.androauth.api;

/**
 * A class to use OAuth on the Imgur.com api
 * @author pfives
 *
 */
public class ImgurApi implements OAuth20Api {
	
	private static final String AUTHORIZE_URL = "https://api.imgur.com/oauth2/authorize";
	private static final String ACCESS_TOKEN_RESOURCE = "https://api.imgur.com/oauth2/token";

	@Override
	public String getAuthorizeUrl() {
		return AUTHORIZE_URL;
	}

	@Override
	public String getAccessTokenResource() {
		return ACCESS_TOKEN_RESOURCE;
	}

}
