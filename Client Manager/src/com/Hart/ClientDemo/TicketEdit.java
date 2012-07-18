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

//import java.sql.Date;
//import java.text.DateFormat;
//import java.text.*;
import java.util.Calendar;

import com.Hart.ClientDemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.database.CharArrayBuffer;
import android.database.Cursor;
//import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
//import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TextView;
//import android.widget.AdapterView.OnItemSelectedListener;

public class TicketEdit extends Activity implements AdapterView.OnItemSelectedListener{
    private TextView mContactText;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    private String array_services[];    // holds the services for the spinner from the table
    private Long services_id[];
    private Integer numServices;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;   // 24 hour version
    private int mHourDisplay;	// pretty version
    private int mMinute;
    private int amPMer;
    private String mStatusText;
    private String mServiceText;
    private EditText mNoteText;
    private String mPriorityText;
    private Button mStartDate;
    private Button mStartTime;
    private EditText mDollarsText;
    private EditText mTotalTimeText;
    private Long mContactId;     // contact row
    private Long mTicketId;      // Ticket row in table
    private Integer FirstHitPriority;
    private Integer FirstHitStatus;
    private Integer FirstHitService;
    private Spinner sService;
    private Spinner sPriority;
    private Spinner sState;
//    private Cursor serviceCursor;
    
//    private Long mServiceId;
    
    
    protected int mPos;   // spinner position
    protected String mSelection;  // spinner selection
    /**
     * ArrayAdapter connects the spinner widget to array-based data.
     */
    protected ArrayAdapter<CharSequence> adapterStatus;		// for the Status
    protected ArrayAdapter<CharSequence> adapterPriority;
    protected ArrayAdapter<CharSequence> adapterService;

    /**
     *  The initial position of the spinner when it is first installed.
     */
    public static final int DEFAULT_POSITION = 1;

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
        FirstHitPriority = 0;
        FirstHitStatus = 0;
        FirstHitService = 0;
        
        setContentView(R.layout.ticket_edit);
        setTitle(R.string.edit_ticket);        
        sState = (Spinner) findViewById(R.id.ticketStateSpinner);
        adapterStatus = ArrayAdapter.createFromResource(
                this, R.array.ticketStatuses, android.R.layout.simple_spinner_item) ;
     

//        mAdapterStatus = adapter;
// for code defined array of strings
//  	ArrayAdapter adapter = ArrayAdapter<String>( this, android.R.layout.simple_spinner_item, stringArray );        
        
        
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sState.setAdapter(adapterStatus);
//		OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(this, adapter);
        sState.setOnItemSelectedListener( this);
        
        sPriority = (Spinner) findViewById(R.id.ticketPrioritySpinner);
        adapterPriority = ArrayAdapter.createFromResource(
                this, R.array.ticketPriorities, android.R.layout.simple_spinner_item) ;
       
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        sPriority.setAdapter(adapterPriority);
//		OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(this, adapter);
        sPriority.setOnItemSelectedListener( this);

        // read in the services from the table, build an array, and then hook the array to the spinner
        
        Integer  i;
        numServices = 0;

        Cursor ServiceCursor = mDbHelper.fetchAllServices();
        if (ServiceCursor != null) {
        	ServiceCursor.moveToFirst();
            numServices = ServiceCursor.getCount();
            
//  System.out.println("numservices="+Long.toString(numServices));
            if (numServices > 0){
 
            	array_services = new String[numServices];
            	services_id = new Long[ numServices];
            	for ( i=1; i<= numServices; i++){
            		array_services[i-1] = ServiceCursor.getString(
          	            	ServiceCursor.getColumnIndexOrThrow( ContactDbAdapter.SERVICE_NAME));
            		services_id[ i-1] = ServiceCursor.getLong(0);	 // get the row number
//System.out.println("Service # "+ Long.toString(services_id[i-1])+ " at "+ Integer.toString(i) + " name "+ array_services[i-1] );
            		if ( i < numServices){
            			ServiceCursor.moveToNext();
            		}
            	}
            }
          
        }
        if ( numServices == 0){
/*        	
        	array_services = new String[ 1];
        	array_services[0] = "Undefined";
        	services_id = new Long[2];
        	services_id[0] = Long.getLong("-1");
*/
        	Toast.makeText(this, " Need to Define Services. ", Toast.LENGTH_LONG).show();
        	
            Intent iService = new Intent(this, ServiceList.class);
            startActivityForResult(iService, 0); 
            finish();
        	return;
        }
        
        
        sService = (Spinner) findViewById(R.id.ticketServiceSpinner);
        adapterService = new ArrayAdapter( this, android.R.layout.simple_spinner_item, array_services) ;
        
