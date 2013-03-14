package my.b1701.SB.Fragments;

import my.b1701.SB.R;
import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HttpClient.GetMatchingCarPoolUsersRequest;
import my.b1701.SB.HttpClient.GetMatchingNearbyUsersRequest;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.HttpClient.SBHttpRequest;
import my.b1701.SB.Users.UserAttributes;
import my.b1701.SB.Util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class ShowActiveReqPrompt extends DialogFragment{
	TextView carpoolsource;
	TextView carpooldestination;
	TextView carpooltime;
	TextView instasource;
	TextView instadestination;
	TextView instatime;
	View instaActiveLayout;
	View carPoolActiveLayout;
	TextView carPoolNoActiveReq;
	TextView instaNoActiveReq;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.show_active_req_prompt, container);
        carpoolsource = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_carpool_source);
        carpooldestination = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_carpool_destination);
        carpooltime = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_carpool_time);
        instasource = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_insta_source);
        instadestination = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_insta_destination);
        instatime = (TextView) dialogView.findViewById(R.id.show_active_req_prompt_insta_time);              
        instaActiveLayout = (View)dialogView.findViewById(R.id.show_active_req_prompt_instadetails);
        carPoolActiveLayout = (View)dialogView.findViewById(R.id.show_active_req_prompt_carpooldetails);
        carPoolNoActiveReq = (TextView)dialogView.findViewById(R.id.show_active_req_prompt_carpool_noreq);
        instaNoActiveReq = (TextView)dialogView.findViewById(R.id.show_active_req_prompt_insta_noreq);
        
        
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL); 
        
        if(!StringUtils.isBlank(carpoolReqJson))
        { 
        	try {
        		carPoolActiveLayout.setVisibility(View.VISIBLE);
            	carPoolNoActiveReq.setVisibility(View.GONE);
            	
            	
    				final JSONObject responseJsonObj = new JSONObject(carpoolReqJson);
    				String source = responseJsonObj.getString(UserAttributes.SRCLOCALITY);
    				String destination = responseJsonObj.getString(UserAttributes.DSTLOCALITY);
    				String datetime = responseJsonObj.getString(UserAttributes.DATETIME);
    				carpoolsource.setText(source);
    				carpooldestination.setText(destination);
    				carpooltime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "hh:mm a", datetime));
    							
            	carPoolActiveLayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View paramView) {
							try {
								MapListActivityHandler.getInstance().setSourceAndDestination(responseJsonObj);
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
        		instaActiveLayout.setVisibility(View.VISIBLE);
            	instaNoActiveReq.setVisibility(View.GONE);
            	
    				final JSONObject responseJsonObj = new JSONObject(instaReqJson);
    				String source = responseJsonObj.getString(UserAttributes.SRCLOCALITY);
    				String destination = responseJsonObj.getString(UserAttributes.DSTLOCALITY);
    				String datetime = responseJsonObj.getString(UserAttributes.DATETIME);
    				instasource.setText(source);
    				instadestination.setText(destination);
    				instatime.setText(StringUtils.formatDate("yyyy-MM-dd HH:mm", "d MMM, hh:mm a", datetime));
    					
				 instaActiveLayout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View paramView) {
							try {
								MapListActivityHandler.getInstance().setSourceAndDestination(responseJsonObj);
								ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Fetching  matches", "Please wait");
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
       
        
        
        Button newRequestButton = (Button)dialogView.findViewById(R.id.show_active_req_prompt_newreqbutton);
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
