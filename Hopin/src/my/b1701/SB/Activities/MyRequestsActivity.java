package my.b1701.SB.Activities;

import my.b1701.SB.R;
import my.b1701.SB.ActivityHandlers.MyRequestActivityHandler;
import my.b1701.SB.HelperClasses.BroadCastConstants;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HttpClient.DeleteRequest;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.Users.UserAttributes;
import my.b1701.SB.Util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MyRequestsActivity extends Activity {
	
	private final String TAG = "my.b1701.SB.Avtivity.MyRequestActivity";
	TextView carpoolsource;
	TextView carpooldestination;
	TextView carpooltime;
	TextView instasource;
	TextView instadestination;
	TextView instatime;
	Button deleteCarpoolReq;
	Button deleteInstaReq;	
	View instaActiveLayout;
	View carPoolActiveLayout;
	TextView carPoolNoActiveReq;
	TextView instaNoActiveReq;
	MyRequestActivityHandler reqHandler = null; // this receives broadcast on response post delete
	
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_requests_layout);  
        carpoolsource = (TextView) findViewById(R.id.my_requests_carpool_source);
        carpooldestination = (TextView) findViewById(R.id.my_requests_carpool_destination);
        carpooltime = (TextView) findViewById(R.id.my_requests_carpool_details);
        instasource = (TextView) findViewById(R.id.my_requests_insta_source);
        instadestination = (TextView) findViewById(R.id.my_requests_insta_destination);
        instatime = (TextView) findViewById(R.id.my_requests_insta_details);
        deleteCarpoolReq = (Button) findViewById(R.id.my_requests_carpool_deletereq);
        deleteInstaReq = (Button) findViewById(R.id.my_requests_insta_deletereq);      
        instaActiveLayout = (View)findViewById(R.id.my_requests_instareq_layout);
        carPoolActiveLayout = (View)findViewById(R.id.my_requests_carpoolreq_layout);
        carPoolNoActiveReq = (TextView)findViewById(R.id.my_requests_carpool_noactivereq);
        instaNoActiveReq = (TextView)findViewById(R.id.my_requests_insta_noactivereq);
        reqHandler = new MyRequestActivityHandler(this);
        deleteCarpoolReq.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				registerReceiver(reqHandler, new IntentFilter(BroadCastConstants.CARPOOLREQ_DELETED));
				ProgressHandler.showInfiniteProgressDialoge(MyRequestsActivity.this, "Deleting carpool request", "Please wait");
				DeleteRequest deleteRequest = new DeleteRequest(0);
                SBHttpClient.getInstance().executeRequest(deleteRequest);
                
			}
		});
        
		deleteInstaReq.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View paramView) {
						registerReceiver(reqHandler, new IntentFilter(BroadCastConstants.INSTAREQ_DELETED));
						ProgressHandler.showInfiniteProgressDialoge(MyRequestsActivity.this, "Deleting insta request", "Please wait");
						DeleteRequest deleteRequest = new DeleteRequest(1);
		                SBHttpClient.getInstance().executeRequest(deleteRequest);
		                
					}
				});
       
    }

    @Override
    protected void onResume(){
    	super.onResume();
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);   
        Log.i(TAG,"carpooljson:"+carpoolReqJson);
        Log.i(TAG,"instajson:"+instaReqJson);
        if(!StringUtils.isBlank(carpoolReqJson))
        {
        	carPoolActiveLayout.setVisibility(View.VISIBLE);
        	carPoolNoActiveReq.setVisibility(View.GONE);
        	
        	try {
				JSONObject responseJsonObj = new JSONObject(carpoolReqJson);
				JSONObject body = responseJsonObj.getJSONObject("body");
				String source = body.getString(UserAttributes.SRCADDRESS);
				String destination = body.getString(UserAttributes.DSTADDRESS);
				String datetime = body.getString(UserAttributes.DATETIME);				
				carpoolsource.setText(source);
				carpooldestination.setText(destination);
				carpooltime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "hh:mm a", datetime));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!StringUtils.isBlank(instaReqJson))
        {   
        	instaActiveLayout.setVisibility(View.VISIBLE);
        	instaNoActiveReq.setVisibility(View.GONE);
        	try {
				JSONObject responseJsonObj = new JSONObject(instaReqJson);
				JSONObject body = responseJsonObj.getJSONObject("body");
				String source = body.getString(UserAttributes.SRCADDRESS);
				String destination = body.getString(UserAttributes.DSTADDRESS);
				String datetime = body.getString(UserAttributes.DATETIME);
				instasource.setText(source);
				instadestination.setText(destination);
				instatime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "d MMM, hh:mm a", datetime));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
    
    public void setCarpoolReqLayoutToNoActiveReq()
    {
    	carPoolActiveLayout.setVisibility(View.GONE);
    	carPoolNoActiveReq.setVisibility(View.VISIBLE);
    	unregisterReceiver(reqHandler);
    }
    
    public void setInstaReqLayoutToNoActiveReq()
    {
    	instaActiveLayout.setVisibility(View.GONE);
    	instaNoActiveReq.setVisibility(View.VISIBLE);
    	unregisterReceiver(reqHandler);
    }
}
