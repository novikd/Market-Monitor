package alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ruslanthakohov on 19/12/15.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //set the alarm
            /*Intent serviceIntent = new Intent(context, UpdateAllTargetsService.class);
            PendingIntent pIntent = PendingIntent.getService(context, 1, serviceIntent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    10000, 60000 * 2, pIntent);*/
        }
    }
}
