package com.androauth.oauth;

public class OAuthPostException extends RuntimeException {
	private static final long serialVersionUID = -5013043796820620168L;
	
	public OAuthPostException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
