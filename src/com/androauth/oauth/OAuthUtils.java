package com.androauth.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.androauth.exceptions.OAuthEncodingException;

public class OAuthUtils {
	
	private OAuthUtils(){
		
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
          return URLEncoder.encode(s, OAuthService.UTF8).
          		replace("%7E", "~").replace("*", "%2A").replace("+", "%20");
      } catch (UnsupportedEncodingException e) {
          throw new OAuthEncodingException("Error encoding values: Unsupported Encoding UTF-8", e);
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
	public static String extract(String response, Pattern pattern)
	  {
		String extraction = null;
	    Matcher matcher = pattern.matcher(response);
	    if (matcher.find() && matcher.groupCount() >= 1)
	    {
	    	extraction = URLDecoder.decode(matcher.group(1));
	    }
	    return extraction;
	 }
	
	public static void appendFirstQueryParam(StringBuilder sb, String key, String value) {
	    if (!isNullOrEmpty(value)) {
	        sb.append("?").append(key).append("=").append(value);
	    }
	}

	public static void appendQueryParam(StringBuilder sb, String key, String value) {
	    if (!isNullOrEmpty(value)) {
	        sb.append("&").append(key).append("=").append(value);
	    }
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length()==0;
	}
	
}