        adapterService.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sService.setAdapter(adapterService);
//		OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(this, adapter);
        sService.setOnItemSelectedListener( this);     
        
 //       mStatusText = (EditText) findViewById(R.id.ticketState);
        
        mContactText = (TextView) findViewById(R.id.contact);
//        mServiceText = (EditText) findViewById(R.id.ticketService);
//        mPriorityText = (EditText) findViewById(R.id.ticketPriority);
        mNoteText = (EditText) findViewById(R.id.note);
        mStartDate = (Button) findViewById(R.id.ticketDate1);
        mStartTime = (Button) findViewById(R.id.ticketTime1);
        mDollarsText = (EditText) findViewById(R.id.ticketDollar);
        mTotalTimeText = (EditText) findViewById(R.id.ticketTime2);
        
        Button confirmButton = (Button) findViewById(R.id.confirm);
        Button deleteButton = (Button) findViewById(R.id.delete);
        
        {
			String str1, strArr[], delims = " ";
     
        	str1 = (savedInstanceState == null) ? null :
        		(String) savedInstanceState.getSerializable(ContactDbAdapter.CONTACT_ID);
			if( str1 != null){
				strArr = str1.split(delims);
				mContactId = Long.parseLong( strArr[0]);
				mTicketId = Long.parseLong( strArr[1]);
//				System.out.println( "tryed serializable cid =" + Long.toString( mContactId)+ " ticket id = " + Long.toString( mTicketId));
			}
			
                    	
        }
		if (mContactId == null || mContactId ==0) {
			Bundle extras = getIntent().getExtras();
			String str1, strArr[], delims = " ";
			
			str1 = extras != null ? extras.getString(ContactDbAdapter.CONTACT_ID)
									: null;
			if( str1 != null){
				strArr = str1.split(delims);
				mContactId = Long.parseLong( strArr[0]);
				mTicketId = Long.parseLong( strArr[1]);
			}
//			System.out.println( "tryed getextras cid =" + Long.toString( mContactId)+ " ticket id = " + Long.toString( mTicketId));
        }
		if( mContactId == null || mContactId == 0){
			Toast.makeText(this, " Invalid Contact ", Toast.LENGTH_LONG).show();
        	return;
		}
		
//		System.out.println( " tickets oncreate contactid =" + Long.toString( mContactId) ); //+ " ticketid = "+ Long.toString( mTicketId));		
		populateFieldsContact();
		populateFieldsTicket();
		
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
//                System.out.println( " confirm on ticket");
                finish();
            }

        });
        if( mContactId != null && mTicketId != null && mContactId > 0 && mTicketId > 0)
        {
        	deleteButton.setOnClickListener(new View.OnClickListener() {

        		public void onClick(View view) {
        			confirmDeleteTicketDiag();
        			
        		}
        		

        	});
        	
        }

        // add a click listener to the button
        mStartDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//            	System.out.println( "click hit date");
                showDialog(DATE_DIALOG_ID);
                updateDate();
            }
        });

        // add a click listener to the button
        mStartTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//            	System.out.println( "click hit time");
                showDialog(TIME_DIALOG_ID);
                updateTime();
            }
        });
        
    }
// METHODS START HERE
 
        /**
         * When the user selects an item in the spinner, this method is invoked by the callback
         * chain. Android calls the item selected listener for the spinner, which invokes the
         * onItemSelected method.
         *
         * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(
         *  android.widget.AdapterView, android.view.View, int, long)
         * @param parent - the AdapterView for this listener
         * @param v - the View for this listener
         * @param pos - the 0-based position of the selection in the mLocalAdapter
         * @param row - the 0-based row number of the selection in the View
         */
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

    	TicketEdit.this.mPos = pos;
    	TicketEdit.this.mSelection = parent.getItemAtPosition(pos).toString();
    	Object thisAdapt = parent.getAdapter();
