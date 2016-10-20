package com.lzf.waterpolicy.http;

import android.annotation.TargetApi;
import android.os.Build;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttp {
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String submit(String url, Map<String, String> params, Map<String, File> files) {
		String message = "fail"; // 连接不到服务器，请检查你的网络或稍后重试
		OkHttpClient client = new OkHttpClient();
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
			e1.printStackTrace();
		}
		return message;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getData(String url) {
		String message = "fail"; // 连接不到服务器，请检查你的网络或稍后重试
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) {
			message = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}
}
