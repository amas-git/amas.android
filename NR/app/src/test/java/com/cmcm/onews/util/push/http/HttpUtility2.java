package com.cmcm.onews.util.push.http;//package com.cmcm.onews.util.push.http;
//
//import android.net.http.AndroidHttpClient;
//import android.text.TextUtils;
//import android.util.Log;
//import android.webkit.CookieManager;
//import android.webkit.CookieSyncManager;
//
//
//import com.cmcm.onews.util.push.comm.StringUtil;
//
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.HttpVersion;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.conn.scheme.PlainSocketFactory;
//import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.scheme.SchemeRegistry;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpParams;
//import org.apache.http.params.HttpProtocolParams;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.ExecutionContext;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.protocol.HttpContext;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.Socket;
//import java.net.URL;
//import java.net.URLDecoder;
//import java.net.UnknownHostException;
//import java.security.KeyManagementException;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//public class HttpUtility2 {
//    final static String TAG = "HttpUtility2";
//
//    public interface IObserver {
//        public void onHttpUtility2Result(String headers, String html);
//    }
//
//    public interface ITicketObserver {
//        public void onTicketHttpResult(HttpResponse response, String urlAfterRedirects);
//    }
//
//    private String mDefaultUserAgent;
//
//    public HttpUtility2(String defaultUa) {
//        mDefaultUserAgent = defaultUa;
//    }
//
//    public HttpClient getHttpsClient() {
//        try {
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(null, null);
//
//            SSLSocketFactory sf = getSSLSocketFactory();
//            HttpParams params = new BasicHttpParams();
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//            registry.register(new Scheme("https", sf, 443));
//
//            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//
//            return new DefaultHttpClient(ccm, params);
//        } catch (Exception e) {
//            return new DefaultHttpClient();
//        }
//    }
//
//    public void ticketHttpRequest(String propertiesJson, ITicketObserver observer) {
//        DefaultHttpClient client = null;
//        HttpResponse response = null;
//        HttpContext httpContext = new BasicHttpContext();
//
//        JSONObject jsonObj = null;
//        final String headers;
//        final String method;
//        final String url;
//        final String postdata;
//        try {
//            jsonObj = new JSONObject(propertiesJson);
//            headers = jsonObj.getString("headers");
//            method = jsonObj.getString("method");
//            url = jsonObj.getString("url");
//            postdata = jsonObj.getString("postdata");
//         } catch (Throwable e) {
//             return;
//         }
//
//        HttpParams params = new BasicHttpParams();
//        SchemeRegistry schemeRegistry = new SchemeRegistry();
//        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//        schemeRegistry.register(new Scheme("https", getSSLSocketFactory(), 443));
//        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
//
//        try {
//            client = new DefaultHttpClient(manager, params);
//            JSONObject headersJson = new JSONObject(headers);
//            if (method.equalsIgnoreCase("GET")) {
//                response = client.execute(httpGetRequest(url, "", headersJson),
//                        httpContext);
//            } else if (method.equalsIgnoreCase("POST")) {
//                if (!headersJson.has("Content-Type"))
//                    headersJson.put("Content-Type", "application/x-www-form-urlencoded");
//                response = client.execute(ticketHttpPostRequest(url, postdata, headersJson), httpContext);
//            } else {
//            }
//        } catch (Throwable e) {
//        } finally {
//            observer.onTicketHttpResult(response, getUrlAfterRedirects(httpContext));
//        }
//    }
//
//    public void httpRequest(String url, String paramJson, String headersJson, IObserver ob) {
//        HttpClient client = null;
//        HttpResponse response = null;
//        HttpContext httpContext = new BasicHttpContext();
//        String htmlValue = null;
//        String headersValue = null;
//        try {
//            url = url.trim();
//            URL requestUrl = new URL(url);
//            String schema = "";
//
//            schema = requestUrl.getProtocol();
//            if (schema.equals("https")) {
//                client = getHttpsClient();
//            }else{
//                client = new DefaultHttpClient();
//            }
//
//            JSONObject headers = fromJson(headersJson);
//            String method = getMethodFromParamJson(paramJson);
//
//            if (method.equalsIgnoreCase("GET")) {
//                response = client.execute(httpGetRequest(url, paramJson, headers),
//                        httpContext);
//            } else if (method.equalsIgnoreCase("POST")) {
//                response = client.execute(httpPostRequest(url, paramJson, headers),
//                        httpContext);
//            } else {
//                Log.e(TAG, "video_get_http method error mothed=" + method);
//            }
//
//            if (response != null) {
//
//                int status = response.getStatusLine().getStatusCode();
//                boolean redirect = status == HttpStatus.SC_MOVED_PERMANENTLY
//                            || status == HttpStatus.SC_MOVED_TEMPORARILY
//                            || status == HttpStatus.SC_SEE_OTHER;
//
//                String lastUrl = url;
//
//                if (response != null) {
//                    HttpEntity entity = response.getEntity();
//                    InputStream input = AndroidHttpClient.getUngzippedContent(entity);
//                    String charset = HttpUtility.getContentCharset(response);
//                    htmlValue = StringUtil.inputStream2String(input, charset);
//                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//                        Log.w(TAG, "http_request error!" + response.getStatusLine().toString());
//                    } else {
//                        syncCookieByResponse(response, getUrlAfterRedirects(httpContext));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "httpRequest exception", e);
//        } finally {
//
//            headersValue = getHeaderParamByHttpResponse(url, response, getUrlAfterRedirects(httpContext));
//            ob.onHttpUtility2Result(headersValue, htmlValue);
//        }
//    }
//
//    private JSONObject fromJson(String str) {
//        JSONObject json = new JSONObject();
//        try {
//            json = new JSONObject(str);
//        } catch (Exception e) {
//        }
//        return json;
//    }
//
//    private HttpGet httpGetRequest(String url, String params, JSONObject jsonHeaders) {
//        // 创建 HttpGet 方法，该方法会自动处理 URL 地址的重定向
//        HttpGet httpGet = new HttpGet(url);
//        addHeader(url, httpGet, jsonHeaders);
//        return httpGet;
//    }
//
//    private HttpPost httpPostRequest(String url, String params, JSONObject jsonHeaders) {
//        HttpPost httppost = new HttpPost(url);
//        String post_data = getPostDataFromParamJson(params);
//        addHeader(url, httppost, jsonHeaders);
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(post_data, HTTP.UTF_8);
//        } catch (UnsupportedEncodingException e) {
//        }
//        httppost.setEntity(entity);
//        return httppost;
//    }
//
//    private HttpPost ticketHttpPostRequest(String url, String postData, JSONObject jsonHeaders) {
//        HttpPost httppost = new HttpPost(url);
//        addHeader(url, httppost, jsonHeaders);
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(postData, HTTP.UTF_8);
//        } catch (UnsupportedEncodingException e) {
//        }
//        httppost.setEntity(entity);
//        return httppost;
//    }
//
//    private void addHeader(String url, HttpRequestBase httprequest, JSONObject jsonHeaders) {
//        httprequest.setHeader("User-Agent", mDefaultUserAgent);
//        httprequest.setHeader("Accept-Encoding", "gzip,deflate,sdch");
//        httprequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//
//        String cookie = CookieManager.getInstance().getCookie(url);
//        if (!TextUtils.isEmpty(cookie))
//            httprequest.setHeader("Cookie", cookie);
//
//        try {
//            Iterator<?> it = jsonHeaders.keys();
//            while (it.hasNext()) {
//                String name = (String) it.next().toString();
//                String value = jsonHeaders.getString(name);
//                if (TextUtils.isEmpty(name) || name.equals("Content-Length"))
//                    continue;
//                else if (name.equalsIgnoreCase("referer")) {
//                    httprequest.setHeader("Referer", value);
//                } else
//                    httprequest.setHeader(name, value);
//                // Log.d(TAG, "header (" + name + ":" + value + ")");
//            }
//        } catch (JSONException e) {
//        }
//    }
//
//    private void syncCookieByResponse(HttpResponse httpResponse, String toUrl) {
//        try {
//            toUrl = URLDecoder.decode(toUrl, "utf-8");
//            if (TextUtils.isEmpty(toUrl)) {
//                return;
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return;
//        }
//        Header[] headers = httpResponse.getHeaders("Set-Cookie");
//        if (headers == null)
//            return;
//
//        CookieManager cookieManager = CookieManager.getInstance();
//        for (int i = 0; i < headers.length; i++) {
//            String cookieString = headers[i].getValue();
//            cookieManager.setCookie(toUrl, cookieString);
//        }
//        CookieSyncManager.getInstance().sync();
//    }
//
//    private static SSLSocketFactory getSSLSocketFactory() {
//        KeyStore trustStore = null;
//        SSLSocketFactory sf = null;
//        try {
//            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(null, null);
//            sf = new MySSLSocketFactory(trustStore);
//            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        }
//
//        return sf;
//    }
//
//    private static class MySSLSocketFactory extends SSLSocketFactory {
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//
//        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
//                KeyManagementException, KeyStoreException, UnrecoverableKeyException {
//            super(truststore);
//
//            TrustManager tm = new X509TrustManager() {
//                public void checkClientTrusted(X509Certificate[] chain, String authType)
//                        throws CertificateException {
//                }
//
//                public void checkServerTrusted(X509Certificate[] chain, String authType)
//                        throws CertificateException {
//                }
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            };
//
//            sslContext.init(null, new TrustManager[] { tm }, null);
//        }
//
//        @Override
//        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
//                throws IOException, UnknownHostException {
//            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
//        }
//
//        @Override
//        public Socket createSocket() throws IOException {
//            return sslContext.getSocketFactory().createSocket();
//        }
//    }
//    private String getMethodFromParamJson(String jsonStr) {
//        try {
//            JSONObject jsonObj = new JSONObject(jsonStr);
//            return jsonObj.getString("method");
//        } catch (JSONException e) {
//            Log.e(TAG, "Json parse error");
//        }
//        return "";
//    }
//
//    private String getPostDataFromParamJson(String jsonStr) {
//        try {
//            JSONObject jsonObj = new JSONObject(jsonStr);
//            return jsonObj.getString("post_data");
//        } catch (JSONException e) {
//            Log.e(TAG, "Json parse error");
//        }
//        return "";
//    }
//
//    private String getUrlAfterRedirects(HttpContext context) {
//        if (context == null)
//            return null;
//
//        HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
//        HttpHost currentHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
//        if (currentHost == null || currentReq == null || currentReq.getURI() == null)
//            return null;
//        return (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
//    }
//
//    private String getReponseString(HttpResponse response) {
//        String reponsString = null;
//        try {
//            Header[]  headers = response.getAllHeaders();
//            if(headers != null&& headers.length > 0){
//                Map<String, String> headerMap = new HashMap<String, String>();
//                for (int i = 0; i < headers.length; i++) {
//                    Header header = headers[i];
//                    String nameString = header.getName();
//                    String valueString  = header.getValue();
//                    if (!TextUtils.isEmpty(nameString) && !TextUtils.isEmpty(valueString)) {
//                        headerMap.put(nameString, valueString);
//                    }
//                }
//                if(!headerMap.isEmpty()){
//                    JSONObject headerJsonObj = new JSONObject(headerMap);
//                    reponsString  =  headerJsonObj.toString();
//                }
//            }
//        } catch (Exception e) {
//        }
//        return reponsString;
//    }
//
//    private String getHeaderParamByHttpResponse(String requestUrl, HttpResponse response, String url) {
//        String strJson = null;
//        try {
//            Map<String, String> map = new HashMap<String, String>();
//            int status_code = response != null && response.getStatusLine() != null ? response.getStatusLine().getStatusCode() : 505;
//            String responsString = getReponseString(response);
//            if (responsString != null) {
//                map.put("responseHeaders", responsString);
//            }
//            map.put("url", url != null ? url : requestUrl);
//            map.put("status", String.valueOf(status_code) );
//            JSONObject jsonObj = new JSONObject(map);
//            strJson = jsonObj.toString();
//        } catch (Exception e) {
//        }
//        return strJson;
//    }
//
//
//    @SuppressWarnings("unused")
//    private void testHttpRequst() {
//        // String url = "http://www.sina.com.cn";
//        // String url = "http://10.33.41.51/fs1.html";
//        // String url =
//        // "http://v.youku.com/v_show/id_XNzI1MjczNzk2.html?f=22365984&ev=1";
//        // String params = "{\"method\":\"get\"}";
//        // String url = "http://www.baidu.com";
//        // String params =
//        // "{\"method\":\"post\", \"post_data\":\"liebaoyidong\"}";
//        String params = "{\"method\":\"POST\",\"post_data\":\"method=getprotourl&data=%7B%22vid%22%3A%22XNzI1NTg5NjY0%22%2C%22playurl%22%3A%22http%3A%2F%2Fv.youku.com%2Fv_show%2Fid_XNzI1NTg5NjY0.html%3Ff%3D22373929%26ev%3D2%22%7D&web_site=youku\"}";
//        String url = "http://v.youku.com/v_show/id_XNzI1NTg5NjY0.html?f=22373929&ev=2";
//        // String headers =
//        // "{\"Accept\":\"text/html\",\"Content-Type\":\"application/x-www-form-urlencoded\",\"Content-Length\":\"182\"}";
//        // String headers = "{\"Accept\":\"text/html\"}";
//        // String headers =
//        // "{\"Accept\":\"text/html\",\"Content-Length\":\"182\"}";
//        String headers = "{\"Accept\":\"text/html\",\"Content-Type\":\"application/x-www-form-urlencoded\"}";
//        httpRequest(url, params, headers, new IObserver() {
//
//            @Override
//            public void onHttpUtility2Result(String headers, String html) {
//
//            }
//        });
//    }
//}
