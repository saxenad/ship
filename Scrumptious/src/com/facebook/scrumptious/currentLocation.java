package com.facebook.scrumptious;


import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTable;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTableDatabaseHandler;
import com.facebook.scrumptious.GlobalMethods.globalmethods;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class currentLocation extends FragmentActivity {

	private EditText streetAddress;
	private Button saveAddress;
	private UserProfileTable userProfileTable;
	private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currentlocation);
        context=currentLocation.this;
        
        String title = getIntent().getStringExtra(com.facebook.scrumptious.MainActivity.EXTRA_TITLE);
        setTitle(title);
       
        String email= globalmethods.getDefaultsForPreferences("email", getApplicationContext());

        userProfileTable=GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), email);
        streetAddress=(EditText)this.findViewById(R.id.street);
        saveAddress=(Button)findViewById(R.id.saveAddress);
        AttachClickEventToSaveAddress();
        getActionBar().setDisplayHomeAsUpEnabled(true);
      
       	}
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(currentLocation.this,MainActivity.class);
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

		  private void AttachClickEventToSaveAddress(){
			  
			  saveAddress.setOnClickListener(new OnClickListener() {
	                 public void onClick(View v) {
	                
	                	   String title = getResources().getString(R.string.shippingAdress);
	   		            final EditText input = new EditText(context);
	   		           
	   		            LayoutInflater inflater = getLayoutInflater();

	   		 	       final View destinationAddress = inflater.inflate(R.layout.confirmcurrentlocation,null);
	   		        TextView confirmCurrentLocation=(TextView)destinationAddress.findViewById(R.id.confirmCurrentLocation);
	   		     confirmCurrentLocation.setText(streetAddress.getText().toString()); 
	   		        AlertDialog.Builder builder = new AlertDialog.Builder(context);
	   	             
	   		            builder.setTitle(title)
	   		                    .setCancelable(true)
	   		                    .setView(destinationAddress)
	   		                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	   		                        @Override
	   		                        public void onClick(DialogInterface dialogInterface, int i) {
	   		                         String email=getIntent().getStringExtra("email");
	   		                      Intent intent = new Intent(currentLocation.this,MainActivity.class);
	   		                      intent.putExtra("currentLocation","HoHa");
	   		                      intent.putExtra("manualZipCode","60661");
	   		                      startActivity(intent);
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
    
}
