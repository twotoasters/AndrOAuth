package com.androauthexample.activities;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.androauth.api.TwitterApi;
import com.androauth.oauth.OAuth10Service;
import com.androauth.oauth.OAuth10Service.OAuthRequestTokenCallback;
import com.androauth.oauth.OAuthService;
import com.androauth.oauth.Token;
import com.androauth.oauth.OAuthService.OAuthAccessTokenCallback;
import com.twotoasters.androauthexample.R;
import com.twotoasters.android.hoot.Hoot;
import com.twotoasters.android.hoot.HootRequest;
import com.twotoasters.android.hoot.HootRequest.HootRequestListener;
import com.twotoasters.android.hoot.HootResult;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MainActivity extends Activity {

	public final static String APIKEY = "HAA4bTChDO2cDeC8Rgx6NA";
	public static final String APISECRET = "7GsU75v5vQmXTVlIQilCbF2Y2dW6TRq9qxPRZftKKQ";
	public final static String CALLBACK = "oauth://twitter";
	OAuth10Service service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startOAuth();
			}
		});
		
	}
	
	private void startOAuth(){
		service = OAuthService.newInstance(new TwitterApi());
		service.setApiCallback(CALLBACK);
		service.setApiKey(APIKEY);
		service.setApiSecret(APISECRET);
		service.getOAuthRequestToken(new OAuthRequestTokenCallback() {
			
			@Override
			public void onOAuthRequestTokenReceived(Token token) {
				getUserAuthorization(token);
			}
		});
		
	}
	
	private void getUserAuthorization(final Token token){
		String authUrl = String.format(TwitterApi.AUTHORIZE_URL,token.getAccess_token());
		
		final WebView webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				// Checking for our successful callback
				if(url.startsWith(CALLBACK)) {
					webview.setVisibility(View.GONE);
					service.getOAuthAccessToken(token, url, new OAuthAccessTokenCallback() {
						
						@Override
						public void onOAuthAccessTokenReceived(Token token) {
							//save that shit
							Log.v("into","main success: "+token.getAccess_token()+" -- "+token.getUser_secret());
							updateStatus(token);
						}

						
					});		
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

		});
		
		webview.loadUrl(authUrl);
	}
	
	public void updateStatus(Token token){
		
		
//		String baseUrl = "https://api.twitter.com/1/statuses/update.json";
//		OAuthRequest request = new OAuthRequest(baseUrl);
//		request.setoAuth1token(token);
//		Map<String,String> queryParameters = new HashMap<String,String>();
//		queryParameters.put("status", "spider man could be a ghost");
//		
//		request.setParams(queryParameters);
//		
//		request.post(new OnRequestCompleteListener() {
//			
//			@Override
//			public void onSuccess(HootResult result) {
//				Log.v("into","super succcess: "+result.getResponseString());
//			}
//			
//			@Override
//			public void onFailure() {
//				Log.v("into","on failure ");
//				
//			}
//		});
		
		Map<String,String> queryParameters = new HashMap<String,String>();
		String baseUrl = "https://api.twitter.com/1/statuses/update.json";
		String httpMethod = "POST";
		queryParameters.put("status", "super masdfgdfgbatman");
		String auth = null;
		try {
			auth = service.signOAuthRequest(token, baseUrl, httpMethod, queryParameters);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HootRequest request = Hoot.createInstanceWithBaseUrl(baseUrl).createRequest();
		Properties headers = new Properties();
		headers.setProperty("Authorization", auth);
		request.setHeaders(headers);
		request.bindListener(new HootRequestListener() {
			
			@Override
			public void onSuccess(HootRequest arg0, HootResult arg1) {
				Log.v("into","sarxess "+arg1.getResponseString());
			}
			
			@Override
			public void onRequestStarted(HootRequest arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onRequestCompleted(HootRequest arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(HootRequest arg0, HootResult arg1) {
				Log.v("into","on failure: ");
			}
			
			@Override
			public void onCancelled(HootRequest arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		request.post(queryParameters).execute();
		
	}

}
