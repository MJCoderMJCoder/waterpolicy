package com.lzf.waterpolicy.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetData {

	// ����һ����ȡ����ͼƬ���ݵķ���:
	public static byte[] getImage(String path) {
		byte[] bt = null;
		HttpURLConnection conn = null;
		URL url;
		try {
			url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			// �������ӳ�ʱΪ5��
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {

				// 5.����getInputStream()������÷��������ص�������
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

	// ��ȡ��ҳ��htmlԴ����
	public static String getHtml(String path) {
		String html = "fail"; // ���Ӳ������������������������Ժ�����
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
