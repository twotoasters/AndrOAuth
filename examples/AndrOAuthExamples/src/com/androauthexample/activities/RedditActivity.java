package com.androauthexample.activities;

import com.androauth.api.RedditApi;
import com.androauth.oauth.OAuth20Request;
import com.androauth.oauth.OAuth20Service;
import com.androauth.oauth.OAuth20Token;
import com.androauth.oauth.OAuthRequest;
import com.androauth.oauth.OAuthRequest.OnRequestCompleteListener;
import com.androauth.oauth.OAuthService;
import com.twotoasters.androauthexample.R;
import com.twotoasters.android.hoot.HootResult;
import com.androauth.oauth.OAuth20Service.OAuth20ServiceCallback;
import android.app.Activity;
import android.content.SharedPreferences;
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
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedPreferences = getPreferences(MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAuthentication();
				
			}
		});
		
		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				useExistingAuthentication();
			}
		});
	}
	
	private void useExistingAuthentication(){
		service = OAuthService.newInstance(new RedditApi(), APIKEY, APISECRET, new OAuth20ServiceCallback() {
			
			@Override
			public void onOAuthAccessTokenReceived(OAuth20Token token) {
				Log.v("into","got the goods: "+token.getAccessToken()+" -- "+token.getRefreshToken());
				editor.putString("access_token", token.getAccessToken());
				editor.putString("refresh_token", token.getRefreshToken()); 
				editor.commit();
				//getCaptcha(token); 
			}

			@Override
			public void onAccessTokenRequestFailed(HootResult result) {
				// TODO Auto-generated method stub
				
			}			
		});
		String accessToken = sharedPreferences.getString("access_token", null);
		String refreshToken = sharedPreferences.getString("refresh_token", null);
		Log.v("into","using: "+accessToken + "--" + refreshToken);
		OAuth20Token existingToken = new OAuth20Token(accessToken, refreshToken);
		//getCaptcha(existingToken);
		getInfo(existingToken);
	}

	private void startAuthentication(){
		service = OAuthService.newInstance(new RedditApi(), APIKEY, APISECRET, new OAuth20ServiceCallback() {
			
			@Override
			public void onOAuthAccessTokenReceived(OAuth20Token token) {
				Log.v("into","got the goods: "+token.getAccessToken()+" -- "+token.getRefreshToken());
				editor.putString("access_token", token.getAccessToken());
				editor.putString("refresh_token", token.getRefreshToken()); 
				editor.commit();
				//getCaptcha(token); 
			}

			@Override
			public void onAccessTokenRequestFailed(HootResult result) {
				// TODO Auto-generated method stub
				
			}			
		});
		service.setApiCallback(CALLBACK);
		service.setScope("identity");
		service.setDuration("permanent");
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
	
//	private void getCaptcha(OAuth20Token token){
//		
//		OAuth20Request request = OAuthRequest.newInstance("https://oauth.reddit.com/api/new_captcha", token);
//		
//		request.post(new OnRequestCompleteListener() {
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
	
	private void getInfo(OAuth20Token token){
		OAuth20Request request = OAuthRequest.newInstance("https://oauth.reddit.com/api/v1/me",token, service, new OnRequestCompleteListener() {
			
			@Override
			public void onSuccess(HootResult result) {
				Log.v("into","final on success: "+result.getResponseString());
			}
			
			@Override
			public void onNewAccessTokenReceived(OAuth20Token token) {
			}
			
			@Override
			public void onFailure() {
			}
		}); 
		request.get();
	}

	
	
}