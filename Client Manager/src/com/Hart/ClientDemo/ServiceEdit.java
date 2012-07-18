/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.Hart.ClientDemo;

import java.sql.Date;

import com.Hart.ClientDemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ServiceEdit extends Activity{
    private EditText mServiceText;
    private EditText mNoteText;
    private EditText mDollarsText;
    private EditText mTimeText;
    private Long mServiceId;     // contact row

    private ContactDbAdapter mDbHelper;
    private Notepadv3 notepadHelper;
//    private Intent intent2= new Intent();
    private static int DelFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//		intent2.setClass(this, Notepadv3.class);
        
        mDbHelper = new ContactDbAdapter(this);
        mDbHelper.open();
        notepadHelper = new Notepadv3();
        DelFlag = 0;
        
        setContentView(R.layout.service_edit);
        setTitle(R.string.serviceTitle);
        mServiceText = (EditText) findViewById(R.id.serviceName);
        mDollarsText = (EditText) findViewById(R.id.serviceDollar);
        mTimeText = (EditText) findViewById(R.id.serviceTime);
        mNoteText = (EditText) findViewById(R.id.serviceNote);
        
        Button confirmButton = (Button) findViewById(R.id.confirm2);
        Button deleteButton = (Button) findViewById(R.id.delete2);
        
        {
			String str1;
     
        	str1 = (savedInstanceState == null) ? null :
        		(String) savedInstanceState.getSerializable(ContactDbAdapter.SERVICE_ID);
			if( str1 != null){
				mServiceId = Long.parseLong( str1);
				System.out.println( "tryed serializable Sid =" + Long.toString( mServiceId));
			}
			
                    	
        }
		if (mServiceId == null || mServiceId ==0) {
			Bundle extras = getIntent().getExtras();
			String str1;
			
			str1 = extras != null ? extras.getString(ContactDbAdapter.SERVICE_ID)
									: null;
			if( str1 != null){
				mServiceId = Long.parseLong( str1);
				System.out.println( "tryed getextras sid =" + Long.toString( mServiceId) );
			}
        }
		
		populateFieldsService();
		
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                System.out.println( " confirm on service");
                finish();
            }

        });
        if( mServiceId != null && mServiceId > 0)
        {
        	deleteButton.setOnClickListener(new View.OnClickListener() {

        		public void onClick(View view) {
        			confirmDeleteServiceDiag();
        			
        		}
        		

        	});
        	
        }
        
    }
// METHODS START HERE
 

    private void populateFieldsService(){

        if (mServiceId != null && mServiceId > 0) {
        	 System.out.println(" ServiceEdit pop ServiceFields " + Long.toString( mServiceId));
        	 Cursor servicee = mDbHelper.fetchService(mServiceId);
        	 startManagingCursor( servicee);

        	 mServiceText.setText( servicee.getString(
 	            	servicee.getColumnIndexOrThrow( ContactDbAdapter.SERVICE_NAME)));
        	 mDollarsText.setText( servicee.getString(
  	            	servicee.getColumnIndexOrThrow( ContactDbAdapter.SERVICE_DOLLARS)));
        	 mTimeText.setText( servicee.getString(
  	            	servicee.getColumnIndexOrThrow( ContactDbAdapter.SERVICE_TIME)));
         	 mNoteText.setText( servicee.getString(
   	            	servicee.getColumnIndexOrThrow( ContactDbAdapter.SERVICE_NOTE)));
        	 
        }else{
 
        	mServiceText.setText("tbd");
         	
        	System.out.println( " new Record popFieldsService");
        }
 
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        String str1;
        str1 = Long.toString( mServiceId);
        outState.putSerializable(ContactDbAdapter.SERVICE_ID, str1);
    }

    @Override
    protected void onPause() {
        super.onPause();
         saveState();
         mDbHelper.close();
         
//         String str1 = Long.toString( mContactId) + " " + Long.toString( mTicketId);
// /        i.putExtra(ContactDbAdapter.CONTACT_ID, str1);

//         System.out.println("onPause ticket ContactID= " + str1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open();
        populateFieldsService();
    }

    private void saveState() {
    	
    	System.out.println( "Into savesTATE Service DelFlag = " + Long.toString(DelFlag));
    	if( DelFlag != 0){
     		return;
    	}
        String name = mServiceText.getText().toString();
        if(( name.length() == 0) || (name.charAt(0)== ' ')){
        	Toast.makeText(this, " Invalid Service Name ", Toast.LENGTH_LONG).show();
        	return;
        }
        String note = mNoteText.getText().toString();
		//I moved the dialog into the player itself, it is much much nicer nowext().toString();
        String Timee = mTimeText.getText().toString();
        String Dollars = mDollarsText.getText().toString();
 System.out.println( " saveState Dollars "+ Dollars +" Time "+ Timee);
 
        if (mServiceId == null || mServiceId == 0) {
//        	 createService(String name, String dollars, String hours, String note)
        	
        	long id = mDbHelper.createService( name, Dollars, Timee, note);
            if (id > 0) {
                mServiceId = id;
            }
        } else {
//        	 updateService(long rowId, String name, String dollars, String hours, String note) 
        	
        	mDbHelper.updateService(mServiceId, name, Dollars, Timee, note);
        }
    }
    
    public void confirmDeleteServiceDiag()
    {
    	/////////////////////////////////////////////////////////////////////////
        //I moved the dialog into the player itself, it is much much nicer now///
			final AlertDialog.Builder confirmDelete = new AlertDialog.Builder(this);
			//confirmVoice.setTitle("Please Choose Your Gender");
			confirmDelete.setMessage("Delete Service?");
			confirmDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
	             setResult(RESULT_CANCELED);  
			  }
			});
			confirmDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// This code will not run or at least will not do what it is suppose to.
					DelFlag = 1;
					mDbHelper.deleteService( mServiceId);

					mServiceId = null;
			//		notepadHelper.fillData();
	       			//startActivity(intent2);
					finish();
				  }
				});
       
        
			confirmDelete.show();
			//I moved the dialog into the player itself, it is much much nicer now//
			////////////////////////////////////////////////////////////////////////
			
    }

}
