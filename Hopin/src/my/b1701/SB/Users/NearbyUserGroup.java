package my.b1701.SB.Users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import my.b1701.SB.LocationHelpers.SBGeoPoint;

import com.google.android.maps.GeoPoint;

public class NearbyUserGroup {
	
	public List <NearbyUser> mUsersListInGroup = Collections.emptyList();
	public SBGeoPoint mGeoPoint = null;
	public List<String> fbids;
	int lat = 0;
	int longi = 0;
	
	NearbyUserGroup(List<NearbyUser> nearbyUsers)
	{
		mUsersListInGroup = nearbyUsers;
		fbids = new ArrayList<String>();
		lat = nearbyUsers.get(0).getUserLocInfo().getGeoPoint().getLatitudeE6();
		longi = nearbyUsers.get(0).getUserLocInfo().getGeoPoint().getLongitudeE6();
		
		for(NearbyUser n:nearbyUsers)
		{
			fbids.add(n.getUserFBInfo().getFbid());			
			lat = (lat + n.getUserLocInfo().getGeoPoint().getLatitudeE6())/2;
			longi = (longi + n.getUserLocInfo().getGeoPoint().getLongitudeE6())/2;			
		}
		
		mGeoPoint = new SBGeoPoint(lat, longi);	
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

	public void addUserToGroup(NearbyUser n)
	{
		if(mUsersListInGroup.isEmpty())
		{
			mUsersListInGroup = new ArrayList<NearbyUser>();
			lat = n.getUserLocInfo().getGeoPoint().getLatitudeE6();
			longi = n.getUserLocInfo().getGeoPoint().getLongitudeE6();
		}
		lat = (lat + n.getUserLocInfo().getGeoPoint().getLatitudeE6())/2;
		longi = (longi + n.getUserLocInfo().getGeoPoint().getLongitudeE6())/2;			
		mGeoPoint = new SBGeoPoint(lat, longi);		
		mUsersListInGroup.add(n);		
	}
}
