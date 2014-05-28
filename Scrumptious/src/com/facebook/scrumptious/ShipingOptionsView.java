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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ShipingOptionsView extends FragmentActivity  {
	private Context context;
	Button AddNewContact;
    ShippingInformationTable shipInformation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=ShipingOptionsView.this;
        setContentView(R.layout.shipingoptions);
        String title = "Choose a Shipping Option";
        setTitle(title);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Long shipInformationTableId_long= getIntent().getLongExtra("shipInformationTableId",-1);
        int shipInformationTableId=shipInformationTableId_long.intValue();
        shipInformation= GlobalDatabaseHandler.GetShippingInformationTable(this,shipInformationTableId);
        retrieveShippingOptions();
       	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(ShipingOptionsView.this,ShippingInformation.class);
        	Long shipInformationTableId= getIntent().getLongExtra("shipInformationTableId",-1);
    		intent.putExtra("shipInformationTableId", shipInformationTableId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void retrieveShippingOptions() {
		new JSONFeedTask()
				.execute("http://shipbobapi.azurewebsites.net/api/Shipping/GetShippingOptions");

	}

	private class JSONFeedTask extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog = new ProgressDialog(ShipingOptionsView.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog.setMessage("Loading Shipping Options");
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
			final List<ShippingOptions> userContacts=new ArrayList<ShippingOptions>();

			try {
				jsonObject = new JSONObject(contactsResult);
				JSONArray userContactsJsonArray = jsonObject.getJSONArray("PayLoad");

				for (int i=0; i < userContactsJsonArray.length(); i++) {
				    JSONObject jUserContact = userContactsJsonArray.getJSONObject(i);
				    ShippingOptions contact = new ShippingOptions();  // create a new object here
				    contact.ShipOptionsId = jUserContact.getString("ShipOptionsId");
				    contact.Description = jUserContact.getString("Description");
				    contact.Name = jUserContact.getString("Name");
				    userContacts.add(contact);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			ScrollView lv = (ScrollView) findViewById(R.id.ListViewForShippingOptions);
			LinearLayout childln = (LinearLayout) findViewById(R.id.LinearLayoutForShippingOptions);
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
				contactAddress.setText(userContacts.get(current).Description);
				contactAddress.setTextSize(17);
				contactAddress.setTextColor(Color.BLACK);
			
				contactName.setOnClickListener(updateShippingInformationTable(userContacts.get(current),contactName));
				childln.addView(v);
				
			} 
			childln.setVisibility(View.VISIBLE);
			//lv.addView(childln);
		}

	
	
		private OnClickListener updateShippingInformationTable(final ShippingOptions shipOption, final TextView contactName){
	 
	        return new View.OnClickListener() {
				public void onClick(View v) {
					contactName.setBackground(getResources().getDrawable(R.drawable.bluebackgroundcolor));
				       int returnedInteger=new ShippingInformationDatabaseHandler(ShipingOptionsView.this).
	        					updateShipInformationOption(shipInformation.ShipInformationTableId, shipOption.Name);
					 Intent intent = new Intent(ShipingOptionsView.this,ShippingInformation.class);
					  	Long shipInformationTableId= getIntent().getLongExtra("shipInformationTableId",-1);
							intent.putExtra("shipInformationTableId", shipInformationTableId);
					      startActivity(intent);
				};
	 		};
		}
		 
}
