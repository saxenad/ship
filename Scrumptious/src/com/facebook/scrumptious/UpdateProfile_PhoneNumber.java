package com.facebook.scrumptious;


import org.json.JSONObject;

import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTable;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTableDatabaseHandler;
import com.facebook.scrumptious.GlobalMethods.globalmethods;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class UpdateProfile_PhoneNumber extends FragmentActivity {

	private EditText phoneNumber;
	private Button saveButton;
	private UserProfileTable userProfileTable;
	
	public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
	public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
	public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
        setContentView(R.layout.addphonenumber);

        String title = " Update Profile";
        setTitle(title);
        
        
        saveButton=(Button)findViewById(R.id.savePhoneNumber);
        saveButton.setText("Save");
        phoneNumber=(EditText)this.findViewById(R.id.phoneNumber);
        
        //hide the cross button as inheriting the same view.
        ImageButton crossButton= (ImageButton)findViewById(R.id.crossButton);
        crossButton.setVisibility(View.GONE);
        AttachClickEventToSavePhoneNumber();
        getActionBar().setDisplayHomeAsUpEnabled(true);

      
    }
    
    
    @Override
       public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(UpdateProfile_PhoneNumber.this,ProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    
	  
		  private void AttachClickEventToSavePhoneNumber(){
			  
			  saveButton.setOnClickListener(new OnClickListener() {
	                 public void onClick(View v) {
	                
	                	  String emailAddress= globalmethods.getDefaultsForPreferences("email", getApplicationContext());
	            		  UserProfileTable existingUserProfile= GlobalDatabaseHandler.GetUserProfile(getApplicationContext(),emailAddress);
	             	        if(existingUserProfile!=null){
	             	        	userProfileTable=existingUserProfile;
	             	        	userProfileTable.setPhoneNumber(phoneNumber.getText().toString());
	             	        long returnedInteger=new UserProfileTableDatabaseHandler(UpdateProfile_PhoneNumber.this).updateUserProfilePhoneNumber(emailAddress, phoneNumber.getText().toString());
	             	        if(returnedInteger>0){
	             	        	updatePhoneNumber(phoneNumber.getText().toString());
	             	        }
	             	        else{
	             	        	//To DO:: Error in Adding Data to SQLLITE. Create Record in SQLLITE Again.
	             	        }
	             	        
	             	        }
	             	        else{
	             	        	//TO DO:: Make Sure userProfileTable is not null. Make a JSON Call to Server to populate the profile Information
	             	        }
	   	                 }
	             });
		  }

		  
		  //Json Stuff
			public String MakeUpdatePhoneNumberRequest(String URL,JSONObject j) {

				return globalmethods.MakePostRequestWithJsonObject(URL,j);
			}
			
			private class JSONPOSTFeedTask extends AsyncTask<Object, Void, String> {
				ProgressDialog progDailog = new ProgressDialog(UpdateProfile_PhoneNumber.this);

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					progDailog.setMessage("Saving Phone Number...");
					progDailog.setIndeterminate(false);
					progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progDailog.setCancelable(true);
					progDailog.show();
				}

				protected String doInBackground(Object... params) {
					String url = (String) params[0];
					JSONObject j = (JSONObject) params[1];
					return MakeUpdatePhoneNumberRequest(url, j);
				}
				
				protected void onPostExecute(String result) {
					try {
						progDailog.dismiss();
						JSONObject jsonResult = new JSONObject(result);
						String successResponse = jsonResult.getString("Success");
						String payload = jsonResult.getString("PayLoad");
						JSONObject userProfile = new JSONObject(payload);
						if (successResponse.equals("true")) {
							MoveBackToProfileActivity();
					}
						else
							{
								//TO DO Show Error Alert and Make User Save Again. 
								// Show Alert.Class
							}
				
					}
					
					catch (Exception e) {
				
							Log.d("JSONFeedTask", e.getLocalizedMessage());
							progDailog.dismiss();
							//TO DO Show Error Alert and Make User Save Again. 
							// Show Alert.Class
				}
			}
			}

			public void updatePhoneNumber(String phoneNumber) {
				
				try {
					JSONObject j = new JSONObject();
					j.put("EmailAddress", userProfileTable.getEmail());
					j.put("PhoneNumber", phoneNumber);
					new JSONPOSTFeedTask()
							.execute(
									"http://shipbobapi.azurewebsites.net/api/Profile/UpdateUserPhoneNumber",j);

				} catch (Exception e) {
					//TO DO:: Start ErrorActivity
					
				}
	
			}
			

			
			private void MoveBackToProfileActivity(){
				
				Intent intent = new Intent(UpdateProfile_PhoneNumber.this,ProfileActivity.class);
				intent.putExtra(EXTRA_TITLE, "My Profile");
				intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
				intent.putExtra(EXTRA_MODE, 0);
				startActivity(intent);
				startActivity(intent);
			}
			

			
}
