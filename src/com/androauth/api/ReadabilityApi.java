package com.androauth.api;

public class ReadabilityApi implements OAuth10Api {

    private static final String AUTHORIZE_URL = "https://www.readability.com/api/rest/v1/oauth/authorize";
    private static final String REQUEST_TOKEN_RESOURCE = "https://www.readability.com/api/rest/v1/oauth/request_token";
    private static final String ACCESS_TOKEN_RESOURCE = "https://www.readability.com/api/rest/v1/oauth/access_token";
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
}