//       	System.out.println( " Spinner onItemSelected pos =" + Long.toString(pos)+ " item " + TicketEdit.this.mSelection);
       	
    	if( thisAdapt == adapterStatus){
//    		System.out.println( "Click from  Status Spinner");
    		if ( mTicketId == 0 || FirstHitStatus == 0){
//    			TextView resultText = (TextView)findViewById(R.id.ticketState );
//    			resultText.setText( TicketEdit.this.mSelection);
    			mStatusText = TicketEdit.this.mSelection;

//    			System.out.println( "did process click");
    		}
			FirstHitStatus = 0;
		}else{
    		if( thisAdapt == adapterPriority){
//    			System.out.println( "Click from Priority Spinner");
        		if ( mTicketId == 0 || FirstHitPriority == 0){
//        			TextView resultText = (TextView)findViewById(R.id.ticketPriority );
//        			resultText.setText( TicketEdit.this.mSelection);
        			mPriorityText = TicketEdit.this.mSelection;
//        			System.out.println( "did process click");
        		}
    			FirstHitPriority = 0;
    		}else{
    	   		if( thisAdapt == adapterService){
//        			System.out.println( "Click from Service Spinner");
            		if ( mTicketId == 0 || FirstHitService == 0){
//            			TextView resultText = (TextView)findViewById(R.id.ticketService );
//            			resultText.setText( TicketEdit.this.mSelection);
            			mServiceText = TicketEdit.this.mSelection;
// Get the service record and store the dollar, note and time into the ticket
            			
            			if( numServices > 0){
            				String aString;
            				Cursor ServiceCursor = mDbHelper.fetchService(services_id[TicketEdit.this.mPos]);
            		        if (ServiceCursor != null) {
//            		        	System.out.println("got Service="+Long.toString( services_id[TicketEdit.this.mPos] ));
            		           	 mDollarsText.setText( ServiceCursor.getString(
            		  	            	ServiceCursor.getColumnIndexOrThrow( ContactDbAdapter.SERVICE_DOLLARS)));
            		           	 mTotalTimeText.setText( ServiceCursor.getString(
            		 	            	ServiceCursor.getColumnIndexOrThrow( ContactDbAdapter.SERVICE_TIME))); 
            		         	 mNoteText.setText( ServiceCursor.getString(
             		 	            	ServiceCursor.getColumnIndexOrThrow( ContactDbAdapter.SERVICE_NOTE))); 
       		            	}
            			}
            			
//            			System.out.println( "did process click");
            		}
        			FirstHitService = 0;
    	   		}
    		}
    	}
    	
            /*
             * Set the value of the text field in the UI
             */
    	
    }

        /**
         * The definition of OnItemSelectedListener requires an override
         * of onNothingSelected(), even though this implementation does not use it.
         * @param parent - The View for this Listener
         */
    public void onNothingSelected(AdapterView<?> parent) {

            // do nothing

    }

    private void populateFieldsContact() {
        if (mContactId != null && mContactId > 0) {
// System.out.println(" TicketEdit popFields contactID= " + Long.toString( mContactId));
            Cursor contact = mDbHelper.fetchContact(mContactId);
            startManagingCursor(contact);
            mContactText.setText(contact.getString(
            	contact.getColumnIndexOrThrow(ContactDbAdapter.CONTACT_NAME)));
            }else{
            	System.out.println( " popFieldsContact with bad id");
            }
    }

    private void populateFieldsTicket(){
//    	System.out.println( "Ticket Fill");
        if (mTicketId != null && mTicketId > 0) {
//        	 System.out.println(" TicketEdit pop TicketsFields " + Long.toString( mTicketId));
        	 Cursor ticket = mDbHelper.fetchTicket(mTicketId);
        	 startManagingCursor( ticket);
//        	 mStatusText.      ( ticket.getString(
//        	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_STATE)));
        	 mServiceText = ticket.getString(
 	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_SERVICE));
        	 setSpinner( mServiceText, adapterService, sService);
        	 
        	 mNoteText.setText( ticket.getString(
  	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_NOTE)));
        	 mPriorityText = ticket.getString(
  	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_PRIORITY));
        	 setSpinner( mPriorityText, adapterPriority, sPriority);
        	 mStatusText = ticket.getString(
   	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_STATE));
        	 setSpinner( mStatusText, adapterStatus, sState);
        	 mStartDate.setText( ticket.getString(
    	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_START_DATE)));
         	 mStartTime.setText( ticket.getString(
 	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_START_TIME)));
           	 mDollarsText.setText( ticket.getString(
 	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_DLRS)));
           	 mTotalTimeText.setText( ticket.getString(
	            	ticket.getColumnIndexOrThrow( ContactDbAdapter.TICKET_TIME_TTL)));

           	 parseDate();
           	 parseTime();
           	 
         	 FirstHitPriority = 1;         // These prevent the first call into the spinners on an existing ticket from clobbering the data
         	 FirstHitStatus = 1;
         	 FirstHitService = 1;
