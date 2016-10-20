package com.lzf.waterpolicy;

import java.util.Map;

import com.lzf.waterpolicy.fragment.ChargeSheetFragment;
import com.lzf.waterpolicy.fragment.HomeFragment;
import com.lzf.waterpolicy.fragment.LawsFragment;
import com.lzf.waterpolicy.fragment.NoticeFragement;
import com.lzf.waterpolicy.fragment.ReportFragment;
import com.lzf.waterpolicy.fragment.SystemSetFragment;
import com.lzf.waterpolicy.util.LocationManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private final int MAP_LOCATION = 6; // ��ͼ�ص�ѡ���ʶ�������ص㣩

	private TextView topTitleTV;
	private RelativeLayout chargeSheetRL; // ������¼
	private RelativeLayout noticeRL; // ϵͳ����
	private ImageView report; // �ϱ�����
	private ImageView search; // ����
	private RelativeLayout lawsRL; // ���ɷ���
	private RelativeLayout systemSetRL; // ϵͳ����

	private long exitTime = 0;
	private Map<String, String> location;

	private FragmentManager fManager;
	private ChargeSheetFragment chargeSheetF;
	private HomeFragment homeF;
	private LawsFragment lawsF;
	private NoticeFragement noticeF;
	private ReportFragment reportF;
	private SystemSetFragment systemSetF;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		location = LocationManager.getLocation(getApplicationContext());

		fManager = getSupportFragmentManager();

		initView();

		// ��ʼ����ҳ����ʾ����
		topTitleTV.setClickable(true);
		topTitleTV.performClick();
	}

	private void initView() {
		search = (ImageView) findViewById(R.id.search);
		topTitleTV = (TextView) findViewById(R.id.topTitleTV);
		chargeSheetRL = (RelativeLayout) findViewById(R.id.chargeSheetRL);
		noticeRL = (RelativeLayout) findViewById(R.id.noticeRL);
		report = (ImageView) findViewById(R.id.report);
		lawsRL = (RelativeLayout) findViewById(R.id.lawsRL);
		systemSetRL = (RelativeLayout) findViewById(R.id.systemSetRL);

		topTitleTV.setOnClickListener(this);
		chargeSheetRL.setOnClickListener(this);
		noticeRL.setOnClickListener(this);
		report.setOnClickListener(this);
		lawsRL.setOnClickListener(this);
		systemSetRL.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction fTransaction = fManager.beginTransaction();
		hideAllFragment(fTransaction);
		noSelect();
		topTitleTV.setClickable(false);
		switch (v.getId()) {
		case R.id.report:
			topTitleTV.setText("�ϱ�����");
			reportF = new ReportFragment(location, topTitleTV);
			fTransaction.replace(R.id.centerContent, reportF);
			break;
		case R.id.chargeSheetRL:
			topTitleTV.setText("������¼");
			chargeSheetRL.setSelected(true);
			chargeSheetF = new ChargeSheetFragment();
			fTransaction.replace(R.id.centerContent, chargeSheetF);
			break;
		case R.id.noticeRL:
			topTitleTV.setText("ϵͳ����");
			noticeRL.setSelected(true);
			noticeF = new NoticeFragement();
			fTransaction.replace(R.id.centerContent, noticeF);
			break;
		case R.id.lawsRL:
			topTitleTV.setText("���ɷ���");
			lawsRL.setSelected(true);
			lawsF = new LawsFragment();
			fTransaction.replace(R.id.centerContent, lawsF);
			break;
		case R.id.systemSetRL:
			topTitleTV.setText("ϵͳ����");
			systemSetRL.setSelected(true);
			systemSetF = new SystemSetFragment();
			fTransaction.replace(R.id.centerContent, systemSetF);
			break;
		default:
			topTitleTV.setText("ˮ���ƶ�ִ��ƽ̨");
			homeF = new HomeFragment();
			fTransaction.replace(R.id.centerContent, homeF);
			break;
		}
		fTransaction.commit();
	}

	// ��������Fragment
	private void hideAllFragment(FragmentTransaction fTransaction) {
		if (chargeSheetF != null)
			fTransaction.hide(chargeSheetF);
		if (homeF != null) {
			fTransaction.hide(homeF);
		}
		if (lawsF != null) {
			fTransaction.hide(lawsF);
		}
		if (noticeF != null) {
			fTransaction.hide(noticeF);
		}
		if (reportF != null) {
			fTransaction.hide(reportF);
		}
		if (systemSetF != null) {
			fTransaction.hide(systemSetF);
		}
	}

	// ������TextView��״̬��Ϊδѡ��
	private void noSelect() {
		chargeSheetRL.setSelected(false);
		noticeRL.setSelected(false);
		lawsRL.setSelected(false);
		systemSetRL.setSelected(false);
		search.setVisibility(View.GONE);
	}

	public void cancel(View view) {
		topTitleTV.setClickable(true);
		topTitleTV.setText("ˮ���ƶ�ִ��ƽ̨");
		topTitleTV.performClick();
	}

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(MainActivity.this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			LocationManager.stopLocation();
			super.onBackPressed(); // �˳�����
		}
	}

}