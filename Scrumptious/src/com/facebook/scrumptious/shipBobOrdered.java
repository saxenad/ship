package com.facebook.scrumptious;

import org.json.JSONArray;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.scrumptious.GlobalMethods.globalmethods;
import com.facebook.widget.ProfilePictureView;

public class shipBobOrdered extends MainActivity{

	Button trackOrdered;
	
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
		     super.onCreate(savedInstanceState);

		        LayoutInflater inflater = getLayoutInflater();

		        View view = inflater.inflate(R.layout.shipbobordered, (ViewGroup) findViewById(R.id.container));
		        trackOrdered = (Button)view.findViewById(R.id.trackOrder);

		        FragmentManager manager = getFragmentManager();

			     new globalmethods(view,manager).hideMainFragment();
			     
			     String title ="Shipping Order Received";
		            int resId = getIntent().getIntExtra(EXTRA_RESOURCE_ID, 0);
		            setTitle(title);
		      //   js();
		            
		            trackOrdered.setOnClickListener(new View.OnClickListener() {
		   	         @Override
		   	         public void onClick(View v) {
		   	        	
		   	        	 MoveToOrderClass();
		   	        }
		   	  });
		            
	    }
	  
	  public String readJSONFeed(String URL) {

			return globalmethods.MakeandReceiveHTTPResponse(URL);
		}

		private class JSONFeedTask extends AsyncTask<String, Void, String> {
			protected String doInBackground(String... urls) {
				return readJSONFeed(urls[0]);
			}

			protected void onPostExecute(String result) {
				try {

				
					}
				
				 catch (Exception e) {
					Log.d("JSONFeedTask", e.getLocalizedMessage());
				}
			}
		}
		
		// Store ID = 1
		public void js() {
			/*new JSONFeedTask()
					.execute("http://printpictures.azurewebsites.net/Mogreet/GenerateShipBobSMS/?number=12178198539");*/
		}
		
		
	  public void onPause() {
		  super.onPause();
		  this.finish();
		  }
	  public void onStop() {
		  super.onStop();
		  this.finish();
		  }
	  
	
	         
	  public void MoveToOrderClass(){
		   Intent intent = new Intent(this, OrdersActivity.class);
           String fileName= getIntent().getStringExtra("uploadedFileName");
		   intent.putExtra("uploadedFileName", fileName);

           startActivity(intent);
	           // no animation of transition
	           overridePendingTransition(0, 0);
	           finish();		  

	  }
}
