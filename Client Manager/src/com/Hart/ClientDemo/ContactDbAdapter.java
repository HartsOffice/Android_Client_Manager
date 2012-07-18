/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.Hart.ClientDemo;

//import java.sql.Timestamp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class ContactDbAdapter {

 // Tables and fields U
    public static final String TABLE_CONTACT = "contact";
    public static final String TABLE_TICKET = "ticket";
    public static final String TABLE_PICTURE = "picture";
    public static final String TABLE_SERVICE = "service";
    public static final String CONTACT_ID = "_id";
    public static final String CONTACT_NAME= "name";
    public static final String CONTACT_CELLPHN = "cellPhone";
    public static final String CONTACT_HOMEPHN ="homePhone";
    public static final String CONTACT_WORKPHN ="workPhone";
    public static final String CONTACT_INTERNET = "internet";
    public static final String CONTACT_EMAIL = "email";
    public static final String CONTACT_STATE = "state";
    public static final String CONTACT_NOTE = "note";
    public static final String CONTACT_LASTCONTACT_DATE = "lastContactDate";
    public static final String CONTACT_NEXTCONTACT_DATE = "nextContactDate";
    public static final String CONTACT_LASTCONTACT_TIME = "lastContactTime";
    public static final String CONTACT_NEXTCONTACT_TIME = "nextContactTime";
    public static final String CONTACT_HOOKFLD1 = "hookFld1";
    public static final String CONTACT_HOOKFLD2 = "hookFld2";
    public static final String CONTACT_HOOKFLD3 = "hookFld3";
    public static final String CONTACT_HOOKFLD4 = "hookFld4";
    public static final String CONTACT_HOOKFLD5 = "hookFld5";

    public static final String TICKET_ID = "_id";
    public static final String TICKET_CONTACT= "contact_id";
    public static final String TICKET_STATE = "state";
    public static final String TICKET_TIME_TTL ="time_ttl";
    public static final String TICKET_START_TIME ="starttime";
    public static final String TICKET_END_TIME = "endtime";
    public static final String TICKET_START_DATE ="startdate";
    public static final String TICKET_END_DATE = "enddate";
    public static final String TICKET_NOTE = "note";
    public static final String TICKET_DLRS = "dollars";
    public static final String TICKET_SERVICE = "service";
    public static final String TICKET_PRIORITY = "priority";
  
    public static final String SERVICE_ID = "_id";
    public static final String SERVICE_NAME= "service_name";
    public static final String SERVICE_DOLLARS = "service_dollars";
    public static final String SERVICE_TIME = "service_time";
    public static final String SERVICE_NOTE = "service_note";
    
    public static final Integer IsDemo = 1;
    public static Integer TicketRecCount;
    public static Integer ContactRecCount;
    
    private static final String TAG = "ContactDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
    "CREATE TABLE " + TABLE_CONTACT + 
   		 "("+CONTACT_ID+" INTEGER PRIMARY KEY autoincrement, "+CONTACT_NAME+" TEXT not null, "+CONTACT_CELLPHN
   		 +" TEXT, "+CONTACT_HOMEPHN+" TEXT, "+CONTACT_WORKPHN+" TEXT, " +
   		 CONTACT_INTERNET+" TEXT,  "+CONTACT_EMAIL+" TEXT, "+CONTACT_STATE+" INTEGER, "+CONTACT_NOTE+
   		 " TEXT, "+CONTACT_LASTCONTACT_DATE+ " TEXT, "+CONTACT_NEXTCONTACT_DATE + " TEXT," +
   		CONTACT_LASTCONTACT_TIME+ " TEXT, "+CONTACT_NEXTCONTACT_TIME + " TEXT," +
   		CONTACT_HOOKFLD1+" TEXT, "+CONTACT_HOOKFLD2+" TEXT, "+
   		 CONTACT_HOOKFLD3+" TEXT, "+CONTACT_HOOKFLD4+" TEXT)";

    private static final String DATABASE_CREATE_TICKET =
        "CREATE TABLE " + TABLE_TICKET + 
       		 "(" + TICKET_ID + " INTEGER PRIMARY KEY autoincrement, " + TICKET_CONTACT + 
       		 " INTEGER SECONDARY KEY not null, " + TICKET_STATE + " TEXT, " + TICKET_TIME_TTL + " INTEGER, " + 
       		 TICKET_START_TIME+" TEXT, " + TICKET_START_DATE + " TEXT, " + TICKET_END_DATE + " TEXT, " +
       		 TICKET_END_TIME+" TEXT, " + TICKET_NOTE +" TEXT, " + TICKET_DLRS + " TEXT, " + 
       		 TICKET_SERVICE + " TEXT, " + TICKET_PRIORITY + " TEXT )";
    
    private static final String DATABASE_CREATE_SERVICES =
        "CREATE TABLE " + TABLE_SERVICE + 
       		 "(" + SERVICE_ID + " INTEGER PRIMARY KEY autoincrement, " + SERVICE_NAME + 
       		 " TEXT not null, " + SERVICE_DOLLARS + " TEXT, " + SERVICE_TIME + " TEXT, " + SERVICE_NOTE + " TEXT )";
    
    private static final String DATABASE_NAME = "contactData";
