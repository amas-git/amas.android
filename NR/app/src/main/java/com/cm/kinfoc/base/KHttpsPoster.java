package com.cm.kinfoc.base;

import android.util.Log;

import com.cm.kinfoc.HttpResult;
import com.cm.kinfoc.IHttpSender;
import com.cm.kinfoc.KHttpData;
import com.cm.kinfoc.KHttpResultListener;
import com.cm.kinfoc.KInfoControl;
import com.cm.kinfoc.KInfocUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

/**
 * 简易数据Post类
 * @author singun
 *
 */
public class KHttpsPoster implements IHttpSender {
	
	private ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(1, 5,
            60*1000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());;

            public class NullHostNameVerifier implements HostnameVerifier {

                public boolean verify(String hostname, SSLSession session) {
                    InfocLog.getLogInstance().log( "Approving certificate for " + hostname);
                    return true;
                }
            }
            
	/**
	 * post
	 * @param data		数据
	 * @param rul		地址
	 * @param onResult	结果回调
	 * @throws  
	 */
	public void send(KHttpData data, String url, OnResultListener onResult){
		
		if(data.getServerPriority() <= KInfoControl.emKInfoPriority_Unknow || data.getServerPriority() >= KInfoControl.emKInfoPriority_End){
			throw new RuntimeException("server priority is out of range");
		}
		
		if (KInfocUtil.debugLog)
			Log.d(KInfocUtil.LOG_TAG, "Post data on " + url);
		
		HttpsPostConnectionThread postthread = new HttpsPostConnectionThread(
				data, url, onResult);

		if(mThreadPool == null) {
			postthread.start();
		} else {
			mThreadPool.submit(postthread);
		}
	}
	
	//post线程
	private class HttpsPostConnectionThread extends Thread {
		private final String mUrl;
		private final KHttpData			mData;
		private final OnResultListener	mOnResult;
		
		private HttpsPostConnectionThread(KHttpData data, String url,
				OnResultListener onResult) {
			this.mUrl		= url;
			this.mData		= data;
			this.mOnResult	= onResult;
		}
		
		@Override
		public void run() {
			httpsPost(mData, mUrl, mOnResult);			
		}
	}
	
	
//	private static X509TrustManager xtm = new X509TrustManager() {
//		public void checkClientTrusted(X509Certificate[] chain, String authType) {
//		}
//
//		public void checkServerTrusted(X509Certificate[] chain, String authType) {
//		}
//
//		public X509Certificate[] getAcceptedIssuers() {
//			return null;
//		}
//	};
//	private static X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
	public boolean httpsPost(KHttpData data, String strUrl, OnResultListener onResult){
		
		String result		= null;
		HttpResult	postRes		= null;
		boolean		bresult		= false;
		
		HttpURLConnection conn 				= null;
		InputStream inputStream 		= null;
		BufferedReader br					= null;
		
		try {
			boolean bSSLException = InfocCommonBase.getInstance().getSSLException();
			if(bSSLException){
				strUrl = strUrl.replaceFirst("https", "http");
			}
			byte[] entity = data.getData();
			
			URL url = new URL(strUrl);
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(50 * 1000);
			conn.setReadTimeout(50 * 1000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);// 允许输出数据
//			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(entity);
			outStream.flush();
			outStream.close();

			inputStream	= conn.getInputStream();
			br		= new BufferedReader(new InputStreamReader(inputStream));
			String res		= "";
			String line	= "";
			int cnt = 0;
			while((line = br.readLine()) != null) {
				res += line;
				res += "\r\n";
				++cnt;
				//最多读4行，infoc现在最多返回3行
				if (cnt > 4) {
					break;
				}
			}
			result = res;
			postRes = HttpResult.parserString(result); 
			if(postRes != null && postRes.nResult == 1) {
				bresult = true;
			}
			if(KInfocUtil.debugLog)InfocLog.getLogInstance().log("https upload infoc data " + result);
			InfocCommonBase.getInstance().setSSLException(false);
		} catch(SSLException e){
			InfocCommonBase.getInstance().setSSLException(true);
			if(KInfocUtil.debugLog) Log.e(KInfocUtil.LOG_TAG, Log.getStackTraceString(e));
		}catch (FileNotFoundException e){
			e.printStackTrace();
			if(KInfocUtil.debugLog) Log.e(KInfocUtil.LOG_TAG, Log.getStackTraceString(e));
		} 
		catch (IOException e) {
			e.printStackTrace();
			if(KInfocUtil.debugLog) Log.e(KInfocUtil.LOG_TAG, Log.getStackTraceString(e));
		} catch (Exception e) {
			e.printStackTrace();
			if(KInfocUtil.debugLog) Log.e(KInfocUtil.LOG_TAG, Log.getStackTraceString(e));
		}catch (Error e) {
			e.printStackTrace();
			if(KInfocUtil.debugLog) Log.e(KInfocUtil.LOG_TAG, Log.getStackTraceString(e));
		}finally{
			try{
				if(inputStream != null){
					inputStream.close();
				}
				if(br != null){
					br.close();
				}
				if(null != conn){
					conn.disconnect();
					conn = null;
				}
			}catch(Exception e){
				e.printStackTrace();
				if(KInfocUtil.debugLog) Log.e(KInfocUtil.LOG_TAG, Log.getStackTraceString(e));
			}
		}
		
		KHttpResultListener listener = data.getHttpListener();
		if(listener != null) {
			listener.onHttpResult(postRes);
		}
		
		if (onResult != null) {
			if (bresult){
				onResult.onSuccess(postRes.nServeTime, data);
			} else {
				onResult.onFail(data);
			}
		}
		
		return bresult;
	}
}