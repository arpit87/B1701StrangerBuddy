package my.b1701.SB.Server;

import android.content.Intent;
import android.util.Log;
import my.b1701.SB.HelperClasses.BroadCastConstants;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.Platform.Platform;
import org.apache.http.HttpResponse;
import org.json.JSONException;

public class FeedbackResponse extends ServerResponseBase{
	
	private static final String TAG = "my.b1701.SB.Server.FeedbackResponse";
	
	public FeedbackResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);
		
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();
		Log.i(TAG,"processing FeedbackResponse");
		Log.i(TAG,"server response:"+jobj.toString());
		try {			
			String body = jobj.getString("body");
			ToastTracker.showToast("Feedback saved successfully");			
		} catch (JSONException e) {
			ToastTracker.showToast("Some error occured in saving feedback");
			e.printStackTrace();
		}
	}
}
