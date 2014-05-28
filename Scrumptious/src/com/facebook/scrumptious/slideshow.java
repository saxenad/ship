package com.facebook.scrumptious;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class slideshow extends Activity{
    private ImageView imageView;
    int i=0;
    int imgid[]={R.drawable.shippingicon,R.drawable.bgcolor};
    
    ImageSwitcher Switch;
    ImageView images;
    float initialX;
    private Cursor cursor;
    private  int columnIndex, position = 0;
    
    RefreshHandler refreshHandler=new RefreshHandler();
    
    class RefreshHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            imageView.setImageResource(imgid[0]);

        //    slideshow.this.updateUI();
        }
        public void sleep(long delayMillis){
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
    public void updateUI(){
        int currentInt=0+10;
        if(currentInt<=100){
            refreshHandler.sleep(2000);
        
            if(i<imgid.length){
                imageView.setImageResource(imgid[i]);
                
                // imageView.setPadding(left, top, right, bottom);
                i++;
            }
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
     // TODO Auto-generated method stub
     switch (event.getAction()) {
           case MotionEvent.ACTION_DOWN:
               imageView.setImageResource(imgid[0]);

             //  initialX = event.getX();
               //int imageID = cursor.getInt(columnIndex);
               //imageView.setImageResource(imgid[1]);

               //  images.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
               //images.setBackgroundResource(R.drawable.ic_launcher);
              /* Toast.makeText(getApplicationContext(), "previous Image",
                      Toast.LENGTH_LONG).show();*/
              // Switch.showPrevious();
             //  position= position-1;
              break;
           case MotionEvent.ACTION_UP:
               float finalX = event.getX();
              
                // cursor.moveToPosition(position);
                // int imageID = cursor.getInt(columnIndex);
                 if(position==1){
                	 imageView.setImageResource(imgid[1]);
                	 position=0;
                 }
                 else{
                	 imageView.setImageResource(imgid[0]);
                	 position=1;
                 }
                 //  images.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
                 //images.setBackgroundResource(R.drawable.ic_launcher);
                 /*Toast.makeText(getApplicationContext(), "next Image",
                        Toast.LENGTH_LONG).show();*/
                 //Switch.showPrevious();
         
                 break;
           
     }
	return false;
     
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    	
    	 this.requestWindowFeature(Window.FEATURE_NO_TITLE);

    	    //Remove notification bar
    	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow);
        this.imageView=(ImageView)this.findViewById(R.id.splash_icon);
        updateUI();
    }

}