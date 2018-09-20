package a.m.a.s.tools.apkdiff;

import com.beust.jcommander.JCommander;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import a.m.a.s.utils.LongHNode;
import a.m.a.s.utils.StringHNode;
import a.m.a.s.utils.Vendor;
import a.m.a.s.utils.ZipUtils;


/**
 * Created by amas on 7/27/17.
 * 1. 基本比较,基于对zip文件的分析,给出APK的变化,主要是大小
 * 2. AndroidManifest的比较, 可以看看哪些组件发生了变化
 * 3. DEX的比较, 这个需要可以看出哪些模块发生了变化, 变化的程度如何
 * 4. 对于资源的比较
 * 5. List Component by process
 * 6. List top reduced entry
 * 7. For resource common
 * * list the all name length
 * 需要思考的问题:
 * 1. 可否识别出类或者资源文件重命名?
 */

public class ApkDiff {
    public static class ZipDiffListener {
        public void onDiffEntry(String target, ZipEntry l, ZipEntry r) {

        }
    }

    public static class DiffGroup {
        Pattern pattern = null;
        String groupId = "";
        String description = null;
        ArrayList<LongHNode> xs = new ArrayList<>();
        LongHNode size = null;

        public DiffGroup(String pattern, String groupId, String description) {
            this.pattern = Pattern.compile(pattern);
            this.description = description;
            this.groupId = groupId;
            size = new LongHNode("", description);
        }

        public boolean add(LongHNode item) {
            if (pattern.matcher(item.getId()).find()) {
                xs.add(item);
                size.add(item);
                return true;
            }
            return false;
        }

        public List<DiffResult> top(int max) {
            List<DiffResult> rs = new ArrayList<>();
            if (max < 0) {
                return rs;
            }
            for (int i = 0; i < xs.size(); ++i) {
                DiffResult dr = xs.get(i).makeDiff("L", "R");
                if (dr.isNotChanged()) {
                    continue;
                }
                rs.add(dr);
            }
            Collections.sort(rs);
            if (max > rs.size()) {
                max = rs.size();
            }
            return rs.subList(0, max);
        }

        @Override
        public String toString() {
            return getSummary();
        }

        private String getSummary() {
            StringBuilder sb = new StringBuilder();
            DiffResult dr = size.makeDiff("L", "R");
            dr.format(1);
            sb.append(String.format("%s", dr));
            return sb.toString();
        }
    }


    public static class APK_SIZE_DIFF_LISTENER extends ZipDiffListener {
        TreeMap<String, LongHNode> items = new TreeMap<>();
        LinkedList<DiffGroup> groups = new LinkedList<DiffGroup>();

        public APK_SIZE_DIFF_LISTENER() {
            addGroup("^.*.dex$", "GID_DEX", "DEX");
            addGroup("^lib/.*", "GID_LIB", "LIB");
            addGroup("^assets/.*", "GID_ASSETS", "ASSETS");
            addGroup("^res/drawable.*(.png|.jpg)$", "GID_IMAGE", "PNG");
            addGroup("^res/layout.*.xml$", "GID_LAYOUT", "XML");
            addGroup("^.*", "GID_OTHERS", "OTHER");
        }

        public void addGroup(String pattern, String groupId, String description) {
            groups.add(new DiffGroup(pattern, groupId, description));
        }

        boolean isSkipNotChanged = true;

        @Override
        public void onDiffEntry(String target, ZipEntry l, ZipEntry r) {
            LongHNode node = new LongHNode(target);
            if (l != null) {
                node.set("L", l.getCompressedSize());
            }

            if (r != null) {
                node.set("R", r.getCompressedSize());
            }
            items.put(target, node);

            for (DiffGroup g : groups) {
                if (g.add(node)) {
                    break;
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (DiffGroup g : groups) {
                sb.append(g.toString()).append('\n');
            }

            // TOP
            for (DiffGroup g : groups) {
                sb.append(g.groupId + "/TOP:").append('\n');

                List<DiffResult> xs = g.top(20);
                for (DiffResult x : xs) {
                    x.format(1);
                    x.level(1);
                    sb.append(x).append('\n');
                }
            }
            return sb.toString();
        }

    }


    public static String diffsummary(File l, File r) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("# %s  VS.  %s", l.getName(), r.getName())).append('\n');
        sb.append(String.format("@size  %d(%+d)  %d", l.length(), (l.length() - r.length()), r.length()));
        return sb.toString();
    }

    public static Options OPT = new Options();

    public static void help() {
        System.err.println("apkdiff will diff the two apk files in the following dimensions:");
        System.err.println(" 1. size");
        System.err.println(" 2. manifest changes");
        System.err.println(" 3. dex changes");
        System.err.println("");
        System.err.println("EXAMPLE:");
        System.err.println("# check one apk files");
        System.err.println("$ java -jar apkdiff a.apk");
        System.err.println("# diff two apk file");
        System.err.println("$ java -jar apkdiff a.apk b.apk");
        System.err.println("");
        System.err.println("OTHERS: ");
        System.err.println(" IF USE PROGUARD");
        System.err.println(" 1. copy mapping file to the same directory of apk file");
        System.err.println(" 2. rename the mapping file to <apk-file-name>.map");
        System.err.println(" e.g: a.apk -> a.map(this is proguard mapping file)");
        System.err.println("");
    }

    public static void TEST(String[] argv) {
        //argv = new String[] {"/src/amas/ego/apkscope/appy4_71100_gp.apk", "", "-debug"};
        System.out.println("APKDIFF V1.1 @AMAS");
        JCommander.newBuilder()
                .addObject(OPT)
                .build()
                .parse(argv);

        if(!OPT.isValidate()) {
            help();
            System.exit(1);
            return;
        }

        File zipL = OPT.getFirstApk();
        File zipR = OPT.getSecondApk();


        APK_SIZE_DIFF_LISTENER diffresult = new APK_SIZE_DIFF_LISTENER();
        System.out.println(diffsummary(zipL, zipR));
        System.out.println("\n# TOP变化");
        ZipUtils.diff(zipL, zipR, diffresult);
        System.out.println(diffresult);


        Map<String, StringHNode> manifest = aapt.manifest(aapt.manifest(null, zipL), zipR);

        System.out.println("\n# AndroidManifest变化");
        print(manifest, zipL.getAbsolutePath(), zipR.getAbsolutePath());


        System.out.println("\n# 配置变化");
        Map<String, StringHNode> configrations = aapt.configurations(aapt.configurations(null, zipL), zipR);
        print(configrations, zipL.getAbsolutePath(), zipR.getAbsolutePath());


        System.out.println("\n# DEX变化");
        DexDiffer dex = new DexDiffer(zipL, zipR);
        print(dex.diff(3));
    }


    public static void print(Collection<DiffResult> ds) {
        for (DiffResult d : ds) {
            if (d.isNotChanged()) {
                continue;
            }
            System.out.println(d);
        }
    }

    public static void print(Map<String, StringHNode> map, String l, String r) {
        for (StringHNode n : map.values()) {
            DiffResult dr = n.makeDiff(l, r);
            //System.err.println(n.toString());
            if (dr.isNotChanged()) {
                continue;
            }
            System.out.println(dr);
        }
    }

    public static void print_(Map<String, Vendor.ClassHNode> map, String l, String r) {
        for (Vendor.ClassHNode n : map.values()) {
            DiffResult dr = n.makeDiff(l, r);
            //System.err.println(n.toString());
            if (dr.isNotChanged()) {
                continue;
            }
            System.out.println(dr);
        }
    }

}