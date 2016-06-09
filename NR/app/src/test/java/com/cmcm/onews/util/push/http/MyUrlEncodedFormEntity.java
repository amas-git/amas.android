/*
 * MyUrlEncodedFormEntity.java
 *
 *  Created on: Mar 23, 2012 11:08:41 AM
 *      Author: dreamer
 */

package com.cmcm.onews.util.push.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MyUrlEncodedFormEntity extends UrlEncodedFormEntity {
	
	public MyUrlEncodedFormEntity(final List<? extends NameValuePair> parameters,
        final String encoding) throws UnsupportedEncodingException {
        super(parameters, encoding);
        setContentType(URLEncodedUtils.CONTENT_TYPE + HTTP.CHARSET_PARAM +
                (encoding != null ? encoding : HTTP.DEFAULT_CONTENT_CHARSET));

	}
	
//	public MyUrlEncodedFormEntity (
//        final List <? extends NameValuePair> parameters) throws UnsupportedEncodingException {
//        super(parameters);
//        setContentType(URLEncodedUtils.CONTENT_TYPE + HTTP.CHARSET_PARAM + HTTP.DEFAULT_CONTENT_CHARSET);
//    }
}

