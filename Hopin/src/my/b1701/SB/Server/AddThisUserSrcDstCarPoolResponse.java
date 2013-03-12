package my.b1701.SB.Server;

import android.util.Log;
import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.HttpClient.GetMatchingCarPoolUsersRequest;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.HttpClient.SBHttpRequest;
import my.b1701.SB.Users.UserAttributes;
import org.apache.http.HttpResponse;
import org.json.JSONException;

public class AddThisUserSrcDstCarPoolResponse extends ServerResponseBase{

    private static final String TAG = "my.b1701.SB.Server.AddUserResponse";
    

	String user_id;
		
	public AddThisUserSrcDstCarPoolResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);

				
	}
	
	@Override
	public void process() {
		//this process is not called if u make syncd consecutive requests,that time only last process called
		Log.i(TAG,"processing AddUsersResponse response.status:"+this.getStatus());	
		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		Log.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");
			ToastTracker.showToast("added this user src,dst for car pool,fetching match");
            body.put(UserAttributes.DAILYINSTATYPE, 0);
            ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_CARPOOL, body.toString());
            MapListActivityHandler.getInstance().setSourceAndDestination(body);
			SBHttpRequest getNearbyUsersRequest = new GetMatchingCarPoolUsersRequest();
	        SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
			
		} catch (JSONException e) {
			Log.e(TAG, "Error returned by server on user add scr dst");
			ToastTracker.showToast("Network error,try again");
			ProgressHandler.dismissDialoge();
			e.printStackTrace();
		}
		
	}
	

	
}

