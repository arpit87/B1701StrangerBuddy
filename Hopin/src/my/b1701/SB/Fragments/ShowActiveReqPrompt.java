package my.b1701.SB.Fragments;

import org.json.JSONException;
import org.json.JSONObject;

import my.b1701.SB.R;
import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.HttpClient.GetMatchingCarPoolUsersRequest;
import my.b1701.SB.HttpClient.GetMatchingNearbyUsersRequest;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.HttpClient.SBHttpRequest;
import my.b1701.SB.Users.UserAttributes;
import my.b1701.SB.Util.StringUtils;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class ShowActiveReqPrompt extends DialogFragment{
	TextView carpooldetails;
	TextView instadetails;
	View carpoollayout;
	View instalayout;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.show_active_req_prompt, container);
        carpooldetails = (TextView) dialogView.findViewById(R.id.show_active_req_carpooldetails);
        instadetails = (TextView) dialogView.findViewById(R.id.show_active_req_instadetails);
        carpoollayout = (View) dialogView.findViewById(R.id.show_active_req_carpoollayout);
        instalayout = (View) dialogView.findViewById(R.id.show_active_req_instalayout);
        
        
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL); 
        
        if(!StringUtils.isBlank(carpoolReqJson))
        { 
        	try {
				JSONObject responseJsonObj = new JSONObject(carpoolReqJson);
				final JSONObject body = responseJsonObj.getJSONObject("body");
				String source = body.getString(UserAttributes.SRCLOCALITY);
				String destination = body.getString(UserAttributes.DSTLOCALITY);
				String datetime = body.getString(UserAttributes.DATETIME);
				String formatteddate = StringUtils.formatDate("yyyy-MM-dd HH:mm", "hh:mm a", datetime);
				carpooldetails.setTextColor(getResources().getColor(R.color.black));
				carpooldetails.setTextSize(15);
				carpooldetails.setText(source +" to " + destination + " @ "+formatteddate );
				
				 carpoollayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View paramView) {
							try {
								MapListActivityHandler.getInstance().setSourceAndDestination(body);
								ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Fetching carpool matches", "Please wait");
								SBHttpRequest getNearbyUsersRequest = new GetMatchingCarPoolUsersRequest();
						        SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
						        dismiss(); 
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
					});
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(!StringUtils.isBlank(instaReqJson))
        {  
        
        	try {
				JSONObject responseJsonObj = new JSONObject(instaReqJson);
				final JSONObject body = responseJsonObj.getJSONObject("body");
				String source = body.getString(UserAttributes.SRCLOCALITY);
				String destination = body.getString(UserAttributes.DSTLOCALITY);
				String datetime = body.getString(UserAttributes.DATETIME);
				String formatteddate = StringUtils.formatDate("yyyy-MM-dd HH:mm", "d MMM, hh:mm a", datetime);
				instadetails.setTextColor(getResources().getColor(R.color.black));
				instadetails.setTextSize(15);
				instadetails.setText(source +" to " + destination + " @ "+formatteddate );
		
				 instalayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View paramView) {
							try {
								MapListActivityHandler.getInstance().setSourceAndDestination(body);
								ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Fetching carpool matches", "Please wait");
								SBHttpRequest getNearbyUsersRequest = new GetMatchingNearbyUsersRequest();
						        SBHttpClient.getInstance().executeRequest(getNearbyUsersRequest);
						        dismiss(); 
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
					});
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }        
       
        
        
        Button newRequestButton = (Button)dialogView.findViewById(R.id.show_active_req_newreqbutton);
		// if button is clicked, close the custom dialog
        newRequestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	       
		return dialogView;
	}
	
	
}
