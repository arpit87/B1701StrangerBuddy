package my.b1701.SB.Activities;

import my.b1701.SB.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class SettingsActivity extends Activity{
	
	CheckBox showNewUserPopup;
	CheckBox showChatPopup;
	View activeRequestView;
	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.settings_layout);
		 showNewUserPopup = (CheckBox)findViewById(R.id.settings_newuser_showpopup_checkbox);
		 showChatPopup = (CheckBox)findViewById(R.id.settings_newuser_showpopup_checkbox);
		 activeRequestView = (View)findViewById(R.id.settings_activerequset_layout);
		 
		 activeRequestView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
			}
		});
	 }

}
