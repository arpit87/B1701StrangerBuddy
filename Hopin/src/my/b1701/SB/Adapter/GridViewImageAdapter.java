package my.b1701.SB.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import my.b1701.SB.HelperClasses.SBImageLoader;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.R;

import java.util.List;

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
            String imageUrl = "http://graph.facebook.com/" + fbids.get(position) + "/picture";
	        SBImageLoader.getInstance().displayImageElseStub(imageUrl, userPicImageView, R.drawable.userpicicon);
		return thisUserView;
	}



}
