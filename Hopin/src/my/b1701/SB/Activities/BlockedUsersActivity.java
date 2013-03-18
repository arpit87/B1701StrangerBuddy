package my.b1701.SB.Activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import my.b1701.SB.Adapter.BlockedUsersAdapter;
import my.b1701.SB.HelperClasses.BlockedUser;
import my.b1701.SB.R;

import java.util.List;

public class BlockedUsersActivity extends ListActivity{
    private static final String TAG = "my.b1701.SB.Activities.BlockedUsersActivity";

    private List<BlockedUser> blockedUsers;
    private BlockedUsersAdapter blockedUsersAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_users_layout);
        this.blockedUsers = BlockedUser.getList();
        this.blockedUsersAdapter = new BlockedUsersAdapter(this, this.blockedUsers);
        setListAdapter(blockedUsersAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        BlockedUser blockedUser = (BlockedUser) getListAdapter().getItem(position);
        buildUnblockAlertMessage(blockedUser);
    }

    private void buildUnblockAlertMessage(final BlockedUser blockedUser) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to unblock '"+ blockedUser.getName() +"'?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        BlockedUser.deleteFromList(blockedUser.getFbId());
                        blockedUsers.remove(blockedUser);
                        blockedUsersAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
