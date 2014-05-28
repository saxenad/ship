package com.facebook.scrumptious;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class logOutActivity extends MainActivity {
	
	
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        LayoutInflater inflater = getLayoutInflater();

	        inflater.inflate(R.layout.splash, (ViewGroup) findViewById(R.id.container));
	        
	        TextView text=(TextView)this.findViewById(R.id.profile_name);
	        text.setText("Log Out from Facebook.");
	    }

}