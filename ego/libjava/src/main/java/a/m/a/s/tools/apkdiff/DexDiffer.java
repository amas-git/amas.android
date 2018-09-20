package a.m.a.s.tools.apkdiff;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import a.m.a.s.utils.HNode;
import a.m.a.s.utils.LongHNode;
import a.m.a.s.utils.StringHNode;

/**
 * Created by amas on 8/8/17.
 * 1. 有多少个包
 * 2. 每个包下面有多少类, 多少函数, 多少静态方法等等
 * 3.
 */

public class DexDiffer {
    public static class ClassEntryHNode extends HNode {
        public ClassEntryHNode(String parent, String name) {
            super(parent, name);
        }

        public ClassEntryHNode(String parentId) {
            super(parentId);
        }


        @Override
        protected HNode setValue(String tag, Object value) {
            Dex.ClassEntry ce = (Dex.ClassEntry) value;
            return super.setValue(tag, value);
        }

        @Override
        public long calcDifferentRate(String tagL, String tagR) {
            long i = (long) this.calcDifferent(getValue(tagL), getValue(tagR));
            return i;
        }

        @Override
        protected Object calcDifferent(Object l, Object r) {
            Dex.ClassEntry enL = (Dex.ClassEntry) l;
            Dex.ClassEntry enR = (Dex.ClassEntry) r;

            long enL_SIZE = 0;
            long enR_SIZE = 0;

            if (enL != null) {
                enL_SIZE = enL.classDef.getSize();
            }

            if (enR != null) {
                enR_SIZE = enR.classDef.getSize();
            }
            return (long)enL_SIZE - enR_SIZE;
        }

        @Override
        public boolean isEqual(Object l, Object r) {
            if (l == null && r != null) return false;
            if (l != null && r == null) return false;
            Dex.ClassEntry enL = (Dex.ClassEntry) l;
            Dex.ClassEntry enR = (Dex.ClassEntry) r;
            return enL.classDef.getSize() == enR.classDef.getSize();
        }
    }

    public static class PackageHNode extends StringHNode {
        ArrayList<ClassEntryHNode> classes = new ArrayList<>();
        LongHNode size = null;
        LongHNode classNum = null;
        ArrayList<DiffResult> topchanged = new ArrayList<>();

        public PackageHNode(String parent, String name) {
            super(parent, name);
            init();
        }

        public PackageHNode(String id) {
            super(id);
            init();
        }

        private void init() {
            size = new LongHNode(getId(), "@size");
            classNum = new LongHNode(getId(), "@class-num");
        }

        @Override
        public long calcDifferentRate(String tagL, String tagR) {
            return size.calcDifferentRate(tagL, tagR);
        }

        @Override
        public Object getDifferent(String tagL, String tagR) {
            return super.getDifferent(tagL, tagR);
        }

        public void addClass(ClassEntryHNode cl) {
            Set<String> ks = cl.getKeySet();
            for (String tag : ks) {
                Dex.ClassEntry en = (Dex.ClassEntry) cl.getValue(tag);
                size.add(tag, en.classDef.getSize());
                classNum.add(tag, 1);
            }
            classes.add(cl);
        }

        public DiffResult getDifferentResult(String tagL, String tagR) {

            for(ClassEntryHNode n : classes) {
                DiffResult r = n.makeDiff(tagL,tagR);
                if(r.isNotChanged() || r.target.calcDifferentRate(tagL, tagR) <= 10) {
                    continue;
                }
                r.format(1);
                topchanged.add(r);
            }
            Collections.sort(topchanged);

            DiffResult dr = DiffResult.createDiffResult(this, tagL, tagR);
            dr.addSubResult(size.getDifferentResult(tagL, tagR));
            dr.addSubResult(classNum.getDifferentResult(tagL, tagR));

            for(int i=0; i<topchanged.size(); ++i) {
                dr.addSubResult(topchanged.get(i));
            }
            return dr;
        }
    }

    File apkL;
    File apkR;

    public DexDiffer(final File apkL, final File apkR) {
        this.apkL = apkL;
        this.apkR = apkR;
    }

    TreeMap<String, PackageHNode> packages = new TreeMap<>();
    TreeMap<String, ClassEntryHNode> classes = new TreeMap<>();

    public static class DifferClassEntryVistor extends Dex.ClassEntryVistor {
        String tag = "";
        Map<String, ClassEntryHNode> amap = null;

        public DifferClassEntryVistor(String tag, Map<String, ClassEntryHNode> amap) {
            this.tag = tag;
            this.amap = amap;
        }

        @Override
        public void onNewClassEntry(Dex.ClassEntry ce) {
            if (amap == null) {
                amap = new TreeMap<>();
            }

            ClassEntryHNode cn = amap.get(ce.id());
            if (cn == null) {
                cn = new ClassEntryHNode(ce.id());
            }
            cn.setValue(tag, ce);
            amap.put(cn.getId(), cn);
        }
    }

    public List<DiffResult> diff(int level) {

        Dex.getAllClassEntry(apkL, new DifferClassEntryVistor(apkL.getName(), classes));
        Dex.getAllClassEntry(apkR, new DifferClassEntryVistor(apkR.getName(), classes));


        List<DiffResult> result = new ArrayList<>();


        PackageHNode pn = null;
        for (ClassEntryHNode n : classes.values()) {
            if (pn != null) {
                if (n.getParentId().startsWith(pn.getId())) {
                    pn.addClass(n);
                }
            }
            if (n.level() - 1 == level) {
                if (!packages.containsKey(n.getParentId())) {
                    pn = new PackageHNode(n.getParentId());
                    packages.put(n.getParentId(), pn);
                    pn.addClass(n);
                }
            }
        }


        for (PackageHNode ppn : packages.values()) {
            DiffResult dresult = ppn.getDifferentResult(apkL.getName(), apkR.getName());
            result.add(dresult);
        }

        Collections.sort(result);
        return result;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("PACKAGE: %d", packages.size())).append('\n');
        //sb.append(String.format("CLASSES: %d", classes.size())).append('\n');

//        for(PackageHNode p: packages.values()) {
//            System.out.println(p);
//        }
        for (ClassEntryHNode cn : classes.values()) {
            System.out.println(cn);
        }
        return sb.toString();
    }

    //public static ArrayList<String> rankPackageName(ArrayList<Pa>)
}
