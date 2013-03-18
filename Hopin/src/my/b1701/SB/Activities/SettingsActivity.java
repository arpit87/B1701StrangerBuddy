package my.b1701.SB.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import my.b1701.SB.HelperClasses.ThisAppConfig;
import my.b1701.SB.R;

public class SettingsActivity extends Activity{
	
	CheckBox showNewUserPopup;
	CheckBox showChatPopup;
	View activeRequestView;
    View blockedUsersView;

	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.settings_layout);
		 showNewUserPopup = (CheckBox)findViewById(R.id.settings_newuser_showpopup_checkbox);		
		 activeRequestView = (View)findViewById(R.id.settings_activerequset_layout);
         blockedUsersView = findViewById(R.id.settings_blockedusers_layout);
         showNewUserPopup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ThisAppConfig.getInstance().putBool(ThisAppConfig.NEWUSERPOPUP, isChecked);				
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
	 }

}
