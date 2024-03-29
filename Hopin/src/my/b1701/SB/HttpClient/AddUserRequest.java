package my.b1701.SB.HttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import my.b1701.SB.HelperClasses.ThisAppConfig;
import my.b1701.SB.Server.AddUserResponse;
import my.b1701.SB.Server.ServerConstants;
import my.b1701.SB.Server.ServerResponseBase;
import my.b1701.SB.Users.UserAttributes;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class AddUserRequest extends SBHttpRequest{
	public static final String URL = ServerConstants.SERVER_ADDRESS+ServerConstants.USERSERVICE+"/addUser/";

	HttpPost httpQuery;
	JSONObject jsonobj;	
	String uuid;
	HttpClient httpclient = new DefaultHttpClient();
	AddUserResponse addUserResponse;
	String jsonStr;
	Activity tutorial_activity;
	
	public AddUserRequest(String uuid,String username,Activity tutorial_activity)
	{
		super();
		this.uuid=uuid;		
		queryMethod = QueryMethod.Get;	
		this.tutorial_activity = tutorial_activity;
		jsonobj=new JSONObject();
		httpQuery =  new HttpPost(URL);
		
		try {
			jsonobj.put(ThisAppConfig.APPUUID, uuid);
			jsonobj.put(UserAttributes.USERNAME, username);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	
		
		StringEntity postEntityUser = null;
		try {
			postEntityUser = new StringEntity(jsonobj.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postEntityUser.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		Log.d("debug", "calling server:"+jsonobj.toString());	
		httpQuery.setEntity(postEntityUser);
	}
	
	public ServerResponseBase execute() {
			try {
				response=httpclient.execute(httpQuery);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(response==null)
					return null;
				jsonStr = responseHandler.handleResponse(response);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
					
			addUserResponse =	new AddUserResponse(response,jsonStr,tutorial_activity);
			return addUserResponse;
		
	}
	
	

}