//System.out.println( " Date = "+ mStartDate.getText().toString() + " tIME=" + mStartTime.getText().toString());
        	 
        }else{

        	 final Calendar c = Calendar.getInstance();
             mYear = c.get(Calendar.YEAR);
             mMonth = c.get(Calendar.MONTH);
             mDay = c.get(Calendar.DAY_OF_MONTH);
             mHour = c.get(Calendar.HOUR_OF_DAY);
             mHourDisplay = c.get(Calendar.HOUR);
             mMinute = c.get(Calendar.MINUTE);
             amPMer = c.get(Calendar.AM);
             updateDate();
             updateTime();
       	
 /*       	
        	Long aTime = System.currentTimeMillis();
        	
        	System.out.println("curTime= "+Long.toString(aTime));
        	Date date = new Date(aTime);
        	java.text.DateFormat dateFormat =
        	    android.text.format.DateFormat.getDateFormat(getApplicationContext());
    	
        	mStartDateText.setText(dateFormat.format(date));
        	
        	java.sql.Time timer =
        	    new java.sql.Time(aTime);
        	
        	mStartTimeText.setText( timer.toString());
        	mServiceText.setText("tbd");
     */
             
//        	System.out.println( " new Record popFieldsTicket");
        }
 
    }
    
    private int setSpinner( String findText, ArrayAdapter<CharSequence> adapterThis, Spinner thisSpinner ){
    	int i;
    	
    	i = adapterThis.getPosition( findText);
  //  	System.out.println(" selection from " + findText + " at "+ Integer.toString(i));
    	thisSpinner.setSelection( i);
    	    	
    	return i;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        String str1;
        str1 = Long.toString( mContactId)+ " " + Long.toString( mTicketId);
        outState.putSerializable(ContactDbAdapter.CONTACT_ID, str1);
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
        populateFieldsContact();
        populateFieldsTicket();
    }

    

    private void saveState() {
    	
//    	System.out.println( "Into savesTATE TICKET DelFlag = " + Long.toString(DelFlag));
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
        String State = mStatusText;
        String Service = mServiceText;
        String Priority = mPriorityText;
        String StartDate = mStartDate.getText().toString();
        String StartTime = mStartTime.getText().toString();
        String Dollarss = mDollarsText.getText().toString();
        String TtlTime = mTotalTimeText.getText().toString();
// System.out.println( " saveState mTicketID = "+ Long.toString(mTicketId) +"  state "+ State +" Service "+ Service + " Contact "+ 
//			Long.toString(mContactId) + " Priority " + Priority);
// System.out.println(" date = "+ StartDate + " time "+ StartTime);
        if (mTicketId == null || mTicketId == 0) {
//          public long createTicket(long Contact_id, String state, 
//    		String startTime, String endTime, String startDate, String endDate,  
//    		String note, String Dollars,
//    		String Service, String Priority) {
        	if( ContactDbAdapter.IsDemo > 0){
        		if( ContactDbAdapter.TicketRecCount >= 4){
        			Toast.makeText(this, " Exceeded Demo Version Limit ", Toast.LENGTH_LONG).show();
        			mTicketId = 0L;
        		}
        	}
        	long id = mDbHelper.createTicket( mContactId, State, 
            		StartTime, null, StartDate, null,  
            		note, null,
            		Service, Priority, TtlTime);
            if (id > 0) {
                mTicketId = id;
            }
        } else {
        	
//        	( long rowId, long Contact_id, String state, 
//            		String startDate, String startTime, String endDate, String endTime, String note, String Dollars,
//        	String Service, String Priority) 

        	mDbHelper.updateTicket(mTicketId, mContactId, State, StartDate, StartTime, null, null, note, Dollarss, 
        			Service, Priority, TtlTime);
        }
    }
    
    
    // updates the date in the TextView
    private void updateDate() {
//       	System.out.println( " updateDate called y="+Integer.toString(mYear)+ " m= "+ Integer.toString(mMonth)+ " d= "+ Integer.toString(mDay));
           mStartDate.setText(
               new StringBuilder()
                       // Month is 0 based so add 1
                       .append(mMonth + 1).append("/")
                       .append(mDay).append("/")
                       .append(mYear).append(" "));
    
    }
    
    
    
    // updates the date in the TextView
    private void updateTime() {
    	StringBuilder timeStr;
    	String Pmer, Miner;
 //      	System.out.println( " updateTime called hour="+Integer.toString(mHourDisplay)+ " minu= "+ Integer.toString(mMinute)+ " amPM= "+ 
 //      			Integer.toString(amPMer));
        if( amPMer == 0){
     	   Pmer = "am";
        }else{
     	   Pmer = "pm";
        }
        Miner = Integer.toString(mMinute);
        if(mMinute < 10){
        	Miner = "0"+Miner;
        }
        timeStr = new StringBuilder()
                       // Month is 0 based so add 1
                       .append(Integer.toString(mHourDisplay)).append(":")
                       .append(Miner).append(" ").append(Pmer);
        mStartTime.setText(timeStr);               
    }
    private void parseDate(){
	
   // This has to be made locale dependent to handle d m y and y m d	
    	//	Calendar c =Calendar.getInstance(); 
    		    		 		
    		String s1 = mStartDate.getText().toString();
    		int i, j, tdd, whichh;
    		char ac;
    		i = s1.length();
    		j = 0;
    		tdd = 0;
    		whichh = 0;
    		while ( j < i && whichh < 3 ){
    			ac = s1.charAt(j);
    			if( ac != '/' && ac != '-' && ac != ' ' && ac >='0' && ac <= '9'){
    				tdd = tdd *10 + ac -'0';
    			}else{
    				if ( whichh == 0){
    					mMonth = tdd -1;
    					whichh++;
    					tdd = 0;
    				}else{
    					if ( whichh == 1){
    						mDay = tdd;
    						tdd = 0;
    						whichh++;
    					}else{
    						if( whichh == 2){
    							mYear = tdd;
    							j += i +10;
    							whichh++;
    						}
    					}
    				}
    			}
    			j++;
    		}
    		if( whichh == 2){
    			mYear = tdd;
    		}
    		
//    System.out.println( "parse month ="+Integer.toString(mMonth) + " Year " + Integer.toString(mYear) + " Day "+ Integer.toString(mDay));		
        	
    }
        
    private void parseTime(){
	
    	String ST = mStartTime.getText().toString();
		int i, j, tdd, whichh;
		char ac;
		i = ST.length();
		j = 0;
		tdd = 0;
		whichh = 0;
		while ( j < i && whichh < 3 ){
			ac = ST.charAt(j);
			if( ac != ':' && ac != '-' && ac != ' ' && ac >='0' && ac <= '9'){
				tdd = tdd *10 + ac -'0';
			}else{
				if( (ac == 'a' || ac == 'p') && whichh == 2 ){
					tdd = ac;
					if ( j +1 < i){
						ac = ST.charAt(j);
						if ( ac == 'm'){
							if ( tdd == 'a'){
								amPMer = 0;
							}else{
								amPMer = 1;
								mHour += 12;
								
							}
							whichh++;
							j += i +10;
							continue;
						}
					}
					j++;
				}	
				if ( whichh == 0){
					mHour = tdd;
					mHourDisplay = mHour;
					whichh++;
					tdd = 0;
				}else{
					if ( whichh == 1){
						mMinute = tdd;
						tdd = 0;
						whichh++;
					}
				}
			}
			j++;
		}
		
    	if ( mHourDisplay > 12){
    		amPMer = 1;
    		mHourDisplay = mHour -12;
    	}else{
    		amPMer = 0;
    	}
    	 
    		
//    System.out.println( "parse hour ="+Integer.toString(mHour) + " Minutes " + Integer.toString(mMinute) + " amPM "+ Integer.toString(amPMer));		
        		        
    	
    	
    }
  
    @Override
    protected  Dialog onCreateDialog(int id) {
        switch (id) {
        case TIME_DIALOG_ID:
            return new TimePickerDialog(this,
                    mTimeSetListener, mHour, mMinute, false);
            
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
    
        }
        return null;
    }

    
    final DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
  //          	System.out.println( " OnDateSetListener hit ");
            	
            	mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDate();
            }
        };
        
        
        final TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                public void onTimeSet(TimePicker view, int hours, 
                                      int minutes) {
//                	System.out.println( " OnTimeSetListener hit hours = " + Integer.toString(hours) + " minutes "+ Integer.toString(minutes));
                	
                	mHour = hours;
                    mMinute = minutes;
                    amPMer = 0;
                    if( mHour > 12){
                    	amPMer = 1;
                    	mHourDisplay = mHour -12;
                    }
                    updateTime();
                }
            };
            
            
            public void confirmDeleteTicketDiag()
            {
            	/////////////////////////////////////////////////////////////////////////
                //I moved the dialog into the player itself, it is much much nicer now///
        			final AlertDialog.Builder confirmDelete = new AlertDialog.Builder(this);
        			//confirmVoice.setTitle("Please Choose Your Gender");
        			confirmDelete.setMessage("Delete Ticket?");
        			confirmDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
        			  public void onClick(DialogInterface dialog, int whichButton) {
        	             setResult(RESULT_CANCELED);  
        			  }
        			});
        			confirmDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int whichButton) {
        					// This code will not run or at least will not do what it is suppose to.
        					DelFlag = 1;
        					mDbHelper.deleteTicket( mTicketId);

        					mTicketId = null;
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
