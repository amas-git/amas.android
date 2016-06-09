/**
 * 
 */
package com.cmcm.onews.util.push.http;

import org.apache.http.NameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author zengwei
 *
 */
public class SimpleNameValuePair implements NameValuePair {
	
	private String name;
	private String value;
	
	/* (non-Javadoc)
	 * @see org.apache.http.NameValuePair#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.NameValuePair#getValue()
	 */
	@Override
	public String getValue() {
		return value; // encodeValue(value);
	}
	
	public SimpleNameValuePair(String name, String value) {
		if (name == null || name.trim().length() == 0) return;
		
		this.name = name;
		this.value = (value == null) ? "" : value;
	}
	
	public SimpleNameValuePair(String name, Integer value) {
		if (name == null || name.trim().length() == 0) return;
		
		this.name = name;
		this.value = (value == null) ? "" : value.toString();
	}
	
	public SimpleNameValuePair(String name, Long value) {
		if (name == null || name.trim().length() == 0) return;
		
		this.name = name;
		this.value = (value == null) ? "" : value.toString();
	}
	
//	public SimpleNameValuePair(String name, Double value) {
//		if (name == null || name.trim().length() == 0) return;
//		
//		this.name = name;
//		this.value = (value == null) ? "" : value.toString();
//	}
	
	@Override
	public String toString() {
		return name + "=" + encodeValue(value);
	}
	
	private String encodeValue(String value) {
		String _value = null;
		try {
			_value = URLEncoder.encode(value, "utf-8");
		} catch (UnsupportedEncodingException uee) {
			_value = value;
		}
		return _value;
	}
}
