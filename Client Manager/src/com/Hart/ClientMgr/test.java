package com.Hart.ClientMgr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class test extends Activity{
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            TextView t = new TextView(this);
            t=(TextView)findViewById(R.id.tester); 
            t.setText("Step One: blast egg");
            t.getText  ();

    }
}
