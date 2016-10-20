package com.lzf.waterpolicy.fragment;

import com.amap.api.col.v;
import com.lzf.waterpolicy.HelpActivity;
import com.lzf.waterpolicy.R;
import com.lzf.waterpolicy.http.GetData;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.view.ViewGroup;

public class SystemSetFragment extends Fragment {

	private String message;

	private final int UPDATE = 0;

	private ProgressDialog progress;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE:
				if (progress != null) {
					progress.dismiss();
				}
				if (message.equals("fail")) {
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "您当前的系统已经为最新版。", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		final String ip = sp.getString("ip", "192.168.2.89:4001");

		View view = inflater.inflate(R.layout.fragment_system_set, container, false);
		view.findViewById(R.id.updateRL).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				progress = ProgressDialog.show(getActivity(), null, "检查更新...", true, false);
				new Thread() {
					public void run() {
						message = GetData.getHtml("http://" + ip + "/Phone/Phone/DataMGAction?lxid=91");
						handler.sendEmptyMessage(UPDATE);
					}
				}.start();
			}
		});

		view.findViewById(R.id.helpRL).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), HelpActivity.class);
				startActivity(intent);
			}
		});
		return view;
	}
}
