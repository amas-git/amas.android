package com.cm.kinfoc;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.cm.kinfoc.api.MMM;
import com.cm.kinfoc.base.InfocCommonBase;
import com.cm.util.Md5Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


/**
 * Infoc工具类
 * @author singun
 *
 * checkInfocFile检查两个配置文件是否正确
 * 不正确则从apk中释放正确版本
 */
public class KInfocUtil {
	public static final String KFMT_DAT		= "kfmt.dat";
	public static final String KCTRL_DAT	= "kctrl.dat";
	public static final String LOG_TAG		= "kinfoc";
	public static final String CACHE_DIR 		= "infoc_";
	public static final String CACHE_DIR_FORCE = "infoc_force_";
	public static final String CACHE_DIR_GET	= "infoc_get";              // 用HTTP GET方式上报, 貌似目前已经废了
	public static final String CACHE_DIR_FORCE_GET	= "infoc_force_get";  // 用HTTP POST方式上报数据, 貌似目前已经废了
	public static final String FILE_EXT 		= ".ich";
	public static final String FILE_EXT2 		= ".ich2"; ///<为了非强制数据改成批量上报跟以前的cache数据区分
	public static final char 	SEP_CHAR 		= '_';

	public static boolean serverbugLog = false;	//是否使用测试页面
	public static boolean debugLog = true; //设置是否在LogCat中打印调试信息
	
	private static Object mMutexForCheckFiles = new Object();

