package my.b1701.SB.HelperClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import my.b1701.SB.Platform.Platform;
import my.b1701.SB.provider.BlockedUsersProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockedUsers {

    private static final String TAG = "my.b1701.SB.HelperClasses";
    private static Uri mUriFetch = Uri.parse("content://" + BlockedUsersProvider.AUTHORITY + "/db_fetch_only");
    private static Uri mUri = Uri.parse("content://" + BlockedUsersProvider.AUTHORITY + "/blockedUsers");
    private static String[] columns = new String[] {"fbId"};

    public static List<String> getList(){
        Log.i(TAG, "Fetching blocked Users");
        List<String> blockedUsers;
        
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            Log.i(TAG, "Empty result");
            blockedUsers = Collections.emptyList();
        } else {
            blockedUsers = new ArrayList<String>();
            if (cursor.moveToFirst()) {
                do {
                    blockedUsers.add(cursor.getString(1));
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null){
            cursor.close();
        }

        return blockedUsers;
    }

    public static boolean isUserBlocked(String fbId){
        Log.i(TAG, "Checking if '" + fbId + "' is blocked");
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
        Cursor cursor = cr.query(mUriFetch, columns, "fbId = ?", new String[]{fbId}, null);

        boolean isUserBlocked = true;
        if (cursor == null || cursor.getCount() == 0){
            isUserBlocked = false;
        }

        if (cursor != null) {
            cursor.close();
        }

        return isUserBlocked;
    }

    public static void addtoList(final String fbId){
        Log.i(TAG, "Adding '" +fbId + "' to blocked users list");
        new Thread("blockUser") {
            @Override
            public void run() {
                saveHistoryBlocking(fbId);
            }
        }.start();

    }

    private static void saveHistoryBlocking(String fbId) {
        ContentResolver cr = Platform.getInstance().getContext().getContentResolver();

        try {
            ContentValues values = new ContentValues();
            values.put(columns[0], fbId);
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

    public static void deleteFromList(String fbId){
        try {
            ContentResolver cr = Platform.getInstance().getContext().getContentResolver();
            cr.delete(mUri, "fbId = ?", new String[]{fbId});
        } catch (RuntimeException e){
            Log.e(TAG, "Error in deleting user : " + fbId, e);
        }
    }
}