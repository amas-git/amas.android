package com.cmcm.onews.util.push.http;//package com.cmcm.onews.util.push.http;
//
//import android.net.http.AndroidHttpClient;
//
//import com.cmcm.onews.util.push.comm.StringUtil;
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpHead;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//public class HttpUtility {
//	public static final int connectTimeOut = 20 * 1000;
//	public static final int socketTimeOut = 20 * 1000;
//	public static final int socketBufferSize = 8192;
////	public static final String userAgent = "k8_android";
//
//	/**
//	 * 简单的获取网络数据，通过给定的url
//	 * @param url
//	 * @return
//	 * @throws IOException
//	 * @throws ClientProtocolException
//	 */
//	public static String httpGetString(String url) throws ClientProtocolException, IOException {
//		return httpGetString(url,null);
//	}
//
//	/**
//	 * 简单的获取网络数据流，通过给定的url
//	 * @param url
//	 * @return
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static InputStream httpGetInputStream(String url) throws ClientProtocolException, IOException {
//		return httpGetInputStream(url,null);
//	}
//
//	/**
//	 * post表单提交获取数据
//	 * @param url
//	 * @param postData
//	 * @param dataEncoding
//	 * @return
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static String httpPostString(String url, List<NameValuePair> postData, String dataEncoding) throws ClientProtocolException, IOException {
//		return httpPostString(url,null,postData, dataEncoding);
//	}
//
//	/**
//	 * post表单提交获取数据
//	 * @param url
//	 * @param postData
//	 * @param dataEncoding
//	 * @return
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static HttpResponse httpPostInputStream(String url,List<NameValuePair> postData,  String dataEncoding) throws ClientProtocolException, IOException {
//		return httpPostHttpResponse(url, null, postData, dataEncoding);
//	}
//
//	/**
//	 * 简单的获取网络数据，通过给定的url
//	 * @param url
//	 * @param heads
//	 * @return 返回字符串
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static String httpGetString(String url,Map<String, String> heads) throws ClientProtocolException, IOException {
//		DefaultHttpClient httpClient = newHttpClient();
//		HttpGet getMethod = new HttpGet(url);
//		if(heads != null) {
//			for(Entry<String, String> entry : heads.entrySet()) {
//				getMethod.addHeader(entry.getKey(), entry.getValue());
//			}
//		}
//		HttpResponse resp = httpClient.execute(getMethod);
//
//		int status = resp.getStatusLine().getStatusCode();
//		if(status != HttpStatus.SC_OK) {
//			return "";
//		}
//		HttpEntity entity = resp.getEntity();
//		InputStream input = AndroidHttpClient.getUngzippedContent(entity);
//		String charset = getContentCharset(resp);
//		String result = StringUtil.inputStream2String(input, charset);
//		return result;
//	}
//
//	/**
//	 * 简单的获取网络数据流，通过给定的url
//	 * @param url
//	 * @param heads
//	 * @return
//	 * @throws IOException
//	 * @throws ClientProtocolException
//	 */
//	public static InputStream httpGetInputStream(String url, Map<String,String> heads) throws ClientProtocolException, IOException {
//		DefaultHttpClient httpClient = newHttpClient();
//		HttpGet getMethod = new HttpGet(url);
//		if(heads != null) {
//			for(Entry<String,String> entry : heads.entrySet()) {
//				getMethod.addHeader(entry.getKey(), entry.getValue());
//			}
//		}
//		HttpResponse resp = httpClient.execute(getMethod);
//
//		int status = resp.getStatusLine().getStatusCode();
//		if(status != HttpStatus.SC_OK) {
//			throw new IOException("http code = " + status);
//		}
//		HttpEntity entity = resp.getEntity();
//		InputStream stream = AndroidHttpClient.getUngzippedContent(entity);
//		return stream;
//	}
//
//	/**
//	 * 发送数据到服务端，通过form提交
//	 * @param url     执行的url
//	 * @param heads   包含的头
//	 * @param postData 发送的数据
//	 * @param dataEncoding 数据的编码 null or “” 默认的是utf-8
//	 * @return 返回结果
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static String httpPostString(String url,Map<String,String> heads, List<NameValuePair> postData, String dataEncoding) throws ClientProtocolException, IOException {
//		DefaultHttpClient httpClient = newHttpClient();
//		HttpPost postMethod = new HttpPost(url);
//		if(heads != null) {
//			for(Entry<String,String> entry : heads.entrySet()) {
//				postMethod.addHeader(entry.getKey(),entry.getValue());
//			}
//		}
//		HttpEntity httpEntity = null;
//		if(postData != null) {
//			if(dataEncoding == null || dataEncoding.length() == 0) {
//				dataEncoding = "utf-8";
//			}
//			httpEntity = new MyUrlEncodedFormEntity(postData,dataEncoding);
//		}
//		if(httpEntity != null) {
//			postMethod.setEntity(httpEntity);
//		}
//		HttpResponse resp = httpClient.execute(postMethod);
//
//		int status = resp.getStatusLine().getStatusCode();
//		if(status != HttpStatus.SC_OK) {
//			return "";
//		}
//		HttpEntity entity = resp.getEntity();
//		InputStream input = AndroidHttpClient.getUngzippedContent(entity);
//		String charset = getContentCharset(resp);
//		String result = StringUtil.inputStream2String(input, charset);
//		return result;
//	}
//
//	/**
//	 * 发送数据到服务端，通过form提交
//	 * @param url
//	 * @param heads
//	 * @param postData
//	 * @param dataEncoding
//	 * @return
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static HttpResponse httpPostHttpResponse(String url, Map<String,String> heads, List<NameValuePair> postData, String dataEncoding) throws ClientProtocolException, IOException {
//		DefaultHttpClient httpClient = newHttpClient();
//		HttpPost postMethod = new HttpPost(url);
//		if(heads != null) {
//			for(Entry<String,String> entry : heads.entrySet()) {
//				postMethod.addHeader(entry.getKey(),entry.getValue());
//			}
//		}
//		HttpEntity httpEntity = null;
//		if(postData != null) {
//			if(dataEncoding == null || dataEncoding.length() == 0) {
//				dataEncoding = "utf-8";
//			}
//			httpEntity = new MyUrlEncodedFormEntity(postData,dataEncoding);
//		}
//		if(httpEntity != null) {
//			postMethod.setEntity(httpEntity);
//		}
//		HttpResponse resp = httpClient.execute(postMethod);
//
//		int status = resp.getStatusLine().getStatusCode();
//		if(status != HttpStatus.SC_OK) {
//			throw new IOException("http code = " + status);
//		}
//		return resp;
//	};
//
//	/**
//	 * post方式获取数据，返回HttpResponse未加状态判断
//	 * @param url
//	 * @param heads
//	 * @param postData
//	 * @param dataEncoding
//	 * @return
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static HttpResponse httpPostHttpResponseNoJude(String url,Map<String,String> heads ,List<NameValuePair> postData,String dataEncoding) throws ClientProtocolException, IOException {
//		DefaultHttpClient httpClient = newHttpClient();
//		HttpPost postMethod = new HttpPost(url);
//		if(heads != null) {
//			for(Entry<String,String> entry : heads.entrySet()) {
//				postMethod.addHeader(entry.getKey(),entry.getValue());
//			}
//		}
//		HttpEntity httpEntity = null;
//		if(postData != null) {
//			if(dataEncoding == null || dataEncoding.length() == 0) {
//				dataEncoding = "utf-8";
//			}
//			httpEntity = new MyUrlEncodedFormEntity(postData,dataEncoding);
//		}
//		if(httpEntity != null) {
//			postMethod.setEntity(httpEntity);
//		}
//		HttpResponse resp = httpClient.execute(postMethod);
//
//		return resp;
//	};
//
//	/**
//	 * get方式获取数据，返回HttpResponse未加状态判断
//	 * @param url
//	 * @param heads
//	 * @return
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static HttpResponse httpGetHttpResponseNoJude(String url, Map<String,String> heads) throws ClientProtocolException, IOException {
//		DefaultHttpClient httpClient = newHttpClient();
//		HttpGet getMethod = new HttpGet(url);
//		if(heads != null) {
//			for(Entry<String,String> entry : heads.entrySet()) {
//				getMethod.addHeader(entry.getKey(), entry.getValue());
//			}
//		}
//		HttpResponse resp = httpClient.execute(getMethod);
//
//		return resp;
//	}
//
//
//	/**
//	 * 发送数据到服务端，通过form提交
//	 * @param url     执行url
//	 * @param heads   包含的头
//	 * @param postData 发送的数据
//	 * @param dataEncoding 数据编码， null or “” 默认是utf-8
//	 * @return 返回结果流，默认都是utf8编码
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static InputStream httpPostInputStream(String url,Map<String,String> heads ,List<NameValuePair> postData,String dataEncoding) throws ClientProtocolException, IOException {
//		DefaultHttpClient httpClient = newHttpClient();
//		HttpPost postMethod = new HttpPost(url);
//		if(heads != null) {
//			for(Entry<String,String> entry : heads.entrySet()) {
//				postMethod.addHeader(entry.getKey(),entry.getValue());
//			}
//		}
//		HttpEntity httpEntity = null;
//		if(postData != null) {
//			if(dataEncoding == null || dataEncoding.length() == 0) {
//				dataEncoding = "utf-8";
//			}
//			httpEntity = new MyUrlEncodedFormEntity(postData,dataEncoding);
//		}
//		if(httpEntity != null) {
//			postMethod.setEntity(httpEntity);
//		}
//		HttpResponse resp = httpClient.execute(postMethod);
//
//		int status = resp.getStatusLine().getStatusCode();
//		if(status != HttpStatus.SC_OK) {
//			throw new IOException("http code = " + status);
//		}
//		HttpEntity entity = resp.getEntity();
//		InputStream stream = AndroidHttpClient.getUngzippedContent(entity);
//		return stream;
//	};
//
//
//	/**
//	 * 通过head方式提交数据
//	 * @param url 执行url
//	 * @param heads head数据
//	 * @return 返回执行状态 200 302 404等等
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public static int httpHead(String url,Map<String,String> heads) throws ClientProtocolException, IOException {
//		DefaultHttpClient httpClient = newHttpClient();
//		HttpHead headMethod = new HttpHead(url);
//		if(heads != null) {
//			for(Entry<String, String> entry : heads.entrySet()) {
//				headMethod.addHeader(entry.getKey(),entry.getValue());
//			}
//		}
//		HttpResponse resp = httpClient.execute(headMethod);
//		int status = resp.getStatusLine().getStatusCode();
//		return status;
//	}
//
//	/**
//	 * 获取urlencode字符串，encoder默认为utf-8
//	 * @param str
//	 * @param encoder
//	 * @return
//	 * @throws UnsupportedEncodingException
//	 */
//	public static String getEncodeString(String str,String encoder) throws UnsupportedEncodingException {
//		if(encoder == null || encoder.length() == 0) {
//			encoder = "utf-8";
//		}
//		return URLEncoder.encode(str, encoder);
//	}
//
//	public static String params2String(List<NameValuePair> params) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < params.size(); i++) {
//			SimpleNameValuePair p = (SimpleNameValuePair)params.get(i);
//			if (i == 0) {
//				sb.append(p.toString());
//			} else {
//				sb.append("&" + p.toString());
//			}
//		}
//		return sb.toString();
//	}
//
//	/**
//	 * 获取数据的字符集
//	 * @param
//	 * @return 返回字符集
//	 */
//	public static String getContentCharset(HttpResponse resp) {
//		String charset = "ISO-8859-1";
//		Header header = resp.getEntity().getContentType();
//		if(header != null) {
//			String s = header.getValue();
//			if(StringUtil.matcher(s, "(charset)\\s?=\\s?(utf-?8)")) {
//				charset = "utf-8";
//			} else if(StringUtil.matcher(s, "(charset)\\s?=\\s?(gbk)")) {
//				charset = "gbk";
//			} else if(StringUtil.matcher(s, "(charset)\\s?=\\s?(gb2312)")) {
//				charset = "gb2312";
//			}
//		}
//		return charset;
//	}
//
//	private static DefaultHttpClient newHttpClient() {
//		HttpParams httpParameters = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(httpParameters, connectTimeOut);
//		HttpConnectionParams.setSoTimeout(httpParameters, socketTimeOut);
//		HttpConnectionParams.setSocketBufferSize(httpParameters, socketBufferSize);
//		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
//		return httpClient;
//	}
//}
//
