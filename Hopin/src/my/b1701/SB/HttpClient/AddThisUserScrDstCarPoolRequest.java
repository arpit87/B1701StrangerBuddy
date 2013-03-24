package my.b1701.SB.HttpClient;

import android.util.Log;
import my.b1701.SB.HelperClasses.ThisAppConfig;
import my.b1701.SB.LocationHelpers.SBGeoPoint;
import my.b1701.SB.Server.AddThisUserSrcDstCarPoolResponse;
import my.b1701.SB.Server.ServerConstants;
import my.b1701.SB.Server.ServerResponseBase;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.Users.UserAttributes;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AddThisUserScrDstCarPoolRequest extends SBHttpRequest {
    private final String TAG = "my.b1701.SB.HttpClient.AddThisUserSrcDstCarpoolRequest";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.REQUESTSERVICE + "/addCarpoolRequest/";

    HttpPost httpQueryAddRequest;
    JSONObject jsonobjAddRequest;
    HttpClient httpclient = new DefaultHttpClient();
    AddThisUserSrcDstCarPoolResponse addThisUserResponse;
    String jsonStr;

    public AddThisUserScrDstCarPoolRequest() {
        //we will post 2 requests here
        //1)addrequest to add source and destination
        //2) getUsersRequest to get users
        super();
        queryMethod = QueryMethod.Post;
        jsonobjAddRequest = GetServerAuthenticatedJSON();
        httpQueryAddRequest = new HttpPost(URL);
        try {
            populateEntityObject();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        StringEntity postEntityAddRequest = null;
        try {
            postEntityAddRequest = new StringEntity(jsonobjAddRequest.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        }
        postEntityAddRequest.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        Log.d(TAG, "calling server:" + jsonobjAddRequest.toString());
        httpQueryAddRequest.setEntity(postEntityAddRequest);
    }

    private void populateEntityObject() throws JSONException {    	
    	SBGeoPoint sourceGeoPoint =  ThisUserNew.getInstance().getSourceGeoPoint();
    	SBGeoPoint destinationGeoPoint =  ThisUserNew.getInstance().getDestinationGeoPoint();
        jsonobjAddRequest.put(UserAttributes.SHAREOFFERTYPE, ThisUserNew.getInstance().get_Take_Offer_Type());
        if (sourceGeoPoint != null) {
            jsonobjAddRequest.put(UserAttributes.SRCLATITUDE, ThisUserNew.getInstance().getSourceGeoPoint().getLatitude());
            jsonobjAddRequest.put(UserAttributes.SRCLONGITUDE, ThisUserNew.getInstance().getSourceGeoPoint().getLongitude());
        } 
        if (destinationGeoPoint != null) {
            jsonobjAddRequest.put(UserAttributes.DSTLATITUDE, ThisUserNew.getInstance().getDestinationGeoPoint().getLatitude());
            jsonobjAddRequest.put(UserAttributes.DSTLONGITUDE, ThisUserNew.getInstance().getDestinationGeoPoint().getLongitude());
        }
        jsonobjAddRequest.put(UserAttributes.SRCADDRESS, ThisUserNew.getInstance().getSourceFullAddress());
        jsonobjAddRequest.put(UserAttributes.DSTADDRESS, ThisUserNew.getInstance().getDestinationFullAddress());       
        jsonobjAddRequest.put(UserAttributes.DATETIME, ThisUserNew.getInstance().getDateAndTimeOfTravel());
        if(ThisAppConfig.getInstance().getBool(ThisAppConfig.WOMANFILTER))
        	jsonobjAddRequest.put(UserAttributes.WOMANFLTER, 1);
        if(ThisAppConfig.getInstance().getBool(ThisAppConfig.FBFRIENDONLYFILTER))
        	jsonobjAddRequest.put(UserAttributes.FBFRIENDSFILTER, 1);
    }

    public ServerResponseBase execute() {
        try {
            response = httpclient.execute(httpQueryAddRequest);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        try {
        	if(response==null)
				return null;
            jsonStr = responseHandler.handleResponse(response);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        addThisUserResponse = new AddThisUserSrcDstCarPoolResponse(response, jsonStr);
        return addThisUserResponse;
    }
}
