package my.b1701.SB.ChatService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import my.b1701.SB.ChatClient.IMessageListener;
import my.b1701.SB.ChatClient.SBChatMessage;
import my.b1701.SB.HelperClasses.ThisUserConfig;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.HttpClient.GetFBInfoForUserIDAndShowPopup;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.Util.StringUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

/***
 * There is a chatAdapter for every chat which stores a list of all chat msgs for this chat
 * + a list of all msgs sent but not yet delivered in a hashmap.
 * the processMessage method here is the first one to receive any incoming msg and it prcesses the 
 * message to refresh its list of sent/delivered msg and calls back listeners of chatwindow(IMessageListeners)
 * i.e. processMessage of chatwindow in IMessageListener.Stub is called 
 * @author arpit87
 *
 */
 class ChatAdapter extends IChatAdapter.Stub{

	private static final int HISTORY_MAX_SIZE = 50;
    private static final String TAG = "my.b1701.SB.ChatService.ChatAdapter";
    private Boolean mIsOpen = false;
    private final Chat mSmackChat;
    private final String mParticipant;    
    private final List<Message> mMessages; 
    private final HashMap<Long,Message> mSentNotDeliveredMsgHashSet;
    private SBChatManager mChatManager;
    SBMsgListener mMsgListener = null;
    int notificationid = 0;
    String mTravelinfo = "";
    String mImageURL = "";
    private final RemoteCallbackList<IMessageListener> mRemoteListeners = new RemoteCallbackList<IMessageListener>();
    private LinkedBlockingQueue<Message> mMsgqueue= null;
    SenderThread mSenderThread = null;
    
    //small chat participant should be complete as to is overridden inside sendMsg by smack to participant
    public ChatAdapter(final Chat chat,SBChatManager chatManager ) {
    	mSmackChat = chat;
    	mParticipant = chat.getParticipant();
    	mMessages = new LinkedList<Message>();
    	mMsgListener = new SBMsgListener();
    	mMsgqueue= new LinkedBlockingQueue<Message>();
    	mSmackChat.addMessageListener(mMsgListener);
    	mChatManager = chatManager;    	    	
    	mSentNotDeliveredMsgHashSet = new HashMap<Long,Message>();
    	notificationid = mChatManager.numChats()+1;
    	mImageURL = ThisUserConfig.getInstance().getString(ThisUserConfig.FBPICURL);
    	mSenderThread = new SenderThread();
    	mSenderThread.start();
    	Log.i(TAG,"chatadapter created for:"+mParticipant);
        }
    
	@Override
	public void sendMessage(Message msg) throws RemoteException{
		//here we just put on queue
		try {
			mMsgqueue.put(msg);
			Log.e(TAG,"added msg in queue of:"+mParticipant);
			if(msg.getType() == Message.MSG_TYPE_CHAT)
				mMessages.add(msg);	
		} catch (InterruptedException e) {
			Log.e(TAG,"unable to put msg on queue of:"+mParticipant);
			e.printStackTrace();
		}			
		
	}
	
	
	 private void addMessage(Message msg) {
			if (mMessages.size() == HISTORY_MAX_SIZE)
			    mMessages.remove(0);
			mMessages.add(msg);
	 }
		
	@Override
	public void setOpen(boolean value) throws RemoteException {
		mIsOpen = value;
		
	}

	@Override
	public void addMessageListener(IMessageListener listener)
			throws RemoteException {
		if(listener!=null)
			mRemoteListeners.register(listener);
		
	}
	
	@Override
    public void removeMessageListener(IMessageListener listener) {
	if (listener != null) {		
			mRemoteListeners.unregister(listener);
	}
    }
	
	private class SenderThread extends Thread{

		@Override
		public void run() {
			Message m = null;
			while(true)
			{
				boolean msgsent = false;
			try {
				if(m==null)
					m = mMsgqueue.take();	
				
				switch(m.getType())
				{
				case Message.MSG_TYPE_CHAT:
					msgsent = sendChatMessage(m);
				    break;	
				case Message.MSG_TYPE_ACK:
					msgsent = sendChatMessage(m);
				    break;
				case Message.MSG_TYPE_NEWUSER_BROADCAST:
					msgsent = sendBroadCastMessage(m);
				    break;
				    
				}
				} catch (InterruptedException e1) {
					Log.e(TAG,"not able to take msg from queue");
					e1.printStackTrace();
				}
				if(!msgsent)
					try {
						synchronized (mMsgqueue) {
							mMsgqueue.wait();
							}
						
					} catch (InterruptedException e) {
						Log.e(TAG,"couldnt wait on msg queue after trying to send");
						e.printStackTrace();
					}	
				else
					m = null; //if sent put m = null so it picks next msg
			}			
		}		
	}
	
	public void notifyMsgQueue()
	{
		synchronized (mMsgqueue) {
			mMsgqueue.notify();
			}
		
	}
	
	private class SBMsgListener implements MessageListener {		

		//first of all the msg comes here
		// we have different msg types
		
		@Override
		public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
		    Message msg = new Message(message);		    
		    Log.d(TAG, "new msg " + msg.getBody());	
		    Log.d(TAG, "chat is open?"+mIsOpen) ;
		    //if broadcast message from new user then do getMatch req
		    if(msg.getType() == Message.MSG_TYPE_NEWUSER_BROADCAST)
		    {
		        //caution..call back listener to chatwindow might not be registered yet for this chat
		    	//listener get registered only when chat window opens		
		    	
		    	String thisNearbyUserUSERID = (String) message.getProperty(Message.USERID);
		    	int daily_insta_type = (Integer) message.getProperty(Message.DAILYINSTATYPE);
		    	ToastTracker.showToast("got broadcast from userid:"+thisNearbyUserUSERID);
		    	GetFBInfoForUserIDAndShowPopup req = new GetFBInfoForUserIDAndShowPopup(thisNearbyUserUSERID,daily_insta_type);
		    	SBHttpClient.getInstance().executeRequest(req);		    	
		    	return;
		    }

		    if(msg.getType() == Message.MSG_TYPE_ACK)
		    {
		    	//ack has same unique id as the msg
		    	//we are doing so as we cant send long in body
		    	if(msg.getInitiator()=="" || msg.getBody() =="")
					   return;
		    	//do not add ack to list
		    	//ack msgs have time in body	
		    	Message origMsg = mSentNotDeliveredMsgHashSet.get(msg.getUniqueMsgIdentifier());
		    	if(origMsg!=null)
		    	{
		    		Log.i(TAG, "got ack for msg: "+origMsg.getBody()) ;
		    		origMsg.setStatus(SBChatMessage.DELIVERED);
		    		origMsg.setTimeStamp(StringUtils.gettodayDateInFormat("hh:mm"));
		    		mSentNotDeliveredMsgHashSet.remove(origMsg);
		    	}
		    	else
		    	{
		    		Log.d(TAG, "got ack but not msg uniqid: "+msg.getUniqueMsgIdentifier()) ;
		    	}
		    	if(mIsOpen)
			    {
			       Log.i(TAG, "chat is open,sending ack to window ") ;
				   callListeners(msg);
			    }
		    	return;
		    }
		    
		    //this is chat coming from outside,send ack to this msg
		   if(msg.getType() == Message.MSG_TYPE_CHAT)
		   {
			    sendAck(msg);			    
			    if (mMessages.size() == HISTORY_MAX_SIZE)
				    mMessages.remove(0);
			    msg.setStatus(SBChatMessage.RECEIVED);
			    msg.setTimeStamp(StringUtils.gettodayDateInFormat("hh:mm"));
			    msg.setImageURL((String)message.getProperty(Message.IMAGEURL));
			    msg.setTravelInfo((String)message.getProperty(Message.TRAVELINFO));
		    	mMessages.add(msg);
			    if(mIsOpen)
			    {
			    	Log.i(TAG, "chat is open") ;
				    callListeners(msg);
			    }
			    else
			    {
			    	Log.i(TAG, "chat not open,Sending notification") ;
			    	String participant_name = msg.getSubject();
			    	if(participant_name == "")
			    		participant_name = "Unknown";	
			    	String image_url = (String) message.getProperty(Message.IMAGEURL);
			    	String travel_info = (String) message.getProperty(Message.TRAVELINFO);
			    	mChatManager.notifyChat(notificationid,msg.getInitiator(),participant_name,travel_info,image_url);
			    	
			    }
		    	
		    }		
		}
		
		    
		}
	private boolean sendAck(Message msg)
	{		
		org.jivesoftware.smack.packet.Message msgToSend = new org.jivesoftware.smack.packet.Message();
		//msg type is overritten by smack so add property so need to set as property
		//msgToSend.setType(org.jivesoftware.smack.packet.Message.Type.headline);	
		msgToSend.setProperty(Message.UNIQUEID, msg.getUniqueMsgIdentifier());
		msgToSend.setProperty(Message.SBMSGTYPE, Message.MSG_TYPE_ACK);
		
		try { 
			mSmackChat.sendMessage(msgToSend);
			Log.i(TAG, "ack message sent  ");
		} catch (XMPPException e) {
			//TODO retry sending msg?
			Log.e(TAG, "couldnt send ack");					
		    e.printStackTrace();
		}catch (IllegalStateException e)
		{						
		    e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	private boolean sendChatMessage(Message msg)
	{
		org.jivesoftware.smack.packet.Message msgToSend = new org.jivesoftware.smack.packet.Message();		
		String msgBody = msg.getBody();		
		Log.i(TAG, "message sending to " + msg.getTo());
		msgToSend.setBody(msgBody);
		msgToSend.setSubject(msg.getSubject());
		msgToSend.setProperty(Message.UNIQUEID, msg.getUniqueMsgIdentifier());		
		msgToSend.setProperty(Message.SBMSGTYPE, Message.MSG_TYPE_CHAT);
		msgToSend.setProperty(Message.IMAGEURL, mImageURL);
		msgToSend.setProperty(Message.TRAVELINFO, mTravelinfo);		
		
		try { 
			mSmackChat.sendMessage(msgToSend);
			Log.i(TAG, "chat message sent to " + msg.getTo());
			msg.setStatus(SBChatMessage.SENT);					
			mSentNotDeliveredMsgHashSet.put(msg.getUniqueMsgIdentifier(), msg);
		} catch (XMPPException e) {
			//TODO retry sending msg?
			Log.i(TAG, "message sending to had xmpp exception" + msg.getTo());
			msg.setStatus(SBChatMessage.SENDING_FAILED);								
		    e.printStackTrace();
		} catch (IllegalStateException e)
		{						
		    e.printStackTrace();
		    return false;
		}
		//we do a callback to update this msg status to sent or sending failed
		//no user might have switched chat after sending msg, in that case we wont get a 
		//message in current chat window with this unique identifier. But later when it fetches all
		// msgs it ll get status as sent/failed
		try {
			if(isOpen())
				callListeners(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	
	private boolean sendBroadCastMessage(Message msg)
	{		
		//every msg should have sb_msg_type n uniqueid
		org.jivesoftware.smack.packet.Message msgToSend = new org.jivesoftware.smack.packet.Message();
		msgToSend.setProperty(Message.SBMSGTYPE, Message.MSG_TYPE_NEWUSER_BROADCAST);
		msgToSend.setProperty(Message.DAILYINSTATYPE, msg.getDailyInstaType());
		msgToSend.setProperty(Message.USERID, ThisUserNew.getInstance().getUserID());
		msgToSend.setProperty(Message.UNIQUEID, System.currentTimeMillis());
		try { 
			mSmackChat.sendMessage(msgToSend);
			Log.i(TAG, "broadcast message sent to " + msg.getTo());
		} catch (XMPPException e) {
			//TODO retry sending msg?
			Log.e(TAG, "couldnt send broadcast");					
		    e.printStackTrace();
		} catch (IllegalStateException e)
		{						
		    e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	private void callListeners(Message msg)
	{
		int n = mRemoteListeners.beginBroadcast();
	    for (int i = 0; i < n; i++) {
		IMessageListener listener = mRemoteListeners.getBroadcastItem(i);
		try {
		    if (listener != null)
			listener.processMessage(ChatAdapter.this, msg);
		} catch (RemoteException e) {
		    Log.w(TAG, "Error while diffusing message to listener", e);
		}
	    }
	    mRemoteListeners.finishBroadcast();
	}

	@Override
	public boolean isOpen() throws RemoteException {
		return mIsOpen;		
	}

	@Override
	public String getParticipant() throws RemoteException {
		
		return mParticipant;
	}

	@Override
	public List<Message> getMessages() throws RemoteException {		
		return mMessages;
	}

	

}
