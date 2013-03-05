package my.b1701.SB.Activities;

import my.b1701.SB.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;

public class SettingsActivity extends Activity{
	
	CheckedTextView showNewUserPopup;
	CheckedTextView showChatPopup;
	View activeRequestView;
	 @Override
	    protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.settings_layout);
		 showNewUserPopup = (CheckedTextView)findViewById(R.id.settings_newuser_showpopup);
		 showChatPopup = (CheckedTextView)findViewById(R.id.settings_showincomingchat);
		 activeRequestView = (View)findViewById(R.id.settings_activerequset_layout);
		 
		 activeRequestView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
			}
		});
	 }

}
