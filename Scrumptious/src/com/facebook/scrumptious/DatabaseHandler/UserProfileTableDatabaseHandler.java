package com.facebook.scrumptious.DatabaseHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class UserProfileTableDatabaseHandler  extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION =26;

	// Database Name
	private static final String DATABASE_NAME = "ShipBobDb";

	// Contacts table name
	private static final String TABLE_CONTACTS = "UserProfileTable";

	// Contacts Table Columns names
	private static final String KEY_UserId = "UserId";
	private static final String KEY_FirstName = "FirstName";

	private static final String KEY_LastName = "LastName";

	private static final String KEY_Email = "Email";

	private static final String KEY_PhoneNumber = "PhoneNumber";

	private static final String KEY_LastFour = "LastFourCreditCard";

	public UserProfileTableDatabaseHandler(Context context) {
		  
		    
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		// String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
		// + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT)";

		String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "("
				+ KEY_UserId + " INTEGER PRIMARY KEY,"
				 + KEY_Email + " TEXT,"
				+  KEY_FirstName + " TEXT,"
				+ KEY_LastName + " TEXT,"
				+ KEY_LastFour + " TEXT,"
				+ KEY_PhoneNumber + " TEXT)";

		db.execSQL(CREATE_CONTACTS_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		// Create tables again
		onCreate(db);
	}

	public long addContact(UserProfileTable contact, Context c) {
		deleteContact(contact);
		SQLiteDatabase db = this.getWritableDatabase();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();

		ContentValues values = new ContentValues();
		values.put(KEY_UserId, contact.getUserId());
		values.put(KEY_Email,contact.getEmail());
		values.put(KEY_FirstName,contact.getFirstName());
		values.put(KEY_LastName,contact.getLastName());
		values.put(KEY_LastFour,contact.getLastFourCreditCard());
		values.put(KEY_PhoneNumber,contact.getPhoneNumber());

		// Inserting Row
		long insertedId = db.insert(TABLE_CONTACTS, null, values);
		// db.close(); // Closing database connection
		// copyDataBase(c);
		return insertedId;

	}

	public long deleteContact(UserProfileTable contact) {
		
		try{
			SQLiteDatabase db = this.getWritableDatabase();
		
			db.delete(TABLE_CONTACTS, KEY_UserId + "=" + contact.UserId, null);
		return 1;
		}
		catch(Exception e){
			return -1;
		}
	}

	// Getting single contact
	public UserProfileTable getContact(String email) {

		UserProfileTable userProfile = getContact(email, true);

/*		List<UserProfileTable> finalList = new ArrayList<UserProfileTable>();
		for (UserProfileTable contact : qrCodeUsers) {

			finalList.add(contact);
		}

		for (UserProfileTable contact : emailUsers) {

			finalList.add(contact);
		}*/

		return userProfile;
	}




	public UserProfileTable getContact(String queryString, Boolean checkEmail) {
		UserProfileTable contact = new UserProfileTable();

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor;
			// Cursor cursor = db.rawQuery(selectQuery, null);
			if (!checkEmail)
				cursor = db.rawQuery(
						"SELECT * FROM UserProfileTable WHERE UserId=?;",
						new String[] { queryString });
			else
				cursor = db.rawQuery("SELECT * FROM UserProfileTable WHERE Email=?;",
						new String[] { queryString });
			// looping through all rows and adding to list
			if(cursor==null) return null;
			if (cursor.moveToFirst()) {
				do {
					UserProfileTable tempcontact = new UserProfileTable();
					tempcontact.setUserId(Integer.parseInt(cursor.getString(0)));
					tempcontact.setEmail(cursor.getString(1));
					tempcontact.setFirstName(cursor.getString(2));
					tempcontact.setLastName(cursor.getString(3));
					tempcontact.setLastFourCreditCard(cursor.getString(4));
					tempcontact.setPhoneNumber(cursor.getString(5));
					contact=tempcontact;
					// Adding contact to list
				} while (cursor.moveToNext());
			}

			// return contact list

			return contact;
		} catch (Exception e) {

			Log.i("ERRROR", e.getMessage());
			return null;
		}
	}

	public int updateContact(String email, String firstName, String lastName,
			int userId) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UserId, userId);
		values.put(KEY_Email, email);
		values.put(KEY_FirstName, firstName);
		values.put(KEY_LastName, lastName);
	
		// updating row
		int id = db.update(TABLE_CONTACTS, values, KEY_UserId + " = ?",
				new String[] { String.valueOf(userId) });

		return id;
	}

	public int updateUserProfilePhoneNumber(String email, String phoneNumber) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_Email, email);
		values.put(KEY_PhoneNumber, phoneNumber);
		// updating row
		int id = db.update(TABLE_CONTACTS, values, KEY_Email + " = ?",
				new String[] { String.valueOf(email) });

		return id;
	}
	
	public int updateUserProfileLastFourCreditCard(String email, String lastFourCreditCard) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_Email, email);
		values.put(KEY_LastFour, lastFourCreditCard);
		// updating row
		int id = db.update(TABLE_CONTACTS, values, KEY_Email + " = ?",
				new String[] { String.valueOf(email) });

		return id;
	}
	
	public int updateUserLastFourCreditCard(String email, String lastFourCreditCard) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_Email, email);
		values.put(KEY_LastFour, lastFourCreditCard);
		// updating row
		int id = db.update(TABLE_CONTACTS, values, KEY_Email + " = ?",
				new String[] { String.valueOf(email) });

		return id;
	}
	
	public void copyDataBase(Context context) {
		Log.i("info", "in copy data base at finally");
		try {

			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();
			if (sd.canWrite()) {

				File currentDB = context.getDatabasePath(DATABASE_NAME);

				Log.i("info", "Can Write");

				// File currentDB=new File(Path);

				String backupDBPath = TABLE_CONTACTS;

				File backupDB = new File(sd, backupDBPath);
				if (currentDB.exists()) {
					Log.i("info", "Writing into DB");
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
		} catch (Exception e) {
			Log.i("info", "in copy of bata base 10 ");

		}
	}


}
