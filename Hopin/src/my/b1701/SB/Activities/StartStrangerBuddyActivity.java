package my.b1701.SB.Activities;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import my.b1701.SB.R;
import my.b1701.SB.HelperClasses.SBConnectivity;
import my.b1701.SB.HelperClasses.ThisAppConfig;
import my.b1701.SB.HelperClasses.ThisAppInstallation;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.LocationHelpers.SBGeoPoint;
import my.b1701.SB.LocationHelpers.SBLocationManager;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.Users.ThisUserNew;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class StartStrangerBuddyActivity extends Activity {
	
	private ProgressBar mProgress;
	private static final String TAG = "my.b1701.SB.Activities.StartStrangerBuddyActivity";
	Runnable startMapActivity;
	Intent showSBMapViewActivity;
	Timer timer;
	AtomicBoolean mapActivityStarted = new AtomicBoolean(false);	
	private Context platformContext;
	
	
    /** Called when the activity is first created. */
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        
        /*ThisAppConfig.getInstance().putLong(ThisAppConfig.NETWORKFREQ, 30*1000); //.5 min
        ThisAppConfig.getInstance().putLong(ThisAppConfig.GPSFREQ, 2*60*1000);	 //2 min
        ThisAppConfig.getInstance().putLong(ThisAppConfig.USERCUTOFFDIST,1000);  //1000 meter
        ThisAppConfig.getInstance().putLong(ThisAppConfig.USERPOSCHECKFREQ,2*60*1000);  //2min*/
        SBLocationManager.getInstance().StartListeningtoNetwork(500,10);      
        
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO)
        {
        	Log.d(TAG, "requested for single loc intent");
        	SBLocationManager.getInstance().requestSingleLocationUpdate();
        }*/
        //SBLocationManager.getInstance().StartListeningtoGPS(ThisAppConfig.getInstance().getLong("gpsfreq"),100);
        Log.i(TAG,"started network listening "); 
        platformContext = Platform.getInstance().getContext();
        
        if(!SBConnectivity.isConnected())
        {
			Toast.makeText(platformContext, "No network connection", Toast.LENGTH_SHORT);
			finish();
			return;
        }
        
      //this might only connect to xmpp server and not login if new user and not yet fb login
      //  startChatService();
               
        //map activity can get started from 3 places, timer task if location found instantly
        //else this new runnable posted after 3 seconds
        //else on first run
        showSBMapViewActivity = new Intent(platformContext, MapListViewTabActivity.class);
        showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        startMapActivity = new Runnable() {
	          public void run() {	        		  
	        	  platformContext.startActivity(showSBMapViewActivity);
	          }};
        
        
        if(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID) == "")
        { 	
			firstRun();		
        }
		else	
		{
			ThisUserNew.getInstance().setUserID(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID));
			//showSBMapViewActivity = new Intent(this, MapListViewTabActivity.class);
	        //showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//startActivity(showSBMapViewActivity);			
			timer = new Timer();
			timer.scheduleAtFixedRate(new GetNetworkLocationFixTask(), 500, 500);		
	        
	       // Platform.getInstance().getHandler().postDelayed(startMapActivity,1000 * 3); 
		}   
        
    }
    
    private void firstRun() {
		//get user_id from the server				
		String uuid = ThisAppInstallation.id(this.getBaseContext());
		ThisAppConfig.getInstance().putString(ThisAppConfig.APPUUID,uuid);
		//with uuid means first time start
		final Intent show_tutorial = new Intent(this,Tutorial.class);
		show_tutorial.putExtra("uuid", uuid);
		Runnable r = new Runnable() {
	          public void run() {	        		  
	        	  startActivity(show_tutorial);
	        	  finish();
	          }};
		Platform.getInstance().getHandler().postDelayed(r, 2000);
	}
    
   
    
    public void onResume()
    {   	
    	super.onResume();    	
    }
    
    public void onPause()
    {
    	super.onPause();
    	//SBLocationManager.getInstance().StopListeningtoGPS();    	
    	//SBLocationManager.getInstance().StopListeningtoNetwork();
    }

    public void onStart(){
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    public void onStop()
    {   	
    	super.onStop();
        EasyTracker.getInstance().activityStop(this);
    	finish();
    }
    
    private class GetNetworkLocationFixTask extends TimerTask
    { 
    	private int counter = 0;
         public void run() 
         {
        	 counter++;
        	 Log.i(TAG, "timer task counter:"+counter);
        	 SBGeoPoint currGeo;
        	 
        	 //check if it got location by singleUpdateintent which works for froyo+
        	 currGeo = ThisUserNew.getInstance().getCurrentGeoPoint();
        	 
        	 if(currGeo == null)
        	 {
        		 Location lastBestLoc = SBLocationManager.getInstance().getLastXSecBestLocation(10*60);  
        		 if(lastBestLoc!=null)
        			 currGeo = new SBGeoPoint((int)(lastBestLoc.getLatitude()*1e6), (int)(lastBestLoc.getLongitude()*1e6));
        	 }
        	 
        	 if(currGeo != null || counter>5)
        	 {       
        		 timer.cancel();
       		  	 timer.purge();
        		 ToastTracker.showToast("starting activity in counter:"+counter);  
        		 if(currGeo != null)
        			 ThisUserNew.getInstance().setCurrentGeoPoint(currGeo);        		 
        		 if(!mapActivityStarted.getAndSet(true))
	        	  {
        			 Platform.getInstance().getHandler().post(startMapActivity);
	        	  }
        	 }
          }
     }
    
   /* public void startChatService(){
     
          Intent i = new Intent("my.b1701.SB.ChatService.SBChatService");                  
          Log.d( TAG, "Service starting" );
          platformContext.startService(i);
         
         }*/
                    
}