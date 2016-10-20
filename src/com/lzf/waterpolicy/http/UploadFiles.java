package com.lzf.waterpolicy.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ʵ���ļ��ϴ����ļ���������Ϣ��
 */
public class UploadFiles {

	public static String submit(String url, Map<String, String> params, Map<String, File> files) {
		String message = "fail"; // ���Ӳ������������������������Ժ�����
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
			String boundary = UUID.randomUUID().toString(); // �ֽ���
			String end = "\r\n--" + boundary + "--"; // ������ʶ

			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setReadTimeout(5000);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false); // ������ʹ�û���
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("connection", "keep-alive");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			OutputStream outputSteam = httpURLConnection.getOutputStream();
			DataOutputStream dos = new DataOutputStream(outputSteam);
			StringBuffer sb = new StringBuffer();
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					sb.append("\r\n--" + boundary + "\r\n");
					sb.append("Content-Disposition: form-data; name=" + entry.getKey() + "\r\n");
					sb.append("Content-Type: text/plain; charset=UTF-8" + "\r\n\r\n");
					sb.append(entry.getValue());
				}
			}
			if (files.size() > 0) {
				for (Map.Entry<String, File> entry : files.entrySet()) {
					sb.append("\r\n--" + boundary + "\r\n");
					sb.append("Content-Disposition:form-data;name=img;filename=" + entry.getValue().getName() + "\r\n");
					sb.append("Content-Type: application/octet-stream; charset=UTF-8" + "\r\n\r\n");
					InputStream is = new FileInputStream(entry.getValue());
					byte[] bytes = FlowBinary.binary(is);
					sb.append(new String(bytes, "UTF-8"));
					is.close();
				}
			}
			sb.append(end);
			System.out.println(sb.toString());
			dos.write(sb.toString().getBytes()); // д������
			dos.flush();

			/**
			 * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ����
			 */
			if (httpURLConnection.getResponseCode() == 200) {
				InputStream inputStream = httpURLConnection.getInputStream();
				byte[] bt = FlowBinary.binary(inputStream);
				message = new String(bt, "UTF-8");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}
}
