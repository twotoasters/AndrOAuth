package com.androauth.api;

public class WordpressApi implements OAuth20Api {
    
    private static final String AUTHORIZE_URL = "https://public-api.wordpress.com/oauth2/authorize";
    private static final String ACCESS_TOKEN_RESOURCE = "https://public-api.wordpress.com/oauth2/token";

    @Override
    public String getAuthorizeUrl() {
        return AUTHORIZE_URL;
    }

    @Override
    public String getAccessTokenResource() {
        return ACCESS_TOKEN_RESOURCE;
    }
}
