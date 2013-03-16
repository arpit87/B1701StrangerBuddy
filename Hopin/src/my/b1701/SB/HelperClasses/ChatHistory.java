package my.b1701.SB.HelperClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import my.b1701.SB.ChatService.Message;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.provider.ChatHistoryProvider;

import java.util.*;

public class ChatHistory {
    private static final String TAG = "my.b1701.SB.HelperClasses.ChatHistory";
    private static Uri mUri = Uri.parse("content://" + ChatHistoryProvider.AUTHORITY + "/db_fetch_only");
    private static String[] columns = new String[] {"fbIdTo",
                                                    "fbIdFrom",
                                                    "body",
                                                    "dailyInstaType",
                                                    "groupId",
                                                    "timestamp"
                                                   };

    public static List<Message> getChatHistory(String fbid){
        Log.i(TAG, "Fetching chat history for " + fbid);
        List<Message> messages;

        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUri, columns, "fbIdTo = ? or fbIdFrom = ?", new String[]{fbid, fbid}, columns[5]);

        if (cursor == null || cursor.getCount() == 0) {
            Log.i(TAG, "Empty result");
            messages = Collections.emptyList();
        } else {
            messages = new LinkedList<Message>();
            if (cursor.moveToFirst()) {
                do {
                    messages.add(buildMessage(cursor));
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null){
            cursor.close();
        }

        return messages;
    }

    public static Map<String, List<Message>> getCompleteChatHistory(String thisUserFbId){
        Log.i(TAG, "Fetching complete chat history");
        Map<String, List<Message>> chatHistory = new HashMap<String, List<Message>>();
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUri, columns, null, null, columns[5]);

        if (!(cursor == null || cursor.getCount() == 0)){
            Message message = buildMessage(cursor);
            String userId = (message.getFrom().equals(thisUserFbId) ? message.getTo() :
                    message.getTo().equals(thisUserFbId) ? message.getFrom() : null);
            if (userId != null) {
                List<Message> messages = chatHistory.get(userId);
                if (messages == null){
                    messages = new ArrayList<Message>();
                    chatHistory.put(userId, messages);
                }
                messages.add(message);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return chatHistory;
    }

    public static void addtoChatHistory(final Message message){
        Log.i(TAG, "Saving chathistory for user " + message.getFrom());
        new Thread("blockUser") {
            @Override
            public void run() {
                saveChatHistoryBlocking(message);
            }
        }.start();

    }

    private static void saveChatHistoryBlocking(Message message) {
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();

        try {
            ContentValues values = new ContentValues();
            values.put(columns[0], message.getTo());
            values.put(columns[1], message.getFrom());
            values.put(columns[2], message.getBody());
            values.put(columns[3], message.getDailyInstaType());
            values.put(columns[4], -1);
            values.put(columns[5], message.getTimestamp());
            cr.insert(mUri, values);
        } catch (RuntimeException e) {
            Log.e(TAG, "BlockUserQueryError", e);
        }
    }

    public static void clearList(){
        try {
            ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
            cr.delete(mUri, null, null);
        } catch (RuntimeException e) {
            Log.e(TAG, "ClearAllQueryError", e);
        }
    }

    private static Message buildMessage(Cursor cursor){
        String to = cursor.getString(1);
        String from = cursor.getString(2);
        String body = cursor.getString(3);
        int dailyInstaType = cursor.getType(4);
        String time = cursor.getString(6);
        return new Message(to, from, body, dailyInstaType, time);
    }
}