package a.m.a.s.utils;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by amas on 8/1/17.
 * 用于保存一些实验性的代码
 */

public class Vendor {
    public static void manifest() {

    }

//    public static void diffAnroidManifest(String zipL) {
//        HashMap<String, StringVNode> permissons = new HashMap<>();
//        try {
//            //Document doc = new CompressedXmlParser().parseDOM(new FileInputStream(am));
//            Document doc = new CompressedXmlParser().parseDOM(getFileAsInputStream(zipL, "AndroidManifest.xml"));
//            System.out.println(toXmlString(doc));
//            NodeList nodes = doc.getElementsByTagName("service");
//
//            for (int i = 0; i < nodes.getLength(); ++i) {
//                Node node = nodes.item(i);
//                NamedNodeMap attrs = node.getAttributes();
//
//                StringVNode parent = new StringVNode("/service", attrs.getNamedItem("android:name").getNodeValue());
//                System.out.println(parent);
//                permissons.put(parent.getId(), parent);
//            }
//
//            VNode.print(permissons);
//
//            //printNote(nodes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//    }






    private static FullClassName smaliClassNameToJavaName(String classname) {
        File f = new File(classname.substring(1,classname.length()-1));
        FullClassName fn = new FullClassName();
        fn.classname = f.getName();
        fn.packagename = f.getParent();
        return fn;
    }

    public static class FullClassName {
        public String classname   = "";
        public String packagename = "";
        public String toJava() {
            return packagename.replaceAll("/",".")+"."+classname;
        }

        public static FullClassName createFromJava(String fullname) {
            FullClassName cn = new FullClassName();
            int i = fullname.lastIndexOf(".");
            cn.classname = fullname.substring(i, fullname.length());
            cn.packagename = fullname.substring(0, i);
            return cn;
        }
        @Override
        public String toString() {
            return String.format("%s.%s", packagename, classname );
        }
    }
    public static class ClassHNode extends LongHNode {
        public DexBackedClassDef dexEntry = null;

        public ClassHNode(String parent, String name) {
            super(parent, name);
        }

        public ClassHNode(String parentId) {
            super(parentId);
        }

        public void setDexEntry(DexBackedClassDef dexEntry) {
            this.dexEntry = dexEntry;
        }



    }



    public static InputStream getFileAsInputStream(String zipFile, String targetFile) {
        try {
            ZipFile zip = null;
            zip = new ZipFile(zipFile);
            ZipEntry entry = zip.getEntry(targetFile);
            InputStream is = zip.getInputStream(entry);
            return is;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toXmlString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void printNote(NodeList nodeList) {

        for (int count = 0; count < nodeList.getLength(); count++) {

            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
                System.out.println("Node Value =" + tempNode.getTextContent());

                if (tempNode.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        System.out.println("attr name  : " + node.getNodeName());
                        System.out.println("attr value : " + node.getNodeValue());
                    }

                }

                if (tempNode.hasChildNodes()) {
                    printNote(tempNode.getChildNodes());

                }
                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");

            }

        }

    }
}
