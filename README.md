AndrOAuth
=========

A simple OAuth library for Android based on Hoot rest client<br><br>


##Installation

  Download the androauth.jar file here<br>
  Detailed installation instructions can be found here<br>

##Implementation
  
  The OAuth flow is slightly different between versions 1.0 and 2.0 but both are easy to implement.<br>
  Detailed OAuth 1.0 instructions <br>
  Detailed OAuth 2.0 instructions <br>
  
  See if your API is already supported here.<br>
  If not, it's as easy as extending the OAuth10Api or OAuth20Api and copy-pasting the correct URL's. (and then add it to the group!)
  <br><br>
  But basically, to get an access token:
  
      service = OAuthService.newInstance(new GoogleApi(), APIKEY, APISECRET, new OAuth20ServiceCallback() {
      		
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
    				// TODO Auto-generated method stub
    				
    			}
    			
    			@Override
    			public void onNewAccessTokenReceived(OAuth20Token token) {
    				// TODO Auto-generated method stub
    				
    			}
    
    			@Override
    			public void onFailure(HootResult result) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		Map<String,String> queryParameters = new HashMap<String,String>();
    		queryParameters.put("status", "that's all folks");
    		request.setRequestParams(queryParameters);
  
 It's that simple.
  
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
