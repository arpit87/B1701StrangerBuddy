package my.b1701.SB.HelperClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.provider.ActiveChatProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActiveChat {

    private static final String TAG = "my.b1701.SB.HelperClasses.ActiveChat";
    private static Uri mUriFetch = Uri.parse("content://" + ActiveChatProvider.AUTHORITY + "/db_fetch_only");
    private static Uri mUri = Uri.parse("content://" + ActiveChatProvider.AUTHORITY + "/activechat");
    private static String[] columns = new String[] {"fbId", "lastMessage"};

    private final String userId;
    private final String lastMessage;

    public ActiveChat(String userId, String lastMessage){
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    public String getUserId() {
        return userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public static List<ActiveChat> getActiveChats(){
        Log.i(TAG, "Fetching active chats");
        List<ActiveChat> activeChats;

        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            Log.i(TAG, "Empty result");
            activeChats = Collections.emptyList();
        } else {
            activeChats = new ArrayList<ActiveChat>();
            if (cursor.moveToFirst()) {
                do {
                    ActiveChat activeChat = new ActiveChat(cursor.getString(0), cursor.getString(1));
                    activeChats.add(activeChat);
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null){
            cursor.close();
        }

        return activeChats;
    }

    public static void addChat(final String fbId, final String lastMessage){
        Log.i(TAG, "Saving chat for fbId : " + fbId );
        new Thread("addlastchat") {
            @Override
            public void run() {
                saveChat(fbId, lastMessage);
            }
        }.start();

    }

    private static void saveChat(String fbId, String lastMessage) {
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, "fbId = ?", new String[] {fbId}, null);

        boolean isPresent = false;
        if (!(cursor == null || cursor.getCount() == 0)){
            Log.i(TAG, "History Exists");
            isPresent = true;
        }

        if (cursor != null) {
            cursor.close();
        }

        ContentValues contentValues = new ContentValues();
        if (isPresent) {
            contentValues.put(columns[1], lastMessage);
            cr.update(mUri, contentValues, "fbId = ?", new String[] {fbId});
        } else {
            contentValues.put(columns[0], fbId);
            contentValues.put(columns[1], lastMessage);
            cr.insert(mUri, contentValues);
        }
    }
}
