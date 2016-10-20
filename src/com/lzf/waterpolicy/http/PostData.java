package com.lzf.waterpolicy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PostData {
	public static String submit(String path, String user_name, String password) {
		String message = "fail"; // 连接不到服务器，请检查你的网络或稍后重试
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setReadTimeout(5000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			// 设置为false,POST方式不能缓存
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("connection", "keep-alive");
			String data = "user_name=" + URLEncoder.encode(user_name, "UTF-8") + "&password="
					+ URLEncoder.encode(password, "UTF-8");
			OutputStream output = httpURLConnection.getOutputStream();
			output.write(data.getBytes());
			// 刷新此输出流并强制写出所有缓冲的输出字节
			output.flush();
			output.close();
			if (httpURLConnection.getResponseCode() == 200) {
				InputStream inputStream = httpURLConnection.getInputStream();

				byte[] bt = FlowBinary.binary(inputStream);
				// 返回字符串
				message = new String(bt, "UTF-8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	public static String submit(String path, String xiaoquGuid, String FangHaoGuid, String phone, String ShuRuyzm,
			String password, String uuid, String WeiXin, String xingming, String xiaoquName, String FangHaoName,
			String IsIosOrAndroid, String RoleID, String token, String clientid, String appid, String appkey) {
		String message = "fail"; // 连接不到服务器，请检查你的网络或稍后重试
		// "系统升级维护中，由此给您带来的不便敬请谅解！感谢您的支持与理解！";
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setReadTimeout(5000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestProperty("connection", "keep-alive");
			// 设置为false,POST方式不能缓存
			httpURLConnection.setUseCaches(false);
			String data = "xiaoquGuid=" + URLEncoder.encode(xiaoquGuid, "UTF-8") + "&FangHaoGuid="
					+ URLEncoder.encode(FangHaoGuid, "UTF-8") + "&phone=" + URLEncoder.encode(phone, "UTF-8")
					+ "&ShuRuyzm=" + URLEncoder.encode(ShuRuyzm, "UTF-8") + "&password="
					+ URLEncoder.encode(password, "UTF-8") + "&uuid=" + URLEncoder.encode(uuid, "UTF-8") + "&WeiXin="
					+ URLEncoder.encode(WeiXin, "UTF-8") + "&xingming=" + URLEncoder.encode(xingming, "UTF-8")
					+ "&xiaoquName=" + URLEncoder.encode(xiaoquName, "UTF-8") + "&FangHaoName="
					+ URLEncoder.encode(FangHaoName, "UTF-8") + "&IsIosOrAndroid="
					+ URLEncoder.encode(IsIosOrAndroid, "UTF-8") + "&RoleID=" + URLEncoder.encode(RoleID, "UTF-8")
					+ "&token=" + URLEncoder.encode(token, "UTF-8") + "&clientid="
					+ URLEncoder.encode(clientid, "UTF-8") + "&appid=" + URLEncoder.encode(appid, "UTF-8") + "&appkey="
					+ URLEncoder.encode(appkey, "UTF-8");
			OutputStream output = httpURLConnection.getOutputStream();
			output.write(data.getBytes());
			// 刷新此输出流并强制写出所有缓冲的输出字节
			output.flush();
			output.close();
			if (httpURLConnection.getResponseCode() == 200) {
				InputStream inputStream = httpURLConnection.getInputStream();

				byte[] bt = FlowBinary.binary(inputStream);
				// 返回字符串
				message = new String(bt, "UTF-8");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}
}
