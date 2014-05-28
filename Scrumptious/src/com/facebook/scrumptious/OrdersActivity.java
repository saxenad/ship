package com.facebook.scrumptious;

import java.io.File;
import java.io.FileInputStream;

import com.facebook.scrumptious.GlobalMethods.globalmethods;

import BitmapHandling.BitmapScaler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class OrdersActivity extends MainActivity {
	
	String fileName;
    Context context;

	
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        LayoutInflater inflater = getLayoutInflater();

	        View view =  inflater.inflate(R.layout.ordersplaced, (ViewGroup) findViewById(R.id.container));
	        
	        FragmentManager manager = getFragmentManager();

		     new globalmethods(view,manager).hideMainFragment();
		     
		     
		     String title ="Orders Placed";
	            int resId = getIntent().getIntExtra(EXTRA_RESOURCE_ID, 0);
	            fileName= getIntent().getStringExtra("uploadedFileName");

	            setTitle(title);
	            
	            FillRewardsTable();
	            context=OrdersActivity.this;
	    }
	  
	  private void FillRewardsTable(){
		  

			ScrollView lv = (ScrollView) findViewById(R.id.ListViewForPoints);
			LinearLayout childln = (LinearLayout) findViewById(R.id.LinearLayoutForPoints);
			childln.setVisibility(View.INVISIBLE);

			int k=0;
			for (int current = 0; current <2 ; current++) {

				if(current==0){
					
				}
				childln.setVisibility(View.INVISIBLE);
				LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v =  inflater.inflate(R.layout.rewards_text_view, null);
				TextView  rewards_Text1   = (TextView)    v.findViewById(R.id.name_user);
				TextView  rewards_Text2   = (TextView)    v.findViewById(R.id.name_user123);

				rewards_Text1.setText("Track your package");
				rewards_Text1.setTextColor(Color.BLUE);
				TextView  rewards_Text   = (TextView)    v.findViewById(R.id.success_message);
				rewards_Text.setText("Status:Pick Up on the way");
				rewards_Text.setTextSize(17);
				rewards_Text.setTextColor(Color.RED);
				if(current==0){
					ImageView productInformation= (ImageView)v.findViewById(R.id.orderThumbnails);
					if(fileName!=null){
						ShowThumbnail(fileName,productInformation);
			
					}
			
				}
				else
				{
				    File pictureFileDir = getDir();
				    String photoFile="Picture_20144025114051.jpg";
				    String filename = pictureFileDir.getPath() + File.separator + photoFile;
					rewards_Text.setText("Status:Delivered");
					ImageView productInformation= (ImageView)v.findViewById(R.id.orderThumbnails);
					String imageName="";
					ShowThumbnail(filename,productInformation);

				}
				
				rewards_Text2.setOnClickListener(new View.OnClickListener() {
			   	         @Override
			   	         public void onClick(View v) {
			   	          AlertDialog.Builder builder = new AlertDialog.Builder(context);
				            final TextView input = new TextView(context);
	                         input.setText("Navigation Tracking feature Coming Soon!!");

				            builder.setTitle("Tracking")
				                    .setCancelable(true)
				                    .setView(input)
				                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				                        @Override
				                        public void onClick(DialogInterface dialogInterface, int i) {
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
			   	        }
			   	  });
				childln.addView(v);

				
			} 
			childln.setVisibility(View.VISIBLE);
			//lv.addView(childln);
		}
		  
		
	  private File getDir() {
		    File sdDir = Environment
		      .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		    return new File(sdDir, "CameraAPIDemo");
		  }

	  
	  public void onPause() {
		  super.onPause();
		  this.finish();
		  }
		  public void onStop() {
		  super.onStop();
		  this.finish();
		  }
		  
		    private void ShowThumbnail(String file,ImageView productInformation){
		        try     
		        {

		            File fileName = new File(file);
		            final int THUMBNAIL_SIZE = 512;

		          /*  FileInputStream fis = new FileInputStream(fileName);
		            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

		            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);*/
		    /*
		            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		            imageData = baos.toByteArray();*/
		            BitmapScaler scaler = new BitmapScaler(fileName, THUMBNAIL_SIZE);
		            productInformation.setImageBitmap(scaler.getScaled());

		        }
		        
		        catch(Exception ex) {

		        }
		        
		    }

}
