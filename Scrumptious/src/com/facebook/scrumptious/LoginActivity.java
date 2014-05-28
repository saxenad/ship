/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.scrumptious;

import java.io.Console;
import java.security.MessageDigest;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTable;
import com.facebook.scrumptious.GlobalMethods.globalmethods;

public class LoginActivity extends Activity {

	private boolean isResumed = false;

	public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
	public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
	public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";
	private UiLifecycleHelper uiHelper;
	private GraphUser facebookGraphUser;
	private String userEmail;
	
	private ImageView imageView;
	int i = 0;
	int imgid[] = { R.drawable.rsz_1shipcaptain, R.drawable.rsz_intro1 };

	ImageSwitcher Switch;
	ImageView images;

	private int position = 1;

	//RefreshHandler refreshHandler = new RefreshHandler();
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.facebook.scrumptious", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String key = Base64.encodeToString(md.digest(), Base64.DEFAULT);
				System.out.print("KeyHash:"+key);
			}
		} catch (Exception e) {
			Intent logOutintent = new Intent(LoginActivity.this, OrdersActivity.class);
			startActivity(logOutintent);
		}
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		Session session = Session.getActiveSession();
		if (session == null) {
			// try to restore from cache
			session = Session
					.openActiveSessionFromCache(getApplicationContext());
		}

		if (session != null && session.isOpened()) {
			invokeMainActivity(getString(R.string.title1), R.drawable.icon);

		} else {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);

			// Remove notification bar
			this.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

			super.onCreate(savedInstanceState);
			setContentView(R.layout.slideshow);
			this.imageView = (ImageView) this.findViewById(R.id.imageView);
			imageView.setImageResource(imgid[0]);

			//updateUI();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;

	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (isResumed) {
			if (state.isOpened()) {
				makeMeRequest(session);

				// showFragment(SELECTION, false);
			} else if (state.isClosed()) {
				// If the session state is closed:
				// Show the login fragment
				// showFragment(SPLASH, false);
				setContentView(R.layout.slideshow);

			}
		}
	}

	// Retrieve Facebook Details
	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								Button logInButton = (Button) findViewById(R.id.login_button);
								logInButton.setVisibility(View.INVISIBLE);
								facebookGraphUser = user;
								checkIfUserExists(user);
							}
						}
						if (response.getError() != null) {
							// Handle errors, will do so later.
						}
					}
				});
		request.executeAsync();
	}

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//imageView.setImageResource(imgid[0]);

			// slideshow.this.updateUI();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

