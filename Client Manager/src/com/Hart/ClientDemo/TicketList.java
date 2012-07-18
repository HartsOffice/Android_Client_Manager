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

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TicketList extends ListActivity {
    private TextView mContactText;
	
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private Long mContactId;
    static final int INSERT_ID = Menu.FIRST;
    static final int DELETE_ID = Menu.FIRST + 1;
    static final int RETURN_ID = Menu.FIRST + 2;
    private Cursor contactCursor;

    private ContactDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_list);
        setTitle(R.string.list_ticket);
        mDbHelper = new ContactDbAdapter(this);
        mDbHelper.open();
                
        mContactId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(ContactDbAdapter.CONTACT_ID);
		if (mContactId == null) {
        	
			Bundle extras = getIntent().getExtras();
			mContactId = extras != null ? extras.getLong(ContactDbAdapter.CONTACT_ID)
									: null;
//			System.out.println( "TicketList ContactID = " + Long.toString(mContactId));
		}
		
	    mContactText = (TextView) findViewById(R.id.contact);
		
        fillTicketsData();

        registerForContextMenu(getListView());
    }

    public void fillTicketsData() {

    	contactCursor = mDbHelper.fetchContact(mContactId);
    	if( contactCursor == null ){
    System.out.println(" Bad contact cursor in fillTicketsData");
    		return;
    	}
    	
    	// fill in the Contact Name
    	mContactText.setText(contactCursor.getString(
        		contactCursor.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_NAME)));   	
    	
        Cursor ticketsCursor = mDbHelper.fetchTickets_Contact( mContactId);
        if( ticketsCursor != null){
        	//System.out.println( " after FetchTickets ticketsCursor = " + ticketsCursor.toString());
        }else{
        	//System.out.println( " No tickets in Cursor");
        }
        startManagingCursor(ticketsCursor);
        ContactDbAdapter.TicketRecCount = ticketsCursor.getCount();

        // Create an array to specify the fields we want to display in the list (only TITLE)
         String[] from = new String[]{ContactDbAdapter.TICKET_STATE, ContactDbAdapter.TICKET_SERVICE,
        		ContactDbAdapter.TICKET_START_DATE};
//		String[] from = new String[]{ContactDbAdapter.TICKET_SERVICE};


        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1, R.id.text2, R.id.text3};

        // Now create a simple cursor adapter and set it to display
        
        SimpleCursorAdapter tickets = 
            new SimpleCursorAdapter(this, R.layout.tickets_row, ticketsCursor, from, to);
   
        setListAdapter( tickets);
    
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert_ticket);
        menu.
        add(0, RETURN_ID, 0, R.string.menu_return_ticket);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createTicket( );
                return true;
            case RETURN_ID:
                backToContacts( );
                return true;
        
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteTicket(info.id);
                fillTicketsData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

  // I need the contact ID for the ticket
    private void createTicket( ) {
        Intent i = new Intent(this, TicketEdit.class);
        
        String str1 = Long.toString( mContactId) + " 0";
        i.putExtra(ContactDbAdapter.CONTACT_ID, str1);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    // I need the contact ID for the ticket
    private void backToContacts( ) {
        Intent i = new Intent(this, Notepadv3.class);
        
//        String str1 = Long.toString( mContactId) + " 0";
//        i.putExtra(ContactDbAdapter.CONTACT_ID, str1);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
//        Intent i = new Intent(this, ContactEdit.class);
        Intent i = new Intent(this, TicketEdit.class);
        String str1 = Long.toString( mContactId) + " " + Long.toString( id);
        i.putExtra(ContactDbAdapter.CONTACT_ID, str1);
   //     i.putExtra(ContactDbAdapter.TICKET_ID, id);
//       System.out.println("edit ticket ID="+ Long.toString(id) + " ContactID= " + Long.toString( mContactId));
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        String str1;
        str1 = Integer.toString(requestCode);
//  System.out.println(" Ticket onActivityResult code="+ str1);
//  		System.out.println("re-opening DB");
  		mDbHelper.open();
        fillTicketsData();
    }
    
    @Override
    protected void onStart() {
//    	System.out.println("re-opening DB");
    	mDbHelper.open();
    	super.onStart();
    	
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
//    	System.out.println("closing DB");
    	mDbHelper.close();
    }
    
    @Override
    protected void onResume() {
//    	System.out.println("re-opening DB");
    	mDbHelper.open();
    	super.onResume();
    }
}
