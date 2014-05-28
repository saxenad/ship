package com.facebook.scrumptious;


import java.util.ArrayList;

import org.json.JSONObject;

import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTable;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTableDatabaseHandler;
import com.facebook.scrumptious.GlobalMethods.globalmethods;
import com.stripe.android.model.Token;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class DestinationAddress extends FragmentActivity {

	private EditText streetAddress1;
	private TextView streetAddress2;
	private Button saveContact;
	private UserProfileTable userProfileTable;
	private Context context;
	EditText contactName;
	EditText apptNumber;
	EditText streetAddress;
	EditText city;
	EditText zipCode;
	EditText country;

		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destinationaddress);
        context=DestinationAddress.this;

        String title = "Add a New Contact";
        int resId = getIntent().getIntExtra(com.facebook.scrumptious.MainActivity.EXTRA_RESOURCE_ID, 0);
        setTitle(title);
        String email= globalmethods.getDefaultsForPreferences("email", getApplicationContext());
        userProfileTable=GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), email);
    
        saveContact=(Button)findViewById(R.id.saveContact);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        AttachClickEventToSaveAddress();
        
 
       	}
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(DestinationAddress.this,DestinationAddressHome.class);
	           Long shipInformationTableId_long= getIntent().getLongExtra("shipInformationTableId",-1);
	           intent.putExtra("shipInformationTableId", shipInformationTableId_long);

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    
	  
		public UserProfileTable GetUserProfile(Context c, String email)
		{
			
			UserProfileTableDatabaseHandler userDbHandler=new UserProfileTableDatabaseHandler(c);
	       UserProfileTable existingUserProfile= userDbHandler.getContact(email);
	       return existingUserProfile;
		}
		
		
        AlertDialog.Builder builder;

	private String constructDestinationAddress(){
	    contactName = (EditText)findViewById(R.id.contactName);
    	 apptNumber = (EditText)findViewById(R.id.apptNumber);
    	 streetAddress = (EditText)findViewById(R.id.streetName);
    	 city= (EditText)findViewById(R.id.city);
    	 zipCode= (EditText)findViewById(R.id.zipCode);
    	 country= (EditText)findViewById(R.id.Country);
    	
	           String constructedAddress= apptNumber.getText().toString()+","+streetAddress.getText().toString()+","+
	        		   city.getText().toString()+","+country.getText().toString()+","+zipCode.getText().toString();
 			
           return constructedAddress.toString();
 		}
	
		  private void AttachClickEventToSaveAddress(){
			  
			  saveContact.setOnClickListener(new OnClickListener() {
	                 public void onClick(View v) {
	                
	 	   		        if(!validateContact()) return;

	                	   String title = getResources().getString(R.string.shippingAdress);
	   		            final EditText input = new EditText(context);
	   		           
	   		            LayoutInflater inflater = getLayoutInflater();

	   		 	       final View destinationAddress = inflater.inflate(R.layout.confirmcurrentlocation,null);
	   		        TextView confirmCurrentLocation=(TextView)destinationAddress.findViewById(R.id.confirmCurrentLocation);
	   		        
	   		  
	   		        confirmCurrentLocation.setText(constructDestinationAddress()); 
	   		        AlertDialog.Builder builder = new AlertDialog.Builder(context);
	   	             
	   		            builder.setTitle(title)
	   		                    .setCancelable(true)
	   		                    .setView(destinationAddress)
	   		                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	   		                        @Override
	   		                        public void onClick(DialogInterface dialogInterface, int i) {
	   		                        	insertNewContact();
	   		                        }
	   		                    })
	   		                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	   		                        @Override
	   		                        public void onClick(DialogInterface dialogInterface, int i) {
	   		                        }
	   		                    });
	   		            
	   		            AlertDialog dialog = builder.create();
	   		            // always popup the keyboard when the alert dialog shows
	   		            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	   		            dialog.show();
	   	        	 
	   		            
	   		            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	   		            lp.copyFrom(dialog.getWindow().getAttributes());
	   		            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	   		            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
	   		            dialog.show();
	   		            dialog.getWindow().setAttributes(lp);
	                 }
	             });
		  }

			public void insertNewContact() {

				try {
					
		 			UserContacts contact=new UserContacts();
		 			contact.Name=contactName.getText().toString();
		 			contact.StreetAddress1=streetAddress.getText().toString();
		 			contact.HouseNumber=apptNumber.getText().toString();
		 			contact.City= city.getText().toString();
		 			contact.Country=country.getText().toString();
		 			contact.ZipCode=zipCode.getText().toString();

					JSONObject j = new JSONObject();
					j.put("Name", contact.Name);
					j.put("StreetAddress1", contact.StreetAddress1);
					j.put("HouseNumber", contact.HouseNumber);
					j.put("City", contact.City);
					j.put("State", contact.State);
					j.put("Country", contact.Country);
					j.put("ZipCode",contact.ZipCode);
					j.put("EmailAddress",userProfileTable.getEmail());

					new JSONPOSTFeedTask()
							.execute(
									"http://shipbobapi.azurewebsites.net/api/UserContacts/InsertUserContact",j);

				} catch (Exception e) {
					// TO DO Show Error Alert and Make User Save Again.
					// Show Alert.Class

				}

			}
		  
		
			private class JSONPOSTFeedTask extends AsyncTask<Object, Void, String> {
				ProgressDialog progDailog = new ProgressDialog(DestinationAddress.this);

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					progDailog.setMessage("Adding Contact...");
					progDailog.setIndeterminate(false);
					progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progDailog.setCancelable(true);
					progDailog.show();
				}

				protected String doInBackground(Object... params) {
					String url = (String) params[0];
					JSONObject j = (JSONObject) params[1];
					return MakeInsertContactRequest(url, j);
				}

				protected void onPostExecute(String result) {
					try {
						progDailog.dismiss();
						JSONObject jsonResult = new JSONObject(result);
						String successResponse = jsonResult.getString("Success");
						String payload = jsonResult.getString("PayLoad");
						JSONObject userProfile = new JSONObject(payload);
						if (successResponse.equals("true")) {
							MoveToNextActivity();
						} else {
							// TO DO:: Move To Error Page
						}
					}

					catch (Exception e) {

						Log.d("JSONFeedTask", e.getLocalizedMessage());
						progDailog.dismiss();
						// TO DO Show Error Alert and Make User Save Again.
						// Show Alert.Class
					}
				}
			}

			// Json Stuff
			public String MakeInsertContactRequest(String URL, JSONObject j) {
				return globalmethods.MakePostRequestWithJsonObject(URL, j);
			}
			
			public boolean validateContact(){
				boolean validateContactResult=true;
				String errorNames = null;
			     EditText enteredzipCode = (EditText)findViewById(R.id.zipCode);
		   		  if( enteredzipCode.getText().toString().length() == 0 ){
		   			enteredzipCode.setError( "Zip Code is required!" );
		   			validateContactResult=false;
		   			errorNames="ZipCode";
		   		  }
		   		  
		   		EditText contactName = (EditText)findViewById(R.id.contactName);
		  	    if( contactName.getText().toString().length() == 0 ){
		  	    	contactName.setError( "Contact Name is required!" );
		   			validateContactResult=false;
		   			if(errorNames!=null) {
		   				errorNames=errorNames.concat(", Contact Name");
		   			}
		   			else errorNames="Contact Name";
		   		  }
		  	    else{
		  	    	contactName.setError(null);
		  	    }
		  	   
		 		EditText streetName = (EditText)findViewById(R.id.streetName);
		  	    if( streetName.getText().toString().length() == 0 ){
		  	    	streetName.setError( "Street Name is required!" );
		   			validateContactResult=false;
		   			if(errorNames!=null) {
		   				errorNames=errorNames.concat(", Street Name");
		   			}
		   			else errorNames="Street Name";
		   		  }
		  	  else{
		  		streetName.setError(null);
		  	    }
		  	    
		 		EditText city = (EditText)findViewById(R.id.city);
		  	    if( city.getText().toString().length() == 0 ){
		  	    	city.setError( "Street Name is required!" );
		   			validateContactResult=false;
		   			if(errorNames!=null) {
		   				errorNames=errorNames.concat(", city");
		   			}
		   			else errorNames="City";
		   		  }
		  	  else{
		  		streetName.setError(null);
		  	    }
		  	    
		 		EditText country = (EditText)findViewById(R.id.Country);
		  	    if( country.getText().toString().length() == 0 ){
		  	    	country.setError( "Country is required!" );
		   			validateContactResult=false;
		   			if(errorNames!=null) {
		   				errorNames=errorNames.concat(", Country");
		   			}
		   			else errorNames="Country";
		   		  }
		  	  else{
		  		country.setError(null);
		  	    }
		  	    
		  	    if(!validateContactResult){
		  	  Toast.makeText(this, errorNames+" is required.", 
		                Toast.LENGTH_SHORT).show();
		  	    }
		  	    return validateContactResult;
		   		  
		  	 
			}
	
			private void MoveToNextActivity(){
			   	Intent intent = new Intent(DestinationAddress.this,DestinationAddressHome.class);
                 	Long shipInformationTableId_long= getIntent().getLongExtra("shipInformationTableId",-1);
           			intent.putExtra("shipInformationTableId", shipInformationTableId_long);
           			startActivity(intent);
			}

}