/*	public void updateUI() {
		int currentInt = 0 + 10;
		if (currentInt <= 100) {
			refreshHandler.sleep(2000);

			if (i < imgid.length) {
				//imageView.setImageResource(imgid[i]);

				// imageView.setPadding(left, top, right, bottom);
				i++;
			}
		}
	}*/

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {

		case MotionEvent.ACTION_UP:
			float finalX = event.getX();

			// cursor.moveToPosition(position);
			// int imageID = cursor.getInt(columnIndex);
			if (position == 1) {
				imageView.setImageResource(imgid[1]);
				position = 0;
			} else {
				imageView.setImageResource(imgid[0]);
				position = 1;
			}
			// images.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
			// "" + imageID));
			// images.setBackgroundResource(R.drawable.ic_launcher);
			/*
			 * Toast.makeText(getApplicationContext(), "next Image",
			 * Toast.LENGTH_LONG).show();*/
			 
			// Switch.showPrevious();

			break;

		}
		return false;

	}

	// Make JsonCall with Information
	private class JSONFeedTask extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog = new ProgressDialog(LoginActivity.this);
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog.setMessage("Retrieving your Location...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... urls) {
			return readJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				if (result.equals("true")) {
					// Make sure User also Exists in SQLLite
					String email= getEmailFromPreferencesOrFb();
	       	        if(email!=null) {
	       	        	UserProfileTable existingUserProfile= GlobalDatabaseHandler.GetUserProfile(getApplicationContext(),email);
	       	        	if(existingUserProfile!=null && (existingUserProfile!=null && existingUserProfile.getEmail()!=null && !existingUserProfile.getEmail().isEmpty())){
	       	        		invokeMainActivity(getString(R.string.title1),
	    							R.drawable.icon);
	       	        	
	       	        	}
	       	        	else{
	       	        		new GetUserProfileJsonTask()
	    					.execute(
	    							"http://shipbobapi.azurewebsites.net/api/Profile/GetUserProfile/?email="+email);
	       	        	}
	       	        }
				
				}

				else {
					InsertNewUser();
				}

			}

			catch (Exception e) {
				Log.d("JSONFeedTask", e.getLocalizedMessage());
				Intent logOutintent = new Intent(LoginActivity.this, OrdersActivity.class);
				startActivity(logOutintent);
			}
		}
	}

	public String readJSONFeed(String URL) {
		return globalmethods.MakeandReceiveHTTPResponse(URL);
	}

	public void checkIfUserExists(GraphUser user) {
		if (user != null) {
			String email = user.getProperty("email").toString();
			setEmailSharedPrferences(user);
			new JSONFeedTask()
					.execute("http://shipbobapi.azurewebsites.net/api/Profile/UserExists/?identifier="
							+ email);
		}
	}

	private class InsertNewUserJsonTask extends AsyncTask<Object, Void, String> {
		ProgressDialog progDailog = new ProgressDialog(LoginActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog.setMessage("Fetching your Information...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(Object... params) {
			String url = (String) params[0];
			JSONObject j = (JSONObject) params[1];
			return makeInsertNewUserUrlRequest(url, j);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				JSONObject jsonResult = new JSONObject(result);
				String successResponse = jsonResult.getString("Success");
				String payload = jsonResult.getString("PayLoad");
				JSONObject userProfile = new JSONObject(payload);
				if (successResponse.equals("true")) {
					// insert userProfileInformation into the database
					UserProfileTable userProfileTable = new UserProfileTable();
					userProfileTable.FirstName = userProfile.getString("FirstName");
					userProfileTable.LastName = userProfile.getString("LastName");
					userProfileTable.UserId = userProfile.getInt("UserId");
					userProfileTable.Email = userProfile.getString("Email");
				
					GlobalDatabaseHandler.InsertUserProfileInSqlLite(
							LoginActivity.this, userProfileTable);
					
					setEmailSharedPrferences();
					MoveToCompletePhoneNumberActivity(true);

				} else {
					
							//TO DO:: ERROR PAGE
				}

			}

			catch (Exception e) {
				//TO DO:: ERROR PAGE
				}
		}
	}

	public String makeInsertNewUserUrlRequest(String URL, JSONObject jsonObject) {
		return globalmethods.MakePostRequestWithJsonObject(URL, jsonObject);
	}

	private void InsertNewUser() {

		String firstName = facebookGraphUser.getFirstName();
		String lastName = facebookGraphUser.getLastName();
		String email = facebookGraphUser.getProperty("email").toString();
		String facebookUid = facebookGraphUser.getId();
		try {
			JSONObject j = new JSONObject();
			j.put("Email", email);
			j.put("FirstName", firstName);
			j.put("LastName", lastName);
			j.put("FacebookUid", facebookUid);

			new InsertNewUserJsonTask()
					.execute(
							"http://shipbobapi.azurewebsites.net/api/Profile/InsertNewUser",
							j);

		} catch (Exception e) {
			Intent logOutintent = new Intent(LoginActivity.this, OrdersActivity.class);
			startActivity(logOutintent);
		}
	}

	private void invokeMainActivity(String title, int resId) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(EXTRA_TITLE, "Home");
		intent.putExtra(EXTRA_RESOURCE_ID, resId);
		intent.putExtra(EXTRA_MODE, 0);
		setEmailSharedPrferences();

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		onDestroy();
		startActivity(intent);
		finish();
	}

	private void MoveToCompletePhoneNumberActivity(boolean firstTimeLogin) {
		//setting the email in the shared preferences
		Intent intent = new Intent(LoginActivity.this, CompleteProfile_PhoneNumber.class);
		intent.putExtra(EXTRA_TITLE, "PhoneNumber");
		intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
		intent.putExtra(EXTRA_MODE, 0);
		if(firstTimeLogin){
		intent.putExtra("FirstTimeLogin", "true");
		}
		startActivity(intent);
		// no animation of transition
		overridePendingTransition(0, 0);
	}
	
	
	private void setEmailSharedPrferences(){
		setEmailSharedPrferences(null);
	}
	private void setEmailSharedPrferences(GraphUser user){
		
		if(user!=null){
			 globalmethods.setDefaultsForPreferences("email", user.getProperty("email").toString(), LoginActivity.this);
			 return;
		}
		if(facebookGraphUser!=null){
			 globalmethods.setDefaultsForPreferences("email", facebookGraphUser.getProperty("email").toString(), LoginActivity.this);
		}
	}
	
	private String getEmailFromPreferencesOrFb(){
        String email= globalmethods.getDefaultsForPreferences("email", getApplicationContext());
        if(email!=null) return email;
        
        if(facebookGraphUser!=null) return facebookGraphUser.getProperty("email").toString();
        else{
        	return null;
        	
        }
        
	}

	 
	private class GetUserProfileJsonTask extends AsyncTask<Object, Void, String> {
		ProgressDialog progDailog = new ProgressDialog(LoginActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog.setMessage("Saving your Information...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(Object... params) {
			String url = (String) params[0];
			return makeGetProfileRequest(url);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				JSONObject jsonResult = new JSONObject(result);
				String successResponse = jsonResult.getString("Success");
				String payload = jsonResult.getString("PayLoad");
				JSONObject userProfile = new JSONObject(payload);
				if (successResponse.equals("true")) {
					// insert userProfileInformation into the database
					UserProfileTable userProfileTable = new UserProfileTable();
					userProfileTable.FirstName = userProfile.getString("FirstName");
					userProfileTable.LastName = userProfile.getString("LastName");
					userProfileTable.UserId = userProfile.getInt("UserId");
					userProfileTable.Email = userProfile.getString("Email");
					userProfileTable.PhoneNumber = userProfile.getString("PhoneNumber");
					userProfileTable.LastFourCreditCard=userProfile.getString("LastFourCreditCard");
					userProfileTable.CreditCardType=userProfile.getString("CreditCardType");
					userProfileTable.CreditCardType=userProfile.getString("CreditCardType");
					userProfileTable.CardExpiryMonth=userProfile.getString("CardExpiryMonth");
					userProfileTable.CardExpiryYear=userProfile.getString("CardExpiryYear");

					GlobalDatabaseHandler.InsertUserProfileInSqlLite(
							LoginActivity.this, userProfileTable);
					
					if(userProfileTable.PhoneNumber==null ||userProfileTable.PhoneNumber=="" ||userProfileTable.PhoneNumber.isEmpty() || userProfileTable.PhoneNumber.equals("null")){
						MoveToCompletePhoneNumberActivity(false);
	 	        	}
	 	        	else if (userProfileTable.LastFourCreditCard==null||userProfileTable.LastFourCreditCard.equals("null")||userProfileTable.LastFourCreditCard.isEmpty()||userProfileTable.LastFourCreditCard==""){
	 	        		MoveToCompleteCreditCardProfile(false);
	 	        	}
	 	        	else invokeMainActivity(getString(R.string.title1),R.drawable.icon);
					
				

				} else {
					
							//TO DO:: ERROR PAGE
				}

			}

			catch (Exception e) {
				//TO DO:: ERROR PAGE
				}
		}
	}

	private void MoveToCompleteCreditCardProfile(boolean firstTimeLogin){
		
		Intent intent = new Intent(LoginActivity.this,CompleteProfile_CreditCard.class);
		if(firstTimeLogin){
		intent.putExtra("FirstTimeLogin", "true");
		}
		startActivity(intent);
	}
	
	public String makeGetProfileRequest(String Url) {
		return globalmethods.MakeandReceiveHTTPResponse(Url);
	}

}

