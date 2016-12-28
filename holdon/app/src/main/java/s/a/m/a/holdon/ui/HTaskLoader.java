package s.a.m.a.holdon.ui;

import android.app.ListFragment;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import s.a.m.a.holdon.HTask;
import s.a.m.a.holdon.storage.LocalStorage;

/**
 * Created by amas on 15-5-20.
 */
public class HTaskLoader extends AsyncTask<Void,Void,List<HTask>> {
    @Override
    protected List<HTask> doInBackground(Void... params) {
        List<HTask> htasks = LocalStorage.getsInstance().getAllHTask();
        List<HTask> syncs = new ArrayList<>();
        for(HTask task : htasks) {
            boolean statusChanged = task.updateStatus(null);
            if(statusChanged) {
                syncs.add(task);
                LocalStorage.getsInstance().updateHTask(task);
            }
        }
        return htasks;
    }

    @Override
    protected void onPostExecute(List<HTask> aVoid) {
    }
}
