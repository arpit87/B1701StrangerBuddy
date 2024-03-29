package my.b1701.SB.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.CustomViewsAndListeners.SBMapView;
import my.b1701.SB.FacebookHelpers.FacebookConnector;
import my.b1701.SB.Fragments.FBLoginDialogFragment;
import my.b1701.SB.Fragments.SBListFragment;
import my.b1701.SB.Fragments.SBMapFragment;
import my.b1701.SB.Fragments.ShowActiveReqPrompt;
import my.b1701.SB.HelperClasses.BlockedUser;
import my.b1701.SB.HelperClasses.BroadCastConstants;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ThisAppConfig;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.HttpClient.*;
import my.b1701.SB.LocationHelpers.SBLocationManager;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.R;
import my.b1701.SB.Users.CurrentNearbyUsers;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.Util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;


public class MapListViewTabActivity extends SherlockFragmentActivity  {
	//public View mMapViewContainer;
	
	private static final String TAG = "my.b1701.SB.Activities.MapListViewTabActivity";
	
	MapListActivityHandler mapListActivityHandler = MapListActivityHandler.getInstance();
	private ViewGroup mMapViewContainer;	
	private SBMapView mMapView;
	
	
	private ImageButton selfLocationButton = null;
	private ToggleButton offerRideButton = null;
	
	ActionBar ab;
	private boolean currentIsOfferMode;
	
	private FacebookConnector fbconnect;
	FragmentManager fm = getSupportFragmentManager();
	private boolean isMapShowing = true;
   
    private SBMapFragment sbMapFragment;
    private SBListFragment sbListFragment;
    private ImageView mFbLogin;
    private Menu mMenu;
	
	public FacebookConnector getFbConnector()
	{
		return fbconnect;
	}
	
	

