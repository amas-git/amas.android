
package com.cmcm.onews.util.push.http;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpMsg {

    private static int currentPriority = 0;

    public static enum ResponseType {
        TEXT,
        STREAM,
        BINARY
    }

    public static enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    private ResponseType type = ResponseType.TEXT;
    private Method method = Method.GET;
    private String url;
    private int priority = 0;
    private String reqTextData;
    private byte[] reqBinaryData;
    private HttpMsgListener listener;
    private boolean canceled = false;
    private int id;
    private int retry;
    private Map<String, String> headers = new HashMap<String, String>();
    private String redirectUrl;

    public HttpMsg(String url) {
        this.url = url;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getReqTextData() {
        return reqTextData;
    }

    public void setReqTextData(String reqTextData) {
        this.reqTextData = reqTextData;
    }

    public byte[] getReqBinaryData() {
        return reqBinaryData;
    }

    public void setReqBinaryData(byte[] reqBinaryData) {
        this.reqBinaryData = reqBinaryData;
    }

    public HttpMsgListener getListener() {
        return listener;
    }

    public void setListener(HttpMsgListener listener) {
        this.listener = listener;
    }

    boolean isCanceled() {
        return canceled;
    }

    void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    int getRetry() {
        return retry;
    }

    void setRetry(int retry) {
        this.retry = retry;
    }

    String getRedirectUrl() {
        return redirectUrl;
    }

    void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public static int getNewPriority(){

        return currentPriority++;
    }

    public static interface HttpMsgListener {
        public void beforeSend();

        public void onResponse(int responseCode, HashMap<String, String> headers,
                               int responseLength, String respData);

        public void onResponse(int responseCode, HashMap<String, String> headers,
                               int responseLength, InputStream is);

        public void onResponse(int responseCode, HashMap<String, String> headers,
                               int responseLength, byte[] respData);

        public void afterSend();

        public void onError(HttpException exception);

    }

    public static abstract class AbstractHttpMsgListener implements HttpMsgListener {
        @Override
        public void beforeSend() {
        }

        @Override
        public void onResponse(int responseCode, HashMap<String, String> headers,
                int responseLength, String respData) {
        }

        @Override
        public void onError(HttpException exception) {
        }

        @Override
        public void onResponse(int responseCode, HashMap<String, String> headers,
                int responseLength,
                byte[] respData) {
        }

        @Override
        public void onResponse(int responseCode, HashMap<String, String> headers,
                int responseLength,
                InputStream is) {
        }

        @Override
        public void afterSend() {
        }
    }

}
