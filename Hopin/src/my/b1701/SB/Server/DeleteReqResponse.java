package my.b1701.SB.Server;

import my.b1701.SB.HelperClasses.BroadCastConstants;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.Server.ServerResponseBase.ResponseStatus;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DeleteReqResponse extends ServerResponseBase{
	
	private static final String TAG = "my.b1701.SB.Server.DeleteUserResponse";
	int daily_insta_type;

	public DeleteReqResponse(HttpResponse response,String jobjStr,int daily_insta_type) {
		super(response,jobjStr);
		this.daily_insta_type = daily_insta_type;
	}

	@Override
	public void process() {
		ProgressHandler.dismissDialoge();
		Log.i(TAG,"processing PostUserReqDataResponse");
		Log.i(TAG,"server response:"+jobj.toString());
		try {
			//body = jobj.getJSONObject("body");
			String body = jobj.getString("body");
			ToastTracker.showToast("Request deleted successfully");
			Intent notifyUpdateintent = new Intent();
			if(daily_insta_type == 1)
				notifyUpdateintent.setAction(BroadCastConstants.INSTAREQ_DELETED);
			else
				notifyUpdateintent.setAction(BroadCastConstants.CARPOOLREQ_DELETED);
			//this broadcast is for my active req page to update itself to no active req
			Platform.getInstance().getContext().sendBroadcast(notifyUpdateintent);
		} catch (JSONException e) {
			ToastTracker.showToast("Some error occured in delete request");
			e.printStackTrace();
		}
	}
}
