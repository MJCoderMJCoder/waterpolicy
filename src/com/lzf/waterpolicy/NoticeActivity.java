package com.lzf.waterpolicy;

import com.lzf.waterpolicy.bean.Notice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class NoticeActivity extends Activity {

	private TextView noticeTitle;
	private TextView releaseDate;
	private TextView noticeContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);

		initView();

		Notice notice = (Notice) getIntent().getSerializableExtra("notice");
		noticeTitle.setText(notice.getNtitle());
		releaseDate.setText(notice.getNdate());
		noticeContent.setText(Html.fromHtml(notice.getNdes()));
	}

	private void initView() {
		noticeTitle = (TextView) findViewById(R.id.noticeTitle);
		releaseDate = (TextView) findViewById(R.id.releaseTime);
		noticeContent = (TextView) findViewById(R.id.noticeContent);
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
