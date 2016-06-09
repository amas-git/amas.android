
package com.cmcm.onews.util.push.http;

public class RespErrCodeException extends HttpException {

    /**
     * 
     */
    private static final long serialVersionUID = 5534878268376282140L;

    private int mErrCode;

    public RespErrCodeException(int errCode, String message) {
        super(message);
        mErrCode = errCode;
    }

    public int getErrCode() {
        return mErrCode;
    }

}
