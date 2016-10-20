package com.lzf.waterpolicy;

import com.lzf.waterpolicy.bean.Laws;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

/**
 * 法律法规详情页
 */
public class LawsActivity extends Activity {

	private TextView lawsTitle;
	private TextView lawsSubjectClass;
	private TextView releaseTime; // 发布时间
	private TextView lawsContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_laws);

		initView();

		Laws law = (Laws) getIntent().getSerializableExtra("law");
		lawsTitle.setText(law.getLTitle());
		lawsSubjectClass.setText(law.getLxname());
		releaseTime.setText(law.getLTime());
		lawsContent.setText(Html.fromHtml(law.getLText()));
	}

	private void initView() {
		lawsTitle = (TextView) findViewById(R.id.lawsTitle);
		lawsSubjectClass = (TextView) findViewById(R.id.lawsSubjectClass);
		releaseTime = (TextView) findViewById(R.id.releaseTime);
		lawsContent = (TextView) findViewById(R.id.lawsContent);
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

}