//    private static final String DATABASE_TABLE = "contact"; 
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
 
            db.execSQL(DATABASE_CREATE);
            System.out.println("Created Contact Table " + DATABASE_CREATE_TICKET);
            db.execSQL(DATABASE_CREATE_TICKET);
            System.out.println("Created TICKET Table");
            db.execSQL(DATABASE_CREATE_SERVICES);
            System.out.println("Created SERVICES Table");

            //       Toast.makeText(this, "Picked Item: " + DATABASE_CREATE, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACT );
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ContactDbAdapter(Context ctx) {
        this.mCtx = ctx;
 
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ContactDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createContact(String name, String cellPhone, String homePhone, String workPhone, String email, String Domain, String note) {
    	long retVal;
 //   	String str2;
        ContentValues initialValues = new ContentValues();
        initialValues.put(CONTACT_NAME, name);
        initialValues.put(CONTACT_CELLPHN, cellPhone);
        initialValues.put(CONTACT_HOMEPHN, homePhone);
        initialValues.put(CONTACT_WORKPHN, workPhone);
        initialValues.put(CONTACT_INTERNET, Domain);
        initialValues.put(CONTACT_EMAIL, email);
        initialValues.put(CONTACT_NOTE, note);
        

        retVal = mDb.insert(TABLE_CONTACT, null, initialValues);
 
        return retVal;
    }
//    java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime(

    
    public long createTicket(long Contact_id, String state, 
    		String startTime, String endTime, String startDate, String endDate,  
    		String note, String Dollars,
    		String Service, String Priority, String TotalTime) {
    	long retVal;
    	String str2;
    	
    	System.out.println( " into createTicket contactId= " + Long.toString( Contact_id));
        ContentValues initialValues = new ContentValues();
        initialValues.put(TICKET_STATE, state);
        initialValues.put(TICKET_CONTACT, Contact_id);
        initialValues.put(TICKET_TIME_TTL, TotalTime);
        initialValues.put(TICKET_START_TIME, startTime);
        initialValues.put(TICKET_END_TIME, endTime);
        initialValues.put(TICKET_START_DATE, startDate);
        initialValues.put(TICKET_END_DATE, endDate);
        initialValues.put(TICKET_NOTE, note);
        initialValues.put(TICKET_DLRS, Dollars);
        initialValues.put(TICKET_SERVICE, Service);
        initialValues.put(TICKET_PRIORITY, Priority);
        

        retVal = mDb.insert(TABLE_TICKET, null, initialValues);
// System.out.println( "after Create Ticket retval=" + Long.toString( retVal));
        return retVal;
    }

    public long createService(String name, String dollars, String hours, String note) {
    	long retVal;
 //   	String str2;
        ContentValues initialValues = new ContentValues();
        initialValues.put(SERVICE_NAME, name);
        initialValues.put(SERVICE_DOLLARS, dollars);
        initialValues.put(SERVICE_TIME, hours);
        initialValues.put(SERVICE_NOTE, note);
        
        retVal = mDb.insert(TABLE_SERVICE, null, initialValues);
 
        return retVal;
    }

    
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteContact(long rowId) {
    	boolean retVal;
 //   	String str1, str2;
    	
        retVal = mDb.delete( TABLE_CONTACT, CONTACT_ID + "=" + rowId, null) > 0;

        return retVal;
    }

    public boolean deleteTicket(long rowId) {
    	boolean retVal;
 //   	String str1, str2;
    	
        retVal = mDb.delete( TABLE_TICKET, TICKET_ID + "=" + rowId, null) > 0;

        return retVal;
    }


    public boolean deleteService(long rowId) {
    	boolean retVal;
 //   	String str1, str2;
    	
        retVal = mDb.delete( TABLE_SERVICE, SERVICE_ID + "=" + rowId, null) > 0;

        return retVal;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllContacts() {

        return mDb.query( TABLE_CONTACT, new String[] 
        {
        		CONTACT_ID, 
        		CONTACT_NAME, 
        		CONTACT_CELLPHN,
        		CONTACT_HOMEPHN,
        		CONTACT_WORKPHN,
        		CONTACT_INTERNET,
        		CONTACT_EMAIL,
                CONTACT_NOTE}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchContact(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_CONTACT, new String[] 
            {CONTACT_ID,
             CONTACT_NAME,
             CONTACT_CELLPHN,
             CONTACT_HOMEPHN,
             CONTACT_WORKPHN,
             CONTACT_INTERNET,
             CONTACT_EMAIL,
             CONTACT_NOTE}, CONTACT_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
//            System.out.println( " found some contacts");
        }else{
//        	System.out.println( " no contacts found");
        }
        return mCursor;

    }

    public Cursor fetchTicket(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_TICKET, new String[] 
            {TICKET_ID, TICKET_STATE, TICKET_CONTACT, TICKET_TIME_TTL, 
            TICKET_START_TIME, TICKET_END_TIME, TICKET_START_DATE, TICKET_END_DATE, 
            TICKET_NOTE, TICKET_DLRS, TICKET_SERVICE, 
            TICKET_PRIORITY}, TICKET_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    // id is the Contact, Ticket id

    public Cursor fetchTickets_Contact(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_TICKET, new String[] 
            {TICKET_ID, TICKET_STATE, TICKET_CONTACT, TICKET_TIME_TTL, TICKET_START_TIME, 
            TICKET_END_TIME, TICKET_NOTE, TICKET_DLRS, TICKET_SERVICE, 
            TICKET_PRIORITY, TICKET_START_DATE, TICKET_END_DATE}, TICKET_CONTACT + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
//        	System.out.println(" found some tickets for "+ Long.toString( rowId));
            mCursor.moveToFirst();
        }else{
//        	System.out.println("no Tickets for "+ Long.toString(rowId));
        }
        return mCursor;

    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllServices() {
        return mDb.query( TABLE_SERVICE, new String[] 
        {
        		SERVICE_ID, 
        		SERVICE_NAME, 
        		SERVICE_DOLLARS,
        		SERVICE_TIME,
                SERVICE_NOTE}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchService(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_SERVICE, new String[] 
            {SERVICE_ID, 
            	SERVICE_NAME, 
            	SERVICE_DOLLARS,
            	SERVICE_TIME,
                SERVICE_NOTE}, SERVICE_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
//            System.out.println( " found some contacts");
        }else{
//        	System.out.println( " no contacts found");
        }
        return mCursor;

    }

    
    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateContact(long rowId, String name, String cellPhone, String homePhone, String workPhone, 
    		String email, String Domain, String note) {
    	boolean retVal;
 //   	String str2;
        ContentValues args = new ContentValues();
        args.put(CONTACT_NAME, name);
        args.put(CONTACT_CELLPHN, cellPhone);
        args.put(CONTACT_HOMEPHN, homePhone);
        args.put(CONTACT_WORKPHN, workPhone);
        args.put(CONTACT_INTERNET, Domain);
        args.put(CONTACT_EMAIL, email);
        args.put(CONTACT_NOTE, note);

        retVal=  mDb.update(TABLE_CONTACT, args, CONTACT_ID + "=" + rowId, null) > 0;
 
        return retVal;
    }

    public boolean updateTicket( long rowId, long Contact_id, String state, 
    		String startDate, String startTime, String endDate, String endTime, String note, String Dollars,
	String Service, String Priority, String TotalTime ) {
    	boolean retVal;
 //   	String str2;
        ContentValues args = new ContentValues();
        args.put(TICKET_STATE, state);
        args.put(TICKET_CONTACT, Contact_id);
        args.put(TICKET_TIME_TTL, TotalTime);
        args.put(TICKET_START_TIME, startTime);
        args.put(TICKET_START_DATE, startDate);
        args.put(TICKET_END_DATE, endDate);
        args.put(TICKET_END_TIME, endTime);
        args.put(TICKET_NOTE, note);
        args.put(TICKET_DLRS, Dollars);
        args.put(TICKET_SERVICE, Service);
        args.put(TICKET_PRIORITY, Priority);

        retVal=  mDb.update(TABLE_TICKET, args, TICKET_ID + "=" + rowId, null) > 0;
 
        return retVal;
    }

    public boolean updateService(long rowId, String name, String dollars, String hours, String note) {
    	boolean retVal;
 //   	String str2;
        ContentValues args = new ContentValues();
        args.put(SERVICE_NAME, name);
        args.put(SERVICE_DOLLARS, dollars);
        args.put(SERVICE_TIME, hours);
        args.put(SERVICE_NOTE, note);

        retVal=  mDb.update(TABLE_SERVICE, args, SERVICE_ID + "=" + rowId, null) > 0;
 
        return retVal;
    }

}

