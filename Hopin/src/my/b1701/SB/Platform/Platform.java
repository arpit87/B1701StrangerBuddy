package my.b1701.SB.Platform;

import java.util.LinkedList;

import my.b1701.SB.Adapter.HistoryAdapter;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.Users.CurrentNearbyUsers;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.provider.HistoryContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class Platform {
	
	private final String TAG = "my.b1701.SB.Platform.Platform";
	private static Platform instance = new Platform();
	private Context context;	
	private Handler handler;
	public boolean SUPPORTS_NEWAPI = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
	private static Uri mHistoryUri = Uri.parse("content://" + HistoryContentProvider.AUTHORITY + "/db_fetch_only");
	    private static String[] columns = new String[]{ 
	    	"sourceAddress",
	        "destinationAddress",
	        "timeOfTravel",
	        "dateOfTravel",        
	        "dailyInstantType",
	        "planInstantType",
	        "takeOffer",
	        "reqDate",	      
	        "radioButtonId",
	        "date"
	};
		
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
		loadHistoryFromDB() ;
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
	 
	 private void loadHistoryFromDB() {
	        LinkedList<HistoryAdapter.HistoryItem> historyItemList = null;
	        Log.e(TAG, "Fetching searches");
	        ContentResolver cr = context.getContentResolver();
	        Cursor cursor = cr.query(mHistoryUri, columns, null, null, null);

	        if (cursor == null || cursor.getCount() == 0) {
	            Log.e(TAG, "Empty result");
	        } else {
	            LinkedList<HistoryAdapter.HistoryItem> historyItems = new LinkedList<HistoryAdapter.HistoryItem>();
	            if (cursor.moveToFirst()) {
	                do {
	                    HistoryAdapter.HistoryItem historyItem = new HistoryAdapter.HistoryItem(cursor.getString(0),
	                            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4),cursor.getInt(5),
	                            cursor.getInt(6), cursor.getString(7),cursor.getInt(8));
	                    historyItems.add(historyItem);
	                } while (cursor.moveToNext());
	                
	            }
	            if(historyItems.size()>0)
	                historyItemList = historyItems;
	        }

         if (cursor!= null) {
             cursor.close();
         }

	        if (historyItemList == null) {
	            historyItemList = new LinkedList<HistoryAdapter.HistoryItem>();
	        }

	        ThisUserNew.getInstance().setHistoryItemList(historyItemList);
	    }


}
