/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.scrumptious;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import LocationHandling.LocationManagerHelper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTable;
import com.facebook.scrumptious.GlobalMethods.globalmethods;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.*;

import android.app.AlertDialog;
import android.app.ProgressDialog;

public class MainActivity extends SherlockActivity implements
		ISideNavigationCallback, OnMapClickListener, OnMapLongClickListener,
		OnMarkerClickListener, OnMarkerDragListener, OnCameraChangeListener {

	private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
	static final LatLng TutorialsPoint = new LatLng(21, 57);

	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS + 1;
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private boolean isResumed = false;

	public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
	public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
	public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";
	private SideNavigationView sideNavigationView;

	private Button initiateShipBob;
	private LocationManager locationManager;
	private LocationManagerHelper locationListener;
	private ImageView icon;
	private TextView streetAddress;
	private MenuItem settings;
	private UiLifecycleHelper uiHelper;
	private GoogleMap googleMap;
	private boolean manualCurrentLocation = false;

	private Location googleCoordinates;
	private String manualZipCode;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		initiateShipBob = (Button) findViewById(R.id.initiateshipBob);
		streetAddress = (TextView) findViewById(R.id.streetAddress);

		String IntentCurrentLocation = getIntent().getStringExtra(
				"currentLocation");
		if (IntentCurrentLocation != null && !IntentCurrentLocation.equals("")) {
			TextView textbox = (TextView) this.findViewById(R.id.streetAddress);
			textbox.setText("Current Location: " + IntentCurrentLocation);
			manualZipCode = getIntent().getStringExtra("manualZipCode");
			manualCurrentLocation = true;

		}

		InitiateShipBobEventHandler();

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		try {
			if (googleMap == null) {
				googleMap = ((MapFragment) getFragmentManager()
						.findFragmentById(R.id.map)).getMap();
			}

			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationListener = new LocationManagerHelper(googleMap, this);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);

			// retrieve past known location
			Location currentGeoLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (currentGeoLocation != null) {

				LatLng currentLocation = new LatLng(
						currentGeoLocation.getLatitude(),
						currentGeoLocation.getLongitude());
				MarkerOptions mOptions = new MarkerOptions();
				mOptions.position(currentLocation);
				mOptions.draggable(true);
				mOptions.title("Current Location");
				mOptions.snippet("Hold and Drag to change position.");
				Marker TP = googleMap.addMarker(mOptions);
				TP.showInfoWindow();
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						currentLocation, 17.0f));
				getStreetAddress(currentGeoLocation);

				/*
				 * googleMap.setOnMapClickListener(this);
				 * googleMap.setOnMapLongClickListener(this);
				 */
				googleMap.setOnMarkerClickListener(this);
				googleMap.setOnMarkerDragListener(this);

				/*
				 * googleMap.setOnCameraChangeListener(this);
				 */
				AttachClickEventToStreetAddress();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		icon = (ImageView) findViewById(R.id.icon);
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);

		if (getIntent().hasExtra(EXTRA_TITLE)) {
			String title = getIntent().getStringExtra(EXTRA_TITLE);
			int resId = getIntent().getIntExtra(EXTRA_RESOURCE_ID, 0);
			setTitle(title);
			// icon.setImageResource(resId);
			sideNavigationView
					.setMode(getIntent().getIntExtra(EXTRA_MODE, 0) == 0 ? Mode.LEFT
							: Mode.RIGHT);
		}

		ActionBar something = getSupportActionBar();
		if (something != null)
			something.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		if (sideNavigationView.getMode() == Mode.RIGHT) {
			menu.findItem(R.id.mode_right).setChecked(true);
		} else {
			menu.findItem(R.id.mode_left).setChecked(true);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;

	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

		/*
		 * outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
		 */}



	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return true;
		/*
		 * // only add the menu when the selection fragment is showing if
		 * (fragments[SELECTION].isVisible()) { if (menu.size() == 0) {
		 * settings= menu.add(R.string.settings);
		 * menu.add(R.string.action_eating);; } return true; } else {
		 * menu.clear(); settings = null; } return false;
		 */
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * if (item.equals(settings)) { showFragment(SETTINGS, true); return
		 * true; }
		 */

		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			break;
		case R.id.mode_left:
			item.setChecked(true);
			sideNavigationView.setMode(Mode.LEFT);
			break;
		case R.id.mode_right:
			item.setChecked(true);
			sideNavigationView.setMode(Mode.RIGHT);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;

	}

	/*
	 * public void showSettingsFragment() { showFragment(SETTINGS, false); }
	 */
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (isResumed) {
			if (state.isOpened()) {
				// If the session state is open:
				// Show the authenticated fragment
				invokeActivity(getString(R.string.title1), R.drawable.icon);
				// showFragment(SELECTION, false);
			} else if (state.isClosed()) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				// no animation of transition
				overridePendingTransition(0, 0);
			}
		}
	}

	/*
	 * private void showFragment(int fragmentIndex, boolean addToBackStack) {
	 * FragmentManager fm = getSupportFragmentManager(); FragmentTransaction
	 * transaction = fm.beginTransaction(); for (int i = 0; i <
	 * fragments.length; i++) { if (i == fragmentIndex) {
	 * transaction.show(fragments[i]); } else { //
	 * transaction.hide(fragments[i]); } } if (addToBackStack) {
	 * transaction.addToBackStack(null); } transaction.commit(); }
	 */

	@Override
	public void onSideNavigationItemClick(int itemId) {
		switch (itemId) {

		case R.id.home:
			Intent mainActivityIntent = new Intent(this, MainActivity.class);
			startActivity(mainActivityIntent);
			// no animation of transition
			overridePendingTransition(0, 0);
			finish();
			break;

		case R.id.side_navigation_menu_item1:
			Intent intent = new Intent(this, ProfileActivity.class);
			intent.putExtra(EXTRA_TITLE, "My Profile");
			intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
			intent.putExtra(EXTRA_MODE, 0);
			startActivity(intent);
			// no animation of transition
			overridePendingTransition(0, 0);
			finish();
			break;

		case R.id.side_navigation_menu_item2:
			// invokeActivity(getString(R.string.title1), R.drawable.icon);
			Intent intent_firstActivity = new Intent(this, OrdersActivity.class);
			// all of the other activities on top of it will be closed and this
			// Intent will be delivered to the (now on top) old activity as a
			// new Intent.
			intent_firstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent_firstActivity);
			finish();

			break;

		case R.id.side_navigation_menu_item3:
			Intent logOutintent = new Intent(this, logOutActivity.class);
			startActivity(logOutintent);
			// no animation of transition
			overridePendingTransition(0, 0);
			finish();
			break;

		default:
			return;
		}
		finish();

	}

	private void invokeActivity(String title, int resId) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(EXTRA_TITLE, title);
		intent.putExtra(EXTRA_RESOURCE_ID, resId);
		intent.putExtra(EXTRA_MODE,
				sideNavigationView.getMode() == Mode.LEFT ? 0 : 1);

		// all of the other activities on top of it will be closed and this
		// Intent will be delivered to the (now on top) old activity as a
		// new Intent.
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);
		// no animation of transition
		overridePendingTransition(0, 0);
	}

	private void getStreetAddress(Location location) {

		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 1);
			String address = addresses.get(0).getAddressLine(0);
			if (!manualCurrentLocation)
				streetAddress.setText("Pick Up Location: " + address);
			googleCoordinates = location;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		googleMap.addMarker(new MarkerOptions().position(marker.getPosition())
				.draggable(true));

		googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker
				.getPosition()));
		getStreetAddress(marker.getPosition());

		// TODO Auto-generated method stub
		LatLng position = marker.getPosition();
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
		getStreetAddress(position);
		return false;
	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		/*
		 * googleMap.clear();
		 * 
		 * googleMap.addMarker(new MarkerOptions() .position(point)
		 * .draggable(true));
		 * 
		 * googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
		 * getStreetAddress(point);
		 */

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		LatLng dragPosition = marker.getPosition();
		double dragLat = dragPosition.latitude;
		double dragLong = dragPosition.longitude;
		LatLng position = marker.getPosition();
		googleMap.clear();
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
		googleMap.addMarker(
				new MarkerOptions().position(position).draggable(true)
						.title("Current Location")
						.snippet("Hold and Drag to change position."))
				.showInfoWindow();

		getStreetAddress(position);

	}

	private void getStreetAddress(LatLng location) {

		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(location.latitude,
					location.longitude, 1);
			String address = addresses.get(0).getAddressLine(0);
			TextView textbox = (TextView) this.findViewById(R.id.streetAddress);
			textbox.setText("Current Location: " + address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		/*
		 * LatLng dragPosition = marker.getPosition(); double dragLat =
		 * dragPosition.latitude; double dragLong = dragPosition.longitude;
		 * LatLng position= marker.getPosition();
		 * googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
		 * getStreetAddress(position);
		 */

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		/*
		 * googleMap.clear(); LatLng dragPosition = marker.getPosition(); double
		 * dragLat = dragPosition.latitude; double dragLong =
		 * dragPosition.longitude; LatLng position= marker.getPosition();
		 * googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
		 * getStreetAddress(position);
		 */

	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		googleMap.clear();
		googleMap.addMarker(new MarkerOptions().position(arg0.target));
		getStreetAddress(arg0.target);

	}

	public void AttachClickEventToStreetAddress() {
		TextView textbox = (TextView) this.findViewById(R.id.streetAddress);
		textbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						currentLocation.class);
				intent.putExtra(EXTRA_TITLE, "Current Location");
				intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
				intent.putExtra(EXTRA_MODE, 0);
				startActivityForResult(intent, 0);

			}
		});
	}

	private void InitiateShipBobEventHandler() {

		initiateShipBob.setOnClickListener(new OnClickListener() {
			final TextView zipCode = new TextView(MainActivity.this);
			public void onClick(View v) {
				if (manualCurrentLocation) {
					isCurrentLocationWithinDeliveryZone(manualZipCode);
				} else {
					isCurrentLocationWithinDeliveryZone(googleCoordinates);
				}
			}

		});

	}
	
	public void isCurrentLocationWithinDeliveryZone(Location location) {

		Geocoder geoCoder = new Geocoder(MainActivity.this);
		try {
			List<Address> matches = geoCoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 1);
			final EditText zipCode = new EditText(this);
			zipCode.setInputType(InputType.TYPE_CLASS_NUMBER);
		
			if (matches != null) {
				Address address = matches.get(0);
				String guessedZipCode = address.getPostalCode();
				if (guessedZipCode != null) {
					isCurrentLocationWithinDeliveryZone(guessedZipCode);
				} else {
					AlertDialog.Builder alertDialogBox= new AlertDialog.Builder(MainActivity.this)
							.setTitle("Enter the Zip Code for the pick up location")
							.setView(zipCode)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											manualZipCode = zipCode.getText()
													.toString();
											isCurrentLocationWithinDeliveryZone(manualZipCode);
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											// Do nothing.
										}
									});
					AlertDialog dialog = alertDialogBox.create();

					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

					dialog.show();
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void isCurrentLocationWithinDeliveryZone(String zipCode) {
		new JSONFeedTask()
				.execute("http://shipbobapi.azurewebsites.net/api/PickUpLocation/IsPickUpLocationValid/?zipCode="+zipCode);

	}

	private class JSONFeedTask extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog = new ProgressDialog(MainActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog.setMessage("Checking for Delivery Zone...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... urls) {
			return readJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result) {
			try {
				if (result.equals("true")) {
					checkForExistingProfileInformationAndIntentForward();
					progDailog.dismiss();

				} else {
					ShowDeliveryAreaFailureMessage();
					progDailog.dismiss();

				}

			} catch (Exception e) {
				Log.d("JSONFeedTask", e.getLocalizedMessage());
				ShowDeliveryAreaFailureMessage();
				progDailog.dismiss();

			}
		}
	}

	private void ShowDeliveryAreaFailureMessage() {
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("Sorry!!")
				.setMessage("Sorry this address is outside our delivery zone.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
						dialog.dismiss();

					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
								dialog.dismiss();

							}
						}).create();

		LayoutInflater inflater = MainActivity.this.getLayoutInflater();
		alertDialog.show();

	}

	public String readJSONFeed(String URL) {
		return globalmethods.MakeandReceiveHTTPResponse(URL);
	}

	private void checkForExistingProfileInformationAndIntentForward(){
       String emailAddress= globalmethods.getDefaultsForPreferences("email", getApplicationContext());

		  UserProfileTable existingUserProfile= GlobalDatabaseHandler.GetUserProfile(getApplicationContext(),emailAddress);
 	        if(existingUserProfile!=null){
 	        	UserProfileTable userProfile=existingUserProfile;
 	        	String phoneNumber=userProfile.getPhoneNumber();
 	        	String creditCard=userProfile.getLastFourCreditCard();

 	        	if(phoneNumber==null ||phoneNumber=="" ||phoneNumber.isEmpty() || phoneNumber.equals("null"))
 	        	{
 	        		MoveToCompletePhoneNumberActivity();
 	        	}
 	        	else if (creditCard==null||creditCard.equals("null")||creditCard.isEmpty()||creditCard=="")
 	        	{
 	        		MoveToCompleteCreditCardActivity();
 	        	}
 	        	else MoveToCameraActivity();
 	        }
	}
	
	private void MoveToCompletePhoneNumberActivity() {
		Intent intent = new Intent(MainActivity.this, CompleteProfile_PhoneNumber.class);
		intent.putExtra(EXTRA_TITLE, "PhoneNumber");
		intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
		intent.putExtra(EXTRA_MODE, 0);
		startActivity(intent);
		// no animation of transition
		overridePendingTransition(0, 0);

	}
	
	private void MoveToCompleteCreditCardActivity() {
		Intent intent = new Intent(MainActivity.this, CompleteProfile_CreditCard.class);
		intent.putExtra(EXTRA_TITLE, "Credit Card");
		intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
		intent.putExtra(EXTRA_MODE, 0);
		startActivity(intent);
		// no animation of transition
		overridePendingTransition(0, 0);

	}
	
	
	private void MoveToCameraActivity() {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtra(EXTRA_TITLE, "Camera");
		intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
		intent.putExtra(EXTRA_MODE, 0);
		startActivity(intent);
		// no animation of transition
		overridePendingTransition(0, 0);

	}
	
	
}
