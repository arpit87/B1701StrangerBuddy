package my.b1701.SB.Server;

import my.b1701.SB.Activities.NewUserDialogActivity;
import my.b1701.SB.Activities.OtherUserProfileActivity;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.Users.NearbyUser;
import my.b1701.SB.Users.UserAttributes;
import my.b1701.SB.Users.UserFBInfo;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;


public class GetOtherUserProfileResponse extends ServerResponseBase{

	String status;
	
	NearbyUser thisNearbyUser ;
	UserFBInfo thisNearbyUserFBInfo;	
	
	
	private static final String TAG = "my.b1701.SB.Server.GetOtherUserProfileResponse";
	public GetOtherUserProfileResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);		
	}
	
	@Override
	public void process() {
		Log.i(TAG,"processing GetOtherUserProfileResponse response.status:"+this.getStatus());	
		
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		Log.i(TAG,"got json "+jobj.toString());
		try {
			ProgressHandler.dismissDialoge();
			body = jobj.getJSONObject("body");
			JSONObject nearbyUsersFbInfo =  body.getJSONObject(UserAttributes.FBINFO);			
			Intent i = new Intent(Platform.getInstance().getContext(),OtherUserProfileActivity.class);			
			i.putExtra("fb_info", nearbyUsersFbInfo.toString());
			Platform.getInstance().getContext().startActivity(i);
			//status = body.getString("Status");			
			//ThisUserConfig.getInstance().putBool(ThisUserConfig.FBINFOSENTTOSERVER, true);
			
			//ToastTracker.showToast("fb save:"+status);
		} catch (JSONException e) {			
			ToastTracker.showToast("Some error occured");
			Log.e(TAG, "Error returned by server get fb info for user and show popup");
			e.printStackTrace();
		}
		
	}
	
}