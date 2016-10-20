package com.lzf.waterpolicy;

import java.io.File;

import com.amap.api.col.v;
import com.lzf.gallery.utils.ImageLoader;
import com.lzf.gallery.utils.ImageLoader.Type;
import com.lzf.waterpolicy.bean.Case;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class CaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_case);

		final Case caseObj = (Case) getIntent().getSerializableExtra("case");

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String photoPath = caseObj.getPic();
				File photo = new File(photoPath);
				if (photo.exists()) {
					Intent intent = new Intent(CaseActivity.this, MediaActivity.class);
					intent.putExtra("image", photoPath);
					startActivity(intent);
				} else {
					Toast.makeText(CaseActivity.this, "本地库暂无该图片", Toast.LENGTH_SHORT).show();
				}
			}
		});

		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String videoPath = caseObj.getVideo();
				File video = new File(videoPath);
				if (video.exists()) {
					Intent intent = new Intent(CaseActivity.this, MediaActivity.class);
					intent.putExtra("video", videoPath);
					startActivity(intent);
				} else {
					Toast.makeText(CaseActivity.this, "本地库暂无该视频", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		finish();
	}

}
