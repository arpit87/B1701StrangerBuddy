package my.b1701.SB.Adapter;

import java.util.List;

import my.b1701.SB.R;
import my.b1701.SB.HelperClasses.SBImageLoader;
import my.b1701.SB.Platform.Platform;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewImageAdapter extends BaseAdapter{

	private LayoutInflater inflater= null;
	List <String> fbids = null;
	
	public GridViewImageAdapter(List<String> fbids)
	{
		inflater= (LayoutInflater)Platform.getInstance().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fbids = fbids;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fbids.size();
	}

	@Override
	public Object getItem(int i) {
		// TODO Auto-generated method stub
		return fbids.get(i);
	}

	@Override
	public long getItemId(int i) {
		// TODO Auto-generated method stub
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			View thisUserView=convertView;
	        if(thisUserView==null)
	        	thisUserView = inflater.inflate(R.layout.grid_imageview, null);
	        ImageView userPicImageView = (ImageView)thisUserView.findViewById(R.id.grid_imageview_pic);
	        SBImageLoader.getInstance().displayImageElseStub(fbids.get(position), userPicImageView, R.drawable.userpicicon);
		return thisUserView;
	}



}
