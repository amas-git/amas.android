package com.cmcm.onews.util.push.comm;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final String TAG = "StringUtil";
    
//	public static final String AT_PATTERN = "@[\\u4e00-\\u9fa5\\w\\-]+";//find @***:
//	public static final String TAG_PATTERN = "#([^\\#|.]+)#";//find #***#
	
	private static Random randGen = null;
	private static Object initLock = new Object();
	private static char[] numbersAndLetters = null;
	
	/**
	 * 判断传入的字符串是否为空串
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return (s == null || s.length() == 0);
	}
	
	/**
	 * 判断传入的字符串是否为空串（包括全为空格）
	 * @return
	 */
//	public static boolean isEmptyIgnoreSpace(String s) {
//		return (s == null || s.trim().length() == 0);
//	}

    /*
     * judge string equal, and check null 
     */
    public static boolean equalString(String str, String str2) {
        if (str != null && str2 != null) {
            return str.compareToIgnoreCase(str2) == 0;
        } else if ( TextUtils.isEmpty(str) && TextUtils.isEmpty(str2) ) {
            return true;
        }
        return (str == null && str2 == null);
    }

//	public static String randomNumericString(int length) {
//		if (length < 1) {
//			return null;
//		}
//		if (randGen == null) {
//			synchronized (initLock) {
//				if (randGen == null) {
//					randGen = new Random();
//					numbersAndLetters = ("0123456789"
//							+ "abcdefghijklmnopqrstuvwxyz"
//							+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
//				}
//			}
//		}
//		char[] randBuffer = new char[length];
//		for (int i = 0; i < randBuffer.length; i++) {
//			randBuffer[i] = numbersAndLetters[randGen.nextInt(9)];
//		}
//		return new String(randBuffer);
//	}


	public static boolean matcher(String s, String pattern) {
		Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
		Matcher matcher = p.matcher(s);
		if(matcher.find()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 替换源串的指定字符
	 * @param src 源串
	 * @param regEx 需要替换的字符串或正则表达式
	 * @param rep 需要替换的目标字符串
	 * @return
	 */
	public static String replace(String src, String regEx, String rep) {
		Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
        Matcher matcher = pat.matcher(src);
        if (matcher.find()) {
        	return matcher.replaceAll(rep);
        } else {
        	return src;
        }
	}
	
	public static String matcherHtml(String content, String regEx) {
		String info = null;
    	Pattern pattern = Pattern.compile(regEx);
    	Matcher matcher = pattern.matcher(content);
    	if (matcher.find()){
    		info = matcher.group(1);
    	}
    	return info;
	}
	
	

    static public String checkValidString(String text) {
        try {
            String utfStr = new String(text.getBytes("ISO-8859-1"), "UTF-8");
            boolean isCn = isValidUtf8String(utfStr);
            
            boolean isSpecialChar = utf8ContainsSpecialCharacter(text);
            if (isSpecialChar) {
                isCn = true;
            }
            if (isCn) 
                return text;
        } catch (UnsupportedEncodingException e) {
        }
        
        return "";
    }

    
    /**
     * @return raw text encoded by the barcode
     */
    static public String tryConvertString(String text) {
        String utfStr = "";
        boolean isCn = false;
        try {
            utfStr = new String(text.getBytes("ISO-8859-1"), "UTF-8");
            isCn = isValidUtf8String(utfStr);
            // 防止有人特意使用乱码来生成二维码来判断的情况
            boolean isSpecialChar = utf8ContainsSpecialCharacter(text);
            if (isSpecialChar) {
                isCn = true;
            }
            if (!isCn) {
                return new String(text.getBytes("ISO-8859-1"), "GB2312");
            } else {
                return utfStr;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return text;
    }
    
    public static final boolean isValidUtf8String(String chineseStr) {
        char[] charArray = chineseStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD')
                    || ((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF'))) {
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    public static final boolean utf8ContainsSpecialCharacter(String str) {
        if (str.contains("ï¿½")) {
            return true;
        }
        return false;
    }
	
	/**
     * <p>Checks if the string contains only ASCII printable characters.</p>
     * 
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     * 
     * <pre>
     * StringUtils.isAsciiPrintable(null)     = false
     * StringUtils.isAsciiPrintable("")       = true
     * StringUtils.isAsciiPrintable(" ")      = true
     * StringUtils.isAsciiPrintable("Ceki")   = true
     * StringUtils.isAsciiPrintable("ab2c")   = true
     * StringUtils.isAsciiPrintable("!ab-c~") = true
     * StringUtils.isAsciiPrintable("\u0020") = true
     * StringUtils.isAsciiPrintable("\u0021") = true
     * StringUtils.isAsciiPrintable("\u007e") = true
     * StringUtils.isAsciiPrintable("\u007f") = false
     * StringUtils.isAsciiPrintable("Ceki G\u00fclc\u00fc") = false
     * </pre>
     *
     * @param str the string to check, may be null
     * @return <code>true</code> if every character is in the range
     *  32 thru 126
     * @since 2.1
     */
    public static boolean isAsciiPrintable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (isAsciiPrintable(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * <p>Checks whether the character is ASCII 7 bit printable.</p>
     *
     * <pre>
     *   CharUtils.isAsciiPrintable('a')  = true
     *   CharUtils.isAsciiPrintable('A')  = true
     *   CharUtils.isAsciiPrintable('3')  = true
     *   CharUtils.isAsciiPrintable('-')  = true
     *   CharUtils.isAsciiPrintable('\n') = false
     *   CharUtils.isAsciiPrintable('&copy;') = false
     * </pre>
     * 
     * @param ch  the character to check
     * @return true if between 32 and 126 inclusive
     */
    public static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }
    
    //参数样式：10KB
    public static float convertSpaceSizeStr2Float(String str){
        str = str.replace(" ", "");
        str = str.toLowerCase();
        Pattern pattern = Pattern.compile("[0-9.]+");
        Matcher matcher = pattern.matcher(str);
        
        if (matcher.find()) {
            String float_str = matcher.group();
            try{
                float d = Float.parseFloat(float_str);
                Pattern pattern2 = Pattern.compile("[a-z]+");
                Matcher matcher2 = pattern2.matcher(str);
                if (matcher2.find()) {
                    String unitStr = matcher2.group();
                    if(unitStr.equals("b")){
                        d = d/1024;
                    }else if(unitStr.equals("mb")){
                        d = d*1024;
                    }else if(unitStr.equals("gb")){
                        d = d*1024*1024;
                    }
                }
            return d;
            }
            catch(Exception e){
            }
        }
        return -1;
    } 
    
    public static String getSaveTurboString(float saveSize){
        String result = "";
        try{
            if(saveSize < 1024){
                result += (int)saveSize+"KB";
            } else if(saveSize < 1024*1024){
                saveSize /= 102.4f;
                DecimalFormat decimalFormat=new DecimalFormat("0.0");
                String format = decimalFormat.format(saveSize);
                if (format.contains(",")) {
                    format = format.replaceAll(",", ".");
                }
                saveSize = Float.parseFloat(format);
                result += removeZero(saveSize/10.0f)+"MB";
            } else{
                saveSize = (int)(saveSize/(1024*102.4f));
                DecimalFormat decimalFormat=new DecimalFormat("0.0");
                String format = decimalFormat.format(saveSize);
                if (format.contains(",")) {
                    format = format.replaceAll(",", ".");
                }
                saveSize = Float.parseFloat(format);
                result += removeZero(saveSize/10.0f)+"GB";
            }
        }catch (Exception ex){
            ex.printStackTrace();;
        }

        return result;
    }    
    
    private static String removeZero(float savesize){
        String str = String.valueOf(savesize);
        if(str.endsWith(".0")){
            str = str.substring(0, str.indexOf("."));
        }
        return str;
    }

    public static String getShortTitleByTitle(String title,int num) {
        String reg = "^(http|ftp|https|rtsp|mms)://(.+)";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(title);
        if (m.find()) {
            title = m.group(2);
            return title;
        }

        String temp = title;

        String symbolReg = "(,|-|_)";
        Pattern sp = Pattern.compile(symbolReg);
        Matcher sm = sp.matcher(temp);
        temp = sm.replaceAll("|");

        String[] array = temp.split("\\|");
        if (array.length > 0) {
            temp = temp.split("\\|")[0];
            if (temp.equals("")) {
                temp = title.substring(0, title.length() > num ? num : title.length());
            } else if (temp.length() > num) {
                temp = temp.substring(0, num);
            }
        } else {
            temp = title.substring(0, title.length() > num ? num : title.length());
        }

        return temp;
    }

    /**
     * 将数据流转换成对应字符集的字符串
     *
     * @param stream
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String inputStream2String(InputStream stream, String charset)
            throws UnsupportedEncodingException {
        String dataStr = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024 * 8];
        int n = 0;
        try {
            while ((n = stream.read(buf)) >= 0) {
                baos.write(buf, 0, n);
            }
            baos.flush();
            byte[] data = baos.toByteArray();
            dataStr = new String(data, charset);
        } catch (EOFException e) {
            try {
                baos.flush();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            byte[] data = baos.toByteArray();
            dataStr = new String(data, charset);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeQuietly(stream);
            closeQuietly(baos);
        }

        return dataStr;
    }
    public static void closeQuietly(Closeable c) {
        if (c == null)
            return;

        try {
            c.close();
        } catch (Exception e) {
        }
    }
}
