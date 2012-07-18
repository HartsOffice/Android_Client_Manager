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

import com.Hart.ClientDemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
//import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactEdit extends Activity {

    private EditText mContactText;
    private EditText mCellPhnText;
    private EditText mHomePhnText;
    private EditText mWorkPhnText;
    private EditText mDomainText;
    private EditText mEmailText;
    private EditText mNoteText;
    private Long mRowId;
    private ContactDbAdapter mDbHelper;
//    private Notepadv3 notepadHelper;
//    private Intent intent2= new Intent();
    private static int DelFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//		intent2.setClass(this, Notepadv3.class);
        
        mDbHelper = new ContactDbAdapter(this);
        mDbHelper.open();
//        notepadHelper = new Notepadv3();
        DelFlag = 0;
     
        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_contact);

        mContactText = (EditText) findViewById(R.id.contact);
        mCellPhnText = (EditText) findViewById(R.id.cellphoneNumber);
        mHomePhnText = (EditText) findViewById(R.id.homephoneNumber);
        mWorkPhnText = (EditText) findViewById(R.id.workphoneNumber);
        mDomainText = (EditText) findViewById(R.id.domain);
        mEmailText = (EditText) findViewById(R.id.email);
        mNoteText = (EditText) findViewById(R.id.note);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        Button deleteButton = (Button) findViewById(R.id.delete);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(ContactDbAdapter.CONTACT_ID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(ContactDbAdapter.CONTACT_ID)
									: null;
		}

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
        if( mRowId != null)
        {
        	deleteButton.setOnClickListener(new View.OnClickListener() {

        		public void onClick(View view) {
        			confirmDeleteDiag();
        			
        		}
        		

        	});
        	
        }
        
    }
// METHODS START HERE
    private void populateFields() {
        if (mRowId != null) {
            Cursor contact = mDbHelper.fetchContact(mRowId);
            startManagingCursor(contact);
            mContactText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_NAME)));
            mCellPhnText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_CELLPHN)));
            mHomePhnText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_HOMEPHN)));
            mWorkPhnText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_WORKPHN)));
            mDomainText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_INTERNET)));
            mEmailText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_EMAIL)));
            mNoteText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_NOTE)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(ContactDbAdapter.CONTACT_ID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
         saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
    	if( DelFlag != 0){
     		return;
    	}
        String name = mContactText.getText().toString();
        if(( name.length() == 0) || (name.charAt(0)== ' ')){
        	Toast.makeText(this, " Invalid Contact Name ", Toast.LENGTH_LONG).show();
        	return;
        }
        String note = mNoteText.getText().toString();
		//I moved the dialog into the player itself, it is much much nicer nowext().toString();
        String cellPhn = mCellPhnText.getText().toString();
        String homePhn = mHomePhnText.getText().toString();
        String workPhn = mWorkPhnText.getText().toString();
        String domainTxt = mDomainText.getText().toString();
        String email = mEmailText.getText().toString();

        if (mRowId == null) {
        	if( ContactDbAdapter.IsDemo > 0){
        		if( ContactDbAdapter.ContactRecCount > 2){
        			Toast.makeText(this, " Exceeded Demo Version Limit ", Toast.LENGTH_LONG).show();
        			return;
        		}
        	}
            long id = mDbHelper.createContact(name, cellPhn, homePhn, workPhn, email, domainTxt, note);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateContact(mRowId, name, cellPhn, homePhn, workPhn, email, domainTxt, note);
        }
    }
    
    public void confirmDeleteDiag()
    {
    	/////////////////////////////////////////////////////////////////////////
        //I moved the dialog into the player itself, it is much much nicer now///
			final AlertDialog.Builder confirmDelete = new AlertDialog.Builder(this);
			//confirmVoice.setTitle("Please Choose Your Gender");
			confirmDelete.setMessage("Delete Contact?");
			confirmDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
	             setResult(RESULT_CANCELED);  
			  }
			});
			confirmDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// This code will not run or at least will not do what it is suppose to.
					DelFlag = 1;
					mDbHelper.deleteContact( mRowId);

					mRowId = null;
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
