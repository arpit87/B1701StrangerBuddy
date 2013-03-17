package my.b1701.SB.Fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import my.b1701.SB.R;
import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.Adapter.ChatListAdapter;
import my.b1701.SB.Adapter.NearbyUsersListViewAdapter;
import my.b1701.SB.HelperClasses.ActiveChat;
import my.b1701.SB.HelperClasses.CommunicationHelper;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.Users.CurrentNearbyUsers;
import my.b1701.SB.Users.NearbyUser;

import java.util.List;

public class SBChatListFragment extends ListFragment {
	
	private static final String TAG = "my.b1701.SB.Fragments.SBChatListFragment";
	private ViewGroup mListViewContainer;
	private List<ActiveChat> chatUserlist = null;
	
	@Override
	public void onCreate(Bundle savedState) {
        super.onCreate(null);
		//update listview
        Log.i(TAG,"on create list view");
        chatUserlist = ActiveChat.getActiveChats();  
        if(!chatUserlist.isEmpty())
        {
			ChatListAdapter adapter = new ChatListAdapter(getActivity(), chatUserlist);
			setListAdapter(adapter);
			Log.i(TAG,"chatlist users:"+chatUserlist.toString());
        }
	}

    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, null );
		Log.i(TAG,"oncreateview chatlistview");
		mListViewContainer = (ViewGroup) inflater.inflate(R.layout.chatfragment_listview, null);
		TextView mEmptyListTextView = (TextView)mListViewContainer.findViewById(R.id.chatlist_fragment_emptyList);
		if(chatUserlist.isEmpty())
		{
			mEmptyListTextView.setVisibility(View.VISIBLE);
		}
		return mListViewContainer;
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		ActiveChat clickedUser = chatUserlist.get(position);
		String fbid = clickedUser.getUserId();
		String name = clickedUser.getName();
		CommunicationHelper.getInstance().onChatClickWithUser(fbid,name);
		
    }
	
}	
