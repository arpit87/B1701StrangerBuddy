package my.b1701.SB.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.R;

public class MyRequestsActivity extends Activity {
    CheckBox instaReq;
    CheckBox carpoolReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_requests_layout);
        instaReq = (CheckBox) findViewById(R.id.insta_request_checkbox);
        carpoolReq = (CheckBox) findViewById(R.id.carpool_request_checkbox);
    }

    @Override
    protected void onResume(){
        String instaReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_INSTA);
        String carpoolReqJson = ThisUserConfig.getInstance().getString(ThisUserConfig.ACTIVE_REQ_CARPOOL);
    }
}
