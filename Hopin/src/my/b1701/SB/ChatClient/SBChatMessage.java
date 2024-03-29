package my.b1701.SB.ChatClient;

import android.annotation.SuppressLint;
import java.util.Date;

/**
 * this class has message object for displaying on chat window.
 * the chat list adapter has list of these messages
 * @author arpit87
 *
 */
public class SBChatMessage {

	private String mName = "";
	private String mMessage = "";
	private boolean mIsError = false;
	private String mTimestamp = "";
	private String mFrom = "";
	private String mTo = "";
	private String mTime= "";
	private int mStatus ;
	private long mUniqueIdentifier;
	//we are not using enum as its not serializable
	public static final int SENDING_FAILED = -1;
	public static final int SENDING = 1;
	public static final int SENT = 2;
	public static final int DELIVERED = 3;
	public static final int RECEIVED = 4;
	public static final int OLD = 5;
	public static final int UNKNOWN = 6;
	public static final int BLOCKED = 7;
	
	
	/**
	 * Constructor.
	 * @param bareJid A String containing the bare JID of the message's author.
	 * @param name A String containing the name of the message's author.
	 * @param message A String containing the message.
	 * @param isError if the message is an error message.
	 * @param date the time of the message.
	 */
	public SBChatMessage(final String from, String to, final String message, final boolean isError,final String time, int chatMsgStatus, long unique_id) {
		mTo = to;
		mFrom = from;	    
	    mMessage = message;
	    mIsError = isError;
	    mTimestamp = time;
	    mStatus = chatMsgStatus;
	    mUniqueIdentifier = unique_id;
	}

	/**
	 * JID attribute accessor.
	 * @return A String containing the bare JID of the message's author.
	 */
	public String getReceiver() {
	    return mTo;
	}
	
	public String getInitiator()
	{
		return mFrom;
	}

	/**
	 * Name attribute accessor.
	 * @return A String containing the name of the message's author.
	 */
	public String getName() {
	    return mName;
	}

	/**
	 * Message attribute accessor.
	 * @return A String containing the message.
	 */
	public String getMessage() {
	    return mMessage;
	}

	
	/**
	 * Name attribute mutator.
	 * @param name A String containing the author's name of the message.
	 */

	public void setName(String name) {
	    mName = name;
	}

	/**
	 * Message attribute mutator.
	 * @param message A String containing a message.
	 */
	public void setMessage(String message) {
	    mMessage = message;
	}

	/**
	 * Get the message type.
	 * @return true if the message is an error message.
	 */
	public boolean isError() {
	    return mIsError;
	}

	/**
	 * Set the Date of the message.
	 * @param string date of the message.
	 */
	public void setTimestamp(String string) {
	    mTimestamp = string;
	}

	/**
	 * Get the Date of the message.
	 * @return if it is a delayed message get the date the message was sended.
	 */
	public String getTimestamp() {
	    return mTimestamp;
	}
	
	public String getTimeStamp() {	    
	    return mTimestamp;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
	}

	public long getUniqueIdentifier() {
		return mUniqueIdentifier;
	}

	public void setUniqueIdentifier(long mUniqueIdentifier) {
		this.mUniqueIdentifier = mUniqueIdentifier;
	}

   }
