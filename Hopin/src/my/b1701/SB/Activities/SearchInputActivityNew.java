package my.b1701.SB.Activities;

import java.util.LinkedList;

import my.b1701.SB.R;
import my.b1701.SB.Adapter.HistoryAdapter;
import my.b1701.SB.Fragments.HistoryInstaShareFragment;
import my.b1701.SB.Fragments.HistoryPlanFragment;
import my.b1701.SB.Fragments.SearchUserInstaFrag;
import my.b1701.SB.Fragments.SearchUserPlanFrag;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.provider.HistoryContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.google.analytics.tracking.android.EasyTracker;

public class SearchInputActivityNew extends FragmentActivity{
    public static final String TAG = "my.b1701.SB.Activites.SearchInputActivity";   
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

	FragmentManager fm = this.getSupportFragmentManager();
	ToggleButton BtnGotoPastSearch;
	Button BtnInstaSearchView;
	Button BtnPlanSearchView;	
   
	  @Override
	    public void onStart(){
	        super.onStart();
	        EasyTracker.getInstance().activityStart(this);
	        loadHistoryFromDB() ;
	    }

	    @Override
	    public void onStop(){
	        super.onStop();
	        EasyTracker.getInstance().activityStop(this);
	    }
    
	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.search_users_framelayout);
		 BtnInstaSearchView = (Button)findViewById(R.id.search_user_tab_insta);
		 BtnPlanSearchView = (Button)findViewById(R.id.search_user_tab_plan);
		 BtnGotoPastSearch = (ToggleButton)findViewById(R.id.search_user_tab_gotohistory);
		
		 BtnInstaSearchView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    if(BtnGotoPastSearch.isChecked())
			    	showInstaHistoryLayout();
			    else
			    	showInstaSearchLayout();
				v.setSelected(true);
				BtnPlanSearchView.setSelected(false);
								
			}
		});
		 
		 BtnPlanSearchView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(BtnGotoPastSearch.isChecked())
				    	showPlanHistoryLayout();
				    else
				    	showPlanSearchLayout();
					v.setSelected(true);
					BtnInstaSearchView.setSelected(false);
									
				}
			});
		 
		 BtnGotoPastSearch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if(isChecked)
				{
					if(BtnPlanSearchView.isSelected())
						showPlanHistoryLayout();
					else if(BtnInstaSearchView.isSelected())
						showInstaHistoryLayout();
				}
				else
				{
					if(BtnPlanSearchView.isSelected())
						showPlanSearchLayout();
					else if(BtnInstaSearchView.isSelected())
						showInstaSearchLayout();
				}
				
			}
		});
			 
		 BtnInstaSearchView.setSelected(true);
	     showInstaSearchLayout();
		
	 }
	 
	 public void showInstaSearchLayout()
	    {
	    	if (fm != null) {
	            FragmentTransaction fragTrans = fm.beginTransaction();
	            fragTrans.replace(R.id.search_user_instaplan_content, new SearchUserInstaFrag());	            
	            fragTrans.commit();	           
	        }
	    }
	    
	    public void showPlanSearchLayout()
	    {
	    	if (fm != null) {	    		
	            FragmentTransaction ft = fm.beginTransaction();
	            ft.replace(R.id.search_user_instaplan_content, new SearchUserPlanFrag());
	            ft.commit();
	           
	        }
	    }
	    
	    public void showInstaHistoryLayout()
	    {
	    	if (fm != null) {	    		
	            FragmentTransaction ft = fm.beginTransaction();
	            ft.replace(R.id.search_user_instaplan_content, new HistoryInstaShareFragment());
	            ft.commit();
	           
	        }
	    } 
	    
	    public void showPlanHistoryLayout()
	    {
	    	if (fm != null) {	    		
	            FragmentTransaction ft = fm.beginTransaction();
	            ft.replace(R.id.search_user_instaplan_content, new HistoryPlanFragment());
	            ft.commit();
	           
	        }
	    }
	    
	    private void loadHistoryFromDB() {
	        LinkedList<HistoryAdapter.HistoryItem> historyItemList = null;
	        Log.e(TAG, "Fetching searches");
	        ContentResolver cr = getContentResolver();
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

	                cursor.close();
	            }
	            if(historyItems.size()>0)
	                historyItemList = historyItems;
	        }

	        if (historyItemList == null) {
	            historyItemList = new LinkedList<HistoryAdapter.HistoryItem>();
	        }

	        ThisUserNew.getInstance().setHistoryItemList(historyItemList);
	    }

   

}
