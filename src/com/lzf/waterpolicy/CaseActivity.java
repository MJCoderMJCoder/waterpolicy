package com.lzf.waterpolicy;

import java.io.File;
import java.net.URI;

import com.lzf.waterpolicy.bean.Case;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_case);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		final String ip = sp.getString("ip", "192.168.2.89:4001");
		final Case caseObj = (Case) getIntent().getSerializableExtra("case");

		TextView topTile = (TextView) findViewById(R.id.topTitleTV);
		topTile.setText(caseObj.getCnumber());

		TextView time = (TextView) findViewById(R.id.timeValue);
		time.setText(caseObj.getCtime());

		TextView site = (TextView) findViewById(R.id.siteValue);
		site.setText(caseObj.getCaddress());

		TextView type = (TextView) findViewById(R.id.typeValue);
		type.setText(caseObj.getCtype());

		TextView des = (TextView) findViewById(R.id.desValue);
		String desStr = caseObj.getCdes();
		if (desStr.equals("null")) {
			des.setText("");
		} else {
			des.setText(caseObj.getCdes());
		}

		TextView report = (TextView) findViewById(R.id.reportValue);
		report.setText(caseObj.getCname());

		findViewById(R.id.photo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String photoPath = caseObj.getPic();
				File photo = new File(photoPath);
				if (photo.exists()) {
					Intent intent = new Intent(CaseActivity.this, MediaActivity.class);
					intent.putExtra("image", photoPath);
					startActivity(intent);
				} else {
					String photoUrl = caseObj.getPicurl();
					if (photoUrl == null || photoUrl.equals("")) {
						Toast.makeText(CaseActivity.this, "没有图片", Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(CaseActivity.this, MediaActivity.class);
						System.out.println("http://" + ip + photoUrl);
						intent.putExtra("image", "http://" + ip + photoUrl);
						startActivity(intent);
					}
				}
			}
		});

		findViewById(R.id.video).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String videoPath = caseObj.getVideo();
				File video = new File(videoPath);
				if (video.exists()) {
					Intent intent = new Intent(CaseActivity.this, MediaActivity.class);
					intent.putExtra("video", videoPath);
					startActivity(intent);
				} else {
					String videoUrl = caseObj.getVideourl();
					if (videoUrl == null || videoUrl.equals("")) {
						Toast.makeText(CaseActivity.this, "没有视频", Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(CaseActivity.this, MediaActivity.class);
						System.out.println("http://" + ip + videoUrl);
						intent.putExtra("video", "http://" + ip + videoUrl);
						startActivity(intent);
					}
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
