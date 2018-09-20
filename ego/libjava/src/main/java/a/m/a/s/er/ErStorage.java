package a.m.a.s.er;

import java.util.HashMap;

/**
 * Created by amas on 9/4/17.
 */

public class ErStorage {
    private static ErStorage sInstance = new ErStorage();
    HashMap<String,ErBaseWriter> writers = new HashMap<>();

    public static ErStorage getsInstance() {
        return sInstance;
    }

    public void submit(String writerId, IErStorageTask task) {

    }

}
