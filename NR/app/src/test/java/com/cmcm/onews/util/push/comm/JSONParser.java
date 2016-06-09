package com.cmcm.onews.util.push.comm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class JSONParser {
	private static final String TAG = "JSONParser";
	/**
	 * 将 JSON String 转化为 JSON 对象
	 * @param json json数据
	 * @return 
	 */
	public static JSONObject parseFromString(String json) {
		if (json == null || json.length() == 0) return null;
		
		try {
			JSONTokener jsontokener = new JSONTokener(json);
			JSONObject obj = (JSONObject) jsontokener.nextValue();
			return obj;
		} catch (JSONException je) {
		} catch (NullPointerException npe) {
		} catch (ClassCastException cce) {
		} catch (Exception e) {
		}
		return null;
	}
	
	public static JSONObject parseFromFile(File file) {
	    String text = readText(file);
            
        return parseFromString(text);
	}
	
	public static JSONArray parseArrayFromString(String json) {
		if (json == null || json.length() == 0) return null;
		
		try {
			return new JSONArray(json);
		} catch (JSONException je) {
//			KLog.w(TAG, String.format("parse json string<%s> JSONException.", json), je);
		} catch (NullPointerException npe) {
//			KLog.w(TAG, String.format("parse json string<%s> NullPointerException.", json), npe);
		} catch (ClassCastException cce) {
//			KLog.w(TAG, String.format("parse json string<%s> ClassCastException.", json), cce);
		} catch (Exception e) {
//			KLog.w(TAG, String.format("parse json string <%s> error.", json), e);
		}
		return null;
	}
	
	public static JSONArray parseArrayFromFile(File file) {
	    String text = readText(file);
	    
	    return parseArrayFromString(text);
	}
	
	private static String readText(File file) {
	    int readLen = 0;
        FileInputStream fis = null;
        byte[] buffer = new byte[8 * 1024];    // 8K
        StringBuffer sb = new StringBuffer();
        try {
            fis = new FileInputStream(file);
            while (true) {
                readLen = fis.read(buffer);
                if (readLen == -1) break;
                
                sb.append(new String(buffer, 0, readLen));
            }
        } catch (FileNotFoundException e) {
//            KLog.w(TAG, "FileNotFoundException : " + file.getPath(), e);
        } catch (NullPointerException npe) {
//            KLog.w(TAG, "NullPointerException : " + file.getPath(), npe);
        } catch (IOException e) {
//            KLog.w(TAG, "IOException when read " + file.getPath(), e);
        } catch (Exception e) {
//            KLog.w(TAG, "Exception when read " + file.getPath(), e);
        } finally {
            if (fis != null) try { fis.close(); fis = null; } catch (IOException e) {}
        }  
        
        return sb.toString();
	}
}
