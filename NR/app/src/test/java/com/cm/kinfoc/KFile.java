package com.cm.kinfoc;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;


/**
 * 文件同步读写类（写独占，读不独占）
 * @author singun
 *
 */
public class KFile 
{
	private Context context = null;

	public static final int MAX_FILE_LEN = 1024;
	
	public KFile(Context context) {
		this.context = context;
	}

    /**   
     * 保存文件到内部存储 
     * @param fileName
     * @param content
     */   
	public boolean saveFile(String fileName, byte[] content) throws IOException {
		boolean bReturn = false;
		FileOutputStream outStream = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		FileLock fl = outStream.getChannel().tryLock();
        if (fl != null) {
			outStream.write(content);
			fl.release();
			bReturn = true;
        }
		outStream.close();
        return bReturn;
	}

    /**   
     * 保存文件到内部缓存文件夹
     * @param fileName
     * @param content
     */   
	public boolean saveCacheFile(String dirName, String fileName, byte[] content) throws IOException {
		
		boolean bReturn = false;
		File file = new File(dirName+ File.separator+fileName);
		FileOutputStream outStream = new FileOutputStream(file);
		FileLock fl = outStream.getChannel().tryLock();
        if (fl != null) {
			outStream.write(content);
			fl.release();
			bReturn = true;
        }
		outStream.close();
        return bReturn;
	}
	
    /**   
     * 从内部存储 读取文件 
     * @param fileName   
     * @return   
     * @throws IOException
     */
	public String readFile(String fileName) throws IOException {
        return new String(readFileBuffer(fileName));
	}


    /**
     * 从内部存储 读取文件流
     * @param fileName
     * @return
     * @throws IOException
     */
	public byte[] readFileBuffer(String fileName) throws IOException {
		byte byteReturn[] = null;
		FileInputStream fis = context.openFileInput(fileName);
		int len = fis.available();
		if (len > MAX_FILE_LEN)
			len = MAX_FILE_LEN;
		byte[] b = new byte[len];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		while (fis.read(b) != -1) {
			byteArrayOutputStream.write(b);
		}
		fis.close();
		byteReturn = byteArrayOutputStream.toByteArray();
        return byteReturn;
	}
//
//    /**
//     * 从缓存 读取文件
//     * @param fileName
//     * @return
//     * @throws IOException
//     */
//	public String readCacheFile(String dirName, String fileName) throws IOException {
//        byte[] cache = readCacheBuffer(dirName, fileName);
//        if(cache == null) {
//        	return "";
//        }
//
//        return new String(cache);
//	}
//
//    /**
//     * 从缓存读取文件流
//     * @param fileName
//     * @return
//     * @throws IOException
//     */
//	public byte[] readCacheBuffer(String dirName, String fileName) throws IOException {
//		if(!KFileUtil.isCacheDirAvail(context)) {
//			return null;
//		}
//
//		byte byteReturn[] = null;
//		File file = new File(context.getCacheDir().getAbsolutePath() + File.separatorChar + dirName,
//				fileName);
//		FileInputStream fis = new FileInputStream(file);
//		int len = fis.available();
//		if (len > MAX_FILE_LEN)
//			len = MAX_FILE_LEN;
//		byte[] b = new byte[len];
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		while (fis.read(b) != -1) {
//			byteArrayOutputStream.write(b);
//		}
//		fis.close();
//		byteReturn = byteArrayOutputStream.toByteArray();
//        return byteReturn;
//	}

    /**
     * 读取文件流
     * @param file
     * @return
     * @throws IOException
     */
	public static byte[] readBuffer(File file) throws IOException {
		byte byteReturn[] = null;
		FileInputStream fis = new FileInputStream(file);
		int len = fis.available();
		if (len > MAX_FILE_LEN)
			len = MAX_FILE_LEN;

		if (len <= 0) {
			fis.close();
			return null;
		}

		byte[] b = new byte[len];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		while (fis.read(b) != -1) {
			byteArrayOutputStream.write(b);
		}
		fis.close();
		byteReturn = byteArrayOutputStream.toByteArray();
        return byteReturn;
	}

    /**
     * 保存文件到SD卡
     * @param fileName
     * @param content
     */
	public static boolean saveToSdCard(String filename, byte[] content) throws IOException {
		boolean bReturn = false;
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);
		FileOutputStream fos = new FileOutputStream(file);
		FileLock fl = fos.getChannel().tryLock();
        if (fl != null) {
			fos.write(content);
			fl.release();
			bReturn = true;
	    }
		fos.close();
        return bReturn;
	}

    /**
     * 从SD卡读取文件
     * @param fileName
     * @return 文件内容
     * @throws IOException
     */
	public static String readFromSdcard(String filename) throws IOException {
		return new String(readSdcardBuffer(filename));
	}

    /**
     * 从SD卡读取文件流
     * @param fileName
     * @return 文件内容
     * @throws IOException
     */  
	public static byte[] readSdcardBuffer(String filename) throws IOException {
		byte byteReturn[] = null;
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);
		FileInputStream sdstream = new FileInputStream(file);
		int len = sdstream.available();
		if (len > MAX_FILE_LEN)
			len = MAX_FILE_LEN;
		byte[] b = new byte[len]; 
		ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
		while (sdstream.read(b) != -1) {
			byteArrayOS.write(b);
		}
		sdstream.close();
		byteReturn = byteArrayOS.toByteArray();
		return byteReturn;
	}
}