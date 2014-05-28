package com.facebook.scrumptious;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
public class slideshowtest extends Activity {
    Button button;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
         super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshowtest);
        button=(Button)findViewById(R.id.slideshowbutton1);
        button.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
                Intent intent=new Intent(slideshowtest.this,slideshow.class);
                startActivity(intent);
                //startDisplay();
            }
        });
    }
    
}