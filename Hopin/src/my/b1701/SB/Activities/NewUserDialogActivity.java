package my.b1701.SB.Activities;

import my.b1701.SB.R;
import my.b1701.SB.HelperClasses.CommunicationHelper;
import my.b1701.SB.HelperClasses.SBImageLoader;
import my.b1701.SB.Users.NearbyUser;
import my.b1701.SB.Users.UserFBInfo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class NewUserDialogActivity extends Activity{
	
	String source = "";
	String destination = "";
	String daily_insta_type = "";	
	String userid = "";
	
	private TextView dialogHeaderName = null;
	private TextView dialogHeaderTravelInfo = null;	
	private ImageView picViewExpanded = null;
	private ImageView chatIcon = null;
	private ImageView smsIcon = null;
	private ImageView facebookIcon = null;
	private ImageView buttonClose = null;
	NearbyUser thisNearbyUser ;
	UserFBInfo thisNearbyUserFBInfo;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newuserarrived_popup);
        		
	}
	
	@Override
    public void onResume(){
    	super.onResume();

    	String jsonstr = getIntent().getStringExtra("nearbyuserjsonstr");
    	try {
    		thisNearbyUser = new NearbyUser(new JSONObject(jsonstr));
    		thisNearbyUserFBInfo = thisNearbyUser.getUserFBInfo();
    		createAndDisplayNewUserArriveDialog();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
    	
	}

	private void setFBInfoOnExpandedPopup()
	{		
		
		TextView works_at = null;
		TextView studied_at = null;
		TextView hometown = null;
		TextView gender = null;
			
				
		dialogHeaderName.setText(thisNearbyUserFBInfo.getFullName());
		dialogHeaderTravelInfo.setText(thisNearbyUser.getUserLocInfo().getFormattedTravelDetails());
				
		works_at = (TextView)findViewById(R.id.newuserarrive_popup_expanded_work);
		studied_at = (TextView)findViewById(R.id.newuserarrive_popup_expanded_education);
		hometown = (TextView)findViewById(R.id.newuserarrive_popup_expanded_from);
		gender = (TextView)findViewById(R.id.newuserarrive_popup_expanded_gender);
		
		works_at.setText("Works at "+thisNearbyUserFBInfo.getWorksAt());
		studied_at.setText("Studied at " +thisNearbyUserFBInfo.getStudiedAt());
		hometown.setText("HomeTown " + thisNearbyUserFBInfo.getHometown());
		gender.setText("Gender "+thisNearbyUserFBInfo.getGender());
		
	}
	
	protected void createAndDisplayNewUserArriveDialog()
	{	
				
			picViewExpanded = (ImageView)findViewById(R.id.newuserarrive_popup_expanded_pic);		
			dialogHeaderName = (TextView)findViewById(R.id.newuserarrive_popup_header);
			dialogHeaderTravelInfo = (TextView)findViewById(R.id.newuserarrive_popup_travelinfo);
			chatIcon = (ImageView)findViewById(R.id.newuserarrive_popup_chat_icon_view);
			smsIcon = (ImageView)findViewById(R.id.newuserarrive_popup_sms_icon);
			facebookIcon = (ImageView)findViewById(R.id.newuserarrive_popup_fb_icon_view);
			buttonClose = (ImageView)findViewById(R.id.newuserarrive_popup_button_close_balloon_expandedview);
		
					
			if(!thisNearbyUserFBInfo.isPhoneAvailable())
			{				
				smsIcon.setImageResource(R.drawable.sms_icon_disabled);
				smsIcon.invalidate();                
			}
			
			
			buttonClose.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					finish();
				}
				});
			
			smsIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					CommunicationHelper.getInstance().onSmsClickWithUser(userid);
				}
				});
						
			
			SBImageLoader.getInstance().displayImage(thisNearbyUserFBInfo.getImageURL(), picViewExpanded);
			
			chatIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {					
					CommunicationHelper.getInstance().onChatClickWithUser(thisNearbyUserFBInfo.getFbid(),thisNearbyUserFBInfo.getFullName());						
				}
			});
			
			facebookIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {
					CommunicationHelper.getInstance().onFBIconClickWithUser(NewUserDialogActivity.this,thisNearbyUserFBInfo.getFbid(),thisNearbyUserFBInfo.getFBUsername());						
				}
			});	
			
			setFBInfoOnExpandedPopup();
		
	}
	
	
}
