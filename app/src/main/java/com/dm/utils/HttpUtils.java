package com.dm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 
 */
public class HttpUtils {

	/**
	 * get请求
	 * 
	 * @param url
	 *            url链接
	 * @param params
	 *            参数集合
	 * @return
	 * @throws Exception
	 */
	public static String sendGet(String url, HashMap<String, String> params) throws Exception {
		String result = "";
		BufferedReader in = null;
		try {
			String param = parseParams(params);// 组装成参数串
			String urlNameString = url + "?" + param;
			URL getUrl = new URL(urlNameString);
			URLConnection connection = getUrl.openConnection();
			/* 设置一般请求属性 */
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			/* 建立连接 */
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
		return result;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 *            url链接
	 * @param params
	 *            参数集合
	 * @return
	 * @throws Exception
	 */
	public static String sendPost(String url, HashMap<String, String> params) throws Exception {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL postUrl = new URL(url);
			URLConnection connection = postUrl.openConnection();
			/* 设置一般请求属性 */
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			/* URL 连接可用于输入和/或输出。如果打算使用 URL 连接进行输出，则将 DoOutput 标志设置为 true * */
			connection.setDoOutput(true);
			connection.setDoInput(true);
			/** 用PrintWriter包装 返回写入到此连接的输出流 */
			out = new PrintWriter(connection.getOutputStream());
			/* 组装成参数串 */
			String param = parseParams(params);
			out.print(param);
			out.flush();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e2) {
				throw e2;
			}
		}
		return result;
	}

	/**
	 * 把map变成url参数串 例如：key=value&year=2014
	 * 
	 * @param map
	 * @return
	 */
	private static String parseParams(HashMap<String, String> map) {
		StringBuffer sb = new StringBuffer();
		if (map != null) {
			for (Entry<String, String> e : map.entrySet()) {
				sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
			}
			sb.substring(0, sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @Description 将一个网页解析为字符串返回
	 * @return
	 * @return String
	 * @throw
	 */
	public static String getHtml2Str(String url) {
		if (DataValidation.isEmpty(url)) {
			return "";
		}
		try {
			Document doc = Jsoup.connect(url).timeout(10000).get();
			String html = doc.html();
			String h1 = doc.text();
			System.out.println(html);
			System.out.println("-----------------------------------------");
			System.out.println(h1);
			return doc.html();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// String htmlString =
		return null;
	}
	public static void main(String[] args){
		getHtml2Str(null);
	}
}
