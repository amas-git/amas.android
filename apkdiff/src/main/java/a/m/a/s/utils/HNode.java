package a.m.a.s.utils;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import a.m.a.s.tools.apkdiff.DiffResult;

/**
 * Created by amas on 8/1/17.
 */
public abstract class HNode {
    String name = "";
    String parent = "";
    String description = "";
    int level = -1;

    public int level() {
        return level;
    }

    public HNode(String parent, String name) {

        this.parent = parent;
        this.name = name;
        String[] xs = parent.split("/");
        level = xs.length + 1;
    }

    public HNode(String parentId) {
        File f = new File(parentId);
        parent = f.getParent();
        name = f.getName();
        if (parent != null) {
            String[] xs = parent.split("/");
            level = xs.length;
        }
    }

    public String getDescription() {
        return this.description;
    }

    public Set<String> getTagsSet() {
        return xs.keySet();
    }

    public String getParentId() {
        return parent;
    }

    /**
     * The ranking value
     *
     * @param tagL
     * @param tagR
     * @return
     */
    public abstract long calcDifferentRate(String tagL, String tagR);

    public String getName() {
        return name;
    }

    public String getId() {
        if (parent == null) {
            return name;
        }
        return parent + "/" + name;
    }

    public enum NodeStatus {
        INIT, NOT_CHANGED, ADDED, REMOVED, MODIFIED;

        @Override
        public String toString() {
            return super.toString().substring(0, 3);
        }
    }

    Map<String, Object> xs = new TreeMap<>();

    protected HNode setValue(String tag, Object value) {
        xs.put(tag, value);
        return this;
    }

    public Object getValue(String tag) {
        return xs.get(tag);
    }

    public Object getDifferent(String tagL, String tagR) {
        Object l = xs.get(tagL);
        Object r = xs.get(tagR);
        return calcDifferent(l, r);
    }

    protected Object calcDifferent(Object l, Object r) {
        return null;
    }

    public DiffResult makeDiff(String tagL, String tagR) {
        return DiffResult.createDiffResult(this, tagL, tagR);
    }

    public Set<String> getKeySet() {
        return xs.keySet();
    }

    public boolean isEqual(Object l, Object r) {
        if (l != null) {
            return l.equals(r);
        } else if (r != null) {
            return r.equals(l);
        } else {
            return true;
        }
    }


    public HNode.NodeStatus getCompareStatus(String tagL, String tagR) {
        Object l = xs.get(tagL);
        Object r = xs.get(tagR);
        if (l == null && r != null) {
            return NodeStatus.REMOVED;
        } else if (r == null && l != null) {
            return NodeStatus.ADDED;
        } else if (l != null && r != null) {
            if (isEqual(l, r)) {
                return NodeStatus.NOT_CHANGED;
            } else {
                return NodeStatus.MODIFIED;
            }
        } else {
            return NodeStatus.NOT_CHANGED;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %s", getId(), xs.toString()));
        return sb.toString();
    }
}
