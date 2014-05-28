package com.facebook.scrumptious.DatabaseHandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GlobalDatabaseHandler {

	
	public static UserProfileTable GetUserProfile(Context c, String email)
	{
		
	UserProfileTableDatabaseHandler userDbHandler=new UserProfileTableDatabaseHandler(c);
       UserProfileTable existingUserProfile= userDbHandler.getContact(email);
       return existingUserProfile;
	}   
	
	public static long InsertUserProfileInSqlLite(Context c,UserProfileTable userProfile){
    	
    	//make JsonRequest to getUserProfile
    	//and then insert the record. 
		UserProfileTableDatabaseHandler userTableDatabaseHandler = new UserProfileTableDatabaseHandler(c);
		UserProfileTable user = new UserProfileTable();
		user.setEmail(userProfile.getEmail());
		user.setFirstName(userProfile.getFirstName());
		user.setLastName(userProfile.getLastName());
		user.setUserId(userProfile.getUserId());
		user.setPhoneNumber(userProfile.getPhoneNumber());
		user.setLastFourCreditCard(userProfile.getLastFourCreditCard());
		user.setUserId(userProfile.getUserId());
		return userTableDatabaseHandler.addContact(user,c);
	
     }
	
	public static ShippingInformationTable GetShippingInformationTable(Context c, int shippingInformationTableId)
	{
		
		ShippingInformationDatabaseHandler shipDbHandler=new ShippingInformationDatabaseHandler(c);
		ShippingInformationTable shipInformationTable= shipDbHandler.getShippingInformation(String.valueOf(shippingInformationTableId));
       return shipInformationTable;
	}   
	
	public static long InsertShippingInformation(Context c,ShippingInformationTable shipInformation){
    	
    	//make JsonRequest to getUserProfile
    	//and then insert the record. 
		ShippingInformationDatabaseHandler shipDbHandler = new ShippingInformationDatabaseHandler(c);
		ShippingInformationTable shipInfo = new ShippingInformationTable();
		
		shipInfo.setShipInformationTableId(shipInfo.getShipInformationTableId());
		shipInfo.setImageFileName(shipInfo.getImageFileName());
		shipInfo.setDestinationAddress(shipInfo.getDestinationAddress());
		shipInfo.setContactName(shipInfo.getContactName());
		shipInfo.setInsertDate(shipInfo.getInsertDate());
		shipInfo.setShipOption(shipInfo.getShipOption());
		return shipDbHandler.addShipment(shipInformation, c);
	
     }
	
	
	
}
