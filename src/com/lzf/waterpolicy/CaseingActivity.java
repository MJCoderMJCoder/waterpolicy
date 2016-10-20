package com.lzf.waterpolicy;

import com.lzf.waterpolicy.bean.Case;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 1001：受理
 * 
 * 1002：立案
 * 
 * 1003：不立案
 * 
 * 1004：调查通过
 * 
 * 1005：撤销立案
 * 
 * 1006：行政处罚审批
 * 
 * 1007：听证
 * 
 * 1008：结案审批
 *
 */
public class CaseingActivity extends Activity {

	private ImageView caseingImg;
	private TextView a;
	private TextView b;
	private TextView c;
	private TextView d;
	private TextView e;
	private TextView f;
	private TextView g;
	private TextView h;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caseing);

		initView();

		Case case1 = (Case) getIntent().getSerializableExtra("case");
		String state = case1.getFK_CsId();
		if (state.equals("1001")) {
			caseingImg.setImageResource(R.drawable.a);
			a.setTextColor(Color.RED);
		} else if (state.equals("1002")) {
			caseingImg.setImageResource(R.drawable.b);
			b.setTextColor(Color.RED);
		} else if (state.equals("1003")) {
			caseingImg.setImageResource(R.drawable.c);
			c.setTextColor(Color.RED);
		} else if (state.equals("1004")) {
			caseingImg.setImageResource(R.drawable.d);
			d.setTextColor(Color.RED);
		} else if (state.equals("1005")) {
			caseingImg.setImageResource(R.drawable.e);
			e.setTextColor(Color.RED);
		} else if (state.equals("1006")) {
			caseingImg.setImageResource(R.drawable.f);
			f.setTextColor(Color.RED);
		} else if (state.equals("1007")) {
			caseingImg.setImageResource(R.drawable.g);
			g.setTextColor(Color.RED);
		} else if (state.equals("1008")) {
			caseingImg.setImageResource(R.drawable.h);
			h.setTextColor(Color.RED);
		} else {
			caseingImg.setImageResource(R.drawable.a);
			a.setTextColor(Color.RED);
		}

	}

	private void initView() {
		caseingImg = (ImageView) findViewById(R.id.caseingImg);
		a = (TextView) findViewById(R.id.a);
		b = (TextView) findViewById(R.id.b);
		c = (TextView) findViewById(R.id.c);
		d = (TextView) findViewById(R.id.d);
		e = (TextView) findViewById(R.id.e);
		f = (TextView) findViewById(R.id.f);
		g = (TextView) findViewById(R.id.g);
		h = (TextView) findViewById(R.id.h);
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
