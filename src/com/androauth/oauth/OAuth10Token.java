package com.androauth.oauth;

/**
 * An object to hold the OAuth1 user token and user secret
 * @author pfives
 */
public class OAuth10Token {

	private String accessToken = null;
	private String userSecret = null;
	
	/**
	   * An empty token constructor
	   *
	   */
	public OAuth10Token(){
	}
	
	/**
	   * A token constructor that takes an access token and user secret
	   *
	   * @param access_token an api access token
	   * @param user_secret an api user secret
	   */
	public OAuth10Token(String accessToken, String userSecret){
		this.accessToken = accessToken;
		this.userSecret = userSecret;
	}
	
	/**
	   * Gets the access token for the api
	   *
	   * @return the access token  
	   */
	public String getAccessToken() {
		return accessToken;
	}
	/**
	   * Gets the user secret
	   *
	   * @return the user secret
	   */
	public String getUserSecret() {
		return userSecret;
	}

	/**
	   * Sets the access token
	   *
	   * @param access_token the access token returned by the api
	   */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	   * Sets the user secret
	   *
	   * @param user_secret the user secret returned by the api
	   */
	public void setUserSecret(String userSecret) {
		this.userSecret = userSecret;
	}
		
}
