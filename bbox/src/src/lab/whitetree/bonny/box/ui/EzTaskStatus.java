package lab.whitetree.bonny.box.ui;

public class EzTaskStatus {
    public static int    STATUS_START      = 0;
    public static int    STATUS_RUNNING    = 1;
    public static int    STATUS_DONE       = 2;
    public static int    STATUS_ERRO       = 3;

    public static String STATUS_KEY_REPORT = ":REPORT";

    int                  mStatus           = STATUS_START;

    int                  mIconDone         = -1;
    String               mKey              = "...";       // for search
    String               mDescRunning      = "...";
    String               mDescDone         = "done";

    public EzTaskStatus(String key) {
        mKey = key;
    }

    public void setIconDone(int resId) {
        mIconDone = resId;
    }

    public int getIconDone() {
        return mIconDone;
    }

    public void setRunningDesc(String runningDesc) {
        mDescRunning = runningDesc;
    }

    public void setDoneDesc(String doneDesc) {
        mDescDone = doneDesc;
    }

    public String getRunningDesc() {
        return mDescRunning;
    }

    public String getDoneDesc() {
        return mDescDone;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getKey() {
        return mKey;
    }

    public boolean isRunning() {
        return mStatus == STATUS_RUNNING;
    }

    public void setRunning() {
        mStatus = STATUS_RUNNING;
    }

    public boolean isDone() {
        return mStatus == STATUS_DONE;
    }

    public void setDone() {
        mStatus = STATUS_DONE;
    }

}
