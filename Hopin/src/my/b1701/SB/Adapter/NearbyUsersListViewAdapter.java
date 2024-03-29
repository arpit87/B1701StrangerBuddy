package my.b1701.SB.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import my.b1701.SB.Activities.OtherUserProfileActivity;
import my.b1701.SB.FacebookHelpers.FacebookConnector;
import my.b1701.SB.HelperClasses.SBImageLoader;
import my.b1701.SB.R;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.Users.NearbyUser;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.Users.UserFBInfo;
import my.b1701.SB.Users.UserLocInfo;
import my.b1701.SB.Users.UserOtherInfo;
import my.b1701.SB.Util.StringUtils;

import java.util.List;

public class NearbyUsersListViewAdapter extends BaseAdapter{

	List mNearbyUsers;
	Activity underLyingActivity;
	private static LayoutInflater inflater=null;
	public NearbyUsersListViewAdapter(Activity activity,List<NearbyUser> nearbyUsers)
	{
		underLyingActivity = activity;
		mNearbyUsers = nearbyUsers;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNearbyUsers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void clear()
	{		
		mNearbyUsers.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NearbyUser thisUser = (NearbyUser) mNearbyUsers.get(position);
		View thisUserView=convertView;
        if(convertView==null)
        	thisUserView = inflater.inflate(R.layout.nearbyuser_list_row, null);
        ImageView userImageView = (ImageView)thisUserView.findViewById(R.id.nearbyuser_list_image); 
        ImageView fbProfileView = (ImageView)thisUserView.findViewById(R.id.fbProfileView);
        ImageView userProfileView = (ImageView)thisUserView.findViewById(R.id.userProfileView);        
        TextView userName = (TextView)thisUserView.findViewById(R.id.nearbyusername);
        TextView userSource = (TextView)thisUserView.findViewById(R.id.nearbyusersource);
        TextView userDestination = (TextView)thisUserView.findViewById(R.id.nearbyuserdestination);
        TextView userTime = (TextView)thisUserView.findViewById(R.id.nearbyusertime);        
        SBImageLoader.getInstance().displayImageElseStub(thisUser.getUserFBInfo().getImageURL(), userImageView, R.drawable.userpicicon);
        final UserFBInfo thisUserFBInfo = thisUser.getUserFBInfo();
        UserLocInfo thisUserLocInfo = thisUser.getUserLocInfo();
        UserOtherInfo thisUserOtherInfo = thisUser.getUserOtherInfo();
        String name = thisUserFBInfo.getFullName();
        if(StringUtils.isBlank(name))
        	name = thisUserOtherInfo.getUserName();
        String source = thisUserLocInfo.getUserSrcAddress();
        String destination = thisUserLocInfo.getUserDstAddress();
        String formattedTravelInfo = thisUserLocInfo.getFormattedTravelDetails();
        userName.setText(name);
        userSource.setText(source);
        userDestination.setText(destination);
        userTime.setText(formattedTravelInfo);
        
        fbProfileView.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View chatIconView) {
				FacebookConnector fbconnect = new FacebookConnector(underLyingActivity);
				fbconnect.openFacebookPage(thisUserFBInfo.getFbid(),thisUserFBInfo.getFBUsername());						
			}
		});
        
        userProfileView.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View chatIconView) {
				Intent i = new Intent(Platform.getInstance().getContext(),OtherUserProfileActivity.class);				
				i.putExtra("fb_info", thisUserFBInfo.toString());
				Platform.getInstance().getContext().startActivity(i);						
			}
		});
        
		return thisUserView;
	}

}
