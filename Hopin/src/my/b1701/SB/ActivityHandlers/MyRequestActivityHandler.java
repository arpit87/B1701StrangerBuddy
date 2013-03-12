package my.b1701.SB.ActivityHandlers;

import my.b1701.SB.Activities.MyRequestsActivity;
import my.b1701.SB.HelperClasses.BroadCastConstants;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import my.b1701.SB.Users.ThisUserNew;

public class MyRequestActivityHandler extends BroadcastReceiver {

    MyRequestsActivity underlying_activity;

    public MyRequestActivityHandler(Activity underlying_activity) {
        this.underlying_activity = (MyRequestsActivity) underlying_activity;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction.equals(BroadCastConstants.CARPOOLREQ_DELETED)) {
            underlying_activity.setCarpoolReqLayoutToNoActiveReq();
            if (ThisUserNew.getInstance().get_Daily_Instant_Type() == 0) {
                reInitialize();
            }
        } else if (intentAction.equals(BroadCastConstants.INSTAREQ_DELETED)) {
            underlying_activity.setInstaReqLayoutToNoActiveReq();
            if (ThisUserNew.getInstance().get_Daily_Instant_Type() == 1) {
                reInitialize();
            }
        }
    }

    private void reInitialize(){
        MapListActivityHandler.getInstance().clearAllData();
        ThisUserNew.getInstance().reset();
        MapListActivityHandler.getInstance().myLocationButtonClick();
        MapListActivityHandler.getInstance().centreMapTo(ThisUserNew.getInstance().getSourceGeoPoint());
    }

}
