package com.lzf.waterpolicy.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnUploadListener;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

/**
 * https://github.com/yanzhenjie/NoHttp
 * 
 * http://doc.nohttp.net/223788
 *
 * NoHttp基于HttpUrlConnection的网络层框架（国内）
 */
public class NoHttpTest {

	private static OnUploadListener uploadListener = new OnUploadListener() {

		@Override
		public void onStart(int what) {
			switch (what) {
			case 0: // photo
				System.out.println("photo开始上传");
				break;
			case 1: // video
				System.out.println("video开始上传");
				break;
			default:
				break;
			}
		}

		@Override
		public void onProgress(int what, int progress) {
			switch (what) {
			case 0: // photo
				System.out.println("photo上传进度：\t" + progress + "%");
				break;
			case 1: // video
				System.out.println("video上传进度：\t" + progress + "%");
				break;
			default:
				break;
			}
		}

		@Override
		public void onFinish(int what) {
			switch (what) {
			case 0: // photo
				System.out.println("photo上传完成");
				break;
			case 1: // video
				System.out.println("video上传完成");
				break;
			default:
				break;
			}
		}

		@Override
		public void onError(int what, Exception e) {
			switch (what) {
			case 0: // photo
				System.out.println("photo" + e.getMessage());
				break;
			case 1: // video
				System.out.println("video" + e.getMessage());
				break;
			default:
				break;
			}
		}

		@Override
		public void onCancel(int what) {
			switch (what) {
			case 0: // photo
				System.out.println("photo取消");
				break;
			case 1: // video
				System.out.println("video取消");
				break;
			default:
				break;
			}
		}
	};

	public static String submit(String url, Map<String, String> params, Map<String, File> files) {
		String message = "fail"; // 连接不到服务器，请检查你的网络或稍后重试
		Request<String> request = new StringRequest(url, RequestMethod.POST);
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				request.add(entry.getKey(), entry.getValue());
			}
			System.out.println(params);
		}
		if (files.size() > 0) {
			for (Map.Entry<String, File> entry : files.entrySet()) {
				FileBinary fileBinary = new FileBinary(entry.getValue());
				fileBinary.setUploadListener((entry.getKey().equals("photo")) ? 0 : 1, uploadListener);
				request.add(entry.getKey(), fileBinary);
			}
			System.out.println(files);
		}
		request.setMultipartFormEnable(true);
		Response<String> response = NoHttp.startRequestSync(request);
		if (response.isSucceed()) {
			try {
				message = new String(response.get().getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		System.out.println(message);
		return message;
	}

}
