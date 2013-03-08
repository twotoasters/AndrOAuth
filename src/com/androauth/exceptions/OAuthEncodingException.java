package com.androauth.exceptions;

public class OAuthEncodingException extends RuntimeException{
	
	private static final long serialVersionUID = 8960550319634532271L;

	public OAuthEncodingException(String detailMessage, Throwable throwable){
		super(detailMessage, throwable);
	}

}