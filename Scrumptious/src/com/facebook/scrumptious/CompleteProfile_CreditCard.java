package com.facebook.scrumptious;

import org.json.JSONObject;

import com.facebook.scrumptious.DatabaseHandler.GlobalDatabaseHandler;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTable;
import com.facebook.scrumptious.DatabaseHandler.UserProfileTableDatabaseHandler;
import com.facebook.scrumptious.GlobalMethods.globalmethods;
import com.facebook.scrumptious.Stripe.PaymentActivity;
import com.facebook.scrumptious.Stripe.PaymentForm;
import com.facebook.scrumptious.Stripe.ProgressDialogFragment;
import com.facebook.scrumptious.Stripe.TokenList;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class CompleteProfile_CreditCard extends FragmentActivity implements
		PaymentForm {

	private EditText streetAddress1;
	private TextView streetAddress2;
	private Button nextStepButton;
	private UserProfileTable userProfileTable;
	private ProgressDialogFragment progressFragment;
	public static final String PUBLISHABLE_KEY = "pk_test_6pRNASCoBOKtIshFeQd4XMUh";
	EditText cardNumber;
	EditText cvc;
	Spinner monthSpinner;
	Spinner yearSpinner;
	public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
	public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
	public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcreditcard);

		// setContentView(R.layout.returnaddress);

        String title = "My Profile";
	
		setTitle(title);
		
		String emailAddress = globalmethods.getDefaultsForPreferences("email",
				getApplicationContext());
		userProfileTable = GlobalDatabaseHandler.GetUserProfile(
				getApplicationContext(), emailAddress);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		nextStepButton = (Button) findViewById(R.id.saveCreditCard);
		progressFragment = ProgressDialogFragment
				.newInstance(R.string.progressMessage);

		AttachClickEventToSavePhoneNumber();
		CrossImageButton();

		this.cardNumber = (EditText) findViewById(R.id.number);
		this.cvc = (EditText) findViewById(R.id.cvc);
		this.monthSpinner = (Spinner) findViewById(R.id.expMonth);
		this.yearSpinner = (Spinner) findViewById(R.id.expYear);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(CompleteProfile_CreditCard.this,
					MainActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public UserProfileTable GetUserProfile(Context c, String email) {

		UserProfileTableDatabaseHandler userDbHandler = new UserProfileTableDatabaseHandler(
				c);
		UserProfileTable existingUserProfile = userDbHandler.getContact(email);
		return existingUserProfile;
	}

	private void AttachClickEventToSavePhoneNumber() {

		nextStepButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveForm(v);

			}
		});
	}

	public void saveForm(View button) {
		saveCreditCard(this);
	}

	public void saveCreditCard(PaymentForm form) {

		Card card = new Card(form.getCardNumber(), form.getExpMonth(),
				form.getExpYear(), form.getCvc());

		boolean validation = card.validateCard();
		if (validation) {
			startProgress();
			new Stripe().createToken(card, PUBLISHABLE_KEY,
					new TokenCallback() {
						public void onSuccess(Token token) {
							finishProgress();
							SaveTokenInformation(token);
						}

						public void onError(Exception error) {
							handleError(error.getLocalizedMessage());
							finishProgress();
						}
					});
		} else {
			handleError("You did not enter a valid card");
		}
	}

	private void startProgress() {
		progressFragment.show(getSupportFragmentManager(), "Authenticating...");
	}

	private void finishProgress() {
		progressFragment.dismiss();
	}

	private void handleError(String error) {
		DialogFragment fragment = ErrorDialogFragment.newInstance(
				R.string.validationErrors, error);
		fragment.show(getSupportFragmentManager(), "error");
	}

	private TokenList getTokenList() {
		return (TokenList) (getSupportFragmentManager()
				.findFragmentById(R.id.token_list));
	}

	@Override
	public String getCardNumber() {
		return this.cardNumber.getText().toString();
	}

	@Override
	public String getCvc() {
		return this.cvc.getText().toString();
	}

	@Override
	public Integer getExpMonth() {
		return getInteger(this.monthSpinner);
	}

	@Override
	public Integer getExpYear() {
		return getInteger(this.yearSpinner);
	}

	private Integer getInteger(Spinner spinner) {
		try {
			return Integer.parseInt(spinner.getSelectedItem().toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private void SaveTokenInformation(Token token) {
		String lastFour = token.getCard().getLast4();
		String emailAddress = globalmethods.getDefaultsForPreferences("email",
				getApplicationContext());
		UserProfileTable existingUserProfile = GlobalDatabaseHandler
				.GetUserProfile(getApplicationContext(), emailAddress);
		if (existingUserProfile != null) {
			UserProfileTable userProfile = existingUserProfile;
			userProfile.setLastFourCreditCard(lastFour);
			new UserProfileTableDatabaseHandler(CompleteProfile_CreditCard.this)
					.updateUserLastFourCreditCard(emailAddress, lastFour);
			insertCreditCard(token);
		}
	}

	// Json Stuff
	public String MakeUpdatePhoneNumberRequest(String URL, JSONObject j) {

		return globalmethods.MakePostRequestWithJsonObject(URL, j);
	}

	private class JSONPOSTFeedTask extends AsyncTask<Object, Void, String> {
		ProgressDialog progDailog = new ProgressDialog(
				CompleteProfile_CreditCard.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog.setMessage("Saving Credit Card...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(Object... params) {
			String url = (String) params[0];
			JSONObject j = (JSONObject) params[1];
			return MakeUpdatePhoneNumberRequest(url, j);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				JSONObject jsonResult = new JSONObject(result);
				String successResponse = jsonResult.getString("Success");
				String payload = jsonResult.getString("PayLoad");
				JSONObject userProfile = new JSONObject(payload);
				if (successResponse.equals("true")) {
					MoveToNextActivity();
				} else {
					// TO DO:: Move To Error Page
				}
			}

			catch (Exception e) {

				Log.d("JSONFeedTask", e.getLocalizedMessage());
				progDailog.dismiss();
				// TO DO Show Error Alert and Make User Save Again.
				// Show Alert.Class
			}
		}
	}

	public void insertCreditCard(Token token) {

		try {
			JSONObject j = new JSONObject();

			j.put("EmailAddress", userProfileTable.getEmail());
			j.put("StripeCustomerId", token.getId());
			j.put("LastFour", token.getCard().getLast4());
			j.put("CardExpiryMonth", token.getCard().getExpMonth());
			j.put("CardExpiryYear", token.getCard().getExpYear());
			j.put("CardType", token.getCard().getType());

			new JSONPOSTFeedTask()
					.execute(
							"http://shipbobapi.azurewebsites.net/api/Profile/InsertCreditCard",
							j);

		} catch (Exception e) {
			// TO DO Show Error Alert and Make User Save Again.
			// Show Alert.Class

		}

	}

	private void MoveToCameraActivity() {
		Intent intent = new Intent(CompleteProfile_CreditCard.this,
				CameraActivity.class);
		intent.putExtra(EXTRA_TITLE, "Camera");
		intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
		intent.putExtra(EXTRA_MODE, 0);
		startActivity(intent);
		// no animation of transition
		overridePendingTransition(0, 0);

	}
	
	private void MoveToNextActivity(){
		String firstTimeLogItent = getIntent().getStringExtra("FirstTimeLogin");
		Intent intent = new Intent(CompleteProfile_CreditCard.this,CompleteProfile_CreditCard.class);

		if(firstTimeLogItent!=null && firstTimeLogItent.equals("true")){
			MoveToIntroductionActivity();
		}
		else{
			MoveToCameraActivity();
		}
    	
	}
	
	private void MoveToIntroductionActivity() {
		Intent intent = new Intent(CompleteProfile_CreditCard.this,
				FirstTimeLogInComplete.class);
		intent.putExtra(EXTRA_TITLE, "Camera");
		intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
		intent.putExtra(EXTRA_MODE, 0);
		startActivity(intent);
		// no animation of transition
		overridePendingTransition(0, 0);

	}
	
	private void CrossImageButton(){
		ImageButton iv = (ImageButton)findViewById(R.id.crossButton);
	    iv.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
	     // TODO Auto-generated method stub
	    	if(IsFirstTimeLogin()) MoveToIntroductionActivity();
	    	else MoveToMainActivity();

	    }
	    });
	    
		};
		
		private void MoveToMainActivity() {
			Intent intent = new Intent(CompleteProfile_CreditCard.this,
					MainActivity.class);
			intent.putExtra(EXTRA_TITLE, "ShipBob");
			intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
			intent.putExtra(EXTRA_MODE, 0);
			startActivity(intent);
			// no animation of transition
			overridePendingTransition(0, 0);

		}
		
		private boolean IsFirstTimeLogin(){
			
			String firstTimeLogItent = getIntent().getStringExtra("FirstTimeLogin");
			if(firstTimeLogItent!=null && firstTimeLogItent.equals("true"))
			{
					return true;
			}
			else{
				return false;
			}
		}

}
