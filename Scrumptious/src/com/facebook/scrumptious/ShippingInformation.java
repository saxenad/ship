package com.facebook.scrumptious;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import BitmapHandling.BitmapScaler;
import android.R.string;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.ShippingInformationDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.ShippingInformationTable;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTableDatabaseHandler;
import com.facebook.scrumptious.GlobalMethods.globalmethods;
import com.facebook.scrumptious.Stripe.PaymentActivity;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

public class ShippingInformation extends MainActivity {

		private ImageView productInformation;
		byte[] imageData = null;
	    private ListView listView;
	    private List<BaseListElement> listElements;
	    private Context context;
	    private TextView shippingAddress;
	    private TextView chooseShippingOption;
	    private Button shipBobIt;
	    private Handler shipBobHandler = new Handler();
	    private String stateName;
	    private String countryName;
	    private String manualZipCode;
	    ShippingInformationTable shipInformation;


	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        LayoutInflater inflater = getLayoutInflater();

	      View  view = inflater.inflate(R.layout.shippinginformation, (ViewGroup) findViewById(R.id.container));
	        
	       productInformation = (ImageView) view.findViewById(R.id.productThumbnail);
	  
	       FragmentManager manager = getFragmentManager();

		     new globalmethods(view,manager).hideMainFragment();

	        String title = "Ready To Ship";
	        int resId = getIntent().getIntExtra(EXTRA_RESOURCE_ID, 0);
	        setTitle(title);
	        Long shipInformationTableId_long= getIntent().getLongExtra("shipInformationTableId",-1);
	        int shipInformationTableId=shipInformationTableId_long.intValue();
	        shipInformation= GlobalDatabaseHandler.GetShippingInformationTable(this,shipInformationTableId);
	        
	        chooseShippingOption =(TextView)view.findViewById(R.id.chooseShippingOption);
	        shippingAddress=(TextView)view.findViewById(R.id.chooseShippingAddress);
	        shipBobIt=(Button)view.findViewById(R.id.btnSubmit);
	        
	        //Setting Defaults/PreAssigns
	        ShowThumbnail(shipInformation.getImageFileName());
	        if(shipInformation.getShipOption()!=null && shipInformation.getShipOption()!=""){
	        	chooseShippingOption.setText("ShippingOption:  "+shipInformation.getShipOption());
	        }
	        
	        if(shipInformation.getDestinationAddress()!=null && shipInformation.getDestinationAddress()!=""){
	        	shippingAddress.setText(shipInformation.getDestinationAddress());
	        }
	        
	        
	        context=ShippingInformation.this;
	        
	        
	        shipBobIt.setEnabled(false);
	        setOnClickListeners();
	        
	  }
	  
	  
	  public void onPause() {
		  super.onPause();
		  this.finish();

		  }
	  public void onStop() {
		  super.onStop();
		  this.finish();
	

		  }
		
	  

	 

	    
	    private void ShowThumbnail(String file){
    try     
    {
        File fileName = new File(file);
        final int THUMBNAIL_SIZE = 512;
        BitmapScaler scaler = new BitmapScaler(fileName, THUMBNAIL_SIZE);
        productInformation.setImageBitmap(scaler.getScaled());

    }
    
    catch(Exception ex) {

    }
    
}
	
	 
	    
	    private void setOnClickListeners(){
	        final String[] shipChoices;
	        final String[] foodUrls;
	        final	String foodChoice = null;

	        
	    	shippingAddress.setOnClickListener(new View.OnClickListener() {
	         @Override
	         public void onClick(View v) {
	        		Intent intent = new Intent(ShippingInformation.this,
	        				DestinationAddressHome.class);
					intent.putExtra(EXTRA_TITLE, "Current Location");
					Long shipInformationTableId= getIntent().getLongExtra("shipInformationTableId",-1);
		    		intent.putExtra("shipInformationTableId", shipInformationTableId);
					intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
					intent.putExtra(EXTRA_MODE, 0);
					startActivityForResult(intent, 0);
	        }
	    });

	        
	    	chooseShippingOption.setOnClickListener(new View.OnClickListener() {
	         @Override
	         public void onClick(View v) {
	        		Intent intent = new Intent(ShippingInformation.this,
	        				ShipingOptionsView.class);
					Long shipInformationTableId= getIntent().getLongExtra("shipInformationTableId",-1);
		    		intent.putExtra("shipInformationTableId", shipInformationTableId);
					intent.putExtra(EXTRA_MODE, 0);
					startActivityForResult(intent, 0);
	        }
	    });
	        
	    	shipBobIt.setOnClickListener(new View.OnClickListener() {
	         @Override
	         public void onClick(View v) {
	        	 Toast toast=  Toast.makeText(context, "Finding closest ShipBob Agent",Toast.LENGTH_LONG);
	        	   LinearLayout toastLayout = (LinearLayout) toast.getView();
	        	   TextView toastTV = (TextView) toastLayout.getChildAt(0);
	        	   toastTV.setTextSize(20);
	        	   toast.show();
	               shipBobHandler.postDelayed(mUpdateTimeTask, 700);

	        }
	    });
	    	
	   
	    	
	    }
	    
	 	private Runnable mUpdateTimeTask = new Runnable() {
 		   public void run() {
 			  Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
 			 Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
 			 r.play();

               Intent intent = new Intent(ShippingInformation.this, shipBobOrdered.class);
               Long shipInformationTableId= getIntent().getLongExtra("shipInformationTableId",-1);
    		   intent.putExtra("shipInformationTableId", shipInformationTableId);
              startActivity(intent);

 	           // no animation of transition
 	           overridePendingTransition(0, 0);
 	           finish();
 		   }
 		};

 		private String constructDestinationAddress(View dialogBox){
 			
 			EditText streetAddress = (EditText)dialogBox.findViewById(R.id.streetName);
 			EditText apptNumber= (EditText) dialogBox.findViewById(R.id.apptNumber);
 			EditText zipCode= (EditText) dialogBox.findViewById(R.id.zipCode);
 			EditText city= (EditText) dialogBox.findViewById(R.id.city);

 			manualZipCode=zipCode.getText().toString();
 			EditText country= (EditText) dialogBox.findViewById(R.id.Country);

           String constructedAddress= apptNumber.getText().toString()+","+streetAddress.getText().toString()+","+
        		   city.getText().toString()+","+country.getText().toString()+","+zipCode.getText().toString();
 			
           return constructedAddress.toString();
 		}
 		
 		private long updateShippingInformationTable(String shipOption){
	        int returnedInteger=new ShippingInformationDatabaseHandler(ShippingInformation.this).
	        					updateShipInformationOption(shipInformation.ShipInformationTableId, shipOption);
	        return returnedInteger;
 		}


	}

