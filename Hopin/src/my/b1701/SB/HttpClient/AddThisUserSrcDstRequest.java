package my.b1701.SB.HttpClient;

import android.util.Log;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.LocationHelpers.SBGeoPoint;
import my.b1701.SB.Server.AddThisUserSrcDstResponse;
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

public class AddThisUserSrcDstRequest extends SBHttpRequest {
    private final String TAG = "my.b1701.SB.HttpClient.AddThisUserSrcDstRequest";
    public static final String URL = ServerConstants.SERVER_ADDRESS + ServerConstants.REQUESTSERVICE + "/addRequest/";
    
    HttpPost httpQueryAddRequest;
    JSONObject jsonobjAddRequest;
    HttpClient httpclient = new DefaultHttpClient();
    AddThisUserSrcDstResponse addThisUserResponse;
    String jsonStr;

    public AddThisUserSrcDstRequest() {
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
            e.printStackTrace();
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
    }

    public ServerResponseBase execute() {
        try {
            response = httpclient.execute(httpQueryAddRequest);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            jsonStr = responseHandler.handleResponse(response);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (response.getStatusLine().getStatusCode() == 200) {
            ThisUserConfig.getInstance().putString(ThisUserConfig.ACTIVE_REQ_INSTA, jsonStr);
            ThisUserConfig.getInstance().putInt(ThisUserConfig.LAST_ACTIVE_REQ_TYPE, 1);
        }
        addThisUserResponse = new AddThisUserSrcDstResponse(response, jsonStr);
        return addThisUserResponse;
    }
}
