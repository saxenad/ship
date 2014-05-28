package com.facebook.scrumptious;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.ReturnAddressClass;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTable;
import com.facebook.scrumptious.GlobalMethods.globalmethods;
import com.facebook.scrumptious.Stripe.PaymentActivity;
import com.facebook.widget.ProfilePictureView;
import com.facebook.Request;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends MainActivity {
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private TextView emailAddress;
	private TextView creditCard;
	private TextView address;
	private TextView phoneNumber;
	private UserProfileTable userProfile;
	private String userEmail;
	private static int FirstActivityRequestCode = 1;

	
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        LayoutInflater inflater = getLayoutInflater();

	       View view = inflater.inflate(R.layout.myprofile, (ViewGroup) findViewById(R.id.container));
	        
	       	profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
	        profilePictureView.setCropped(true);
	        userNameView=(TextView)view.findViewById(R.id.selection_user_name);
	      
	        emailAddress=(TextView)view.findViewById(R.id.emailAddress);
	        phoneNumber= (TextView)view.findViewById(R.id.phoneNumber);
	        userEmail=globalmethods.getDefaultsForPreferences("email", getApplicationContext());
	        creditCard=(TextView)view.findViewById(R.id.creditCardNumber);
	        address=(TextView)view.findViewById(R.id.address);
	        
       	    UserProfileTable existingUserProfile= GlobalDatabaseHandler.GetUserProfile(getApplicationContext(),userEmail);
       	    if(existingUserProfile!=null) userProfile=existingUserProfile;
            //Setting Values
       	    SetDefaultValues();


	   /*     String str="Underlined Text";
	        SpannableString contentUnderline = new SpannableString(str);
	         contentUnderline.setSpan(new UnderlineSpan(), 0,
	                 contentUnderline.length(), 0);
	         phoneNumber.setText(contentUnderline);*/
	         
	        
		     FragmentManager manager = getFragmentManager();
		     new globalmethods(view,manager).hideMainFragment();

		    String title ="My Profile";
            setTitle(title);
            
	        AttachClickEventToCreditCard();
	        AttachClickEventToAddress();
	        AttachClickEventToPhoneNumber();
	        
    	    Session session=Session.getActiveSession();
	        if(session==null){                      
    	        // try to restore from cache
    	        session = Session.openActiveSessionFromCache(getApplicationContext() );
    	    }

    	    if(session!=null && session.isOpened()){   
    	        makeMeRequest(session);
    	    }
    	    else
    	    {
    	    	// TO DO:: Show ERROR Page
    	    }
    	    
    	    
	    }

	  public void onPause() {
		  super.onPause();
		  this.finish();
		  }
	  public void onStop() {
		  super.onStop();
		  this.finish();
		  }
		
	  private void SetDefaultValues(){
		  // Phone
		  String profilephoneNumber= userProfile.getPhoneNumber();
		  if(profilephoneNumber!=null ||profilephoneNumber!="" ||!profilephoneNumber.isEmpty() || !phoneNumber.equals("null")){
			  phoneNumber.setText(profilephoneNumber);
		  }
		  
		  // Credit card
		  String creditCardNumber= userProfile.getLastFourCreditCard();
		  if(creditCardNumber!=null ||creditCardNumber!="" ||!creditCardNumber.isEmpty() || !creditCardNumber.equals("null")){
			  creditCard.setText("**** "+ creditCardNumber);
		  }
		  
		  // Return address
		  String returnAddress=constructReturnAddress();
		  if(returnAddress==null ||returnAddress=="" ||returnAddress.isEmpty() || returnAddress.equals("null")){
			  address.setText(returnAddress);
		  }
		  
		  // Email
		  String email=userProfile.getEmail();
		  emailAddress.setText(email);
		  
		  // UserName
		  userNameView.setText(userProfile.getFirstName().toString()+" "+userProfile.getLastName().toString());
		  
		    
	  }
	  
	  private String constructReturnAddress(){
		  String houseNumber= userProfile.HouseNumber;
		  String streetAddress= userProfile.Street_Address1;
		  String city= userProfile.City;
		  String country= userProfile.Country;
		  String zipCode=userProfile.ZipCode;
		  
		   String constructedAddress= houseNumber+","+streetAddress+","+
				   city+","+city+","+country+","+zipCode;
		  
		   return constructedAddress;
	  }
	  private void makeMeRequest(final Session session) {
		    // Make an API call to get user data and define a 
		    // new callback to handle the response.
		    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
		        @Override
		        public void onCompleted(GraphUser user, Response response) {
		            // If the response is successful
		            if (session == Session.getActiveSession()) {
		                if (user != null) {
		                    // Set the id for the ProfilePictureView
		                    // view that in turn displays the profile picture.
		                    profilePictureView.setProfileId(user.getId());
		            
		                }
		            }
		            if (response.getError() != null) {
		                // Handle errors, will do so later.
		            }
		        }
		    });
		    request.executeAsync();
		} 
		  
	  private void AttachClickEventToCreditCard(){
		  
		  creditCard.setOnClickListener(new OnClickListener() {
                 public void onClick(View v) {
                     Intent intent = new Intent(ProfileActivity.this, UpdateProfile_CreditCard.class);
                     intent.putExtra(EXTRA_TITLE, "PaymentActivity");
                     intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
                     intent.putExtra(EXTRA_MODE,  0);
                     startActivityForResult(intent,FirstActivityRequestCode);

                 }
             });
	  }


	  private void AttachClickEventToAddress(){
		  
		  address.setOnClickListener(new OnClickListener() {
                 public void onClick(View v) {
                     Intent intent = new Intent(ProfileActivity.this, ReturnAddress.class);
                     intent.putExtra(EXTRA_TITLE, "Return Address");
                     intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
                     intent.putExtra(EXTRA_MODE,  0);
                     ReturnAddressClass currentAddressClass = new ReturnAddressClass();
                    /* currentAddressClass.setStreetAddress1("Android Developer Guide");  
                     currentAddressClass.setStreetAddress2("Leon");  
                     Bundle b = new Bundle();
                     b.putParcelable("currentAddress",currentAddressClass);
                     intent.putExtras(b);*/
                     startActivityForResult(intent,FirstActivityRequestCode);
                 }
             });
	  }

	  private void AttachClickEventToPhoneNumber(){
		  
		  phoneNumber.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  Intent intent = new Intent(ProfileActivity.this, UpdateProfile_PhoneNumber.class);
                  intent.putExtra(EXTRA_TITLE, "Phone Number");
                  intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
                  intent.putExtra(EXTRA_MODE,  0);
                  startActivityForResult(intent,FirstActivityRequestCode);
              }
          });
	  }

}
