package com.androauth.oauth;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.net.Uri;
import android.util.Base64;

import com.androauth.api.OAuth10Api;
import com.androauth.exceptions.OAuthEncodingException;
import com.androauth.exceptions.OAuthKeyException;
import com.androauth.exceptions.OAuthSignatureException;
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
	
	/**
	 * An interface for notifying the caller when a request_token or access_token has been received 
	 *
	 */
	public interface OAuth10ServiceCallback{
		/**
		 * Notifies when an oauth request token has been received
		 */
		public void onOAuthRequestTokenReceived();
		/**
		 * Notifies when a request token request has failed
		 * @param result
		 */
		public void onOAuthRequestTokenFailed(HootResult result);
		/**
		 * Notifies when an oauth access token has been received
		 * @param token and OAuth10 token containing an access token and user secret
		 */
		public void onOAuthAccessTokenReceived(OAuth10Token token);
		/**
		 * Notifies when an access token request has failed
		 * @param result
		 */
		public void onOAuthAccessTokenFailed(HootResult result);
	}
	
	private OAuth10Api api;
	private OAuth10ServiceCallback oAuthCallback;
	
	public static final String AUTHORIZATION = "Authorization";
	public static final String OAUTH_ = "OAuth ";
	public static final String OAUTH_VERIFIER = "oauth_verifier";

	/**
	 * Constructs a new OAuth10Service
	 * 
	 * @param oAuth10Api
	 *            a class that implements OAuth10Api
	 */
	public OAuth10Service(OAuth10Api oAuth10Api, OAuth10ServiceCallback oAuth10Callback) {
		oAuthCallback = oAuth10Callback;
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
		Map<String, String> headersMap = buildAuthorizationHeaderMap(headers, httpMethod, url, userSecret);

		boolean appendEntry = false;
		StringBuilder sb = new StringBuilder(OAUTH_);
		for(Map.Entry<String, String> entry : headersMap.entrySet()) {
			if (appendEntry) {
				sb.append(", ");
			}
			// key = "value"
			sb.append(entry.getKey())
				.append("=\"")
				.append(entry.getValue())
				.append("\"");
			appendEntry = true;
		}

		return sb.toString();
	}

	/**
	 * Builds a map of headers used on OAuth1.0 request
	 * @param headers any additional headers
	 * @param httpMethod post or get
	 * @param url the url the request will be made on 
	 * @param secret the user secret provided by the api after getting a request token
	 * @return a sorted map of headers
	 */
	private Map<String, String> buildAuthorizationHeaderMap(Map<String, String> headers, String httpMethod, String url, String secret) {
		Map<String, String> headersMap = new TreeMap<String, String>();
		if(headers != null) {
			headersMap.putAll(headers);
		}

		long millis = System.currentTimeMillis() / 1000;

		if(getApiCallback() != null) {
			headersMap.put(OAUTH_CALLBACK, OAuthUtils.percentEncode(getApiCallback()));
		}
		headersMap.put(OAUTH_CONSUMER_KEY, getApiKey());
		headersMap.put(OAUTH_NONCE, String.valueOf((millis*1000) + new Random().nextInt()));
		headersMap.put(OAUTH_SIGNATURE_METHOD, METHOD);
		headersMap.put(OAUTH_TIMESTAMP, String.valueOf(millis));
		headersMap.put(OAUTH_VERSION, api.getOauthVersion());
		headersMap.put(OAUTH_SIGNATURE, createSignature(headersMap, secret, httpMethod, url));

		return headersMap;
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
	private String createSignature(Map<String, String> headersMap, String userSecret, String methodType, String baseUrl){

		StringBuilder parameterString = new StringBuilder();
		
		boolean appendParameter = false;
		for(Map.Entry<String, String> entry : headersMap.entrySet()) {
			if(appendParameter) {
				parameterString.append("&");
			}
			parameterString.append(entry.getKey()).append("=").append(entry.getValue());
			appendParameter = true;
		}

		StringBuilder signatureBaseString = new StringBuilder();
		signatureBaseString.append(methodType).append("&").append(OAuthUtils.percentEncode(baseUrl)).append("&").append(OAuthUtils.percentEncode(parameterString.toString()));
		
		String signingKey = getApiSecret() + "&" + (userSecret == null ? "" : userSecret);

		return createHMACSignature(signatureBaseString.toString(), signingKey);

	}
	
	/**
	   * Generates an HMAC signature given a base string and key
	   * 
	   * @param baseString to be turned into oauth signature
	   * @param signingKey used to sign the baseString (consumersecret& or consumersecret&clientsecret)
	   * 
	   * @return the signature used in the oauth header
	   */
	public String createHMACSignature(String baseString, String signingKey) {
		SecretKeySpec key = null;
		try {
			key = new SecretKeySpec(signingKey.getBytes(UTF8), HMAC_SHA1);
		} catch (UnsupportedEncodingException e) {
			throw(new OAuthEncodingException("Error creating signature: Unsupported Encoding UTF-8", e));
		}
	    Mac mac = null;
		try {
			mac = Mac.getInstance(HMAC_SHA1);
		} catch (NoSuchAlgorithmException e) {
			throw(new OAuthSignatureException("Error creating signature: No Algorithm HMAC-SHA1", e));
		}
	    try {
			mac.init(key);
		} catch (InvalidKeyException e) {
			throw new OAuthKeyException("Error creating signature: Invalid Key", e);
		}
	    byte[] bytes = null;
		try {
			bytes = mac.doFinal(baseString.getBytes(UTF8));
		} catch (IllegalStateException e) {
			throw(new OAuthSignatureException("Error creating signature: Illegal State", e));
		} catch (UnsupportedEncodingException e) {
			throw(new OAuthEncodingException("Error creating signature: Unsupported Encoding UTF-8", e));
		}
	    String sig = new String(Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT)).trim();
	    
	    return OAuthUtils.percentEncode(sig);
	}

	/**
	 * Signs an OAuthRequest by building an Authorization header using additional parameters
	 * @param accessToken the accessToken received from the api by the access token url
	 * @param baseUrl the url that the request will be made on
	 * @param httpMethod a post or a get
	 * @param queryParameters additional parameters required on the request
	 * @return a valid Authorization header
	 */
	public String signOAuthRequest(OAuth10Token accessToken, String baseUrl, String httpMethod, Map<String,String> queryParameters) {
		
		Map<String, String> headersMap = new TreeMap<String, String>();
		
		headersMap.put(OAUTH_TOKEN, accessToken.getAccessToken());
		
		if(queryParameters!=null && !queryParameters.isEmpty()){
			headersMap.putAll(queryParameters);
		}
		
		return buildOAuthHeader(httpMethod, baseUrl, headersMap, accessToken.getUserSecret());
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
	public void getOAuthAccessToken(String url) {

		Uri uri = Uri.parse(url);
		String verifier = uri.getQueryParameter(OAUTH_VERIFIER).trim();

		Map<String, String> headersMap = new TreeMap<String, String>();

		headersMap.put(OAUTH_TOKEN, getToken().getAccessToken());
		headersMap.put(OAUTH_VERIFIER, verifier);

		setApiCallback(null);
		String header = buildOAuthHeader(POST, api.getAccessTokenResource(), headersMap, getToken().getUserSecret());
		
		Properties headers = new Properties();
		headers.put(AUTHORIZATION, header);

		Hoot hoot = Hoot.createInstanceWithBaseUrl(api.getAccessTokenResource());
		HootRequest oAuthRequest = hoot.createRequest().setHeaders(headers);
		oAuthRequest.bindListener(new HootRequestListener() {

			@Override
			public void onSuccess(HootRequest request, HootResult result) {
				String response = result.getResponseString();
				String accessToken = extractAccessToken(response);
				String userSecret = extractUserSecret(response);
				OAuth10Token token = new OAuth10Token(accessToken, userSecret);
				oAuthCallback.onOAuthAccessTokenReceived(token);
			}

			@Override
			public void onRequestStarted(HootRequest request) {

			}

			@Override
			public void onRequestCompleted(HootRequest request) {

			}

			@Override
			public void onFailure(HootRequest request, HootResult result) {
				oAuthCallback.onOAuthAccessTokenFailed(result);
			}

			@Override
			public void onCancelled(HootRequest request) {

			}
		});

		oAuthRequest.post().execute();
	}

	/**
	 * Constructs an authorize url which appends the oauth_token and oauth_callback AND any additional parameters passed in to the authorize url defined in the api class
	 * @param additionalAuthorizeParams
	 * @return
	 */
	public String getAuthorizeUrl(Map<String,String>additionalAuthorizeParams){
		StringBuilder url = new StringBuilder(getAuthorizeUrl());
		for(Map.Entry<String, String> entry : additionalAuthorizeParams.entrySet()){
			OAuthUtils.appendQueryParam(url, entry.getKey(), OAuthUtils.percentEncode(entry.getValue()));
		}
		return url.toString();
	}
	
	/**
	 * Constructs an authorize url which appends the oauth_token and oauth_callback to the authorize url defined in the api class
	 * @return authorize url with parameters
	 */
	public String getAuthorizeUrl(){
		StringBuilder url = new StringBuilder(api.getAuthorizeUrl());
		OAuthUtils.appendFirstQueryParam(url, OAUTH_TOKEN, getToken().getAccessToken());
		OAuthUtils.appendQueryParam(url, OAUTH_CALLBACK, OAuthUtils.percentEncode(getApiCallback()));
		return url.toString();
	}
	
	/**
	 * Gets a request token from the api (first step OAuth 1.0)
	 * 
	 * @param oAuthAccessTokenCallback
	 *            interface used to notify when requeset token is received
	 */
	public void getOAuthRequestToken() {
		Hoot hoot = Hoot.createInstanceWithBaseUrl(api.getRequestTokenResource());
		String header = buildOAuthHeader(POST, api.getRequestTokenResource(), null, null);
		Properties headers = new Properties();
		headers.put(AUTHORIZATION, header);

		HootRequest oAuthRequest = hoot.createRequest().setHeaders(headers);

		oAuthRequest.bindListener(new HootRequestListener() {

			@Override
			public void onSuccess(HootRequest request, HootResult result) {
				OAuth10Token token = new OAuth10Token();
				String response = result.getResponseString();
				token.setAccessToken(extractAccessToken(response));
				token.setUserSecret(extractUserSecret(response));
				setToken(token);
				oAuthCallback.onOAuthRequestTokenReceived();

			}

			@Override
			public void onRequestStarted(HootRequest request) {
			}

			@Override
			public void onRequestCompleted(HootRequest request) {
			}

			@Override
			public void onFailure(HootRequest request, HootResult result) {
				oAuthCallback.onOAuthRequestTokenFailed(result);
			}

			@Override
			public void onCancelled(HootRequest request) {
			}
		});
		oAuthRequest.post().execute();
	}
	
	private String extractAccessToken(String response){
		return OAuthUtils.extract(response, TOKEN_REGEX);
	}
	
	private String extractUserSecret(String response){
		return OAuthUtils.extract(response, SECRET_REGEX);
	}
	
	/**
	 * Starts the OAuth10 handshake by requesting a request token
	 */
	public void start(){
		getOAuthRequestToken();
	}
	
}
