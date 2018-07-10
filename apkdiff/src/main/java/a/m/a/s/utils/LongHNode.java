package a.m.a.s.utils;

import java.util.Set;

import a.m.a.s.tools.apkdiff.DiffResult;

/**
 * Created by amas on 8/1/17.
 */

public class LongHNode extends HNode {
    public LongHNode(String parent, String name) {
        super(parent, name);

    }

    public LongHNode(String parentId) {
        super(parentId);
    }

    @Override
    public long calcDifferentRate(String tagL, String tagR) {
        long a = get(tagL);
        long b = get(tagR);
        return a - b;
    }

    protected HNode setValue(String tag, long value) {
        super.setValue(tag, value);
        return this;
    }

    public void set(String tag, long value) {
        setValue(tag, value);
    }

    public long get(String tag) {
        Object o = xs.get(tag);
        if (o != null) {
            return (long) o;
        }
        return 0;
    }

    public void add(LongHNode node) {
        Set<String> tags = node.getTagsSet();
        for (String t : tags) {
            add(t, node.get(t));
        }
    }


    public void add(String tag, long value) {
        set(tag, get(tag) + value);
    }


    @Override
    protected Long calcDifferent(Object l, Object r) {
        long a = l == null ? 0 : (Long) l;
        long b = r == null ? 0 : (Long) r;
        return a - b;
    }

    public static int compare_long(long var0, long var2) {
        return var0 < var2 ? -1 : (var0 == var2 ? 0 : 1);
    }

    public DiffResult getDifferentResult(String tagL, String tagR) {
        return DiffResult.createDiffResult(this, tagL, tagR);
    }
}