	/**
	 * 检查Infoc文件，需要每次启动app时检查一次，如果文件不存在则重新创建
	 * @param context 上下文
	 * @return 是否成功
	 */
	public static boolean checkInfocFile(Context context){
		if (context == null) {
			return false;
		}
		
		boolean bReturn = false;
		
		if (InfocCommonBase.getInstance().isServiceProcess()) {
			synchronized (mMutexForCheckFiles) {
				boolean bKFMT = false, bKCTRL = false;

				File filesDir = InfocCommonBase.getInstance().getFilesDir();
				
				if (null == filesDir) {
					return false;
				}
				Log.d("filesDir", filesDir.getAbsolutePath());

				String filesDirPath = filesDir.getAbsolutePath();
				if (null == filesDirPath) {
					return false;
				}

				String kfmtContent  = InfocCommonBase.getInstance().getKFMTContent();
				String kctrlContent = InfocCommonBase.getInstance().getKCTRLContent();
                MMM.log("======= KFMT ==============");
                MMM.log(kfmtContent);
                MMM.log("======= KCTRL ==============");
                MMM.log(kctrlContent);
                String fileNameKFMT = filesDirPath + File.separatorChar + KFMT_DAT;
				bKFMT  = TextUtils.isEmpty(kfmtContent)
						 ? getAssertFile(context, KFMT_DAT, fileNameKFMT)   
						 : syncKFMT(context, fileNameKFMT, kfmtContent);

				String fileNameKCTRL = filesDirPath + File.separatorChar + KCTRL_DAT;
				bKCTRL = TextUtils.isEmpty(kctrlContent)
						 ? getAssertFile(context, KCTRL_DAT, fileNameKCTRL) 
						 : syncCTRL(context, fileNameKCTRL, kctrlContent);

				if (bKFMT && bKCTRL)
					bReturn = true;
			}
		} else {
			try {
				bReturn = InfocCommonBase.getInstance().checkInfocFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return bReturn;
	}
	
	public static boolean write(final String text, File file) {
		return write(text,file,false);
	}
	
	/**
	 * 将字符串追加到指定文件中
	 * 
	 * @param text
	 * @param file
	 * @param append 是否追加到指定文件中
	 * @return
	 */
	public static boolean write(final String text, File file, boolean append) {
		FileWriter writer = null;
		File parent = file.getParentFile();
		if (!parent.exists()) {
			if (parent.mkdirs()) {
				return false;
			}
		}
		try {
			writer = new FileWriter(file,append);
			writer.write(text);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private static boolean syncCTRL(Context context, String target, String kctrlContent) {
		File file = new File(target);
		String MD5  = null;
		String _MD5 = "";
		boolean sync = true;
		if(!file.exists()) {
			sync = true;
		} else {
			MD5  = Md5Util.getFileMD5(file);
			_MD5 = Md5Util.getByteArrayMD5(kctrlContent.getBytes());
			sync = !_MD5.equals(MD5);
		}
		log("syncCTRL NEW="+_MD5+" FILE="+MD5 + " SYNC="+sync + " @"+target);
		if(sync) {
			file.delete();
			return write(kctrlContent, file);
		}
		
		return true;
	}
	
	public static void log(String text) {
		Log.d("mmm", text);
	}

	private static boolean syncKFMT(Context context, String fileNameKFMT, String kfmtContent) {
		File kfmt = new File(fileNameKFMT);
		String MD5  = null;
		String _MD5 = "";
		
		boolean sync = true;
		if(!kfmt.exists()) {
			sync = true;
		} else {
			MD5  = Md5Util.getFileMD5(kfmt);
			_MD5 = Md5Util.getByteArrayMD5(kfmtContent.getBytes());
			sync = !_MD5.equals(MD5);
		}
		log("syncKFMT NEW=" + _MD5+" FILE=" + MD5 + " SYNC="+sync+ " @"+fileNameKFMT);
		if(sync) {
			kfmt.delete();
			return write(kfmtContent, kfmt);
		}
		return true;
	}

	/**
	 * 取出Assert目录的文件
	 * @param context 上下文
	 * @param srcPath 源文件相对路径
 	 * @param fileName 目标文件路径
	 * @return 是否成功
	 */
	private static boolean getAssertFile(Context context, String srcPath, String fileName) {
		if (context == null) {
			return false;
		}

		boolean bReturn = false;
		InputStream is = null;
		FileOutputStream os = null;
		InputStream in = null;

		try {
			AssetManager assertManager = context.getAssets();
		
			File file = new File(fileName);
			if (file.exists()){
				if (file.isFile()) {
					//如果配置文件变化，为了覆盖安装时能正常更改文件
					//此处需要计算哈希值进行匹配
					String destMD5 = InfocCommonBase.getInstance().getFileMD5(file);
					try {
						in = assertManager.open(srcPath);
					} catch (IOException e) {
	
					}
					String srcMD5 = InfocCommonBase.getInstance().getStreamMD5(in);
					if (destMD5.equals(srcMD5))
						return true;
					else
						file.delete();
				} else {
					KFileUtil.delFolder(fileName);
				}
			}
			
			try {
				is = assertManager.open(srcPath);
				os = new FileOutputStream(fileName);
			} catch (IOException e) {

			}

			final int BUF_SIZE = 4096;
			byte[] buffer = new byte[BUF_SIZE];

			int bytes = 0;
			do {
				bytes = is.read(buffer);
				if (bytes > 0) {
					os.write(buffer, 0, bytes);
				} else {
					break;
				}
			} while (true);
			buffer = null;

			is.close();
			is = null;
			
			os.flush();
			os.close();
			os = null;

			bReturn = true;
		} catch (Exception e) {

		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bReturn;
	}
	
	public static void log2local(String log){
		
		/*if(TextUtils.isEmpty(log)){
			return;
		}
		
		if(!((Environment.MEDIA_MOUNTED).equals(Environment.getExternalStorageState()))) {
			return;
		}
		
		String cachelogPath = Env.getExternalStorageDirectoryx() + "/logs/cache_report.log";
		File logPath = new File(cachelogPath);
		if(!logPath.exists()){
			return;
		}
		
		FileLog.getIns().writeLogSDCard("cache_report.log", log);*/
	}
	
	public static String getCacheFolder(boolean isForce, boolean isGet, int serverPriority){
		
		String dirName = "";
		if(isGet){
			if (isForce) {
				dirName = CACHE_DIR_FORCE_GET;
			} else {
				dirName = CACHE_DIR_GET;
			}			
		} else {
			if (isForce) {
				dirName = CACHE_DIR_FORCE + Integer.toString(serverPriority);
			} else {
				dirName = CACHE_DIR + Integer.toString(serverPriority);
			}
		}
		
		return dirName;
	}
	
	public static File getExisted_CACHE_DIR(Context context, int priority) {
		File dir = get_CACHE_DIR(context, priority);
		return getExistsDir(dir);
	}
	
	public static File getExisted_CACHE_DIR_FORCE(Context context, int priority) {
		File dir = get_CACHE_DIR_FORCE(context, priority);
		return getExistsDir(dir);
	}
	
	
	public static File getExisted_CACHE(Context context) {
		return getExistsDir(InfocCommonBase.getInstance().getFilesDir());
	}
	
	
	public static File get_CACHE_DIR(Context context, int priority) {
		if (context == null) {
			return null;
		}

		File dir = context.getFilesDir();
		return dir == null ? null : new File(dir,CACHE_DIR + Integer.toString(priority));
	}
	
	public static File get_CACHE_DIR_FORCE(Context context, int priority) {
		if(context == null) {
			return null;
		}
		
		File dir = context.getFilesDir();
		return dir == null ? null : new File(dir,CACHE_DIR_FORCE + Integer.toString(priority));
	}
	
	/**
	 * 如果给定路径确实是一个已经存在的目录，则返回路径，否则返回空
	 * @param dir
	 * @return
	 */
	public static File getExistsDir(File dir) {
		if (dir == null) {
			return null;
		} else {
			if (dir.isDirectory() && dir.exists()) {
				return dir;
			} else {
				return null;
			}
		}
	}
	
	/**
	 * 计算日期间隔天数
	 * @return 天数
	 */
	public static long getDayDiff(long dateCache) {
		long dateNow = System.currentTimeMillis();
		long diff = (long)((dateNow - dateCache) / (1000 * 60 * 60 * 24) + 0.5);
		return diff;
	}
	
	public static String getKfmtPath() {
		return null;
	}
	
	public static byte[] encodeHeader(Context context, String dataPublic, int produceId, String kfmtPath) {
		byte[] header = null;
		try {
			header = a.c(dataPublic, produceId, kfmtPath);
		} catch (UnsatisfiedLinkError e) {
			if (!InfocCommonBase.getInstance().getSoFailedCrashReported()) {
				InfocCommonBase.getInstance().setSoFailedCrashReported(true);
				CheckMoreCrashInfo();
			}
		}
		return header;
	}
	
	public static int calcPublicHeaderLength(Context context, String dataPublic, int produceId, String kfmtPath) {
		byte[] header = encodeHeader(context, dataPublic, produceId, kfmtPath);
		return header != null ? header.length : 0;
	}

	public static void CheckMoreCrashInfo() {

		String ownCopy = InfocCommonBase.getInstance().getOwnCopySoPath();
		String sysCopy = InfocCommonBase.getInstance().getSystemCopySoPath();
		long nSizeOwn = 0;
		long nSizeSys = 0;

		File fOwn = new File(ownCopy);
		if (fOwn.exists() && fOwn.isFile()) {
			nSizeOwn = fOwn.length();
		}
		File fSys = new File(sysCopy);
		if (fSys.exists() && fSys.isFile()) {
			nSizeSys = fSys.length();
		}

		String memPath = "";
		String exception = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader("/proc/self/maps"));
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				if (line.contains(InfocCommonBase.getInstance().getLibName())) {
					memPath = line;
					break;
				}
			}
		} catch (Exception e) {
			exception = e.getMessage();
		}

		String throwInfo = "";

		throwInfo += ownCopy + ":" + nSizeOwn + "--" + sysCopy + ":" + nSizeSys + "--mem: " + memPath + "--exp:" + exception;

		throw new RuntimeException(throwInfo);
	}
	public static int getIchCount_CACHE_DIR(Context context, int prioriry) {
		File dirIch = getExisted_CACHE_DIR(context, prioriry);
		return dirIch != null ? KFileUtil.pathFileCount(dirIch.getAbsolutePath()) : 0;
	}

	public static File getExistOrCreate_CACHE_DIR_FORCE(Context context, int priority) {
		File d = get_CACHE_DIR_FORCE(context, priority);
		if (d == null) {
			return null;
		}
		
		if(d.exists()) {
			if(d.isFile()) {
				d.delete();
				d.mkdir();
			}
		} else {
			d.mkdir();
		}
		return d.exists() ? d : null;
	}

	public static File getExistOrCreate_CACHE_DIR(Context context, int priority) {
		File d = get_CACHE_DIR(context, priority);
		if (d == null) {
			return null;
		}
		
		if(d.exists()) {
			if(d.isFile()) {
				d.delete();
				d.mkdir();
			}
		} else {
			d.mkdir();
		}
		return d.exists() ? d : null;
    }

	public static File getExisted_CACHE_DIR_OLD(Context context, int nServerPriority) {
		File dir = new File(context.getCacheDir() + File.separator + getCacheFolder(false, false, nServerPriority));
		return getExistsDir(dir);
	}

	public static File getExisted_CACHE_DIR_FORCE_OLD(Context context, int nServerPriority) {
		File dir = new File(context.getCacheDir() + File.separator + getCacheFolder(true, false, nServerPriority));
		return getExistsDir(dir);
	}
}
