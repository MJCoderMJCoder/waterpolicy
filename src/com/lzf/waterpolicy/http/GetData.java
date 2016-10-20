package com.lzf.waterpolicy.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetData {

	// 定义一个获取网络图片数据的方法:
	public static byte[] getImage(String path) {
		byte[] bt = null;
		HttpURLConnection conn = null;
		URL url;
		try {
			url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			// 设置连接超时为5秒
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {

				// 5.调用getInputStream()方法获得服务器返回的输入流
				InputStream inStream = conn.getInputStream();
				bt = FlowBinary.binary(inStream);
				inStream.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return bt;
	}

	// 获取网页的html源代码
	public static String getHtml(String path) {
		String html = "fail"; // 连接不到服务器，请检查你的网络或稍后重试
		HttpURLConnection conn = null;
		try {
			URL url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				InputStream in = conn.getInputStream();
				byte[] data = FlowBinary.binary(in);
				html = new String(data, "UTF-8");
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return html;
	}
}