	/** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
       // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
       // requestWindowFeature((int) Window.FEATURE_ACTION_BAR & ~Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.maplistview);        
        showMapView();
        ab= getSupportActionBar();
        
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_black));   
        
        ToastTracker.showToast("Your userid:"+ThisUserNew.getInstance().getUserID());
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);       
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        this.registerReceiver(mapListActivityHandler,new IntentFilter(BroadCastConstants.NEARBY_USER_UPDATED));    
        fbconnect = new FacebookConnector(this);
        
        //show prompt if any of req active
        
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);
        if(!StringUtils.isBlank(instaReqJson) || !StringUtils.isBlank(carpoolReqJson))
        {
        	ShowActiveReqPrompt activereq_dialog = new ShowActiveReqPrompt();
       		activereq_dialog.show(getSupportFragmentManager(), "active_req_prompt");
        }
        //checkIfGPSIsEnabled();
        
        
    }
    
    @Override
    public void onStart(){
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    private void checkIfGPSIsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                    	startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onStop(){
        super.onStop();
        EasyTracker.getInstance().activityStop(this);       
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	//we update realtime when on map activity
    	SBLocationManager.getInstance().StartListeningtoNetwork(); 
    	
    }

    //test
    @Override
	public void onPause(){
    	super.onPause();
    	//MapListActivityHandler.getInstance().setUpdateMap(false);
    	SBLocationManager.getInstance().StopListeningtoGPS();    	
        SBLocationManager.getInstance().StopListeningtoNetwork();
    	//mymapview.getOverlays().clear();
    	//mymapview.postInvalidate();
    }
    
    @Override
    public void onDestroy()
    {    
    	super.onDestroy();    	
    	this.unregisterReceiver(mapListActivityHandler);
    	mapListActivityHandler.clearAllData();  
    	ThisUserNew.clearAllData();
    	CurrentNearbyUsers.getInstance().clearAllData();    	  
        int count = ThisAppConfig.getInstance().getInt(ThisAppConfig.APPOPENCOUNT);     	
     	ThisAppConfig.getInstance().putInt(ThisAppConfig.APPOPENCOUNT,++count);
     	if(count%5==0)
     	{
     		//show msg every fifth time ap is closed
     		Intent i = new Intent(Platform.getInstance().getContext(),FeedbackActivity.class);
 			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     	
 			i.putExtra("showprompt", true);
 			Platform.getInstance().getContext().startActivity(i);
     		
     	}
    	
    }
	
 
		
	@Override
	public void onBackPressed() {
        if (!isMapShowing){
            isMapShowing = true;
            showMapView();
            MenuItem menuItem = mMenu.findItem(R.id.btn_listview);
            menuItem.setIcon(R.drawable.maptolist);
        } else {
                
                MapListViewTabActivity.super.onBackPressed();
        }
        
	}
  

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbconnect.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	
        switch (menuItem.getItemId())
        {
        case R.id.menu_search:
        	//onSearchRequested();        	 
	    	 Intent searchInputIntent = new Intent(this,SearchInputActivityNew.class);	
	    	 searchInputIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	   		 startActivity(searchInputIntent);
        	break;
        case R.id.btn_listview:
        	toggleMapListView(menuItem);
        	break;
       /* case R.id.fb_login_menuitem:
        	if(ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
        	{
        		Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
        		break;
        	}
        	FBLoginDialogFragment fblogin_dialog = new FBLoginDialogFragment();
			fblogin_dialog.show(getSupportFragmentManager(), "fblogin_dialog");
			break;
        case R.id.fb_logout_menuitem:
        	//logout from chat server?
			FacebookConnector fbconnect = new FacebookConnector(MapListViewTabActivity.this);
        	fbconnect.logoutFromFB();
        	break;*/
        case R.id.settings_menuitem:
        	Intent i = new Intent(this,SettingsActivity.class);
        	i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	startActivity(i);
        	break;
        case R.id.exit_app_menuitem:
        	//delete user request,close service
        	Platform.getInstance().stopChatService();
        	finish();
        	break; 
   	/* case R.id.test_app_menuitem:
   		ShowActiveReqPrompt activereq_dialog = new ShowActiveReqPrompt();
   		activereq_dialog.show(getSupportFragmentManager(), "fblogin_dialog");
   		 break;*/
     case R.id.my_requests:
         Intent myRequestIntent = new Intent(this, MyRequestsActivity.class);
         myRequestIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
         startActivity(myRequestIntent);
         break;
     case R.id.my_chats:
         Intent myChatsIntent = new Intent(this, MyChatsActivity.class);
         myChatsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
         startActivity(myChatsIntent);
         break;  
     
        } 
        return super.onOptionsItemSelected(menuItem);
    }   
    
    public void buttonOnMapClick(View button)
    {
    	switch(button.getId())
    	{
    	case R.id.my_location_button:
    		MapListActivityHandler.getInstance().myLocationButtonClick();    		
    		break; 	
    	}
    }

    private void toggleMapListView(MenuItem menuItem)
    {
    	if(!isMapShowing)
    	{    		
    		isMapShowing = true;
    		showMapView();
    		menuItem.setIcon(R.drawable.maptolist);
    	}
    	else
    	{    		
    		isMapShowing = false;
    		showListView();
    		menuItem.setIcon(R.drawable.listtomap);
    	}
    		
    }
    
    private void showMapView()
    {
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().sendView("MapView");
    	if (fm != null) {
            
            FragmentTransaction ft = fm.beginTransaction();
            if (sbMapFragment == null) {
                sbMapFragment = new SBMapFragment();
                ft.add(R.id.maplistviewcontent, sbMapFragment);
            } else {
                ft.replace(R.id.maplistviewcontent, sbMapFragment);
            }
            ft.commit();
        }
    }
    
    private void showListView()
    {
        EasyTracker.getTracker().sendView("ListView");
        if (fm != null) {
            
            FragmentTransaction ft = fm.beginTransaction();
            if (sbListFragment == null) {
                sbListFragment = new SBListFragment();
            }
            ft.add(R.id.maplistviewcontent, sbListFragment);
            ft.commit();
        }
    }
   
	public ViewGroup getThisMapContainerWithMapView()
    {
    	if(mMapViewContainer == null)
    	{
    		mMapViewContainer = (ViewGroup) getLayoutInflater().inflate(R.layout.map,null,false);
    		mMapView = (SBMapView) mMapViewContainer.findViewById(R.id.map_view);
    		selfLocationButton = (ImageButton) mMapViewContainer.findViewById(R.id.my_location_button);
    		//offerRideButton = (ToggleButton) mMapViewContainer.findViewById(R.id.offerride_button);
    		if(currentIsOfferMode)
    			offerRideButton.setChecked(true);
    		mMapView.getOverlays().clear();    		
    		MapListActivityHandler.getInstance().setMapView(mMapView);
            MapListActivityHandler.getInstance().setUnderlyingActivity(this);
            Log.i(TAG,"initialize handler");
            Log.i(TAG,"initialize mylocation");
            MapListActivityHandler.getInstance().initMyLocation();
    		//mMapViewContainer.removeView(mMapView);
    	}
    	else
    	{
    		mMapViewContainer.addView(mMapView);
    		mMapViewContainer.addView(selfLocationButton);
    		
    		//mMapViewContainer.addView(offerRideButton);
    		//if(currentIsOfferMode)
    		//	offerRideButton.setChecked(true);
    	}
    	return mMapViewContainer;
    }

      
}
