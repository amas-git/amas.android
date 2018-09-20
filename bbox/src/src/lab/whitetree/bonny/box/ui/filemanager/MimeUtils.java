package lab.whitetree.bonny.box.ui.filemanager;

import java.io.File;
import java.util.HashMap;

import lab.whitetree.bonny.box.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class MimeUtils {
	static HashMap<String, Drawable> mIconSet = new HashMap<String, Drawable>();
	public static final String MIME_APPLICATION_X_DIRECTORY = "application/x-directory";
	public static final String MIME_TEXT_PLAIN              = "text/plain";
	public static final String MIME_APPLICATION_X_EMPTY     = "application/x-empty";
	public static final String MIME_TEXT_HTML               = "text/html";
	public static final String MIME_APPLICATION_APK             = "application/vnd.android.package-archive";
	
	public static final MimeInfo DEFAULT_MIME_TYPE  = new MimeInfo("application/x-empty", R.drawable.mime_type_default);
	public static final MimeInfo X_DIR              = new MimeInfo("application/x-empty", R.drawable.folder);
	public static HashMap<String,MimeInfo> sMimeMap = new HashMap<String, MimeInfo>();
	
	static  {
		sMimeMap.put("mp3",   new MimeInfo("audio/mpeg",        R.drawable.mime_type_mp3));
		sMimeMap.put("mp4",   new MimeInfo("video/mpeg",        R.drawable.mime_type_mp3));
		sMimeMap.put("avi",   new MimeInfo("video/avi",         R.drawable.mime_type_video));
		
		sMimeMap.put("text",  new MimeInfo("text/plain",        R.drawable.mime_type_txt));
		sMimeMap.put("txt",   new MimeInfo("text/plain",        R.drawable.mime_type_txt));
		sMimeMap.put("html",  new MimeInfo("text/html",         R.drawable.mime_type_html));
		sMimeMap.put("htm",   new MimeInfo("text/html",         R.drawable.mime_type_html));
		sMimeMap.put("xhtml", new MimeInfo("text/html",         R.drawable.mime_type_html));
	
		sMimeMap.put("pdf",   new MimeInfo("application/pdf",   R.drawable.mime_type_pdf));
		sMimeMap.put("doc",   new MimeInfo("application/msword",R.drawable.mime_type_txt));
		
		sMimeMap.put("jpg",   new MimeInfo("image/jpeg",        R.drawable.mime_type_image));
		sMimeMap.put("jpeg",  new MimeInfo("image/jpeg",        R.drawable.mime_type_image));
		sMimeMap.put("png",   new MimeInfo("image/png",         R.drawable.mime_type_image));
		sMimeMap.put("gif",   new MimeInfo("image/gif",         R.drawable.mime_type_image));
		sMimeMap.put("bmp",   new MimeInfo("imap/bmp",          R.drawable.mime_type_image));
		sMimeMap.put("apk",   new MimeInfo(MIME_APPLICATION_APK,    R.drawable.mime_type_apk));
	}
	
	
	public static Drawable getIcon(Context context, String mimeType) {
		if (TextUtils.isEmpty(mimeType))
			return null;

		Drawable icon = mIconSet.get(mimeType);
		if (icon == null) {
			if(mimeType.equals(MIME_APPLICATION_X_EMPTY)) {
				icon = context.getResources().getDrawable(R.drawable.mime_type_default);
				mIconSet.put(MIME_APPLICATION_X_EMPTY, icon);
			} else if(mimeType.equals(MIME_APPLICATION_X_DIRECTORY)) {
				icon = context.getResources().getDrawable(R.drawable.folder);
				mIconSet.put(MIME_APPLICATION_X_DIRECTORY, icon);
			} else if (mimeType.startsWith("text/html")) {
				
			} else if (mimeType.startsWith("audio/")) {
				
			} else if (mimeType.startsWith("video/")) {
				
			} else if (mimeType.equals(MIME_APPLICATION_APK)) {
				
			}
		} else {
			return icon;
		}
		
		return context.getResources().getDrawable(R.drawable.mime_type_default);
	}
	
//	/**
//	 * TODO: support customize via xml file
//	 * @param name
//	 * @return
//	 */
//	public static String getMimeType(String name) {
//		String mediaType   = "application";
//		String mediaSubType= "x-empty";
//		String surffix      = getSurffix(name);
//		return sMimeMap.get(surffix);
//	}
	
	
	

	
	
	
	public static MimeInfo getMimeInfo(File file) {
		if(file.isDirectory()) {
			return X_DIR;
		}
		
		String surffix = getSurffix(file.getName());
		MimeInfo m = sMimeMap.get(surffix.toLowerCase());
		return m == null ? DEFAULT_MIME_TYPE : m;
	}
	

	
	/**
	 * Get surfix of the specify file name
	 * eg. getSurffix("readme.txt") -> "txt"
	 * @param name specify file name or absolute path
	 * @return suffix or ""
	 */
	public static String getSurffix(String name) {
		int x = name.lastIndexOf('.');
		if(x > 0) {
			return name.substring(x+1); // TODO: may index out of bound
		}
		return "";
	}
	
	/**
	 * @param bitMap
	 * @param x
	 * @param y
	 * @param newWidth
	 * @param newHeight
	 * @param matrix
	 * @param isScale
	 * @return
	 */
	public static Bitmap fitSizePic(File f) {
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		if (f.length() < 20480) { // 0-20k
			opts.inSampleSize = 1;
		} else if (f.length() < 51200) { // 20-50k
			opts.inSampleSize = 2;
		} else if (f.length() < 307200) { // 50-300k
			opts.inSampleSize = 4;
		} else if (f.length() < 819200) { // 300-800k
			opts.inSampleSize = 6;
		} else if (f.length() < 1048576) { // 800-1024k
			opts.inSampleSize = 8;
		} else {
			opts.inSampleSize = 10;
		}
		resizeBmp = BitmapFactory.decodeFile(f.getPath(), opts);
		return resizeBmp;
	}

	/**
	 * @param f
	 * @return
	 */
	public static String fileSizeMsg(File f) {
		int sub_index = 0;
		String show = "";
		if (f.isFile()) {
			long length = f.length();
			if (length >= 1073741824) {
				sub_index = (String.valueOf((float) length / 1073741824))
						.indexOf(".");
				show = ((float) length / 1073741824 + "000").substring(0,
						sub_index + 3) + "GB";
			} else if (length >= 1048576) {
				sub_index = (String.valueOf((float) length / 1048576))
						.indexOf(".");
				show = ((float) length / 1048576 + "000").substring(0,
						sub_index + 3) + "MB";
			} else if (length >= 1024) {
				sub_index = (String.valueOf((float) length / 1024))
						.indexOf(".");
				show = ((float) length / 1024 + "000").substring(0,
						sub_index + 3) + "KB";
			} else if (length < 1024) {
				show = String.valueOf(length) + "B";
			}
		}
		return show;
	}

	/**
	 * @param newName
	 * @return
	 */
	public static boolean checkDirPath(String newName) {
		boolean ret = false;
		if (newName.indexOf("\\") == -1) {
			ret = true;
		}
		return ret;
	}

	/**
	 * @param newName
	 * @return
	 */
	public static boolean checkFilePath(String newName) {
		boolean ret = false;
		if (newName.indexOf("\\") == -1) {
			ret = true;
		}
		return ret;
	}


}
