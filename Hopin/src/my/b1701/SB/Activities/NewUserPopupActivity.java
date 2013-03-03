package my.b1701.SB.Activities;

import org.json.JSONException;
import org.json.JSONObject;

import my.b1701.SB.R;
import my.b1701.SB.Fragments.FBLoginDialogFragment;
import my.b1701.SB.Fragments.NewUserDialogFragment;
import my.b1701.SB.Users.NearbyUser;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class NewUserPopupActivity extends FragmentActivity{
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
       // requestWindowFeature((int) Window.FEATURE_ACTION_BAR & ~Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.newuserpopup_blanklayout);
     
        
	}
	
	@Override
    public void onResume(){
    	super.onResume();
    	
    	String jsonstr = getIntent().getStringExtra("nearbyuserjsonstr");
    	try {
			NearbyUser n = new NearbyUser(new JSONObject(jsonstr));
			NewUserDialogFragment newuser_dialog = new NewUserDialogFragment(n);
			newuser_dialog.show(this.getSupportFragmentManager(), "newuser_dialog");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}         
    	
	}
	


}
