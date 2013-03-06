package com.androauth.oauth;

/**
 * An object to hold an OAuth20 access token and refresh token
 */
public class OAuth20Token {
	
	private String accessToken;
	private String refreshToken;
	
	
	public OAuth20Token(String accessToken, String refreshToken){
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
	/**
	 * Get oauth access token
	 * @return accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Set oauth access token
	 * @param accessToken
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	/**
	 * Get oauth refresh token
	 * @return refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	
	/**
	 * Set oauth refresh token
	 * @param refreshToken
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}