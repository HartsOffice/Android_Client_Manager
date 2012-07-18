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
///import android.app.AlertDialog;
//import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
import android.widget.TextView;
//import android.widget.Toast;

public class ContactView extends Activity {

    private TextView mContactText;
    private Button mCellPhnText;
    private Button mHomePhnText;
    private Button mWorkPhnText;
    private Button mEmailText;
    private Button mDomain;
    private TextView mNoteText;
    private Long mRowId;
    private ContactDbAdapter mDbHelper;
//    private ContactEdit CEditHelper;
//    private Notepadv3 notepadHelper;
    private Intent intent2= new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		intent2.setClass(this, Notepadv3.class);
        final Intent i = new Intent();
        i.setClass(this, ContactEdit.class);
        final Intent iTicket = new Intent();
        iTicket.setClass(this, TicketList.class);
        
        mDbHelper = new ContactDbAdapter(this);
        mDbHelper.open();
//        notepadHelper = new Notepadv3();
//        CEditHelper = new ContactEdit();
     
        setContentView(R.layout.contact_view);
        setTitle(R.string.view_contact);

        mContactText = (TextView) findViewById(R.id.contact);
        mCellPhnText = (Button) findViewById(R.id.cellphoneNumber);
        mHomePhnText = (Button) findViewById(R.id.homephoneNumber);
        mWorkPhnText = (Button) findViewById(R.id.workphoneNumber);
        mDomain = (Button) findViewById(R.id.URLDomain);
        
        mEmailText = (Button) findViewById(R.id.email);
        mNoteText = (TextView) findViewById(R.id.note);

        Button editButton = (Button) findViewById(R.id.edit);
        Button ticketButton = (Button) findViewById(R.id.ticket);
        Button returningButton = (Button) findViewById(R.id.returning);
        
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(ContactDbAdapter.CONTACT_ID);
		if (mRowId == null) {
        	
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(ContactDbAdapter.CONTACT_ID)
									: null;
		}
		if (mRowId == null) {
			// should never happen
			System.out.println("Null pointer in View class");
			startActivity(intent2);
    	}	
		populateFields();

        editButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
  
//            protected void onListItemClick(ListView l, View v, int position, long id) {
//                super.onListItemClick(l, v, position, id);
//                Intent i = new Intent(this, ContactEdit.class);
                i.putExtra(ContactDbAdapter.CONTACT_ID, mRowId);
            	startActivity(i);
                //startActivityForResult(i, ACTIVITY_EDIT);
//            }
                setResult(RESULT_OK);
                finish();
            }

        });
        
        ticketButton.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
                iTicket.putExtra(ContactDbAdapter.CONTACT_ID, mRowId);
//                System.out.println( " calling Ticket List id = "+ Long.toString( mRowId));
            	startActivity(iTicket);
                setResult(RESULT_OK);
                finish();
      		}
       	});
        	
        returningButton.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
//                iNotepadv3.putExtra(ContactDbAdapter.CONTACT_ID, mRowId);
//                System.out.println( " calling Ticket List id = "+ Long.toString( mRowId));
            	startActivity(intent2);
                setResult(RESULT_OK);
                finish();
      		}
       	});
        	        
        
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
            mEmailText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_EMAIL)));
            mDomain.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_INTERNET)));
            mNoteText.setText(contact.getString(
            		contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_NOTE)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    
        outState.putSerializable(ContactDbAdapter.CONTACT_ID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
  
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }


  
}
