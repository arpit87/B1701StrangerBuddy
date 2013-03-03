package my.b1701.SB.Fragments;

import my.b1701.SB.R;
import my.b1701.SB.HelperClasses.CommunicationHelper;
import my.b1701.SB.HelperClasses.SBImageLoader;
import my.b1701.SB.Users.NearbyUser;
import my.b1701.SB.Users.UserFBInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


public class NewUserDialogFragment extends DialogFragment{
	
	String source = "";
	String destination = "";
	String daily_insta_type = "";
	String detailsOfTravel = "";
	String userid = "";
	View dialogView ;
	private TextView dialogHeaderName = null;
	private TextView dialogHeaderTravelInfo = null;
	private ImageView picViewSmall = null;
	private ImageView picViewExpanded = null;
	private ImageView chatIcon = null;
	private ImageView smsIcon = null;
	private ImageView facebookIcon = null;
	private ImageView buttonClose = null;
	NearbyUser thisNearbyUser ;
	UserFBInfo thisNearbyUserFBInfo;
	
	public NewUserDialogFragment(NearbyUser n) {
		super();
		thisNearbyUser = n;
		thisNearbyUserFBInfo = thisNearbyUser.getUserFBInfo();
	}	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogView = inflater.inflate(R.layout.newuserarrived_popup, container);
        createAndDisplayNewUserArriveDialog();
		return dialogView;
	}
	

	private void setFBInfoOnExpandedPopup()
	{		
		
		TextView works_at = null;
		TextView studied_at = null;
		TextView hometown = null;
		TextView gender = null;
			
				
		dialogHeaderName.setText(thisNearbyUserFBInfo.getFullName());
		dialogHeaderTravelInfo.setText(detailsOfTravel);
				
		works_at = (TextView)dialogView.findViewById(R.id.newuserarrive_popup_expanded_work);
		studied_at = (TextView)dialogView.findViewById(R.id.newuserarrive_popup_expanded_education);
		hometown = (TextView)dialogView.findViewById(R.id.newuserarrive_popup_expanded_from);
		gender = (TextView)dialogView.findViewById(R.id.newuserarrive_popup_expanded_gender);
		
		works_at.setText("Works at "+thisNearbyUserFBInfo.getWorksAt());
		studied_at.setText("Studied at " +thisNearbyUserFBInfo.getStudiedAt());
		hometown.setText("HomeTown " + thisNearbyUserFBInfo.getHometown());
		gender.setText("Gender "+thisNearbyUserFBInfo.getGender());
		
		
	}
	
	protected void createAndDisplayNewUserArriveDialog()
	{	
				
			picViewExpanded = (ImageView)dialogView.findViewById(R.id.newuserarrive_popup_expanded_pic);		
			dialogHeaderName = (TextView)dialogView.findViewById(R.id.newuserarrive_popup_header);
			dialogHeaderTravelInfo = (TextView)dialogView.findViewById(R.id.newuserarrive_popup_travelinfo);
			chatIcon = (ImageView)dialogView.findViewById(R.id.newuserarrive_popup_chat_icon_view);
			smsIcon = (ImageView)dialogView.findViewById(R.id.newuserarrive_popup_sms_icon);
			facebookIcon = (ImageView)dialogView.findViewById(R.id.newuserarrive_popup_fb_icon_view);
			buttonClose = (ImageView)dialogView.findViewById(R.id.newuserarrive_popup_button_close_balloon_expandedview);
		
					
			if(!thisNearbyUserFBInfo.isPhoneAvailable())
			{				
				smsIcon.setImageResource(R.drawable.sms_icon_disabled);
				smsIcon.invalidate();                
			}
			
			
			buttonClose.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					dismiss();
					getActivity().finish();
				}
				});
			
			smsIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View buttonClose) {
					CommunicationHelper.getInstance().onSmsClickWithUser(userid);
				}
				});
			//SBImageLoader.getInstance().displayImageElseStub(mImageURL, picView, R.drawable.userpicicon);
			
			//set balloon info						
			
			SBImageLoader.getInstance().displayImage(thisNearbyUserFBInfo.getImageURL(), picViewExpanded);
			
			chatIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {					
					CommunicationHelper.getInstance().onChatClickWithUser(thisNearbyUser);						
				}
			});
			
			facebookIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View chatIconView) {
					CommunicationHelper.getInstance().onFBIconClickWithUser(getActivity(),thisNearbyUserFBInfo.getFbid(),thisNearbyUserFBInfo.getFBUsername());						
				}
			});	
			
			setFBInfoOnExpandedPopup();
		
	}
	
	
}
