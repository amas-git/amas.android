package a.m.a.s.tools.apkdiff;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a.m.a.s.tools.OS;
import a.m.a.s.utils.StringHNode;
import a.m.a.s.utils.T;

/**
 * Created by amas on 7/29/17.
 * SEE: /src/android/android-7.1.0_r7/frameworks/base/core/res/res/values/attrs.xml
 */
public class aapt {
    File apk = null;

    public aapt(File apk) {
        this.apk = apk;
    }
    public String getAndroidManimest() {
        String command = String.format("aapt d xmltree %s AndroidManifest.xml", apk);
        return OS.exec(command);
    }




    public static class ResourceItem  {
        public String id   = "";
        public String name = "";
        public String type = "";
        public static ResourceItem create(String id, String name, String type) {
            ResourceItem item = new ResourceItem();
            item.id   = id;
            item.name = name;
            item.type = type;
            return item;
        }

        @Override
        public String toString() {
            return String.format("%s:%s@%s", name, type, id);
        }
    }

    // spec resource 0x7f010117 com.mobilesrepublic.appy:attr/bottomSheetStyle: flags=0x00000000
    // resource 0x7f010000 com.mobilesrepublic.appy:attr/OsRecycleBg: <bag>\\
    static Pattern AAPT_RESOURCES_SPEC =  Pattern.compile("\\s+spec\\sresource\\s+(0x[0-9A-Fa-f]+)\\s(.*?):(.*?)/(.*?):\\s.*");
    public Map<String, ResourceItem> getResourcesTable() {
        HashMap<String,ResourceItem> map = new HashMap<>();

        String command = String.format("aapt d resources %s", apk.getAbsolutePath());
        String result = OS.exec(command);
        Matcher m = null;
        String lines[] = result.split("\n");
        for (String line : lines) {
            if ((m = AAPT_RESOURCES_SPEC.matcher(line)).find()) {
                String id   = m.group(1);
                String name = m.group(4);
                String type = m.group(3);
                ResourceItem item = ResourceItem.create(id, name, type);
                map.put(id, item);
            }
        }
        return map;
    }

    public String getConfigurations() {
        String command = String.format("aapt d configurations %s", apk.getAbsolutePath());
        return OS.exec(command);
    }

    public static Map<String,StringHNode> manifest(Map<String,StringHNode> amap, File apkFile) {
        aapt aapt = new aapt(apkFile);
        Map<String, ResourceItem> resTable = aapt.getResourcesTable();
        return aapt.parseAndroidmanifest(amap, apkFile.getAbsolutePath(), aapt.getAndroidManimest(), resTable);
    }

    public static Map<String,StringHNode> configurations(Map<String,StringHNode> amap, File apkFile) {
        aapt aapt = new aapt(apkFile);
        return aapt.parseConfigurations(amap, apkFile.getAbsolutePath(), aapt.getConfigurations());
    }

    private Map<String,StringHNode> parseConfigurations(Map<String, StringHNode> amap, String tag, String configurations) {
        Map<String,StringHNode> map = amap;
        if(map == null) {
            map = new TreeMap<>();
        }

        String[] xs = configurations.split("\n");
        for(String x : xs) {
            StringHNode node = amap != null ?  amap.get("/"+x) : null;
            if(node == null) {
                node = new StringHNode("", x);
            }
            node.set(tag, x);
            map.put(node.getId(), node);
        }
        return map;
    }

