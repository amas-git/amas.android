package com.cm.kinfoc;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * 文件读写工具类
 * @author singun
 *
 */
public class KFileUtil {
	/**
	 * 创建文件
	 * @param fileName
	 * @return 文件对象
	 * @throws IOException
	 */
	public static File createFile(String filePath) throws IOException {
		File file = new File(filePath);
		file.createNewFile();
		return file;
	}

	/**
	 * 创建文件夹
	 * @param dirName
	 * @return 文件对象
	 */
	public static File createDir(String dirPath){
		File dir = new File(dirPath);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断文件是否存在
	 * @param fileName
	 * @return true:存在 false:不存在
	 */
	public static boolean isFileExist(String filePath){
		File file = new File(filePath);
		return file.exists();
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
	
	//当前目录文件数
	public static int getDirFileCount(File dir) {
		int fileCount = 0;
		File list[] = dir.listFiles();
		if (null == list) {
			return fileCount;
		}
		
		for(int i = 0; i < list.length; i++){
		    if(list[i].isFile()){
		        fileCount++;
		    }
		}
		return fileCount;
	}
	
	//该目录全部文件数，包含子文件夹
	public static int getDirAllFileCount(File dir){
		int fileCount = 0;
		
		File list[] = dir.listFiles();
		if(null == list){
			return 0;
		}
		
		for(File file : list){
			if(file.isFile()){
				++fileCount;
			}else if(file.isDirectory()){
				fileCount += getDirAllFileCount(file);
			}
		}
		
		return fileCount;
	}
	
	public static boolean isCacheDirAvail(Context context) {
		if(null == context) {
			return false;
		}
		return null != context.getCacheDir();
	}
}