package com.androauth.oauth;

/**
 * An object to hold the user token and user secret
 * @author pfives
 */
public class Token {

	private String access_token = null;
	private String user_secret = null;
	
	/**
	   * An empty token constructor
	   *
	   */
	public Token(){
	}
	
	/**
	   * A token constructor that takes an access token and user secret
	   *
	   * @param access_token an api access token
	   * @param user_secret an api user secret
	   */
	public Token(String access_token, String user_secret){
		this.access_token = access_token;
		this.user_secret = user_secret;
	}
	
	/**
	   * Gets the access token for the api
	   *
	   * @return the access token  
	   */
	public String getAccess_token() {
		return access_token;
	}
	/**
	   * Gets the user secret
	   *
	   * @return the user secret
	   */
	public String getUser_secret() {
		return user_secret;
	}

	/**
	   * Sets the access token
	   *
	   * @param access_token the access token returned by the api
	   */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	/**
	   * Sets the user secret
	   *
	   * @param user_secret the user secret returned by the api
	   */
	public void setUser_secret(String user_secret) {
		this.user_secret = user_secret;
	}
		
}
