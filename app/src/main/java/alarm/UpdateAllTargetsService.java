package alarm;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import db.MarketDB;
import request.GetItemsService;
import request.Linker;
import target.Target;

/**
 * Created by ruslanthakohov on 19/12/15.
 */
public class UpdateAllTargetsService extends Service implements Runnable {
    private Thread thread = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }

        Log.d(TAG, "Updating targets");

        return START_NOT_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        MarketDB db = new MarketDB(this);
        List<Target> targets = db.getAllTargets();
        for (Target target : targets) {
            Intent serviceIntent = new Intent(this, GetItemsService.class);
            try {
                serviceIntent.setData(Uri.parse(Linker.createFindUrl(target.getName()).toString()));
            } catch (Exception e) {
                Log.d(TAG, "URL Exception: " + e.toString());
            }
            serviceIntent.putExtra(GetItemsService.TARGET_ID_EXTRA, target.getId());
            serviceIntent.putExtra(GetItemsService.TARGET_NAME_EXTRA, target.getName());
            startService(serviceIntent);
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private static final String TAG = UpdateAllTargetsService.class.getSimpleName();
}
