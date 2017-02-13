package com.lzf.waterpolicy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.waterpolicy.bean.Case;
import com.lzf.waterpolicy.http.OKHttp;
import com.lzf.waterpolicy.util.DateTime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class QueryActivity extends Activity {

	private EditText titleET;
	private EditText releaseTime;
	private EditText releaseTime0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);

		// 按标题查询
		titleET = (EditText) findViewById(R.id.titleET);

		// 按发布时间查询
		releaseTime = (EditText) findViewById(R.id.releaseTime);
		releaseTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					DateTime.getDate(QueryActivity.this, releaseTime, releaseTime.getText().toString().trim());
				}
			}
		});
		releaseTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateTime.getDate(QueryActivity.this, releaseTime, releaseTime.getText().toString().trim());
			}
		});
		releaseTime0 = (EditText) findViewById(R.id.releaseTime0);
		releaseTime0.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					DateTime.getDate(QueryActivity.this, releaseTime0, releaseTime0.getText().toString().trim());
				}
			}
		});
		releaseTime0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateTime.getDate(QueryActivity.this, releaseTime0, releaseTime0.getText().toString().trim());
			}
		});

		// 点击查询
		Button query = (Button) findViewById(R.id.query);
		query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String start = releaseTime.getText().toString().trim(); // 小（开始时间）
				String end = releaseTime0.getText().toString().trim(); // 大（结束时间）
				String title = titleET.getText().toString().trim(); // 大（结束时间）
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date dateS = null;
				Date dateE = null;
				try {
					dateS = sdf.parse(start);
					dateE = sdf.parse(end);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (!(start.equals("")) && start != null && !(end.equals("")) && end != null && dateS.after(dateE)) {
					Toast.makeText(QueryActivity.this, "结束时间不能早于开始时间", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(QueryActivity.this, ResultActivity.class);
					intent.putExtra("start", start);
					intent.putExtra("end", end);
					intent.putExtra("title", title);
					startActivity(intent);
				}
			}
		});
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
