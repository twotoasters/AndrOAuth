package com.androauth.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.androauth.api.OAuth10Api;
import com.androauth.api.OAuth20Api;
import com.androauth.oauth.OAuth10Service.OAuth10ServiceCallback;
import com.androauth.oauth.OAuth20Service.OAuth20ServiceCallback;

import android.util.Base64;

/**
 * 
 * OAuth service base class
 * @author pfives
 */
public class OAuthService {

	public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
	public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	public static final String OAUTH_SIGNATURE = "oauth_signature";
	public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
	public static final String OAUTH_NONCE = "oauth_nonce";
	public static final String OAUTH_VERSION = "oauth_version";
	public static final String OAUTH_CALLBACK = "oauth_callback";
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_VERIFIER = "oauth_verifier";
	public static final String UTF8 = "UTF-8";
	public static final String HMAC_SHA1 = "HmacSHA1";
	public static final String METHOD = "HMAC-SHA1";
	public static final String POST = "POST";
	public static final Pattern TOKEN_REGEX = Pattern.compile("oauth_token=([^&]+)");
    public static final Pattern SECRET_REGEX = Pattern.compile("oauth_token_secret=([^&]*)");
    public static final Pattern ACCESS_TOKEN_REGEX = Pattern.compile("\"access_token\":\"(( ([^\"]*))|(([^\"]*)))\"");
    public static final Pattern CODE_REGEX = Pattern.compile("code=([^&]*)");
    
	private static String apiKey;
	private static String apiSecret;
	private String apiCallback;
	private String apiVersion;
	private String scope;
	private Token token;

	/**
	   * Constructs a new OAuthService for apis using OAuth 1.0
	   *
	   * @param oAuth10Api an api class that implements the oauth10api interface
	   */
	public static OAuth10Service newInstance(OAuth10Api oAuth10Api, String apiConsumerKey, String apiConsumerSecret, OAuth10ServiceCallback oAuthCallback){
		apiKey = apiConsumerKey;
		apiSecret = apiConsumerSecret;
		
		return new OAuth10Service(oAuth10Api, oAuthCallback);		
	}
	
	/**
	   * Constructs a new OAuthService for apis using OAuth 2.0
	   *
	   * @param oAuth20Api an api class that implements the oauth20api interface
	   */
	public static OAuth20Service newInstance(OAuth20Api oAuth20Api, String apiConsumerKey, String apiConsumerSecret, OAuth20ServiceCallback oAuthCallback){
		apiKey = apiConsumerKey;
		apiSecret = apiConsumerSecret;
		
		return new OAuth20Service(oAuth20Api, oAuthCallback);
	}

	/**
	   * Gets the api key of the app 
	   * 
	   * @return the api key
	   */
	public String getApiKey() {
		return apiKey;
	}

	/**
	   * Gets the api secret of the app 
	   * 
	   * @return the api secret
	   */
	public String getApiSecret() {
		return apiSecret;
	}

	/**
	   * Gets the api callback used for redirects
	   * 
	   * @return desired extracted string
	   */
	public String getApiCallback() {
		return apiCallback;
	}

	/**
	   * Sets the api callback used for redirects
	   * 
	   * @param apiCallback to be sent to the api, used for redirects
	   */
	public void setApiCallback(String apiCallback) {
		this.apiCallback = apiCallback;
	}
	/**
	   * Gets the api version of the api being used  
	   *  
	   * @return the api version being used
	   */
	public String getApiVersion() {
		return apiVersion;
	}
	/**
	   * Sets the api version of the api being used
	   * 
	   * @param api version of the api 
	   */
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	/**
	   * Gets the scope of this service
	   * 
	   * @return the scope
	   */
	public String getScope() {
		return scope;
	}

	/**
	   * Sets the scope for this service
	   * 
	   * @param scope for oauth2.0 apis
	   */
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	/**
	 * Gets the oauth token
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * Sets the oauth token
	 * @param token
	 */
	public void setToken(Token token) {
		this.token = token;
	}
	
	/**
	   * Generates an HMAC signature given a base string and key
	   * 
	   * @param baseString to be turned into oauth signature
	   * @param signingKey used to sign the baseString (consumersecret& or consumersecret&clientsecret)
	   * 
	   * @return the signature used in the oauth header
	   */
	public String createHMACSignature(String baseString, String signingKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException{
		
		SecretKeySpec key = new SecretKeySpec(signingKey.getBytes(UTF8), HMAC_SHA1);
	    Mac mac = Mac.getInstance(HMAC_SHA1);
	    mac.init(key);
	    byte[] bytes = mac.doFinal(baseString.getBytes(UTF8));
	    String sig = new String(Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT)).trim();
	    
	    return percentEncode(sig);
	}
	
	/**
	   * URLEncodes given string and replaces certain other characters not handled by URLEncoder
	   * 
	   * @param s the string top percent encode 
	   * 
	   * @return the percent encoded string
	   */
	
	public static String percentEncode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, UTF8).
            		replace("%7E", "~").replace("*", "%2A").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
	
	/**
	   * Extracts a desired string given a string and a pattern using regex
	   * 
	   * @param response the string to extract from
	   * @param pattern the regex pattern used to extract 
	   * 
	   * @return desired extracted string
	   */
	@SuppressWarnings("deprecation")
	public String extract(String response, Pattern pattern)
	  {
		String extraction = null;
	    Matcher matcher = pattern.matcher(response);
	    if (matcher.find() && matcher.groupCount() >= 1)
	    {
	    	extraction = URLDecoder.decode(matcher.group(1));
	    }
	    return extraction;
	 }
	
}