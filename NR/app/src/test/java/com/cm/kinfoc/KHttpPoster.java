package com.cm.kinfoc;

import android.util.Log;

import com.cm.kinfoc.base.InfocLog;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * 简易数据Post类
 * @author singun
 *
 */
public class KHttpPoster implements IHttpSender {
	
	private ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(1, 5,
            60*1000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

	/**
	 * post
	 * @param data		数据
	 * @param url		地址
	 * @param onResult	结果回调
	 * @throws  
	 */
	public void send(KHttpData data, String url, OnResultListener onResult){
		
		if(data.getServerPriority() <= KInfoControl.emKInfoPriority_Unknow || data.getServerPriority() >= KInfoControl.emKInfoPriority_End){
			throw new RuntimeException("server priority is out of range");
		}
		
		if (KInfocUtil.debugLog)
			Log.d(KInfocUtil.LOG_TAG, "Post data on " + url);
		
		HttpPostConnectionThread postthread = new HttpPostConnectionThread(
				data, url, onResult);

		if(mThreadPool == null) {
			postthread.start();
		} else {
			mThreadPool.submit(postthread);
		}
	}
	
	//post线程
	private class HttpPostConnectionThread extends Thread {
		private final String mUrl;
		private final KHttpData			mData;
		private final OnResultListener	mOnResult;
		
		private HttpPostConnectionThread(KHttpData data, String url,
				OnResultListener onResult) {
			this.mUrl		= url;
			this.mData		= data;
			this.mOnResult	= onResult;
		}
		
		@Override
		public void run() {
			httpPost(mData, mUrl, mOnResult);			
		}
	}
	
	public boolean httpPost(KHttpData data, String url, OnResultListener onResult){
//		HttpParams httpParameters = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(httpParameters, KInfocCommon.CONNECTION_TIMEOUT);
//		HttpClient	httpclient	= new DefaultHttpClient(httpParameters);
//		HttpPost	httppost	= new HttpPost(url);
//		String result		= null;
//		HttpResult	postRes		= null;
//		boolean		bresult		= false;
//
//		try {
//			httppost.setEntity(new ByteArrayEntity(data.getData()));
//			HttpResponse	response	= httpclient.execute(httppost);
//			HttpEntity		httpEntity	= response.getEntity();
//			InputStream inputStream	= httpEntity.getContent();
//			BufferedReader br		= new BufferedReader(new InputStreamReader(inputStream));
//			String res		= "";
//			String line	= "";
//			int cnt = 0;
//			while((line = br.readLine()) != null) {
//				res += line;
//				res += "\r\n";
//				++cnt;
//				//最多读4行，infoc现在最多返回3行
//				if (cnt > 4) {
//					break;
//				}
//			}
//			result = res;
//			postRes = HttpResult.parserString(result);
//			if(postRes != null && postRes.nResult == 1) {
//                if(Logger.Debug) Log.w(KInfocUtil.LOG_TAG, "bresult==>" + bresult);
//				bresult = true;
//			}
//		} catch (Exception e) {
//            if(Logger.Debug) Log.w(KInfocUtil.LOG_TAG, "url==>" + url);
//            Log.w(KInfocUtil.LOG_TAG, Log.getStackTraceString(e));
//		} catch (Error e) {
//            if(Logger.Debug) Log.w("err", "url==>" + url);
//            Log.w(KInfocUtil.LOG_TAG, Log.getStackTraceString(e));
//		}
//
//		KHttpResultListener listener = data.getHttpListener();
//		if(listener != null) {
//			listener.onHttpResult(postRes);
//		}
//
//		if (onResult != null) {
//			if (bresult){
//				onResult.onSuccess(postRes.nServeTime, data);
//			} else {
//				onResult.onFail(data);
//			}
//		}
//
//		return bresult;
        return false;
	}

//	private static SSLSocketFactory getTrustAll(){
//		try {
//			TrustManager[] tm = { new X509TrustManager(){
//				@Override
//				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//				}
//
//				@Override
//				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//				}
//
//				@Override
//				public X509Certificate[] getAcceptedIssuers() {
//					return new X509Certificate[0];
//				}
//			} };
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init(null, tm, new java.security.SecureRandom());
//			// 从上述SSLContext对象中得到SSLSocketFactory对象
//			SSLSocketFactory ssf = sslContext.getSocketFactory();
//			return ssf;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	public static String postHttps(final String actionUrl, final Map<String, String> params, final FormFile[] files) {
		try {
			String enterNewline = "\r\n";
			String fix = "--";
			String boundary = "---------7d4a6d158c9";
			String MULTIPART_FORM_DATA = "multipart/form-data";

			URL url = new URL(actionUrl);

			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
//			con.setSSLSocketFactory(getTrustAll());
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(10000);
			con.setReadTimeout(20000);
			con.setRequestMethod("POST");
			con.setRequestProperty("connection", "Keep-Alive");
			// con.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			con.setRequestProperty("Charsert", "UTF-8");
			con.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + boundary);

			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			Set<String> keySet = params.keySet();
			Iterator<String> it = keySet.iterator();

			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);
				ds.writeBytes(fix + boundary + enterNewline);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + enterNewline);
				ds.writeBytes(enterNewline);
				ds.write(value.getBytes("UTF-8"));
				// ds.writeBytes(value);//如果有中文乱码，保存改用上面的ds.writeBytes(enterNewline);那句代码
				ds.writeBytes(enterNewline);
			}

			if (files != null) {
				for (FormFile file : files) {
					if (null != file) {
						ds.writeBytes(fix + boundary + enterNewline);
						ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + file.getFormName() + "\"" + "; filename=\""
								+ file.getFileName() + "\"" + enterNewline);
						ds.writeBytes("Content-Type: application/octet-stream");
						ds.writeBytes(enterNewline);
						ds.writeBytes(enterNewline);
						ds.write(file.getData());
						ds.writeBytes(enterNewline);
					}
				}
			}

			ds.writeBytes(fix + boundary + fix + enterNewline);
			ds.flush();

			InputStream is = con.getInputStream();
			ByteArrayOutputStream outP = new ByteArrayOutputStream();
			int read = 0;
			byte bytes[] = new byte[1024];
			while ((read = is.read(bytes, 0, 1024)) != -1) {
				outP.write(bytes, 0, read);
			}
			ds.close();
			is.close();
			return outP.toString();
		} catch (Exception e) {
			InfocLog.getLogInstance().log(e.getLocalizedMessage());
			e.printStackTrace();
			return "";
		}
	}


	public static String post(final String actionUrl, final Map<String, String> params, final FormFile[] files) {
		try {
			String enterNewline = "\r\n";
			String fix = "--";
			String boundary = "---------7d4a6d158c9";
			String MULTIPART_FORM_DATA = "multipart/form-data";

			URL url = new URL(actionUrl);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(10000);
			con.setReadTimeout(20000);
			con.setRequestMethod("POST");
			con.setRequestProperty("connection", "Keep-Alive");
			// con.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			con.setRequestProperty("Charsert", "UTF-8");
			con.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + boundary);

			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			Set<String> keySet = params.keySet();
			Iterator<String> it = keySet.iterator();

			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);
				ds.writeBytes(fix + boundary + enterNewline);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + enterNewline);
				ds.writeBytes(enterNewline);
				ds.write(value.getBytes("UTF-8"));
				// ds.writeBytes(value);//如果有中文乱码，保存改用上面的ds.writeBytes(enterNewline);那句代码
				ds.writeBytes(enterNewline);
			}

			if (files != null) {
				for (FormFile file : files) {
					if (null != file) {
						ds.writeBytes(fix + boundary + enterNewline);
						ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + file.getFormName() + "\"" + "; filename=\""
						                + file.getFileName() + "\"" + enterNewline);
						ds.writeBytes("Content-Type: application/octet-stream");
						ds.writeBytes(enterNewline);
						ds.writeBytes(enterNewline);
						ds.write(file.getData());
						ds.writeBytes(enterNewline);
					}
				}
			}

			ds.writeBytes(fix + boundary + fix + enterNewline);
			ds.flush();

			InputStream is = con.getInputStream();
			ByteArrayOutputStream outP = new ByteArrayOutputStream();
			int read = 0;
			byte bytes[] = new byte[1024];
			while ((read = is.read(bytes, 0, 1024)) != -1) {
				outP.write(bytes, 0, read);
			}
			ds.close();
			is.close();
			return outP.toString();
		} catch (Exception e) {
			InfocLog.getLogInstance().log(e.getLocalizedMessage());
			e.printStackTrace();
			return "";
		}
	}
}
