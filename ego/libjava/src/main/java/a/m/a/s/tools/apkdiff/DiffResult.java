package a.m.a.s.tools.apkdiff;

import java.util.ArrayList;

import a.m.a.s.utils.HNode;
import a.m.a.s.utils.T;

/**
 * Created by amas on 8/10/17.
 */

public class DiffResult implements Comparable<DiffResult> {
    HNode.NodeStatus status = HNode.NodeStatus.INIT;
    public HNode target = null;
    String tagL = "";
    String tagR = "";
    int level = 0;
    ArrayList<DiffResult> subrs = new ArrayList<>();

    int format = 0;

    public static DiffResult createDiffResult(HNode node, String tagL, String tagR) {
        DiffResult result = new DiffResult();
        result.target = node;
        result.tagL = tagL;
        result.tagR = tagR;
        return result;
    }

    public void addSubResult(DiffResult subresult) {
        subresult.level(level + 1);
        subrs.add(subresult);
    }

    public void addSubResult(DiffResult subresult, int forceLevel) {

    }

    public int level() {
        return this.level;
    }

    public void level(int level) {
        this.level = level;
    }

    public HNode.NodeStatus getStatus() {
        if (status == HNode.NodeStatus.INIT) {
            status = target.getCompareStatus(tagL, tagR);

            if (!subrs.isEmpty()) {
                int m = 0, a = 0, r = 0, n = 0;

                for (DiffResult dr : subrs) {
                    if (HNode.NodeStatus.NOT_CHANGED == dr.getStatus()) {
                        ++n;
                    } else if (HNode.NodeStatus.ADDED == dr.getStatus()) {
                        ++a;
                    } else if (HNode.NodeStatus.REMOVED == dr.getStatus()) {
                        ++r;
                    } else if (HNode.NodeStatus.MODIFIED == dr.getStatus()) {
                        ++m;
                    }
                }
                if (n == subrs.size()) {
                    status = HNode.NodeStatus.NOT_CHANGED;
                } else if (a == subrs.size()) {
                    status = HNode.NodeStatus.ADDED;
                } else if (r == subrs.size()) {
                    status = HNode.NodeStatus.REMOVED;
                } else {
                    status = HNode.NodeStatus.MODIFIED;
                }
            }
        }
        return status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String name = target.getId();
        int l = level();
        String padding = T.getPaddingString("  ", l);

        switch (format) {
            case 1: {
                sb.append(String.format("%s[%s:%6s] %s", padding, getStatus(), target.getDifferent(tagL, tagR), name));
                break;
            }
            default: {
                name = target.getId();
                sb.append(String.format("%s[%s] %s %s", padding, getStatus(), name, target.getDifferent(tagL, tagR)));
            }
        }

        for (DiffResult x : subrs) {
            sb.append('\n').append(x.toString());
        }
        return sb.toString();
    }

    public String getId() {
        return target.getId();
    }

    @Override
    public int compareTo(DiffResult b) {
        int i = -Long.compare(this.target.calcDifferentRate(tagL, tagR), b.target.calcDifferentRate(tagL, tagR));
        return i;
    }

    public boolean isNotChanged() {
        return getStatus() == HNode.NodeStatus.NOT_CHANGED;
    }

    public void format(int i) {
        this.format = i;
    }
}