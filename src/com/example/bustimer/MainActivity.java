package com.example.bustimer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.os.SystemClock;
//import android.widget.Button;
import android.widget.TextView;
import android.widget.Chronometer;
//import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.Writer;
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
//	private Button buttonStart;
//	private Button buttonStop;
//	private Button buttonMoving;
//	private Button buttonBusStop;
//	private Button buttonCongestionSlow;
//	private Button buttonCongestionStop;
//	private Button buttonTrafficLight;
	private boolean running;
	private File outFile;
	private static final String LOG_TAG = "Bus Timer";
	
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
//        buttonStart = (Button)findViewById(R.id.buttonStart);
//        buttonStop = (Button)findViewById(R.id.buttonStop);
//        buttonMoving = (Button)findViewById(R.id.buttonMoving);
//        buttonBusStop = (Button)findViewById(R.id.buttonBusStop);
//        buttonCongestionSlow = (Button)findViewById(R.id.buttonCongestionSlow);
//        buttonCongestionStop = (Button)findViewById(R.id.buttonCongestionStop);
//        buttonTrafficLight = (Button)findViewById(R.id.buttonTrafficLight);
        running = false;
//        outFile = getFileLocation(null,"date");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /*
     * Bus is moving
     */
    public void timerMoving (View view) {
    	textState.setText(getResources().getString(R.string.moving));
    }
    
    /*
     * Bus is stopped at a stop
     */
    public void timerBusStop (View view) {
    	textState.setText(getResources().getString(R.string.bus_stop));
    }
    
    /*
     * Bus is stopped at a traffic light
     */
    public void timerTrafficLight (View view) {
    	textState.setText(getResources().getString(R.string.traffic_light));
    }
    
    /*
     * Bus is slowed by congestion
     */
    public void timerCongestionSlow (View view) {
    	textState.setText(getResources().getString(R.string.congestion_slow));
    }
    
    /*
     * Bus is stopped in congestion
     */
    public void timerCongestionStop (View view) {
    	textState.setText(getResources().getString(R.string.congestion_stop));
    }
    
    /*
     * Start the timer
     */
    public void timerStart (View view) {
    	if (running == false) {
    		outFile = this.getFileLocation(this.getFileName());
    		FileWriter fw = FileWriter(outFile);
    		chrono.setBase(SystemClock.elapsedRealtime());
    		chrono.start();
    		running = true;
    	}
    }
    
    /*
     * Stop the timer
     */
    public void timerStop (View view) {
    	if (running == true) {
    		chrono.stop();
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
        // Get the directory for the app's private pictures directory. 
        File file = new File(
        		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        		fileName
        		);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
    
    public String getFileName() {
    	Calendar rightNow = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMDD_HHmmss", Locale.US); 
    	return sdf.format(rightNow);
    }
}
