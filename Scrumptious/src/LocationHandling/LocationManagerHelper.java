package LocationHandling;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.facebook.scrumptious.R;
import com.facebook.scrumptious.R.id;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.ViewGroupOverlay;
import android.widget.TextView;

public class LocationManagerHelper implements LocationListener,OnMapLongClickListener,OnMapClickListener,OnMarkerDragListener 
	{

	GoogleMap googleMap;
	MapView mapView;
	Activity activity; 
	static double latitude;
	 static double longitude;
	 
    public LocationManagerHelper(GoogleMap tv, Activity activity) {
    	this.googleMap = tv;
    	this.activity=activity;
    }
    
    @Override
    public void onLocationChanged(Location loc) {
        

        
        loc.getLongitude();
        String Text = "My current location is: " + "Latitud = "
            + loc.getLatitude() + "Longitud = " + loc.getLongitude();
        LatLng currentLocation=new LatLng(loc.getLatitude() , loc.getLongitude());
        
        MarkerOptions mOptions= new MarkerOptions();
        mOptions.position(currentLocation);
        mOptions.draggable(true);
        mOptions.title("Current Location");
        mOptions.snippet("Hold and Drag to change position.");
        googleMap.clear();
        Marker TP = googleMap.addMarker(mOptions);
        TP.showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 19.0f));
        //adding new line
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        
        getStreetAddress(loc);

    }

    @Override
    public void onProviderDisabled(String provider) { 
    	
    }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

	  private void getStreetAddress(Location location){
		  
		  Geocoder geocoder;
		  List<Address> addresses;
		  geocoder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
		  try {
			addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
			  String address = addresses.get(0).getAddressLine(0);
			 TextView textbox=(TextView)activity.findViewById(R.id.streetAddress);
			textbox.setText("Current Location: "+address);			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	  }
	  
	  private void getStreetAddress(LatLng location){
		  
		  Geocoder geocoder;
		  List<Address> addresses;
		  geocoder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
		  try {
			addresses = geocoder.getFromLocation(location.latitude,location.longitude, 1);
			  String address = addresses.get(0).getAddressLine(0);
			 TextView textbox=(TextView)activity.findViewById(R.id.streetAddress);
			textbox.setText("Current Location: "+address);			  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	  }
	  

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		  LatLng dragPosition = marker.getPosition();
	        double dragLat = dragPosition.latitude;
	        double dragLong = dragPosition.longitude;
	        getStreetAddress(dragPosition);
	        
		
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		
	}
	  
	  


}