package a.m.a.s.utils;

/**
 * Created by amas on 8/1/17.
 */

public class IntHNode extends HNode {
    public IntHNode(String parent, String name) {
        super(parent, name);
    }

    public IntHNode(String parentId) {
        super(parentId);
    }

    @Override
    public long calcDifferentRate(String tagL, String tagR) {
        return 0;
    }

    protected HNode setValue(String tag, int value) {
        super.setValue(tag, value);
        return this;
    }


    @Override
    protected Integer calcDifferent(Object l, Object r) {
        int intL = l == null ? 0 : (Integer) l;
        int intR = r == null ? 0 : (Integer) r;
        return intL - intR;
    }
}
