package my.b1701.SB.Activities;

import my.b1701.SB.R;
import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.Fragments.SearchUserInstaFrag;
import my.b1701.SB.Fragments.SearchUserPlanFrag;
import my.b1701.SB.LocationHelpers.SBGeoPoint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.analytics.tracking.android.EasyTracker;

public class SearchInputActivityNew extends FragmentActivity{
    public static final String TAG = "my.b1701.SB.Activites.SearchInputActivity";   

	FragmentManager fm = this.getSupportFragmentManager();
	Button BtnGotoPastSearch;
	Button BtnInstaSearchView;
	Button BtnPlanSearchView;	
   
	  @Override
	    public void onStart(){
	        super.onStart();
	        EasyTracker.getInstance().activityStart(this);
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
		 BtnGotoPastSearch = (Button)findViewById(R.id.search_user_tab_gotohistory);
		
		 BtnInstaSearchView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setSelected(true);
				BtnPlanSearchView.setSelected(false);
				showInstaSearchLayout();				
			}
		});
		 
		 BtnPlanSearchView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					v.setSelected(true);
					BtnInstaSearchView.setSelected(false);
					showPlanSearchLayout();				
				}
			});
		 
		 BtnGotoPastSearch.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(SearchInputActivityNew.this, SBHistoryActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			        startActivity(intent);			
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
   

}
