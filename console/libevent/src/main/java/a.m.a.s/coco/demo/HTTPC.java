package a.m.a.s.coco.demo;

public class HTTPC {
	public static boolean DEBUG_HTTP = false;
	
//	public static String httpGet(HttpClient client, String url, boolean enableCompress) {
//		if (client == null) {
//			client = getDefaultHttpClient();
//		}
//
//		if(DEBUG_HTTP) {
//			d("GET : " + url);
//		}
//
//		HttpGet getRequest = null;
//
//		try {
//			getRequest = new HttpGet(url);
//
//			if(enableCompress) {
//				getRequest.setHeader("Accept-Encoding", "gzip");
//			}
//			HttpResponse getResponse = client.execute(getRequest);
//			final int statusCode = getResponse.getStatusLine().getStatusCode();
//			if(DEBUG_HTTP) {
//				d(" - code :" + statusCode);
//			}
//			if (statusCode != HttpStatus.SC_OK) {
//				return null;
//			}
//
//			HttpEntity getResponseEntity = getResponse.getEntity();
//			InputStream is = getResponseEntity.getContent();
//			Header contentEncoding = getResponse.getFirstHeader("Content-Encoding");
//			if (contentEncoding != null
//                && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//				is = new GZIPInputStream(new BufferedInputStream(is));
//			}
//			return inputStreamToString(is);
//		} catch (ClientProtocolException e) {
//		} catch (SocketTimeoutException e) {
//		} catch (ConnectException e) {
//		} catch (SocketException e) {
//		} catch (MalformedURLException e) {
//		} catch (ConnectionPoolTimeoutException e) {
//		} catch (ConnectTimeoutException e) {
//		} catch (NullPointerException e) {
//		} catch (IOException e) {
//		} catch (Exception e) {
//		} finally {
//			if (client != null) {
//				client.getConnectionManager().shutdown();
//			}
//		}
//		return null;
//	}
//
//	public InputStream httpPost(HttpClient client, String uri, HttpEntity entity, boolean enableCompress) {
//		if (client == null) {
//			client = getDefaultHttpClient();
//		}
//
//		HttpPost postRequest = new HttpPost(uri);
//		postRequest.setHeader("Content-type", "application/json");
//
//		if(enableCompress) {
//			postRequest.setHeader("Accept-Encoding", "gzip");
//		}
//
//		postRequest.setHeader("Connection", "close");
//		try {
//			if (entity != null) {
//				postRequest.setEntity(entity);
//			}
//
//			HttpResponse response = client.execute(postRequest);
//			final int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode != HttpStatus.SC_OK) {
//				return null;
//			}
//
//			HttpEntity getResponseEntity = response.getEntity();
//			InputStream is = getResponseEntity.getContent();
//			Header contentEncoding = response.getFirstHeader("Content-Encoding");
//			if (contentEncoding != null
//                && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//				is = new GZIPInputStream(new BufferedInputStream(is));
//			}
//			return is;
//		} catch (ClientProtocolException e) {
//		} catch (SocketTimeoutException e) {
//		} catch (ConnectException e) {
//		} catch (SocketException se) {
//		} catch (MalformedURLException e) {
//		} catch (ConnectionPoolTimeoutException e) {
//		} catch (ConnectTimeoutException e) {
//		} catch (NoHttpResponseException e) {
//		} catch (NullPointerException e) {
//		} catch (IOException e) {
//			postRequest.abort();
//		} catch (Exception e) {
//		} finally{
//		}
//
//		return null;
//	}
//
//	public static String inputStreamToString(InputStream is) {
//		final char[] buffer = new char[2048];
//		final StringBuilder out = new StringBuilder();
//		InputStreamReader reader = null;
//		try {
//			try {
//				reader = new InputStreamReader(is, "utf-8");
//
//				int size = reader.read(buffer, 0, buffer.length);
//				while (size > 0) {
//					out.append(buffer, 0, size);
//					size = reader.read(buffer);
//
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (reader != null) {
//					reader.close();
//				}
//			}
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return out.toString();
//	}
//
//	private static void d(String msg) {
//		System.out.println(msg);
//	}
//
//
//
//	protected static HttpClient getDefaultHttpClient() {
//		HttpClient httpclient = new DefaultHttpClient();
//		return httpclient;
//	}
}