    public static Pattern APLAIN_XML_E_REGEX = Pattern.compile("(\\s+)E:\\s*([\\w-]+).*");
    public static Pattern APLAIN_XML_A_REGEX = Pattern.compile("(\\s+)A:\\s*([\\w:-]+)(\\(([0-9a-zA-Z]+)\\))*=(.*)");
    //E: activity (line=1418)
    //A: android:theme(0x01010000)=@0x103000f
    //A: android:name(0x01010003)="com.cmcm.orion.picks.impl.BrandPGVideoActivity" (Raw: "com.cmcm.orion.picks.impl.BrandPGVideoActivity")
    public static Map<String,StringHNode> parseAndroidmanifest(Map<String,StringHNode> amap, String tag, String text,  Map<String, ResourceItem> resTable) {
        Map<String,StringHNode> map = amap;
        if(map == null) {
            map = new TreeMap<>();
        }

        if(text == null) {
            return map;
        }

        String xs[] = text.split("\n");

        String[] ids = new String[64];
        ids[0]="";
        int PL = 2;
        String currentId="";

        Map<String,String> attrs = new HashMap<>();
        for(String x : xs) {
            int level = 0;
            String padding = "";
            String name = "";
            Matcher m = null;
            StringHNode vnode = null;
            String value = null;

            if((m=APLAIN_XML_E_REGEX.matcher(x)).find()) {
                Map<String,StringHNode> ns = createNodes(amap, tag, currentId, attrs, resTable);
                if(!ns.isEmpty()) {
                    map.putAll(ns);
                    attrs.clear();
                }

                padding = m.group(1);
                level = padding.length();
                name = m.group(2);
                ids[level/PL]=name;
                ids[level/PL+1]=null;
                currentId = getId(ids);

                vnode = new StringHNode(currentId);
                map.put(vnode.getId(), vnode);

            } else if((m=APLAIN_XML_A_REGEX.matcher(x)).find()) {
                name  = m.group(2);
                value = m.group(5);
                attrs.put(name, value);
            } else {
                //System.err.println("DROP: "+ x);
            }
        }

        Map<String, StringHNode> ns = createNodes(amap,tag, currentId, attrs, resTable);
        if (!ns.isEmpty()) {
            map.putAll(ns);
            attrs.clear();
        }
        return map;
    }

