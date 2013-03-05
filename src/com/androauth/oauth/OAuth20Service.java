package com.androauth.oauth;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import com.androauth.api.OAuth20Api;
import com.twotoasters.android.hoot.Hoot;
import com.twotoasters.android.hoot.HootRequest;
import com.twotoasters.android.hoot.HootResult;
import com.twotoasters.android.hoot.HootRequest.HootRequestListener;

/**
 * 
 * OAuth service class for apis that conform to the OAuth 2.0 spec
 * @author pfives
 */
public class OAuth20Service extends OAuthService {
	OAuth20Api api;
	private static final String CODE = "code";
	private static final String GRANT_TYPE = "grant_type";
	private static final String AUTHORIZATION_CODE = "authorization_code";
	private static final String REDIRECT_URI = "redirect_uri";
	private static final String RESPONSE_TYPE = "response_type";
	private static final String CLIENT_ID = "client_id";
	private static final String STATE = "state";
	
	private OAuth20ServiceCallback oAuthCallback;

	/**
	 * an interface to notify the caller when the access_token has been received
	 *
	 */
	public interface OAuth20ServiceCallback{
		public void onOAuthAccessTokenReceived(String token);
	}
	
	/**
	 * Constructs a new OAuth20Service
	 * 
	 * @param oAuth20Api
	 *            a class that implements OAuth20Api
	 */
	public OAuth20Service(OAuth20Api oAuth20Api, OAuth20ServiceCallback oAuth20ServiceCallback) {
		oAuthCallback = oAuth20ServiceCallback;
		api = oAuth20Api;
	}

	/**
	 * Gets an access token for the api (final step OAuth 2.0) This method is
	 * called after the user verifies access and is redirected
	 * 
	 * @param url
	 *            the url redirected by the api after user verifies access (base
	 *            is apiCallback plus queryParameters)
	 * @param oAuthAccessTokenCallback
	 *            interface used to notify when access token is received
	 */
	public void getOAuthAccessToken(String url) {
		String code = extract(url, CODE_REGEX);

		Hoot hoot = Hoot.createInstanceWithBaseUrl(api.getAccessTokenResource());

		hoot.setBasicAuth(getApiKey(), getApiSecret());
		HootRequest request = hoot.createRequest();

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put(CODE, code);
		queryParameters.put(GRANT_TYPE, AUTHORIZATION_CODE);
		queryParameters.put(REDIRECT_URI, getApiCallback());

		request.bindListener(new HootRequestListener() {

			@Override
			public void onSuccess(HootRequest request, HootResult result) {
				String extracted = extract(result.getResponseString(), ACCESS_TOKEN_REGEX);
				Log.v("into","the extraction: "+result.getResponseString());
				oAuthCallback.onOAuthAccessTokenReceived(extracted);
			}

			@Override
			public void onRequestStarted(HootRequest request) {

			}

			@Override
			public void onRequestCompleted(HootRequest request) {

			}

			@Override
			public void onFailure(HootRequest request, HootResult result) {
				Log.v("into", "on failure: " + result.getResponseString());
			}

			@Override
			public void onCancelled(HootRequest request) {

			}
		});

		request.post(queryParameters).execute();

	}

	/**
	 * Returns a built authorize url for the api with the appended parameters
	 * necessary for an OAuth 2.0 Authorize call
	 * 
	 * @return the authorize url with appended parameters
	 */
	public String getAuthorizeUrl() {

		String url = null;

		url = api.getAuthorizeUrl();
		url += "?" + RESPONSE_TYPE + "=" + CODE + "&" + CLIENT_ID + "=" + getApiKey() + "&" + STATE + "=" + "blah";
		if(getApiCallback() != null){
			url += "&" + REDIRECT_URI + "=" + percentEncode(getApiCallback()); 
		}
		if(getScope() != null) {
			url += "&" + "scope=" + getScope();
		}

		return url;
	}

}
