package db;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import target.Target;

/**
 * Created by ruslanthakohov on 06/11/15.
 */
public class FetchTargetsTask extends AsyncTask<Void, Void, List<Target>> {

    private FetchTargetsTaskClient client;
    private Context context;

    public FetchTargetsTask(FetchTargetsTaskClient client, Context context) {
        attachClient(client);
        this.context = context;
    }

    @Override
    public List<Target> doInBackground(Void ... params) {
        MarketMonitorDBHelper helper = new MarketMonitorDBHelper(context);
        return helper.getAllTargets();
    }

    //TODO: check that the client has been set
    @Override
    public void onPostExecute(List<Target> targets) {
        client.targetsAreReady(targets);
    }

    public void attachClient(FetchTargetsTaskClient client) {
        this.client = client;
    }
}
