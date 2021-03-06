package com.lzf.waterpolicy.http;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttp {
	private static OkHttpClient client = new OkHttpClient();

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String submit(String url, Map<String, String> params, Map<String, File> files) {
		String message = "fail"; // 连接不到服务器，请检查你的网络或稍后重试
		MultipartBody.Builder builder = new MultipartBody.Builder();
		// 设置类型
		builder.setType(MultipartBody.FORM);
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				builder.addFormDataPart(entry.getKey(), entry.getValue());
			}
		}
		if (files.size() > 0) {
			for (Map.Entry<String, File> entry : files.entrySet()) {
				File temp = entry.getValue();
				builder.addFormDataPart(entry.getKey(), temp.getName(), RequestBody.create(null, temp));
			}
		}
		RequestBody body = builder.build();
		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = client.newCall(request).execute()) {
			message = response.body().string();
		} catch (IOException e1) {
			System.out.println("获取响应时异常" + e1.getMessage());
		}
		return message;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getData(String url) {
		String message = "fail"; // 连接不到服务器，请检查你的网络或稍后重试
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) {
			message = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static byte[] getVideo(String url) {
		byte[] bt = null; // 连接不到服务器，请检查你的网络或稍后重试
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) {
			bt = FlowBinary.binary(response.body().byteStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bt;
	}

}
