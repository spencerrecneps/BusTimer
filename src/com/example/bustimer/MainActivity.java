package com.example.bustimer;
//right now i'm stuck trying to get gps to turn off
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.SystemClock;
import android.widget.TextView;
import android.widget.Chronometer;
import android.widget.Switch;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {
	/*
	 * declare
	 */
	private Chronometer chrono;
	private TextView textState;
	private Switch directionSwitch;
	private boolean running;
	private boolean direction;
	private File outFile;
    BufferedWriter bw;
	private static final String LOG_TAG = "Bus Timer";
	private GPSTracker gps;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /*
         * hook up interface elements to declared objects
         */
        chrono = (Chronometer)findViewById(R.id.chrono);
        textState = (TextView)findViewById(R.id.textState);
        textState.setText(getResources().getString(R.string.moving));
        directionSwitch = (Switch)findViewById(R.id.switchbutton);
        direction = false;		//false = nb/eb
        running = false;
        
        gps = new GPSTracker(MainActivity.this); 	// set up gps tracker
        
        //test gps connection and prompt to enable if disabled
        if (!gps.canGetLocation()) {
        	gps.showSettingsAlert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.app_quit:
            	//gps = null;
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    @Override
    protected void onPause() {
    	gps.stopUsingGPS();
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	gps.getLocation();
    	if (!gps.canGetLocation()) {
        	gps.showSettingsAlert();
        }
    	super.onResume();
    }
    
    public void onSwitchClicked (View view) {
    	/*
    	 * Change direction
    	 */
    	direction = directionSwitch.isChecked();  
    }
    
    public void timerMoving (View view) {
        /*
         * Bus is moving
         */
    	String buttonText = getResources().getString(R.string.moving);
    	if (running) {
    		this.writeEntry(buttonText);
    	}
    	textState.setText(buttonText);
    	return;
    }
    
    public void timerBusStop (View view) {
        /*
         * Bus is stopped at a stop
         */
    	String buttonText = getResources().getString(R.string.bus_stop);
    	if (running) {
    		this.writeEntry(buttonText);
    	}
		textState.setText(buttonText);
		return;
    }
    
    public void timerTrafficLight (View view) {
        /*
         * Bus is stopped at a traffic light
         */
    	String buttonText = getResources().getString(R.string.traffic_light);
    	if (running) {
    		this.writeEntry(buttonText);
    	}
    	textState.setText(buttonText);
    	return;
    }
    
    public void timerCongestionSlow (View view) {
        /*
         * Bus is slowed by congestion
         */
    	String buttonText = getResources().getString(R.string.congestion_slow);
    	if (running) {
    		this.writeEntry(buttonText);
    	}
    	textState.setText(buttonText);
    	return;
    }
    
    public void timerCongestionStop (View view) {
        /*
         * Bus is stopped in congestion
         */
    	String buttonText = getResources().getString(R.string.congestion_stop);
    	if (running) {
    		this.writeEntry(buttonText);
    	}
    	textState.setText(buttonText);
    	return;
    }
    
    public void timerStart (View view) {
        /*
         * Start the timer
         */
    	
    	//check for external storage
    	if (!this.isExternalStorageWritable()) {
    		this.displayError("Could not find external storage for writing");
    		return;
    	}
    	
    	//check if clock is already running
    	if (running == false) {
    		try {
    			outFile = this.getFileLocation(this.getFileName());
    			bw = new BufferedWriter(new FileWriter(outFile));
    			bw.write("timestamp;lon;lat;travel_condition;direction\n");
    	    	this.writeEntry(textState.getText().toString());
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			return;
    		}

    		//start the clock and set running to true
    		chrono.setBase(SystemClock.elapsedRealtime());
    		chrono.start();
    		running = true;
    	}
    	else {
    		this.displayError("The timer is already running");
    	}
    }
    
    public void timerStop (View view) {
        /*
         * Stop the timer
         */
    	if (running == true) {
    		chrono.stop();
    		this.writeEntry("end");
    		try {
    			bw.close();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    		running = false;
    	}
    }
    
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    
    public File getFileLocation(String fileName) {
    	File dir = new File(Environment.getExternalStorageDirectory(),"BusTimer");
    	if (!dir.exists() && !dir.mkdirs()) {
    		this.displayError("Could not create output file");
            Log.e(LOG_TAG, "Directory not created");
    	}
    	File file = new File(dir,fileName.concat(".txt"));
        return file;
    }
    
    public String getFileName() {
    	Calendar rightNow = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmmss", Locale.US); 
    	return sdf.format(rightNow.getTime());
    }
    
    public void displayError(CharSequence text) {
    	Context context = getApplicationContext();
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    }
    
    public void writeEntry(String state) {
    	try {
    		Calendar rightNow = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.US);
        	bw.write(sdf.format(rightNow.getTime()));
      		bw.write(";");
       		bw.write(String.valueOf(gps.getLongitude()));
       		bw.write(";");
       		bw.write(String.valueOf(gps.getLatitude()));
       		bw.write(";");
        	bw.write(state);
        	bw.write(";");
        	if (direction) {	// false = nb/eb
        		bw.write("sb/wb");
        	}
        	else {
        		bw.write("nb/eb");
        	}
        	bw.write("\n");
    	}
    	catch (IOException e) {
    		this.displayError("Could not register input");
    		Log.e(LOG_TAG, "write exception");
    	}
    	return;
    }
    
    
}
