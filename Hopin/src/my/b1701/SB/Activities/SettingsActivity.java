package my.b1701.SB.Activities;

import my.b1701.SB.R;
import my.b1701.SB.HelperClasses.ThisAppConfig;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsActivity extends Activity{
	
	CheckBox showNewUserPopup;
	CheckBox showChatPopup;
	View activeRequestView;
	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.settings_layout);
		 showNewUserPopup = (CheckBox)findViewById(R.id.settings_newuser_showpopup_checkbox);		
		 activeRequestView = (View)findViewById(R.id.settings_activerequset_layout);
		 
		 showNewUserPopup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ThisAppConfig.getInstance().putBool(ThisAppConfig.NEWUSERPOPUP, isChecked);				
			}
		});
	 }
	 
	 private void fillSettingsActivity()
	 {
		 showNewUserPopup.setChecked(ThisAppConfig.getInstance().getBool(ThisAppConfig.NEWUSERPOPUP));
	 }

}
