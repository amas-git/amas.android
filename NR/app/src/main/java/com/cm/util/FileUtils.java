package com.cm.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    // 解压并校验解压后的entry的md5值
    public static boolean unzip(String srcPath, String dstPath, String md5) {
        boolean succeeded = false;
        ZipFile zipFile = null;
        InputStream is = null;
        FileOutputStream os = null;

        try {
            zipFile = new ZipFile(srcPath);
            @SuppressWarnings("rawtypes")
            Enumeration entries = zipFile.entries();
            if (!entries.hasMoreElements()) {
                return false;
            }

            ZipEntry entry = (ZipEntry) entries.nextElement();
            is = zipFile.getInputStream(entry);

            MessageDigest md5Digest = null;
            if (!Miscellaneous.isEmpty(md5)) {
                md5Digest = MessageDigest.getInstance("MD5");
                is = new DigestInputStream(is, md5Digest);
            }

            final int BUF_SIZE = 4096;
            byte[] buffer = new byte[BUF_SIZE];

            os = new FileOutputStream(dstPath);

            int bytes = 0;
            do {
                bytes = is.read(buffer);
                if (bytes > 0) {
                    os.write(buffer, 0, bytes);
                } else {
                    break;
                }
            } while (true);

            os.flush();

            if (md5Digest != null) {
                String md5String = Miscellaneous.encodeHex(md5Digest.digest());
                if (md5String.compareToIgnoreCase(md5) != 0) {
                    return false;
                }
            }

            succeeded = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                if (is != null) {
                    is.close();
                }

                if (zipFile != null) {
                    zipFile.close();
                }

                if (!succeeded) {
                    (new File(dstPath)).delete();
                }
            } catch (Exception e) {
            }
        }
        return succeeded;
    }

    // 删除dstPath文件 并将srcPath文件重命名为dstPath
    public static boolean replaceFile(String srcPath, String dstPath) {
        File dstFile = new File(dstPath);
        if (dstFile.exists()) {
            int retry = 3;
            do {
                if (dstFile.delete()) {
                    return (new File(srcPath)).renameTo(dstFile);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            } while (--retry > 0);
            return false;
        }

        return (new File(srcPath)).renameTo(dstFile);
    }

    // 外部存储设备是否有效
    public static boolean isValidExternalStorage() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    // 内部存储设备可用大小
    public static long getAvailableInternalStorageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return (availableBlocks * blockSize);
    }

    // 添加斜杠
    public static String addSlash(final String path) {
        if (Miscellaneous.isEmpty(path)) {
            return File.separator;
        }

        if (path.charAt(path.length() - 1) != File.separatorChar) {
            return path + File.separatorChar;
        }

        return path;
    }

    // 去掉斜杠
    public static String removeSlash(String path) {
        if (Miscellaneous.isEmpty(path) || path.length() == 1) {
            return path;
        }

        if (path.charAt(path.length() - 1) == File.separatorChar) {
            return path.substring(0, path.length() - 1);
        }

        return path;
    }

    // 最后的斜杠换成0，如果没有斜杠则补0
    public static String replaceEndSlashBy0(String path) {

        if (Miscellaneous.isEmpty(path)) {
            return "0";
        }

        if (path.charAt(path.length() - 1) != File.separatorChar) {
            return path + "0";
        }

        return path.substring(0, path.length() - 1) + "0";
    }

    public static long getFileSize(String filePath){
        File file = new File(filePath);
        try {
            if (file.exists()) {
                return file.length();
            }
        } catch (Throwable e) {

        }
        return 0;
    }

    public static File checkPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (file == null || !file.exists()) {
            return null;
        }
        return file;
    }

    public static File checkDir(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        return dir;
    }

    public static File checkDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return null;
        }
        return dir;
    }

    public static File checkFile(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        return file;
    }

    public static File checkFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        return file;
    }

    public static File checkDirAndCreate(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.delete();
            dir.mkdirs();
        }
        return dir;
    }

    public static File checkDirCreate(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.delete();
            dir.mkdir();
        }
        return dir;
    }

    public static boolean checkFileAndDelete(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        return true;
    }

    public static InputStream checkAssetsFile(Context context, String path) {
        InputStream is = null;
        if(path == null){
            return null;
        }
        try {
            AssetManager manager = context.getAssets();
            is = manager.open(path);

        } catch (Exception e) {
        }

        return is;
    }

    public static boolean checkAssetsDir(Context context, String rootPath, String path) {

        boolean exist = false;

        try {
            context.getAssets().open(path);
            exist = true;
        } catch (Exception e) {
        }
        return exist;
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return int 0:success -1:throw exception  -2: the oldFile not exist
     * @author renjie
     * @date 2014.02.27
     */
    public static int copyFile(String oldPath, String newPath) {
        if(oldPath != null && newPath!= null && oldPath.equals(newPath)){
            return -1;
        }
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                inStream = new FileInputStream(oldPath); // 读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    // System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.getFD().sync();
                return 0;
            } else {
                return -2;
            }
        } catch (Exception e) {
            return -1;
        } finally {
            try{
                if(inStream != null){
                    inStream.close();
                }
            } catch(Exception e){

            }

            try{
                if(fs != null){
                    fs.close();
                }
            } catch(Exception e){

            }
        }
    }

    private static int OFFSET_LENGTH = 8;

    public static boolean verifyOriginalFileWithCopiedFile(String originalPath, String copiedPath) {
        File originalFile = new File(originalPath);
        InputStream originalinStream = null; // 读入原文件
        InputStream copiedInputStream = null; // 读入原文件
        try {
            originalinStream = new FileInputStream(originalPath); // 读入原文件
            copiedInputStream = new FileInputStream(copiedPath); // 读入原文件
            long fileSize = originalFile.length();
            originalinStream.skip(fileSize - OFFSET_LENGTH);
            copiedInputStream.skip(fileSize - OFFSET_LENGTH);
            byte[] originabuffer = new byte[OFFSET_LENGTH];
            byte[] copiedbuffer = new byte[OFFSET_LENGTH];
            byte[] tmpbuffer = new byte[OFFSET_LENGTH];
            int byteread = 0;
            int bytesum = 0;
            while ((byteread = originalinStream.read(tmpbuffer)) != -1) {
                if (bytesum + byteread >= 8) {
                    byteread = 8 - bytesum;
                }
                System.arraycopy(tmpbuffer, 0, originabuffer, bytesum, byteread);
                bytesum += byteread; // 字节数 文件大小
                if (bytesum >= 8) {
                    break;
                }
                // System.out.println(bytesum);
            }

            byteread = 0;
            bytesum = 0;
            while ((byteread = copiedInputStream.read(tmpbuffer)) != -1) {
                // System.out.println(bytesum);
                if (bytesum + byteread >= 8) {
                    byteread = 8 - bytesum;
                }
                System.arraycopy(tmpbuffer, 0, copiedbuffer, bytesum, byteread);
                bytesum += byteread; // 字节数 文件大小
                if (bytesum >= 8) {
                    break;
                }
            }

            for (int i = 0; i < originabuffer.length; i++) {
                if (originabuffer[i] != copiedbuffer[i]) {
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (originalinStream != null) {
                    originalinStream.close();
                }
                if (copiedInputStream != null) {
                    copiedInputStream.close();
                }
            } catch (Exception e) {

            }
        }
    }

    public static  int copyFile(Context context, DocumentFile pickedDir, String oldPath, String newPath) {
        InputStream inStream = null;
        OutputStream fs = null;
        // Create a new file and write into it
        DocumentFile newFile = pickedDir.createFile("image/jpg", newPath);
        OutputStream out;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                inStream = new FileInputStream(oldPath); // 读入原文件
                fs = context.getContentResolver().openOutputStream(newFile.getUri());
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    // System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                if (fs instanceof ParcelFileDescriptor.AutoCloseOutputStream) {
                    ParcelFileDescriptor.AutoCloseOutputStream upperFs = (ParcelFileDescriptor.AutoCloseOutputStream)fs;
                    upperFs.getFD().sync();
                }
                return 0;
            } else {
                return -2;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try{
                if(inStream != null){
                    inStream.close();
                }
            } catch(Exception e){

            }

            try{
                if(fs != null){
                    fs.close();
                }
            } catch(Exception e){

            }
        }
    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     * @author renjie
     * @date 2014.02.27
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+ File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void copyAssetToFiles(Context context, String assetsPath, String datapath) {
        if (TextUtils.isEmpty(assetsPath) || TextUtils.isEmpty(datapath))
            return;

        File datafile = new File(datapath);

        if (datafile.exists() && datafile.isFile()) {

//            Log.d("show", String.format("data: %s exist", datapath));

        } else {
            InputStream is = null;
            try {

                AssetManager asm = context.getAssets();
                is = asm.open(assetsPath);
                FileUtils.copyToFile(is, datafile);

//                Log.d("show", String.format("copyAssetToFiles: %s -> %s", assetsPath, datafile));

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 删除文件
     * @param fileName
     * @return true:成功 false:失败
     */
    public static boolean deleteFile(String fileName){
        File file = new File(fileName);
        if(file.isFile() && file.exists()){
            file.delete();
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除文件夹
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件夹中的所有文件
     * @param path
     * @return true:成功 false:失败
     */
    public static boolean delAllFile(String path) {
        if(path == null || path.length() <= 0) {
            return false;
        }

        boolean bReturn = false;
        File file = new File(path);
        if (!file.exists()) {
            return bReturn;
        }
        if (!file.isDirectory()) {
            return bReturn;
        }
        String[] tempList = file.list();
        if ( tempList != null ){
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path + File.separatorChar + tempList[i]);
                    delFolder(path + File.separatorChar + tempList[i]);
                    bReturn = true;
                }
            }
        }

        return bReturn;
    }

    public static int pathFileCount(String path){
        if(null == path || path.length() <= 0){
            return 0;
        }

        File pathFile = new File(path);
        if(!pathFile.exists() || !pathFile.isDirectory()){
            return 0;
        }

        String[] pathFileNames = pathFile.list();
        if(null == pathFileNames){
            return 0;
        }

        return pathFileNames.length;
    }

    public static boolean isCacheDirAvail(Context context) {
        if(null == context) {
            return false;
        }
        return null != context.getCacheDir();
    }

    public static String makePath(String path1, String path2) {
        if (path1.endsWith(File.separator))
            return path1 + path2;

        return path1 + File.separator + path2;
    }


    public static File checkFilesFile(Context context, String path) {
        if (TextUtils.isEmpty(path))
            return null;
        File dir = getFilesDir(context);
        if (dir == null)
            return null;
        String filepath = FileUtils.addSlash(dir.getAbsolutePath()) + path;
        File file = FileUtils.checkFile(filepath);
        return file;
    }

    public static File checkFilesDirAndCreate(Context context, String path) {
        if (TextUtils.isEmpty(path))
            return null;
        File basedir = getFilesDir(context);
        if (basedir == null)
            return null;
        String dirpath = FileUtils.addSlash(basedir.getAbsolutePath()) + path;
        File dir = FileUtils.checkDirAndCreate(dirpath);
        return dir;
    }

    /**
     * 封装对Context.getFilesDir()的调用
     */
    public static File getFilesDir(Context ctx) {

        if (null == ctx) {
            return null;
        }

        File result = null;
        for (int i = 0; i < 3; ++i) {
            // 因为有时候getFilesDir()在无法创建目录时会返回失败，所以我们在此等待并于半秒内尝试三次。
            result = ctx.getFilesDir();
            if (null != result) {
                break;
            } else {
                try {
                    Thread.sleep(166);
                } catch (InterruptedException e) {
                }
            }
        }

        return result;
    }

}
