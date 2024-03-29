package my.b1701.SB.Platform;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.Users.CurrentNearbyUsers;
import my.b1701.SB.Users.ThisUserNew;

public class Platform {
	
	private final String TAG = "my.b1701.SB.Platform.Platform";
	private static Platform instance = new Platform();
	private Context context;	
	private Handler handler;
	public boolean SUPPORTS_NEWAPI = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
		
	private Platform() {
	}
	
	public static Platform getInstance()
	{
		return instance;
	}
	
	public Context getContext(){
		return context;
	}	
	
	public Handler getHandler(){
		return handler;
	}
	
	public void initialize(Context context) {
		this.context= context;			
		SBHttpClient.getInstance();
		handler = new Handler();
		CurrentNearbyUsers.getInstance().clearAllData();
		ThisUserNew.getInstance();
		startChatService();
	}
	
	 public void startChatService(){
	     
         Intent i = new Intent("my.b1701.SB.ChatService.SBChatService");                  
         Log.d( TAG, "Service starting" );
         context.startService(i);
        
        }
             
	
	 public void stopChatService() {		
	          Intent i = new Intent("my.b1701.SB.ChatService.SBChatService");
	          context.stopService(i);         
	          
	          Log.d( TAG, "Service stopped" );	         
	             
     }

}
