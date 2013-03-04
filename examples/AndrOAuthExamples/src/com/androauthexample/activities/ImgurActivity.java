package com.androauthexample.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.androauth.api.ImgurApi;
import com.androauth.api.RedditApi;
import com.androauth.oauth.OAuth20Service;
import com.androauth.oauth.OAuthService;
import com.androauth.oauth.Token;
import com.androauth.oauth.OAuthService.OAuthAccessTokenCallback;
import com.twotoasters.androauthexample.R;

public class ImgurActivity extends Activity {
	
	OAuth20Service service;
	public final static String APIKEY = "73654a67a7da34b";
	public static final String APISECRET = "e52aff7e0629f695cf854da391459f66175a3d77";
	public final static String CALLBACK = "http://www.twotoasters.com";
	
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
		service = OAuthService.newInstance(new ImgurApi());
		service.setApiCallback(CALLBACK);
		service.setApiKey(APIKEY);
		service.setApiSecret(APISECRET);
		//service.setScope("identity");
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
					service.getOAuthAccessToken(url, new OAuthAccessTokenCallback() {
						
						@Override
						public void onOAuthAccessTokenReceived(Token token) {
							//save that shit
							Log.v("into","main success: "+token.getAccess_token()+" -- "+token.getUser_secret());
							//getCaptcha(token.getAccess_token());
							//getInfo(token.getAccess_token());
						}
					});		
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

		});
		
		webview.loadUrl(service.getAuthorizeUrl());
	}
}
