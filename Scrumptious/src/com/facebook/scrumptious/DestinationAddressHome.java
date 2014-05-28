package com.facebook.scrumptious;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.ShippingInformationDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.ShippingInformationTable;
import com.facebook.scrumptious.GlobalMethods.globalmethods;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DestinationAddressHome extends FragmentActivity  {
	private Context context;
	Button AddNewContact;
    ShippingInformationTable shipInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=DestinationAddressHome.this;
        setContentView(R.layout.destinationaddresshome);
        String title = "Choose Destination Address";
        setTitle(title);
        String email= globalmethods.getDefaultsForPreferences("email", getApplicationContext());
        AddNewContact=(Button)findViewById(R.id.addNewContact);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        AttachClickEventToAddNewContact();
        Long shipInformationTableId_long= getIntent().getLongExtra("shipInformationTableId",-1);
        int shipInformationTableId=shipInformationTableId_long.intValue();
        shipInformation= GlobalDatabaseHandler.GetShippingInformationTable(this,shipInformationTableId);
        
        retrieveUserContacts(email);
       	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(DestinationAddressHome.this,ShippingInformation.class);
        	Long shipInformationTableId= getIntent().getLongExtra("shipInformationTableId",-1);
    		intent.putExtra("shipInformationTableId", shipInformationTableId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void AttachClickEventToAddNewContact(){
       AddNewContact.setOnClickListener(new View.OnClickListener() {
  	         @Override
  	         public void onClick(View v) {
  	           Long shipInformationTableId_long= getIntent().getLongExtra("shipInformationTableId",-1);
  	        	Intent intent = new Intent(DestinationAddressHome.this,DestinationAddress.class);
	    		intent.putExtra("shipInformationTableId", shipInformationTableId_long);

  	  		startActivityForResult(intent, 0);

  	        }
  	    });
    	
    }
    
    public void retrieveUserContacts(String email) {
		new JSONFeedTask()
				.execute("http://shipbobapi.azurewebsites.net/api/UserContacts/GetUserContacts/?email="+email);

	}

	private class JSONFeedTask extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog = new ProgressDialog(DestinationAddressHome.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog.setMessage("Loading Contacts");
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
				FillContactsTable(result);
				progDailog.dismiss();

			} catch (Exception e) {
				Log.d("JSONFeedTask", e.getLocalizedMessage());
				progDailog.dismiss();
			}
		}
	}
	
	public String readJSONFeed(String URL) {
		return globalmethods.MakeandReceiveHTTPResponse(URL);
	}

	  private void FillContactsTable(String contactsResult){
		  
			JSONObject jsonObject;
			List<UserContacts> userContacts=new ArrayList<UserContacts>();

			try {
				jsonObject = new JSONObject(contactsResult);
				JSONArray userContactsJsonArray = jsonObject.getJSONArray("PayLoad");

				for (int i=0; i < userContactsJsonArray.length(); i++) {
				    JSONObject jUserContact = userContactsJsonArray.getJSONObject(i);
					UserContacts contact = new UserContacts();  // create a new object here
				    contact.UserContactId = (int)jUserContact.getInt("UserContactId");
				    contact.UserId = (int)jUserContact.getInt("UserId");
				    contact.StreetAddress1 = jUserContact.getString("StreetAddress1");
				    contact.StreetAddress2 = jUserContact.getString("StreetAddress2");
				    contact.HouseNumber = jUserContact.getString("HouseNumber");
				    contact.ZipCode = jUserContact.getString("ZipCode");
				    contact.Name = jUserContact.getString("Name");
				    contact.State = jUserContact.getString("State");
				    contact.City = jUserContact.getString("City");

				    userContacts.add(contact);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			ScrollView lv = (ScrollView) findViewById(R.id.ListViewForContacts);
			LinearLayout childln = (LinearLayout) findViewById(R.id.LinearLayoutForContacts);
			childln.setVisibility(View.INVISIBLE);

			int k=0;
			for (int current = 0; current <userContacts.size() ; current++) {

				childln.setVisibility(View.INVISIBLE);
				LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v =  inflater.inflate(R.layout.usercontactslistview, null);
				TextView  contactName   = (TextView)    v.findViewById(R.id.contactName);

				contactName.setText(userContacts.get(current).Name);
				contactName.setTextColor(Color.BLACK);
				TextView  contactAddress   = (TextView)v.findViewById(R.id.contactAddress);
				contactAddress.setText(userContacts.get(current).HouseNumber);
				contactAddress.setTextSize(17);
				contactAddress.setTextColor(Color.BLACK);
				contactName.setOnClickListener(updateShippingInformationTable(userContacts.get(current),contactName));

				childln.addView(v);
			} 
			childln.setVisibility(View.VISIBLE);
			//lv.addView(childln);
		}
	  
		private String constructDestinationAddress(UserContacts userContact){
		           String constructedAddress= userContact.HouseNumber.toString()+","+userContact.StreetAddress1.toString()+","+
	        		   userContact.City.toString()+","+userContact.State.toString()+","+userContact.ZipCode.toString();
	 			
	           return constructedAddress.toString();
	 		}

		private OnClickListener updateShippingInformationTable(final UserContacts userContact, final TextView contactName){
			 
	        return new View.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(View v) {
					
					contactName.setBackground(getResources().getDrawable(R.drawable.bluebackgroundcolor));
					String destinationAddress=constructDestinationAddress(userContact);
				       int returnedInteger=new ShippingInformationDatabaseHandler(DestinationAddressHome.this).
				    		   updateShipInformationDestinationAddress(shipInformation.ShipInformationTableId, destinationAddress ,shipInformation.getContactName());
				       
					 Intent intent = new Intent(DestinationAddressHome.this,ShippingInformation.class);
					  	Long shipInformationTableId= getIntent().getLongExtra("shipInformationTableId",-1);
							intent.putExtra("shipInformationTableId", shipInformationTableId);
					      startActivity(intent);
				};
	 		};
		}
}