    private static HashMap<String, StringHNode> createNodes(Map<String,StringHNode> amap, String tag, String currentId, Map<String,String> attrs, Map<String,ResourceItem> resTable) {
        HashMap<String, StringHNode> xs = new HashMap<>();
        if(!attrs.isEmpty()) {
            String key;
            String val;
            String elemId = currentId;
            StringHNode vnode;

            if(attrs.containsKey("android:name")) {
                key = AAPT_XML_EXTRACT_ID(attrs.get("android:name"));
                vnode = amap !=null ? amap.get(currentId+"/"+key) : null;
                if(vnode == null) {
                    vnode = new StringHNode(currentId, key);
                }
                vnode.set(tag,"-");
                elemId = vnode.getId();
                xs.put(elemId, vnode);
                attrs.remove("android:name");
            }

            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                key = entry.getKey();
                val = entry.getValue();
                vnode = amap !=null ? amap.get(elemId+"/@"+key) : null;
                if(vnode == null) {
                    vnode = new StringHNode(elemId, "@" + key);
                }
                String value = AAPT_XML_EXTRACT_VALUE(key, val);
                ResourceItem resourceItem = null;
                if(resTable !=null && value.startsWith("0x")) {
                    resourceItem = resTable.get(value);
                    //System.err.println(resourceItem);
                }
                vnode.set(tag, resourceItem !=null ? resourceItem.name : value);
                xs.put(vnode.getId(), vnode);
            }
        }
        return xs;
    }


    static Pattern PATTERN_AAPT_VALUE_INT = Pattern.compile("@(0x.*)");
    static Pattern PATTERN_AAPT_VALUE_TYPE_VALUE = Pattern.compile("\\(type\\s*(0x[0-9A-Fa-f]+)\\)(0x[0-9A-Fa-f]+)");
    static Pattern PATTERN_AAPT_VALUE_STRING = Pattern.compile(("\\s*\\\"(.*?)\\\""));

    /*
            "mcc",                // 1
            "mnc",                // 10
            "locale",             // 100
            "touchscreen",        // 1000
            "keyboard",           // 10000
            "keyboardHidden",     // 100000
            "navigation",         // 1000000
            "orientation",        // 10000000
            "screenLayout",       // 100000000
            "uiMode",             // 1000000000
            "screenSize",         // 10000000000
            "smallestScreenSize", // 100000000000
            "density",            // 1000000000000
            "layoutDirection",    // 10000000000000
            "fontScale"           // 1000000 00000000 00000000 00000000
    */

    static  ArrayList<String> android_configChanges = new ArrayList<>(Arrays.asList(new String[32]));
    static {
        android_configChanges.ensureCapacity(32);
        android_configChanges.set(0, "mcc");
        android_configChanges.set(1, "mnc");
        android_configChanges.set(2, "locale");
        android_configChanges.set(3, "touchscreen");
        android_configChanges.set(4, "keyboard");
        android_configChanges.set(5, "keyboardHidden");
        android_configChanges.set(6, "navigation");
        android_configChanges.set(7, "orientation");
        android_configChanges.set(8, "screenLayout");
        android_configChanges.set(9, "uiMode");
        android_configChanges.set(10, "screenSize");
        android_configChanges.set(11, "smallestScreenSize");
        android_configChanges.set(12, "density");
        android_configChanges.set(13, "layoutDirection");
        android_configChanges.set(30, "fontScale");
    }

    static String[] android_launchMode= new String[]{"","standard","singleTop","singleTask","singleInstance"};
    // orientation|keyboard|keyboardHidden|screenLayout|screenSize|smallestScreenSize" = 110110110000
    static String[] android_screenOrientation = new String[]{"landscape", "portrait", "user", "behind", "sensor", "nosensor", "sensorLandscape", "sensorPortrait", "reverseLandscape", "reversePortrait", "fullSensor", "userLandscape", "userPortrait", "fullUser", "locked"};
    static String[] android_windowSoftInputMode_0F = new String[]{"stateUnspecified","stateUnchanged","stateHidden","stateAlwaysHidden","stateVisible","stateAlwaysVisible"};
    private static void printAsBinary(int i) {
        System.err.println("i="+Integer.toBinaryString(i));
    }

    private static String FLAGS_TO_NAME(String name, int flags) {
        int mask = 0x1;
        ArrayList<String> xs = new ArrayList<>();
        if("android:configChanges".equals(name)) {
            for(int i=0; i<32; ++i) {
                if((mask & (flags >> i)) == 1){
                    String name_value = android_configChanges.get(i-1);
                    if(name_value != null) {
                        xs.add(name_value);
                    }
                }
            }
            return T.join("|", xs);
        } else if("android:windowSoftInputMode".equals(name)) {
            int _0F = (0x0F & flags);
            if(_0F < android_windowSoftInputMode_0F.length) {
                xs.add(android_windowSoftInputMode_0F[_0F]);
            }

            int _F0 = (0xF0 & flags);
            switch (_F0) {
                case 0x00: xs.add("adjustUnspecified"); break;
                case 0x10: xs.add("adjustResize"); break;
                case 0x20: xs.add("adjustPan"); break;
                case 0x30: xs.add("adjustNothing"); break;
                default:
                    xs.add(Integer.toHexString(_F0));
            }
            return T.join("|", xs);
        }
        return Integer.toBinaryString(flags);
    }

    private static String INT_TO_NAME(String name, int value) {
        if("android:launchMode".equals(name)) {
            if(value < android_launchMode.length) {
                return android_launchMode[value];
            }
        } else if("android:screenOrientation".equals(name)) {
            if(value == -1) {
                return "unspecified";
            }

            if(value < android_screenOrientation.length) {
                return android_screenOrientation[value];
            }
        }
        return ""+value;
    }
    private static String AAPT_XML_EXTRACT_VALUE(String name, String text) {
        Matcher m = null;
        String value = text;
        String type = null;
        if((m =PATTERN_AAPT_VALUE_INT.matcher(text)).find()) {
            value = m.group(1);
        } else if ((m =PATTERN_AAPT_VALUE_TYPE_VALUE.matcher(text)).find()) {
            value = m.group(2);
            type  = m.group(1);

            if("0x12".equals(type)) {           // BOOL
                long i = Long.decode(value);
                value = String.valueOf(i != 0);
            } else if ("0x10".equals(type)) {   // INT_DEX
                int i = Integer.decode(value);
                value = INT_TO_NAME(name, i);
            } else if ("0x11".equals(type)) {   // INT_HEX
                int i = Integer.decode(value);
                value = FLAGS_TO_NAME(name, i);
            } else {
                value = type+"|"+value;
            }
        } else if ((m =PATTERN_AAPT_VALUE_STRING.matcher(text)).find()) {
            value = m.group(1);
        } else {
            System.err.println("DROP VALUE: " + text);
        }
        return value;
    }

    //"com.google.android.gms.ads.purchase.InAppPurchaseActivity" (Raw: "com.google.android.gms.ads.purchase.InAppPurchaseActivity")
    static Pattern PATTERN_AAPT_NAME_VALUE = Pattern.compile("\\\"([\\w-:.]+)\\\"");
    private static String AAPT_XML_EXTRACT_ID(final String text) {
        Matcher m = null;
        if((m = PATTERN_AAPT_NAME_VALUE.matcher(text)).find()) {
            return m.group(1);
        }
        return "";
    }

    private static String getId(String[] section) {
        ArrayList<String> ids = new ArrayList<>();
        for(int i=0; i<section.length; ++i) {
            if(section[i] == null) {
                break;
            }

            ids.add(section[i]);
        }
        return T.join("/", ids);
    }


}
