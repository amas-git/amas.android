package a.m.a.s.er;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import a.m.a.s.cs.BaseDispatcher;
import a.m.a.s.utils.T;

/**
 * Created by amas on 9/4/17.
 */

public class ErFileWriter extends BaseDispatcher<ErFileWriter.WriteObject> {
    public static class WriteObject {
        public boolean withTime = true;
        Object payload = null;
        File target = null;

        public WriteObject(File target, Object o) {
            payload = o;
            if (target == null) {
                this.target = new File(o.getClass().getSimpleName());
            }
            this.target = target;
        }

        public static WriteObject createWithTimeOption(File target, Object o) {
            WriteObject wo = new WriteObject(target, o);
            return wo;
        }

        public static WriteObject create(File target, Object o) {
            WriteObject wo = new WriteObject(target, o);
            wo.withTime = false;
            return wo;
        }

        public String getContent() {
            return payload.toString();
        }
    }

    public static boolean appendText(File target, String content) {
        if (target == null) {
            return false;
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(target, true)));
            out.println(content);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStartUp(long times) {

    }

    @Override
    public void onDispatch(WriteObject elem) {
        String content = null;
        if(elem.withTime) {
            content = T.catd("DD-hh:mm:ss ", elem.getContent());
        } else {
            content = elem.toString();
        }

        appendText(ensureTarget(elem.target), content);
    }

    final protected File ensureTarget(File target) {
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
            return target;
        }
        return target;
    }

    @Override
    protected void onPostDispatch() {

    }


    @Override
    protected void onPreDispatch() {

    }
}
