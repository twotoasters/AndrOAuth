AndrOAuth
=========

A simple OAuth library for Android based on <a href="https://github.com/twotoasters/hoot">Hoot</a><br><br>


##Installation

  Download the <a href="http://linode-staging.twotoasters.com/android/downloads/">AndrOAuth.jar file</a><br>
  <a href="https://github.com/twotoasters/AndrOAuth/wiki/Installation-Instructions">Detailed installation instructions</a><br>

  Or, grab via Maven:
  
      <dependency>
            <groupId>com.twotoasters</groupId>
            <artifactId>androauth</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

##Implementation
  
  The OAuth flow is slightly different between versions 1.0 and 2.0 but both are easy to implement.<br>
  <a href="https://github.com/twotoasters/AndrOAuth/wiki/OAuth-1.0-Implementation">Detailed OAuth 1.0 instructions</a><br>
  <a href="https://github.com/twotoasters/AndrOAuth/wiki/OAuth-2.0-Implementation">Detailed OAuth 2.0 instructions</a><br>
  <a href="https://github.com/twotoasters/AndrOAuth/tree/master/examples/AndrOAuthExamples/src/com/androauthexample/activities">Basic Examples</a><br>
  
  See if your API is already supported <a href="https://github.com/twotoasters/AndrOAuth/tree/master/src/com/androauth/api">here</a>.<br>
  If not, it's as easy as extending the OAuth10Api or OAuth20Api and copy-pasting the correct URL's. (and then add it to the group!)
  <br><br>
  But basically, to get an access token:
  
      OAuth20Service service = OAuthService.newInstance(new GoogleApi(), APIKEY, APISECRET, new OAuth20ServiceCallback() {
      		
    			@Override
    			public void onOAuthAccessTokenReceived(OAuth20Token token) {
    				editor.putString("access_token", token.getAccessToken());
    				editor.putString("refresh_token", token.getRefreshToken()); 
    				editor.commit();
    				getInfo(token); 
    			}
    
    			@Override
    			public void onAccessTokenRequestFailed(HootResult result) {
    				
    			}			
    	});
  
  along with a webview for user authorization.<br><br>
  
  
  And to make a signed request:
  
      String baseUrl = "https://api.twitter.com/1/statuses/update.json";
      	OAuth10Request request = OAuthRequest.newInstance(baseUrl, token, service, new OnRequestCompleteListener() {
    			
    			@Override
    			public void onSuccess(HootResult result) {
    			}
    			
    			@Override
    			public void onNewAccessTokenReceived(OAuth20Token token) {
    			}
    
    			@Override
    			public void onFailure(HootResult result) {
    			}
    		});
    		Map<String,String> queryParameters = new HashMap<String,String>();
    		queryParameters.put("status", "that's all folks");
    		request.setRequestParams(queryParameters);
  
 That's it.

##Other
If you have any trouble configuring the library or adding an API, feel free to reach out: pat@twotoasters.com
  
##License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
