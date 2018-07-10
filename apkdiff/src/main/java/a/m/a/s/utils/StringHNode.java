package a.m.a.s.utils;

/**
 * Created by amas on 8/1/17.
 */

public class StringHNode extends HNode {
    public StringHNode(String parent, String name) {
        super(parent, name);
    }

    public StringHNode(String parentId) {
        super(parentId);
    }

    @Override
    public long calcDifferentRate(String tagL, String tagR) {
        return 0;
    }

    protected HNode setValue(String tag, String value) {
        super.setValue(tag, value);
        return this;
    }

    @Override
    protected String calcDifferent(Object l, Object r) {
        StringBuilder sb = new StringBuilder();
        if(l != null) sb.append(l);
        if(l!=null && r!=null) sb.append(" -> ");
        if(r != null) sb.append(r);
        return sb.toString();
    }

    public void set(String tag, String value) {
        setValue(tag, value);
    }
}
