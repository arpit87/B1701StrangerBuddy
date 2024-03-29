package my.b1701.SB.HelperClasses;

import com.google.android.maps.MapActivity;

import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.ChatClient.ChatWindow;
import my.b1701.SB.FacebookHelpers.FacebookConnector;
import my.b1701.SB.Fragments.FBLoginDialogFragment;
import my.b1701.SB.Fragments.SmsDialogFragment;
import my.b1701.SB.HttpClient.ChatServiceCreateUser;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.HttpClient.SBHttpRequest;
import my.b1701.SB.HttpClient.SaveFBInfoRequest;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.Users.NearbyUser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/****
 * 
 * @author arpit87
 * handler code for various scenaiors on chat click here
 * like not logged in to server,not fb login yet etc
 * to start chat from anywhere call this class
 */
public class CommunicationHelper {
	
	private static String TAG = "my.b1701.SB.ActivityHandler.ChatHandler";
	static CommunicationHelper instance = new CommunicationHelper();
	Context context = Platform.getInstance().getContext();
	
		
	public static CommunicationHelper getInstance()
	{
		return instance;
	}
	
	public void onChatClickWithUser(String fbid,String full_name)
	{
		//chat username and id are set only after successful addition to chat server
		//if these missing =?not yet added on chat server
		
		String thiUserChatUserName = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATUSERID);
		String thisUserChatPassword = ThisUserConfig.getInstance().getString(ThisUserConfig.CHATPASSWORD);
		
		if(thiUserChatUserName == "" || thisUserChatPassword == "")
		{
			if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
			{
				//make popup 
				MapListActivityHandler.getInstance().fbloginpromptpopup_show(true);
			}
			else 
			{
				Log.d(TAG,"FBLogged in but not chat!!Server working properly for chat req?sending again");
				//sending fbinfo n chatreq again
				if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBINFOSENTTOSERVER))
				{
					//server couldnt receive fbinfo
					SBHttpRequest sendFBInfoRequest = new SaveFBInfoRequest(ThisUserConfig.getInstance().getString(ThisUserConfig.USERID), ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID), ThisUserConfig.getInstance().getString(ThisUserConfig.FBACCESSTOKEN));
					SBHttpClient.getInstance().executeRequest(sendFBInfoRequest);
				}
				
				SBHttpRequest chatServiceAddUserRequest = new ChatServiceCreateUser(ThisUserConfig.getInstance().getString(ThisUserConfig.FBUID));
		     	SBHttpClient.getInstance().executeRequest(chatServiceAddUserRequest);							
			}
			//Intent fbLoginIntent = new Intent(context,LoginActivity.class);			
			//MapListActivityHandler.getInstance().getUnderlyingActivity().startActivity(fbLoginIntent);
		}	
		else 
		{
			if(fbid!="" && full_name!="")
			{
				Intent startChatIntent = new Intent(Platform.getInstance().getContext(),ChatWindow.class);					
				startChatIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
			 			| Intent.FLAG_ACTIVITY_NEW_TASK);			
				startChatIntent.putExtra(ChatWindow.PARTICIPANT, fbid);			
				startChatIntent.putExtra(ChatWindow.PARTICIPANT_NAME, full_name);
				context.startActivity(startChatIntent);
			}
			else
				ToastTracker.showToast("Sorry, user is not logged in");
		}
		
	
	}
	
	public void onSmsClickWithUser(String userID, boolean isPhoneAvailable)
	{
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
		{
			//make popup 
			MapListActivityHandler.getInstance().fbloginpromptpopup_show(true);
		}
		else if(userID!="" && isPhoneAvailable)
		{
			
			SmsDialogFragment sms_dialog = new SmsDialogFragment(userID);
			sms_dialog.show(MapListActivityHandler.getInstance().getUnderlyingActivity().getSupportFragmentManager(), "sms_dialog");						
		}
		else
			ToastTracker.showToast("Sorry, user has not provided phone number ");
	}
	
	public void onFBIconClickWithUser(Activity underLyingActivity, String userFBID, String userFBName)
	{
		if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
		{
			//make popup 
			MapListActivityHandler.getInstance().fbloginpromptpopup_show(true);
		}
		else if(userFBID!="" || userFBName !="")
		{
			FacebookConnector fbconnect = new FacebookConnector(underLyingActivity);
			fbconnect.openFacebookPage(userFBID,userFBName);
		}
		else
			ToastTracker.showToast("Not available, user not FB logged in");
	}
	

}
