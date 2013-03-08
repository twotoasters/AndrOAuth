package com.androauth.oauth;

import com.twotoasters.android.hoot.HootResult;

public class OAuth20Request extends OAuthRequest{

	private OAuth20Token token;
	private OAuth20Service service;
	private boolean attemptedRefreshOnce = false;
	
	public interface OAuthRefreshTokenCallback{
		public void onNewAccessTokenReceived(OAuth20Token newToken);
		public void onFailure(HootResult result);
	}
	
	/**
	 * Constructs an OAuth20Request
	 * @param accessToken a string containing the access_token
	 */
	public OAuth20Request(OAuth20Token token) {
		this.token = token;
	}
	
	public OAuth20Request(OAuth20Token token, OAuth20Service oAuth20Service){
		this.token = token;
		service = oAuth20Service;
	}
	
	private void setToken(OAuth20Token token){
		this.token = token;
	}
	
	/**
	 * Builds a valid post request for OAuth20
	 * @param onRequestCompleteListener
	 */
	public void post() {
		post(getAuthHeader());
	}
	
	/**
	 * Builds a valid get request for OAuth20
	 * @param onRequestCompleteListener
	 */
	public void get(){
		get(getAuthHeader());
	}
	
	/**
	 * Sets the authorization header for OAuth2
	 * @return valid auth header
	 */
	private String getAuthHeader(){
		return BEARER + token.getAccessToken();
	}

	@Override
	public void refreshAccessToken(final String method, HootResult result) {
		if(attemptedRefreshOnce == false && token.getRefreshToken()!=null && service!=null){
			attemptedRefreshOnce = true;
			service.refreshAccessToken(token.getRefreshToken(), new OAuthRefreshTokenCallback() {
				
				@Override
				public void onNewAccessTokenReceived(OAuth20Token newToken) {
					onRequestCompleteListener.onNewAccessTokenReceived(newToken);
					setToken(newToken);
					if(method.equals(POST)){
						post();
					}else{
						get();
					}
				}
				
				@Override
				public void onFailure(HootResult result) {
					onRequestCompleteListener.onFailure(result);
				}
			});
			
		}else{
			onRequestCompleteListener.onFailure(result);
		}
	}
}