package com.androauthexample.activities;

import java.util.Properties;

import com.androauth.api.RedditApi;
import com.androauth.api.TwitterApi;
import com.androauth.oauth.OAuth20Request;
import com.androauth.oauth.OAuth20Service;
import com.androauth.oauth.OAuthRequest;
import com.androauth.oauth.OAuthRequest.OnRequestCompleteListener;
import com.androauth.oauth.OAuthService;
import com.androauth.oauth.Token;
import com.twotoasters.androauthexample.R;
import com.twotoasters.android.hoot.HootResult;
import com.androauth.oauth.OAuth20Service.OAuth20ServiceCallback;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class RedditActivity extends Activity{

	OAuth20Service service;
	public final static String APIKEY = "B9yYaOVuYvxnRA";
	public static final String APISECRET = "Q7LWINBuc-NmDMYlJDNr5CkW6YU";
	public final static String CALLBACK = "https://oauth.reddit";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAuthentication();
				
			}
		});
	}

	private void startAuthentication(){
		service = OAuthService.newInstance(new RedditApi(), APIKEY, APISECRET, new OAuth20ServiceCallback() {
			
			@Override
			public void onOAuthAccessTokenReceived(String token) {
				//save that shit
				
				getCaptcha(token);
				//getInfo(token.getAccess_token());
			}
		});
		service.setApiCallback(CALLBACK);
		service.setScope("identity");
		getUserVerification();	
	}
	
	private void getUserVerification(){
		
		final WebView webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				// Checking for our successful callback
				if(url.startsWith(CALLBACK)) {
					webview.setVisibility(View.GONE);					
					service.getOAuthAccessToken(url);

				}
				return super.shouldOverrideUrlLoading(view, url);
			}

		});
		
		webview.loadUrl(service.getAuthorizeUrl());
	}
	
	private void getCaptcha(String token){
		
		OAuth20Request request = OAuthRequest.newInstance("https://oauth.reddit.com/api/new_captcha", token);
		
		request.post(new OnRequestCompleteListener() {
			
			@Override
			public void onSuccess(HootResult result) {
				Log.v("into","finalsuccess: "+result.getResponseString());
			}
			
			@Override
			public void onFailure() {
				Log.v("into","failure");	
			}
		});
	}
	
//	private void getInfo(String token){
//		
//		OAuthRequest request = new OAuthRequest("https://oauth.reddit.com/api/v1/me");
//		request.setToken(token);
//		
//		request.get(new OnRequestCompleteListener() {
//			
//			@Override
//			public void onSuccess(HootResult result) {
//				Log.v("into","finalsuccess: "+result.getResponseString());
//			}
//			
//			@Override
//			public void onFailure() {
//				Log.v("into","failure");	
//			}
//		});
//	}

	
	
}