package a.m.a.s.apidemos.ui;

import android.content.ContentValues;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by amas on 11/16/16.
 */
public class BaseTracer {

    protected ContentValues data = new ContentValues();

    public BaseTracer(int max) {
        for(int i=0; i<max; ++i) {
            data.put(""+System.currentTimeMillis(), i);
        }
    }

    public String toInfocString() {
        if (data.valueSet() == null) {
            return "";
        }
        ArrayList<String> chunk = new ArrayList<String>();
        for (Map.Entry<String, Object> item : data.valueSet()) {
            String key = item.getKey();     // getting key
            Object value = item.getValue(); // getting value
            chunk.add(key + "=" + value);
        }
        return TextUtils.join("&", chunk);
    }

    public  String toInfocString2() {
        if (data.valueSet() == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> item : data.valueSet()) {
            String key = item.getKey();     // getting key
            Object value = item.getValue(); // getting value
            sb.append(key).append("=").append(value).append("&");
        }
        return sb.toString();
    }

}
