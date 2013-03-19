package my.b1701.SB.Users;

import java.util.ArrayList;
import java.util.List;

import my.b1701.SB.LocationHelpers.SBGeoPoint;

import com.google.android.maps.GeoPoint;

public class NearbyUserGroup {
	
	public List <NearbyUser> mUsersListInGroup;
	public SBGeoPoint mGeoPoint;
	public List<String> fbids;
	
	NearbyUserGroup(List<NearbyUser> nearbyUsers)
	{
		mUsersListInGroup = nearbyUsers;
		fbids = new ArrayList<String>();
		
		for(NearbyUser n:nearbyUsers)
			fbids.add(n.getUserFBInfo().getFbid());
			
	}
	
	public int getNumberOfUsersInGroup()
	{
		return mUsersListInGroup.size();
	}
	
	public SBGeoPoint getGeoPointOfGroup()
	{
		return mGeoPoint;
	}
	
	public List <NearbyUser> getAllUsersInGroup()
	{
		return mUsersListInGroup;
	}
	
	public void addNearbyUserToGroup(NearbyUser n)
	{
		mUsersListInGroup.add(n);
	}
	
	public List<String> getAllFBIds()
	{
		return fbids;
	}
	
	public NearbyUser getNearbyUserAt(int i)
	{
		return mUsersListInGroup.get(i);
	}

}
