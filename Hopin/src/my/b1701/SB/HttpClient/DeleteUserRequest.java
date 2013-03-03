package my.b1701.SB.HttpClient;

import my.b1701.SB.Server.DeleteUserResponse;
import my.b1701.SB.Server.ServerConstants;
import my.b1701.SB.Server.ServerResponseBase;
import my.b1701.SB.Users.ThisUserNew;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class DeleteUserRequest extends SBHttpRequest{

    public static final String BASE_URL = ServerConstants.SERVER_ADDRESS + ServerConstants.REQUESTSERVICE + "/deleteRequest/";
    HttpClient httpclient = new DefaultHttpClient();
	HttpGet httpQuery;
	String jsonStr;
	public DeleteUserRequest()
	{
		super();
		queryMethod = QueryMethod.Post;
		url = BASE_URL + "?user_id=" + ThisUserNew.getInstance().getUserID();
        httpQuery =  new HttpGet(url);
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
				jsonStr = responseHandler.handleResponse(response);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   			
			
		return new DeleteUserResponse(response,jsonStr);
	}

}
