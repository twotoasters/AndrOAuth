package com.androauth.oauth;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;
import android.net.Uri;
import com.androauth.api.OAuth10Api;
import com.twotoasters.android.hoot.Hoot;
import com.twotoasters.android.hoot.HootRequest;
import com.twotoasters.android.hoot.HootResult;
import com.twotoasters.android.hoot.HootRequest.HootRequestListener;


/**
 * 
 * OAuth service class for apis that conform to the OAuth 1.0 spec
 * @author pfives
 */
public class OAuth10Service extends OAuthService {
	OAuth10Api api;
	public static final String AUTHORIZATION = "Authorization";

	/**
	 * An interface for request token callbacks
	 * 
	 */
	public interface OAuthRequestTokenCallback {
		public void onOAuthRequestTokenReceived(Token token);
	}

	/**
	 * Constructs a new OAuth10Service
	 * 
	 * @param oAuth10Api
	 *            a class that implements OAuth10Api
	 */
	public OAuth10Service(OAuth10Api oAuth10Api) {
		api = oAuth10Api;
	}

	/**
	 * Builds an authorization header that conforms to the OAuth 1.0 spec
	 * 
	 * @param httpMethod
	 *            a post or get
	 * @param url
	 *            the url that the request will be made on
	 * @param headers
	 *            any additional headers set before building the authorization
	 *            header
	 * @param userSecret
	 *            the user secret provided by the api after getting a request
	 *            token
	 * @return a valid authorization header
	 */
	private String buildOAuthHeader(String httpMethod, String url, Map<String, String> headers, String userSecret) {
		Map<String, String> headersMap = headers;
		if(headersMap == null) {
			headersMap = new TreeMap<String, String>();
		}

		long millis = System.currentTimeMillis();
		long timestamp = millis / 1000;

		if(getApiCallback() != null) {
			headersMap.put(OAUTH_CALLBACK, percentEncode(getApiCallback()));
		}
		headersMap.put(OAUTH_CONSUMER_KEY, getApiKey());
		headersMap.put(OAUTH_NONCE, String.valueOf(millis + new Random().nextInt()));
		headersMap.put(OAUTH_SIGNATURE_METHOD, METHOD);
		headersMap.put(OAUTH_TIMESTAMP, String.valueOf(timestamp));
		headersMap.put(OAUTH_VERSION, api.getOauthVersion());
		try {
			headersMap.put(OAUTH_SIGNATURE, createSignature(headersMap, userSecret, httpMethod, url));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String authorizationHeader = "OAuth ";

		for(Map.Entry<String, String> entry : headersMap.entrySet()) {
			authorizationHeader += entry.getKey();
			authorizationHeader += "=";
			authorizationHeader += "\"";
			authorizationHeader += entry.getValue();
			authorizationHeader += "\"";
			authorizationHeader += ", ";
		}

		// removes the final comma and space from the Authorization header
		authorizationHeader = authorizationHeader.substring(0, authorizationHeader.length() - 2);

		return authorizationHeader;
	}

	/**
	 * Creates an OAuth signature that conforms to the OAuth 1.0 spec
	 * 
	 * @param headersMap
	 *            an ordered map of the OAuth key value pairs
	 * @param userSecret
	 *            the user secret returned by the api after getting a request
	 *            token
	 * @param methodType
	 *            a post or get
	 * @param baseUrl
	 *            the url that the request will be hitting
	 * @return a valid OAuth signature
	 * @throws UnsupportedEncodingException
	 */
	private String createSignature(Map<String, String> headersMap, String userSecret, String methodType, String baseUrl) throws UnsupportedEncodingException {

		String parameterString = "";

		int i = 0;
		for(Map.Entry<String, String> entry : headersMap.entrySet()) {
			if(i > 0) {
				parameterString += "&";
			}
			parameterString += entry.getKey() + "=" + entry.getValue();
			i++;
		}

		String signatureBaseString = "";
		signatureBaseString += methodType;
		signatureBaseString += "&";
		signatureBaseString += percentEncode(baseUrl);
		signatureBaseString += "&";
		signatureBaseString += percentEncode(parameterString);
		String signingKey = getApiSecret() + "&" + (userSecret == null ? "" : userSecret);

		try {
			return createHMACSignature(signatureBaseString, signingKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Signs an OAuthRequest by building an Authorization header using additional parameters
	 * @param accessToken the accessToken received from the api by the access token url
	 * @param baseUrl the url that the request will be made on
	 * @param httpMethod a post or a get
	 * @param queryParameters additional parameters required on the request
	 * @return a valid Authorization header
	 * @throws UnsupportedEncodingException
	 */
	public String signOAuthRequest(Token accessToken, String baseUrl, String httpMethod, Map<String,String> queryParameters) throws UnsupportedEncodingException{
		
		Map<String, String> headersMap = new TreeMap<String, String>();
		
		headersMap.put(OAUTH_TOKEN, accessToken.getAccess_token());
		
		if(queryParameters!=null && !queryParameters.isEmpty()){
			for (Map.Entry<String, String> entry : queryParameters.entrySet())
			{
			   headersMap.put(entry.getKey(), percentEncode(entry.getValue()));
			}
		}
		
		return buildOAuthHeader(httpMethod, baseUrl, headersMap, accessToken.getUser_secret());
	}
	
	/**
	 * Gets an access token for the api (final step OAuth 1.0) This method is
	 * called after the user verifies access and is redirected
	 * 
	 * @param url
	 *            the url redirected by the api after user verifies access (base
	 *            is apiCallback plus queryParameters)
	 * @param token
	 *            a token containing the user token and user secret provided by
	 *            the request token endpoint
	 * @param oAuthAccessTokenCallback
	 *            interface used to notify when access token is received
	 */
	public void getOAuthAccessToken(Token token, String url, final OAuthAccessTokenCallback oAuthAccessTokenCallback) {

		Uri uri = Uri.parse(url);
		String verifier = uri.getQueryParameter("oauth_verifier").trim();

		Map<String, String> headersMap = new TreeMap<String, String>();

		headersMap.put(OAUTH_TOKEN, token.getAccess_token());
		headersMap.put(OAUTH_VERIFIER, verifier);

		setApiCallback(null);
		String header = buildOAuthHeader("POST", api.getAccessTokenResource(), headersMap, token.getUser_secret());

		Properties headers = new Properties();
		headers.put(AUTHORIZATION, header);

		final Token accessToken = new Token();
		Hoot hoot = Hoot.createInstanceWithBaseUrl(api.getAccessTokenResource());
		HootRequest oAuthRequest = hoot.createRequest().setHeaders(headers);
		oAuthRequest.bindListener(new HootRequestListener() {

			@Override
			public void onSuccess(HootRequest request, HootResult result) {
				accessToken.setAccess_token(extract(result.getResponseString(), TOKEN_REGEX));
				accessToken.setUser_secret(extract(result.getResponseString(), SECRET_REGEX));
				oAuthAccessTokenCallback.onOAuthAccessTokenReceived(accessToken);
			}

			@Override
			public void onRequestStarted(HootRequest request) {

			}

			@Override
			public void onRequestCompleted(HootRequest request) {

			}

			@Override
			public void onFailure(HootRequest request, HootResult result) {

			}

			@Override
			public void onCancelled(HootRequest request) {

			}
		});

		oAuthRequest.post().execute();
	}

	/**
	 * Gets a request token from the api (first step OAuth 1.0)
	 * 
	 * @param oAuthAccessTokenCallback
	 *            interface used to notify when requeset token is received
	 */
	public void getOAuthRequestToken(final OAuthRequestTokenCallback oAuthRequestTokenCallback) {

		Hoot hoot = Hoot.createInstanceWithBaseUrl(api.getRequestTokenResource());
		String header = buildOAuthHeader(POST, api.getRequestTokenResource(), null, null);

		Properties headers = new Properties();
		headers.put(AUTHORIZATION, header);

		HootRequest oAuthRequest = hoot.createRequest().setHeaders(headers);

		oAuthRequest.bindListener(new HootRequestListener() {

			@Override
			public void onSuccess(HootRequest request, HootResult result) {
				Token token = new Token();
				token.setAccess_token(extract(result.getResponseString(), TOKEN_REGEX));
				token.setUser_secret(extract(result.getResponseString(), SECRET_REGEX));

				oAuthRequestTokenCallback.onOAuthRequestTokenReceived(token);

			}

			@Override
			public void onRequestStarted(HootRequest request) {
			}

			@Override
			public void onRequestCompleted(HootRequest request) {
			}

			@Override
			public void onFailure(HootRequest request, HootResult result) {
			}

			@Override
			public void onCancelled(HootRequest request) {
			}
		});
		oAuthRequest.post().execute();
	}
}
