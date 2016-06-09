
package com.cmcm.onews.util.push.http;

public class URI {
    private static final short URI_UNKNOWN = 0;
    private static final short URI_HTTP = 1;
    private static final short URI_TEL = 2;
    private static final short URI_MAIL = 3;
    private static final short URI_JAVASCRIPT = 4;
    private static final short URI_HTTPS = 5;
    private static final String defultUrl = "";

    /**
     * @param requestUrl
     * @return
     */
    public static short getRequestType(String requestUrl) {
        char[] start = requestUrl.trim().toCharArray();
        int i = 0;
        if ((start[i] == 'h' || start[i] == 'H')
                && (start[i + 1] == 't' || start[i + 1] == 'T')
                && (start[i + 2] == 't' || start[i + 2] == 'T')
                && (start[i + 3] == 'p' || start[i + 3] == 'P')) {
            if (start[i + 4] == 's' || start[i + 4] == 'S') {
                return URI_HTTPS;// https
            } else {
                return URI_HTTP;// http:
            }
        } else if ((start[i] == 'w' || start[i] == 'W')
                && (start[i + 1] == 't' || start[i + 1] == 'T')
                && (start[i + 2] == 'a' || start[i + 2] == 'A')
                && (start[i + 3] == 'i' || start[i + 3] == 'I')) {
            return URI_TEL;// "wtai://wp/mc;"
        } else if ((start[i] == 'm' || start[i] == 'M')
                && (start[i + 1] == 'a' || start[i + 1] == 'A')
                && (start[i + 2] == 'i' || start[i + 2] == 'I')
                && (start[i + 3] == 'l' || start[i + 3] == 'L')
                && (start[i + 4] == 't' || start[i + 4] == 'T')
                && (start[i + 5] == 'o' || start[i + 5] == 'O')) {
            return URI_MAIL;// mailto:
        } else if ((start[i] == 'j' || start[i] == 'J')
                && (start[i + 1] == 'a' || start[i + 1] == 'A')
                && (start[i + 2] == 'v' || start[i + 2] == 'V')
                && (start[i + 3] == 'a' || start[i + 3] == 'A')
                && (start[i + 4] == 's' || start[i + 4] == 'S')
                && (start[i + 5] == 'c' || start[i + 5] == 'C')
                && (start[i + 6] == 'r' || start[i + 6] == 'R')
                && (start[i + 7] == 'i' || start[i + 7] == 'I')
                && (start[i + 8] == 'p' || start[i + 8] == 'P')
                && (start[i + 9] == 't' || start[i + 9] == 'T')) {
            return URI_JAVASCRIPT;
        } else {
            return URI_UNKNOWN;
        }
    }

    public static String getFullUrl(String requestUrl, short type,
            String currentUrl) {
        try {
            boolean fullPath;
            switch (type) {
                case URI_HTTP:
                case URI_HTTPS:
                    fullPath = true;
                    break;
                case URI_UNKNOWN:
                    fullPath = false;
                    break;
                default:
                    return requestUrl;
            }

            int httpSign = 0, firstSign = 0, lastSign = 0, level = 0, levelSign = 0;
            String host = null, preUrl = null, partUrl = null;

            // get request url
            if (requestUrl != null && requestUrl.length() > 0) {
                partUrl = requestUrl.trim();
            } else {
                return null;
            }
            if (partUrl == null || partUrl.length() <= 0) {
                return requestUrl;// get current url
            }
            if (currentUrl != null && currentUrl.length() > 0) {
                preUrl = currentUrl.trim();
                if (preUrl == null || preUrl.length() <= 0) {
                    return requestUrl;
                }
                int signUrl = preUrl.indexOf('?');
                if (signUrl > 0) {
                    preUrl = preUrl.substring(0, signUrl);
                }
            } else {
                preUrl = defultUrl;
            }
            if (!fullPath) {
                httpSign = (preUrl.substring(0, 4).toLowerCase().compareTo(
                        "http") == 0) ? 8 : 0;
                firstSign = (httpSign > 0) ? preUrl.indexOf('/', httpSign)
                        : preUrl.indexOf('/', 0);
                if (firstSign <= 0) {
                    firstSign = preUrl.length();
                }
                lastSign = preUrl.lastIndexOf('/');
                if (lastSign < httpSign) {
                    lastSign = preUrl.length(); // judge the url type
                }
                if (partUrl.length() > 0 && partUrl.charAt(0) == '/') { // root
                                                                        // directory
                    host = preUrl.substring(0, firstSign);
                } else {
                    if (partUrl.length() > 0 && partUrl.charAt(0) != '.') { // relative
                                                                            // directory
                        host = preUrl.substring(0, lastSign);
                    } else { // lower directory
                        level = -1;
                        levelSign = 0;
                        while (levelSign == 0) {
                            level++;
                            if (partUrl.length() > 3) {
                                levelSign = partUrl.substring(0, 3).compareTo(
                                        "../");
                                if (levelSign == 0) {
                                    partUrl = partUrl.substring(3, partUrl
                                            .length());
                                }
                            } else {
                                levelSign = -1;
                            }
                        }
                        host = preUrl.substring(0, lastSign);
                        while (level-- > 0) {
                            lastSign = host.lastIndexOf('/');
                            if (lastSign > 0) {
                                host = host.substring(0, lastSign);
                            }
                        }
                    }
                }

                if (partUrl.charAt(0) != '/'
                        && host.charAt(host.length() - 1) != '/') {
                    return (host + '/' + partUrl);
                } else if (partUrl.charAt(0) == '/'
                        && host.charAt(host.length() - 1) == '/') {
                    return (host + partUrl.substring(1, partUrl.length()));
                } else {
                    return (host + partUrl);
                }
            } else {
                return partUrl;
            }
        } catch (Exception e) {
        }
        return defultUrl;
    }
}
