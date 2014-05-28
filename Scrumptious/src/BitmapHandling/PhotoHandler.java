package BitmapHandling;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.facebook.scrumptious.DestinationAddressHome;
import com.facebook.scrumptious.ShippingInformation;
import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.ShippingInformationTable;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class PhotoHandler  implements PictureCallback {

  private final Context context;

  public PhotoHandler(Context context) {
    this.context = context;
  }

  @Override
  public void onPictureTaken(byte[] data, Camera camera) {

    File pictureFileDir = getDir();

    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

      Toast.makeText(context, "Can't create directory to save image.",
          Toast.LENGTH_LONG).show();
      return;

      
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
    String date = dateFormat.format(new Date());
    String photoFile = "Picture_" + date + ".jpg";

    String filename = pictureFileDir.getPath() + File.separator + photoFile;

    File pictureFile = new File(filename);

    try {
      FileOutputStream fos = new FileOutputStream(pictureFile);
      fos.write(data);
      fos.close();
    /*  Toast.makeText(context, "New Image saved:" + photoFile,
          Toast.LENGTH_LONG).show();*/
      
      movetoShippingActivity(filename);
    } catch (Exception error) {
    
      Toast.makeText(context, "Image could not be saved.",
          Toast.LENGTH_LONG).show();
    }
  }

  private File getDir() {
    File sdDir = Environment
      .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    return new File(sdDir, "CameraAPIDemo");
  }


  private void movetoShippingActivity(String fileName){

      Intent i = new Intent(context, ShippingInformation.class);
      long shipInformationTableId= CreateShippingRecord(fileName);
      i.putExtra("shipInformationTableId", shipInformationTableId);
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      this.context.startActivity(i);;
  }
  
  private long CreateShippingRecord(String fileName){
		 ShippingInformationTable shipInformation= new ShippingInformationTable();
		 shipInformation.ImageFileName=fileName;
		long shipInformationTableId= GlobalDatabaseHandler.InsertShippingInformation(this.context, shipInformation);
		return shipInformationTableId;
  }
  


} 

