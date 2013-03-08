package com.androauth.exceptions;

public class OAuthSignatureException extends RuntimeException {

	private static final long serialVersionUID = 8960550319634532271L;

	public OAuthSignatureException(String detailMessage, Throwable throwable){
		super(detailMessage, throwable);
	}
}