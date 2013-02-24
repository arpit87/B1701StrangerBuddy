package my.b1701.SB.Server;

import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.HttpClient.GetMatchingNearbyUsersRequest;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.HttpClient.SBHttpRequest;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.util.Log;

public class AddThisUserSrcDstResponse extends ServerResponseBase{

    private static final String TAG = "my.b1701.SB.Server.AddUserResponse";
    private static long TIME_INTERVAL = 1 * 60 * 1000; //1 minute

	String user_id;
		
	public AddThisUserSrcDstResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);

				
	}
	
	@Override
	public void process() {
		//this process is not called if u make syncd consecutive requests,that time only last process called
		Log.i(TAG,"processing AddUsersResponse response.status:"+this.getStatus());	
		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		
		try {
			body = jobj.getJSONObject("body");
			ToastTracker.showToast("added this user src,dst");			
            //Context context = Platform.getInstance().getContext();
            Log.i(TAG, "Fetching nearby users..");
            SBHttpRequest getNearbyUsersRequest = new GetMatchingNearbyUsersRequest();
            SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
            //Intent getNearByUsersIntent = new Intent(context, GetNearByUsersService.class);
            //context.startService(getNearByUsersIntent);

            //Intent checkAndDelUserReqServiceStartIntent = new Intent(context, CheckAndDeleteUserRequestService.class);
            //PendingIntent pendingIntent = PendingIntent.getService(context, 0, checkAndDelUserReqServiceStartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            //AlarmManager alarmManager = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));
            //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, TIME_INTERVAL, TIME_INTERVAL, pendingIntent);
		} catch (JSONException e) {
			Log.e(TAG, "Error returned by server on user add scr dst");
			ProgressHandler.dismissDialoge();
			ToastTracker.showToast("Network error,try again");
			e.printStackTrace();
		}
		
	}
	
}