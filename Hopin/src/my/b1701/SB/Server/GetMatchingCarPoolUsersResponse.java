package my.b1701.SB.Server;

import my.b1701.SB.LocationHelpers.SBGeoPoint;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.Users.CurrentNearbyUsers;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.Users.UserAttributes;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

public class GetMatchingCarPoolUsersResponse extends ServerResponseBase{


	private static final String TAG = "my.b1701.SB.Server.GetCarPoolUsersResponse";
	
	
	public GetMatchingCarPoolUsersResponse(HttpResponse response,String jobjStr) {
		super(response,jobjStr);
				
	}
	
	@Override
	public void process() {
		Log.i(TAG,"processing GetMatchingCarPoolUsersResponse response..geting json");
		//jobj = JSONHandler.getInstance().GetJSONObjectFromHttp(serverResponse);
		Log.i(TAG,"got json "+jobj.toString());
		try {
			body = jobj.getJSONObject("body");
			setSourceAndDestination(body);
		} catch (JSONException e) {
			Log.e(TAG, "Error returned by server in fetching nearby carpool user.JSON:"+jobj.toString());
			e.printStackTrace();
			return;
		}		
		
		CurrentNearbyUsers.getInstance().updateNearbyUsersFromJSON(body);		
		//MapListActivityHandler.getInstance().updateNearbyUsers();	
		
		Intent notifyUpdateintent = new Intent();
		notifyUpdateintent.setAction(ServerConstants.NEARBY_USER_UPDATED);		
		
		//this broadcast is for chat window which queries for nearby users in case of incoming chat 
		//from user which has not yet been fetched by getmatch request
		Platform.getInstance().getContext().sendBroadcast(notifyUpdateintent);
		
	}
	
	public void setSourceAndDestination(JSONObject jsonObject) throws JSONException {
	       double srcLat = Double.parseDouble(jsonObject.getString(UserAttributes.SRCLATITUDE));
	       double srcLong = Double.parseDouble(jsonObject.getString(UserAttributes.SRCLONGITUDE));
	       double destLat = Double.parseDouble(jsonObject.getString(UserAttributes.DSTLATITUDE));
	       double destLong = Double.parseDouble(jsonObject.getString(UserAttributes.DSTLONGITUDE));
	       ThisUserNew.getInstance().setSourceGeoPoint(new SBGeoPoint((int)(srcLat*1e6),(int)(srcLong*1e6)));
	       ThisUserNew.getInstance().setDestinationGeoPoint(new SBGeoPoint((int)(destLat*1e6),(int)(destLong*1e6)));
	   }
	
	

}
