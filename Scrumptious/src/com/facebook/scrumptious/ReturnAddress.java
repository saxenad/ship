package com.facebook.scrumptious;


import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTable;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTableDatabaseHandler;
import com.facebook.scrumptious.GlobalMethods.globalmethods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ReturnAddress extends FragmentActivity {

	private EditText streetAddress1;
	private TextView streetAddress2;
	private Button saveAddress;
	private UserProfileTable userProfileTable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.returnaddress);

//         setContentView(R.layout.returnaddress);

        String title = getIntent().getStringExtra(com.facebook.scrumptious.MainActivity.EXTRA_TITLE);
        int resId = getIntent().getIntExtra(com.facebook.scrumptious.MainActivity.EXTRA_RESOURCE_ID, 0);
        setTitle(title);
        String email= globalmethods.getDefaultsForPreferences("email", getApplicationContext());
        userProfileTable=GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), email);
        streetAddress1=(EditText)this.findViewById(R.id.street1);
        streetAddress1.setText(userProfileTable.getEmail());
        
        saveAddress=(Button)findViewById(R.id.saveAddress);

        AttachClickEventToSaveAddress();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        /*
        Bundle b = this.getIntent().getExtras();
        if(b!=null){
        	
       
        	ReturnAddressClass currentAddress = (ReturnAddressClass)getIntent().getParcelableExtra("currentAddress");
        streetAddress1=(EditText)this.findViewById(R.id.street1);
        streetAddress1.setText(currentAddress.StreetAddress1);
        }
*/    
       	}
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(ReturnAddress.this,ProfileActivity.class);
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
		
		  private void AttachClickEventToSaveAddress(){
			  
			  saveAddress.setOnClickListener(new OnClickListener() {
	                 public void onClick(View v) {
	                
	                	 userProfileTable.setFirstName("HiThere");
	                	 GlobalDatabaseHandler.InsertUserProfileInSqlLite(ReturnAddress.this, userProfileTable);

	                 }
	             });
		  }
    
}
