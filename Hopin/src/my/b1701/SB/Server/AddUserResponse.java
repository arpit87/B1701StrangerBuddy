package my.b1701.SB.Server;

import my.b1701.SB.Activities.MapListViewTabActivity;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.HttpClient.ChatServiceCreateUser;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.HttpClient.SBHttpRequest;
import my.b1701.SB.HttpClient.SaveFBInfoRequest;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.Users.UserAttributes;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class AddUserResponse extends ServerResponseBase{

	
	String user_id;	
	Activity tutorial_activity;//needed to stop activity
	
	private static final String TAG = "my.b1701.SB.Server.AddUserResponse";
	public AddUserResponse(HttpResponse response,String jobjStr,Activity tutorial_activity) {
		super(response,jobjStr);
		this.tutorial_activity = tutorial_activity;
	}
	
	@Override
	public void process() {
		Log.i(TAG,"processing AddUsersResponse response.status:"+this.getStatus());
		
		Log.i(TAG,"got json "+jobj.toString());		
		try {
			body = jobj.getJSONObject("body");
			user_id = body.getString(UserAttributes.USERID);
			ThisUserConfig.getInstance().putString(ThisUserConfig.USERID, user_id);
			ThisUserNew.getInstance().setUserID(user_id);	
			ToastTracker.showToast("Got user_id:"+user_id);
			ProgressHandler.dismissDialoge();
			tutorial_activity.finish();
			//now we will start map activity
			final Intent showSBMapViewActivity = new Intent(Platform.getInstance().getContext(), MapListViewTabActivity.class);	
			showSBMapViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 Platform.getInstance().getHandler().post(new Runnable() {
	          public void run() { 
	              Platform.getInstance().getContext().startActivity(showSBMapViewActivity);	              
	          }
	        });
			 //this happends on fblogin from tutorial activity direct
			 String fbid = ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID);
			 if(fbid != "")
			 {
				 sendAddFBAndChatInfoToServer(fbid);
			 }
		} catch (JSONException e) {
			Log.e(TAG, "Error returned by server on user add");
			ToastTracker.showToast("Unable to communicate to server,try again later");
			e.printStackTrace();
		}
		
	}
	
	 private void sendAddFBAndChatInfoToServer(String fbid) {
	    	//this should only be called from fbpostloginlistener to ensure we have fbid
	    	Log.i(TAG,"in sendAddFBAndChatInfoToServer");
	    	SBHttpRequest chatServiceAddUserRequest = new ChatServiceCreateUser(fbid);
	     	SBHttpClient.getInstance().executeRequest(chatServiceAddUserRequest);
			SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID), fbid, ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
			SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);			
		}
	
}