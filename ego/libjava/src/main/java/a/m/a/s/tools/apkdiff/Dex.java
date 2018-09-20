package a.m.a.s.tools.apkdiff;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import a.m.a.s.utils.ZipUtils;

/**
 * Created by amas on 8/4/17.
 */


public class Dex {
    public static class ClassEntry {
        public DexBackedClassDef classDef = null;
        public String dexFile = "";
        public Proguard proguard = null;
        public String id;

        public static ClassEntry create(String dexFile, DexBackedClassDef classDef, Proguard proguard) {
            ClassEntry entry = new ClassEntry();
            entry.classDef = classDef;
            entry.dexFile = dexFile;
            entry.proguard = proguard;

            String classname = classDef.getType();
            classname = classname.substring(1, classname.length() - 1);
            String realname = null;
            if (proguard != null) {
                realname = proguard.mapping.get(classname);
            }
            entry.id = realname == null ? classname : realname.replace('.', '/');
            return entry;
        }

        public String id() {
            return id;
        }

        public String getPackageName() {
            return id.substring(0,id.lastIndexOf("/"));
        }

        @Override
        public String toString() {
            return String.format("%s:%s", dexFile, id);
        }
    }

    File apkFile = null;
    Map<String, ClassEntry> classes = new HashMap<>();
    public String dexName = "";


    public static class ClassEntryVistor {
        public void onNewClassEntry(ClassEntry ce) {
        }
    }

    public static Map<String, ClassEntry> getClasses(File apkFile, String dexEntryName, Proguard proguard, ClassEntryVistor vistor) {
        HashMap<String, ClassEntry> map = new HashMap<>();

        try {
            DexFile dexFile = DexFileFactory.loadDexEntry(apkFile, dexEntryName, false, Opcodes.forApi(15) /*api level*/);

            for (ClassDef classDef : dexFile.getClasses()) {
                if (classDef instanceof DexBackedClassDef) {
                    ClassEntry entry = ClassEntry.create(dexEntryName, (DexBackedClassDef) classDef, proguard);
                    map.put(entry.id(), entry);
                    if (vistor != null) {
                        vistor.onNewClassEntry(entry);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Dex(String file, String dexName) {
        apkFile = new File(file);
        this.dexName = dexName;
    }

    public static Map<String, Dex.ClassEntry> getAllClassEntry(File apkFile, Proguard proguard, ClassEntryVistor vistor) {
        Map<String, ClassEntry> rs = new HashMap<>();
        List<String> dexList = ZipUtils.getFileName(apkFile, ".*.dex");
        for (String d : dexList) {
            rs.putAll(getClasses(apkFile, d, proguard, vistor));
        }
        return rs;
    }

    public static Map<String, ClassEntry> getAllClassEntry(File apkFile, ClassEntryVistor vistor) {
        return getAllClassEntry(apkFile, searchProguard(apkFile), vistor);
    }

    public static Proguard searchProguard(File apkFile) {
        String name = apkFile.getName();
        File proguardFile = new File(apkFile.getParentFile(), name.replace(".apk", ".map"));
        return proguardFile.exists() ? Proguard.create(proguardFile.getAbsolutePath()) : null;
    }

    @Override
    public String toString() {
        return String.format("%s :class %d", apkFile, classes.size());
    }
}
