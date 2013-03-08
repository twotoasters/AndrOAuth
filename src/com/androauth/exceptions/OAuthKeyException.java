package com.androauth.exceptions;

public class OAuthKeyException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 24684954853598089L;

	public OAuthKeyException(String detailMessage, Throwable throwable){
		super(detailMessage, throwable);
	}

}
