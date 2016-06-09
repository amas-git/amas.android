package s.a.m.a.console;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by amas on 1/16/16.
 */
public class ConsoleTextView extends TextView {

    public ConsoleTextView(Context context) {
        super(context);
    }

    public ConsoleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int MAX = 512;
    LinkedList<CharSequence> xs = new LinkedList<CharSequence>();

    public void setBufferSize(int size) {
        MAX = size;
    }

    public void addLine(CharSequence text, boolean update) {
        xs.addLast(text);
        if(xs.size() > MAX) {
            xs.removeFirst();
        }

        if(update) {
            sync();
        }
    }

    public void sync() {
        setText(toCharSequence());
    }

    private CharSequence toCharSequence() {
        StringBuilder sb = new StringBuilder();
        for(CharSequence cs : xs) {
            sb.append(cs).append("\n");
        }
        return sb.toString();
    }
}
