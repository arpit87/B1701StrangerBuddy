package my.b1701.SB.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.FacebookHelpers.FacebookConnector;
import my.b1701.SB.Fragments.FBLoginDialogFragment;
import my.b1701.SB.HelperClasses.ThisAppConfig;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.R;

public class SettingsActivity extends FragmentActivity{
	
	private static final String TAG = "my.b1701.SB.Activities.SettingsActivity";
	CheckBox showNewUserPopup;
	CheckBox showChatPopup;
	CheckBox womenFilter;
	View womanFilterView;
	CheckBox fbfriendsOnlyFilter;
    View blockedUsersView;
    FacebookConnector fbconnect;
    
   

	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.settings_layout);
		 showNewUserPopup = (CheckBox)findViewById(R.id.settings_newuser_showpopup_checkbox);		
		 womenFilter = (CheckBox)findViewById(R.id.settings_womenfilter_checkbox);
		 fbfriendsOnlyFilter = (CheckBox)findViewById(R.id.settings_fbfriendonly_checkbox);		 
         blockedUsersView = findViewById(R.id.settings_blockedusers_layout);         
         womanFilterView = findViewById(R.id.settings_womenfilter_tablerow);
	 }
	 
	 @Override
	 protected void onResume(){
	     super.onResume();
		 fillSettingsActivity();
         showNewUserPopup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ThisAppConfig.getInstance().putBool(ThisAppConfig.NEWUSERPOPUP, isChecked);				
			}
		});
         
         womenFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
          	
 			@Override
 			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 				if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
 				{
 					Log.i(TAG,"woman filter clicked,checked:"+isChecked);
 					womenFilter.setChecked(false);
 					fbconnect = new FacebookConnector(SettingsActivity.this);
  					FBLoginDialogFragment fblogin_dialog = FBLoginDialogFragment.newInstance(fbconnect);
					fblogin_dialog.show(SettingsActivity.this.getSupportFragmentManager(), "fblogin_dialog"); 					
 				}
 				else
 				{
 					if(ThisUserConfig.getInstance().getString(ThisUserConfig.GENDER) != "female")
 					{
 						womanFilterView.setVisibility(View.GONE);
 					}
 					else
 					{
 						ThisAppConfig.getInstance().putBool(ThisAppConfig.WOMANFILTER, isChecked);	
 					}
 				}
 			}
 		});
         
         fbfriendsOnlyFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
           	
  			@Override
  			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
  				if(!ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN))
  				{
  					Log.i(TAG,"fb chk clicked,checked:"+isChecked);
  					fbfriendsOnlyFilter.setChecked(false);
  					fbconnect = new FacebookConnector(SettingsActivity.this);
  					FBLoginDialogFragment fblogin_dialog = FBLoginDialogFragment.newInstance(fbconnect);
					fblogin_dialog.show(SettingsActivity.this.getSupportFragmentManager(), "fblogin_dialog");  					
  				}
  				else
  				{  					
  					ThisAppConfig.getInstance().putBool(ThisAppConfig.FBFRIENDONLYFILTER, isChecked);  					
  				}
  			}
  		});
          
         
         

         blockedUsersView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent blockedUsersIntent = new Intent(SettingsActivity.this, BlockedUsersActivity.class);
                 blockedUsersIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                 startActivity(blockedUsersIntent);
             }
         });
		 
	 }
	 
	 private void fillSettingsActivity()
	 {
		 showNewUserPopup.setChecked(ThisAppConfig.getInstance().getBool(ThisAppConfig.NEWUSERPOPUP));
		 if(ThisUserConfig.getInstance().getBool(ThisUserConfig.FBLOGGEDIN) &&
		   ThisUserConfig.getInstance().getString(ThisUserConfig.GENDER) != "female")
			womanFilterView.setVisibility(View.GONE);
		else				
			womenFilter.setChecked(ThisAppConfig.getInstance().getBool(ThisAppConfig.WOMANFILTER));
		 fbfriendsOnlyFilter.setChecked(ThisAppConfig.getInstance().getBool(ThisAppConfig.FBFRIENDONLYFILTER));
	 }
	 
	 @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        fbconnect.authorizeCallback(requestCode, resultCode, data);
	    }
	 
	
}
