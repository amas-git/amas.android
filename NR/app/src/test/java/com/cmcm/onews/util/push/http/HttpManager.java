
package com.cmcm.onews.util.push.http;

import android.text.TextUtils;
import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

public class HttpManager {

    private boolean running = true;
    private final static int WORKER_NUMBER = 20;
    private Worker[] workers = new Worker[WORKER_NUMBER];
//    public static boolean PROCESS_PROXY = true;
    private static final int CONNECT_TIME_OUT = 1 * 60 * 1000;
    private static final String ENCODING = "UTF-8";
    private static final int MAX_REDIRECT_NUM = 5;

    private static HttpManager sInstance = null;
    
    public static HttpManager getInstance() {
		if(null == sInstance) {
			synchronized (HttpManager.class) {
				if(null == sInstance) {
					sInstance = new HttpManager();
				}
			}
		}

		return sInstance;
    }
    
    private HttpManager() {
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    private PriorityQueue<HttpMsg> queue = new PriorityQueue<HttpMsg>(11,
            new Comparator<HttpMsg>() {
                @Override
                public int compare(HttpMsg lhs, HttpMsg rhs) {
                    if (lhs.getPriority() > rhs.getPriority()) {
                        return 1;
                    } else if (lhs.getPriority() < rhs.getPriority()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
    private SparseArray<HttpMsg> array = new SparseArray<HttpMsg>();
    private static int idGenerator = 1000;
    private Object lock = new Object();


    public void destroy() {
    	sInstance = null;
        running = false;
        cancelAll();
        workers = null;
    }

    public int send(HttpMsg msg) {
        synchronized (lock) {
            int id = idGenerator++;
            msg.setId(id);
            msg.setPriority(HttpMsg.getNewPriority());
            array.put(id, msg);
            queue.add(msg);
            lock.notify();
            return id;
        }

    }
    
    public void cancel(int msgId) {
        synchronized (lock) {
            HttpMsg msg = array.get(msgId);
            if (msg != null) {
                array.remove(msgId);
                queue.remove(msg);
            } else {
                for (int i = 0; i < workers.length; i++) {
                    if (workers[i].curMsg != null && workers[i].curMsg.getId() == msgId) {
                        workers[i].curMsg.setCanceled(true);
                    }
                }
            }
            lock.notify();
        }

    }
	
    public void cancelMsgInQuery(int msgId) {
    	synchronized (lock) {
    		HttpMsg msg = array.get(msgId);
    		if (msg != null) {
    			array.remove(msgId);
    			queue.remove(msg);
    		}
    		lock.notify();
    	}
    	
    }

    public void cancelAll() {
        synchronized (lock) {
            array.clear();
            queue.clear();
            for (int i = 0; i < workers.length; i++) {
                if (workers[i].curMsg != null) {
                    workers[i].curMsg.setCanceled(true);
                }
            }
            lock.notifyAll();
        }
    }

    public static void sendMsg(HttpMsg msg) throws HttpException {
        sendMsgInternal(msg);
    }
    
    private static void sendMsgInternal(HttpMsg msg) throws HttpException {
        HttpURLConnection con = null;
        OutputStream os = null;
        InputStream is = null;
        HttpMsg.HttpMsgListener listener = msg.getListener();
        try {
            int redirectNum = 0;
            int responseCode;
            while (true) {
                con = getConnection(msg);
                if (msg.isCanceled()) {
                    return;
                }
                if (msg.getMethod() == HttpMsg.Method.POST || msg.getMethod() == HttpMsg.Method.PUT) {
                    os = con.getOutputStream();
                    makeRequestData(os, msg);
                }
                if (msg.isCanceled()) {
                    return;
                }
                responseCode = con.getResponseCode();
                if (msg.isCanceled()) {
                    return;
                }
                if ((responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM)
                        && redirectNum < MAX_REDIRECT_NUM) {
                    String loc = con.getHeaderField("Location");
                    if (loc != null) {
                        try {
                            if (os != null) {
                                os.close();
                            }
                            con.disconnect();
                        } catch (Exception t) {
                        }
                        short type = URI.getRequestType(loc);
                        loc = URI.getFullUrl(loc, type, msg.getRedirectUrl() == null ? msg.getUrl()
                                : msg.getRedirectUrl());
                        msg.setRedirectUrl(loc);
                        redirectNum++;
                        if (msg.isCanceled()) {
                            return;
                        }
                        continue;
                    } else {
                        throw new IOException("Redirect failed!");
                    }
                } else {
                    break;
                }
            }

            if (responseCode == HttpURLConnection.HTTP_OK
                    || responseCode == HttpURLConnection.HTTP_PARTIAL
                    || responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                HashMap<String, String> respHeaders = new HashMap<String, String>();
                int responseLength = 0;
                String key = null;
                int i = 1;
                while ((key = con.getHeaderFieldKey(i++)) != null) {
                    String value = con.getHeaderField(key);
                    respHeaders.put(key, value);
                }
                responseLength = con.getContentLength();
                is = con.getInputStream();
                if (listener != null && msg.getType() == HttpMsg.ResponseType.STREAM) {
                    if (msg.isCanceled()) {
                        return;
                    }
                    listener.onResponse(responseCode, respHeaders, responseLength, is);
                    return;
                }
                if (responseCode != HttpURLConnection.HTTP_NOT_MODIFIED) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = 0;
//                    KLog.d("xgstag_img2","正在读取数据 "+msg.getUrl()+"");
                    while ((n = is.read(buf)) >= 0) {
                        if (msg.isCanceled()) {
//                            KLog.d("xgstag_img2","任务被取消 结束"+msg.getUrl()+"");
                            return;
                        }
                        baos.write(buf, 0, n);
                    }
//                    KLog.d("xgstag_img2","读取数据结束"+msg.getUrl()+"");
                    baos.flush();
                    byte[] data = baos.toByteArray();
                    responseLength = data.length;
                    if (msg.getType() == HttpMsg.ResponseType.TEXT) {
                        String encode = ENCODING;
                        if (con.getContentType() != null) {
                            encode = getContentCharSet(con.getContentType());
                        }
                        String dataStr = new String(data, encode);
                        if (msg.isCanceled()) {
                            return;
                        }
                        listener.onResponse(responseCode, respHeaders, responseLength,
                                dataStr);
                    } else if (msg.getType() == HttpMsg.ResponseType.BINARY)
                    {
                        if (msg.isCanceled()) {
                            return;
                        }
                        listener.onResponse(responseCode, respHeaders, responseLength,
                                data);
                    }
                    return;
                } else {
                    if (listener != null) {
                        if (msg.isCanceled()) {
                            return;
                        }
                        listener.onError(new RespErrCodeException(responseCode, null));
                    }
                    return;
                }
            } else {
                if (listener != null) {
                    listener.onError(new RespErrCodeException(responseCode, null));
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException(e.toString());
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            throw new NetException(e.toString());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private static String getContentCharSet(String contentType) {
        String charset = ENCODING;
        if (contentType != null) {
            String[] s = contentType.split(";");
            if (s != null && s.length > 0) {
                for (String v : s) {
                    if (v.contains("charset") && v.contains("=")) {
                        String[] r = v.split("=");
                        if (r != null && r.length > 1) {
                            if (r[1] == null || r[1].trim().equals("")) {
                                return charset;
                            } else {
                                return r[1].trim();
                            }
                        }
                    }
                }
            }
        }
        return charset;
    }

    private static HttpURLConnection getConnection(HttpMsg msg) throws MalformedURLException, IOException {
        HttpURLConnection conn = null;
        String url = msg.getRedirectUrl();
        if (TextUtils.isEmpty(url)) {
            url = msg.getUrl();
        }
//        String proxyStr = PROCESS_PROXY ? getProxy() : null;
//        if (proxyStr != null) {
//            String host = null;
//            String path = null;
//            int hostIndex = "http://".length();
//            int pathIndex = url.indexOf('/', hostIndex);
//            if (pathIndex < 0) {
//                host = url.substring(hostIndex);
//                path = "";
//            } else {
//                host = url.substring(hostIndex, pathIndex);
//                path = url.substring(pathIndex);
//            }
//            conn = (HttpURLConnection) new URL("http://" + proxyStr + path)
//                    .openConnection();
//            conn.setRequestProperty("X-Online-Host", host);
//        } else {
            conn = (HttpURLConnection) new URL(url).openConnection();
//        }
        HttpMsg.Method method = msg.getMethod();
        conn.setRequestMethod(method.name());
        if (HttpMsg.Method.POST.equals(method)
                || HttpMsg.Method.PUT.equals(method)) {
            conn.setDoOutput(true);
        }
        conn.setDoInput(true);
        conn.setConnectTimeout(CONNECT_TIME_OUT);

        Map<String, String> headers = msg.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            Set<Entry<String, String>> entrys = headers.entrySet();
            for (Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    private static void makeRequestData(OutputStream os, HttpMsg msg) throws UnsupportedEncodingException,
            IOException {
        if (msg.getReqTextData() != null && msg.getReqTextData().length() > 0) {
            os.write(msg.getReqTextData().getBytes(ENCODING));
        } else if (msg.getReqBinaryData() != null && msg.getReqBinaryData().length > 0) {
            os.write(msg.getReqBinaryData());
        }
    }

    private class Worker extends Thread {

        private HttpMsg curMsg = null;

        @Override
        public void run() {
            while (running) {
                HttpMsg msg = null;
                synchronized (lock) {
                    while (queue.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!running) {
                            return;
                        }
                    }
                    msg = queue.poll();
                    array.remove(msg.getId());
                    curMsg = msg;
                }
                if (msg.getListener() != null) {
                    msg.getListener().beforeSend();
                }
                int retry = 0;
                HttpException exception = null;
                boolean taskDone = false;
                while (retry <= msg.getRetry()) {
                    try {
                        HttpManager.this.sendMsgInternal(msg);
                        taskDone = true;
                        break;
                    } catch (HttpException e) {
                        exception = e;
                        retry++;
                    }
                }
                
                if(msg.getListener() != null) {
                	msg.getListener().afterSend();
                }
                
                if (!taskDone) {
                    HttpMsg.HttpMsgListener listener = msg.getListener();
                    if (listener != null) {
                        listener.onError(exception);
                    }
                }
                curMsg = null;
            }
        }
    }
    
    public static byte[] simpleRequest(String url,Map<String,String> header) throws HttpException {
        HttpMsg msg = new HttpMsg(url);
        msg.setType(HttpMsg.ResponseType.BINARY);
        msg.setMethod(HttpMsg.Method.GET);
        SimpleRequestListener listener = new SimpleRequestListener();
        msg.setListener(listener);
        msg.setHeaders(header);
        HttpManager.sendMsg(msg);
        if (listener.exceptionResult != null) {
            throw listener.exceptionResult;
        }
        return listener.respDataByte;
    }
    
    public static String simpleGetRequest(String url) throws HttpException {
        HttpMsg msg = new HttpMsg(url);
        msg.setType(HttpMsg.ResponseType.TEXT);
        msg.setMethod(HttpMsg.Method.GET);
        SimpleRequestListener listener = new SimpleRequestListener();
        msg.setListener(listener);
        HttpManager.sendMsg(msg);
        if (listener.exceptionResult != null) {
            throw listener.exceptionResult;
        }
        return listener.respDataResult;
    }

    public static String simplePostRequest(String url, String postData) throws HttpException {
        HttpMsg msg = new HttpMsg(url);
        msg.setType(HttpMsg.ResponseType.TEXT);
        msg.setMethod(HttpMsg.Method.POST);
        msg.setReqTextData(postData);
        SimpleRequestListener listener = new SimpleRequestListener();
        msg.setListener(listener);
        HttpManager.sendMsg(msg);
        if (listener.exceptionResult != null) {
            throw listener.exceptionResult;
        }
        return listener.respDataResult;
    }

    public static byte[] simpleGetByteRequest(String url) throws HttpException {
        return simpleGetByteRequestInternal(url, null);
    }
    
    public static byte[] simpleGetByteRequest(String url,Map<String,String> headers) throws HttpException {
        return simpleGetByteRequestInternal(url, headers);
    }
    
    private static byte[] simpleGetByteRequestInternal(String url,Map<String,String> headers) throws HttpException {
        HttpMsg msg = new HttpMsg(url);
        msg.setType(HttpMsg.ResponseType.BINARY);
        msg.setMethod(HttpMsg.Method.GET);
        SimpleRequestListener listener = new SimpleRequestListener();
        msg.setListener(listener);
        if(headers!=null){
            msg.setHeaders(headers);
        }
        HttpManager.sendMsg(msg);
        if (listener.exceptionResult != null) {
            throw listener.exceptionResult;
        }
        return listener.respDataByte;
    }
    
    private static class SimpleRequestListener extends HttpMsg.AbstractHttpMsgListener {
        private HttpException exceptionResult;
        private String respDataResult;
        private byte[] respDataByte;

        @Override
        public void onResponse(int responseCode, HashMap<String, String> headers,
                int responseLength,
                String respData) {
            respDataResult = respData;
        }

        @Override
        public void onResponse(int responseCode, HashMap<String, String> headers,
                int responseLength,
                byte[] respData) {
            respDataByte = respData;
        }

        @Override
        public void onError(HttpException exception) {
            exceptionResult = exception;
        }
    }
}
