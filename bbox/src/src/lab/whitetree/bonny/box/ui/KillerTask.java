package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;

import org.whitetree.systable.data.Application;
import org.whitetree.systable.system.U;

import android.content.Context;
import android.os.AsyncTask;

public class KillerTask extends AsyncTask<Void, Integer, Integer> {
    protected static final String    TAG          = "KillerTask";
    protected ArrayList<Application> mTargets     = null;
    protected Context                mContext     = null;
    protected int                    mMaxProgress = 0;
    protected int                    mNumber      = 0;

    public KillerTask() {
    };

    public KillerTask(Context context, ArrayList<Application> apps) {
        mTargets = apps;
        mContext = context;
        mNumber  = apps.size();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mTargets = null;
        mContext = null;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Integer doInBackground(Void... args) {
        for (int i = 0; i < mNumber; ++i) {
        	Application target = mTargets.get(i);
        	if(!onPreKill(target)) {
        		continue;
        	}
            U.kill(mContext, target.packageName, target.pid);
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            publishProgress(i);
        }
        return mTargets.size();
    }

    @Override
    protected void onProgressUpdate(Integer... args) {
    	Application target = mTargets.get(args[0]);
    	//System.out.println("KKK ==== " +args[0] + " total="+mNumber);
    	int percent = (int)((args[0]*100)/mNumber);
        onPostKill(target, percent, args[0]);
    }

    protected boolean onPreKill(Application target) {
		return !mContext.getPackageName().equals(target.packageName);
	}

    protected void onPostExecute(Integer parsedText) {
    }


    protected void onPostKill(Application app, int percent, int i) {

    }

}