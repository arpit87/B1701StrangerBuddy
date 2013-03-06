package my.b1701.SB.Activities;

import my.b1701.SB.R;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.HttpClient.AddUserRequest;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.HttpClient.SBHttpRequest;
import my.b1701.SB.Util.StringUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.EasyTracker;

public class Tutorial extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);
        
        final EditText userNameView = (EditText) findViewById(R.id.tutorial_name_edittext);
        final EditText phoneView = (EditText) findViewById(R.id.tutorial_mobile_edittext);
        
        Intent i = getIntent();
        Bundle b = i.getExtras();
        final String uuid = b.getString("uuid");
        Button startButton = (Button) findViewById(R.id.tutorial_startbutton);
		// if button is clicked, close the custom dialog
        startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
                String userNameText = userNameView.getText().toString();
                if (StringUtils.isBlank(userNameText)) {
                	ToastTracker.showToast("Please enter name");
                    return;
                }
                
               String mobile = phoneView.getText().toString();
               ThisUserConfig.getInstance().putString(ThisUserConfig.USERNAME, userNameText);
               ThisUserConfig.getInstance().putString(ThisUserConfig.MOBILE, mobile);
               SBHttpRequest request = new AddUserRequest(uuid,userNameText,Tutorial.this);		
       		   SBHttpClient.getInstance().executeRequest(request);
       		   ProgressHandler.showInfiniteProgressDialoge(Tutorial.this, "Welcome!", "preparing for first run");       		  
				
			}
		});        
        
	}

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

}
