package com.lzf.waterpolicy;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.waterpolicy.http.PostData;
import com.lzf.waterpolicy.util.FormatMatch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private ProgressDialog progress;
	private EditText usernameET;
	private EditText passwordET;
	private EditText ipET;
	private ImageView login;

	private String user_name;
	private String password;
	private String ip;
	private String message; // ����˷��ص�ԭʼ��Ϣ
	private boolean success; // �Ƿ��¼�ɹ�
	private String id; // �û�ID
	private String fk_role_id; // �û���ɫ

	private final int LOGIN = 0; // ��¼��ʶ

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOGIN:
				if (progress != null) {
					progress.dismiss();
				}
				if (message.equals("fail")) {
					login.setClickable(true);
					Toast.makeText(LoginActivity.this, "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else if (message.equals("")) {
					login.setClickable(true);
					Toast.makeText(LoginActivity.this, "ϵͳδ��⵽���û�����ȷ����������û����Ƿ���ȷ��", Toast.LENGTH_SHORT).show();
					usernameET.requestFocus();
				} else {
					try {
						JSONObject jObject = new JSONObject(message);
						if (jObject.getBoolean("success")) {
							JSONObject data = jObject.getJSONObject("data");
							id = data.getString("id");
							fk_role_id = data.getString("fk_role_id");
							SharedPreferences sp = LoginActivity.this.getSharedPreferences("userInfo",
									Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = sp.edit();
							editor.putString("id", id);
							editor.putString("fk_role_id", fk_role_id);
							editor.putString("user_name", user_name);
							editor.putString("ip", ip);
							editor.commit();
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
							finish();
						} else {
							login.setClickable(true);
							Toast.makeText(LoginActivity.this, "�����������������롣", Toast.LENGTH_SHORT).show();
							passwordET.requestFocus();
						}
					} catch (JSONException e) {
						login.setClickable(true);
						Toast.makeText(LoginActivity.this, "�������Ӧ��Ϣ�����쳣��", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				break;
			default:
				Toast.makeText(LoginActivity.this, "δ֪���û�����", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// ��̬��ȡ�ֻ���Ļ�Ŀ��ȣ�����̬����ͼƬ�Ŀ��ȡ��߶ȣ�����Ӧ��Ļ��С��
		WindowManager wManager = this.getWindowManager();
		int width = (int) ((wManager.getDefaultDisplay().getWidth()) / 1.662817);
		int height = (int) (width / 1.365931);
		Bitmap bitmapRaw = BitmapFactory.decodeResource(getResources(), R.drawable.login_logo);
		Bitmap bitmapOk = Bitmap.createScaledBitmap(bitmapRaw, width, height, true);
		ImageView iView = (ImageView) findViewById(R.id.loginLogo);
		iView.setImageBitmap(bitmapOk);

		initView();

	}

	private void initView() {
		usernameET = (EditText) findViewById(R.id.usernameET);
		passwordET = (EditText) findViewById(R.id.passwordET);
		ipET = (EditText) findViewById(R.id.ipET);
		login = (ImageView) findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				login();
			}
		});
	}

	public void login() {
		user_name = usernameET.getText().toString().trim();
		password = passwordET.getText().toString().trim();
		ip = ipET.getText().toString().trim();
		if (user_name.equals("") || user_name == null) {
			Toast.makeText(LoginActivity.this, "�û�������Ϊ��", Toast.LENGTH_SHORT).show();
		} else if (password.equals("") || password == null) {
			Toast.makeText(LoginActivity.this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
		} else if (ip.equals("") || ip == null) {
			Toast.makeText(LoginActivity.this, "IP��ַ����Ϊ��", Toast.LENGTH_SHORT).show();
			ipET.requestFocus();
		} else if (!(FormatMatch.ip(ip))) {
			Toast.makeText(LoginActivity.this, "IP��ַ��������������", Toast.LENGTH_SHORT).show();
			ipET.requestFocus();
		} else {
			login.setClickable(false);
			progress = ProgressDialog.show(LoginActivity.this, null, "���ڵ�¼...", true, false);
			new Thread() {
				public void run() {
					message = PostData.submit("http://" + ip + "/Phone/Phone/LoginAction", user_name, password);
					handler.sendEmptyMessage(LOGIN);
				}
			}.start();
		}
	}
}