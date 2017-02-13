package com.lzf.waterpolicy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.waterpolicy.bean.Case;
import com.lzf.waterpolicy.http.OKHttp;
import com.lzf.waterpolicy.util.ReusableAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ResultActivity extends Activity implements OnClickListener {

	private String serverCase; // 服务端返回的案件
	private String id;
	private String ip;
	private int totalCount;

	private View footerView;
	private TextView caseClosed; // 已结案
	private TextView noAccept; // 未受理
	private TextView processing; // 正在处理
	private ListView listView;
	private ReusableAdapter<Case> adapter;
	private ProgressDialog progress;

	private List<Case> caseData = new ArrayList<Case>();
	private int page;
	private String start; // 小（开始时间）
	private String end; // 大（结束时间）
	private String title;

	private final int QUERY = 0;
	private final int LOADING = 1;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case QUERY:
				if (progress != null) {
					progress.dismiss();
				}
				if (serverCase.equals("fail")) {
					Toast.makeText(ResultActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverCase);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							caseData.removeAll(caseData);
							if (jArray.length() <= 0) {
								Toast.makeText(ResultActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
							} else {
								for (int i = 0; i < jArray.length(); i++) {
									JSONObject jData = jArray.getJSONObject(i);
									caseData.add(new Case(jData.getString("Cid"), jData.getString("Cnumber"),
											jData.getString("FK_CsId"), jData.getString("Ctime"),
											jData.getString("crtime"), jData.getString("Cname"),
											jData.getString("Caddress"), jData.getString("Ctype"),
											jData.getString("Cdes"), jData.getString("pic"), jData.getString("video"),
											jData.getString("picurl"), jData.getString("videourl")));
								}
							}
							showListView(caseData, processing.isSelected());
						} else {
							Toast.makeText(ResultActivity.this, "服务器异常。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case LOADING:
				if (serverCase.equals("fail")) {
					Toast.makeText(ResultActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverCase);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								adapter.add(new Case(jData.getString("Cid"), jData.getString("Cnumber"),
										jData.getString("FK_CsId"), jData.getString("Ctime"), jData.getString("crtime"),
										jData.getString("Cname"), jData.getString("Caddress"), jData.getString("Ctype"),
										jData.getString("Cdes"), jData.getString("pic"), jData.getString("video"),
										jData.getString("picurl"), jData.getString("videourl")));
							}
						} else {
							Toast.makeText(ResultActivity.this, "服务器异常。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		Intent intent = getIntent();
		start = intent.getStringExtra("start");
		end = intent.getStringExtra("end");
		title = intent.getStringExtra("title");

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		ip = sp.getString("ip", "192.168.2.89:4001");
		id = sp.getString("id", "37");

		caseClosed = (TextView) findViewById(R.id.CaseClosed);
		noAccept = (TextView) findViewById(R.id.NoAccept);
		processing = (TextView) findViewById(R.id.Processing);
		caseClosed.setOnClickListener(this);
		noAccept.setOnClickListener(this);
		processing.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.listView);
		footerView = LayoutInflater.from(this).inflate(R.layout.loading, null, false);
		listView.addFooterView(footerView);
		listView.setOnScrollListener(new OnScrollListener() {

			/** 记录第一行Item的数值 */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当滑动到底部时
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem != 0) {
					if (totalCount > caseData.size()) {
						if (caseClosed.isSelected()) {
							loading(false, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id
									+ "&limit=10&FK_CsId=1");
						} else if (noAccept.isSelected()) {
							loading(false, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id
									+ "&limit=10&FK_CsId=2");
						} else if (processing.isSelected()) {
							loading(false, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id
									+ "&limit=10&FK_CsId=3");
						}
					}
				}
			}

			/*
			 * firstVisibleItem表示在现时屏幕第一个ListItem(部分显示的ListItem也算)在整个ListView的位置
			 * （下标从0开始）
			 * 
			 * visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数
			 * 
			 * totalItemCount表示ListView的ListItem总数
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				this.firstVisibleItem = firstVisibleItem;

				// 判断可视Item是否能在当前页面完全显示
				if (visibleItemCount == totalItemCount || totalCount <= caseData.size()) {
					// removeFooterView(footerView);
					footerView.setVisibility(View.GONE);// 隐藏底部布局
				} else {
					// addFooterView(footerView);
					footerView.setVisibility(View.VISIBLE);// 显示底部布局
				}
			}
		});

		caseClosed.performClick(); // 已结案.
	}

	@Override
	public void onClick(View v) {
		page = 0;
		reset();
		switch (v.getId()) {
		case R.id.CaseClosed:
			caseClosed.setSelected(true);
			showListView(null, processing.isSelected());
			loading(true, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id + "&limit=10&FK_CsId=1");
			break;
		case R.id.NoAccept:
			noAccept.setSelected(true);
			showListView(null, processing.isSelected());
			loading(true, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id + "&limit=10&FK_CsId=2");
			break;
		case R.id.Processing:
			processing.setSelected(true);
			showListView(null, processing.isSelected());
			loading(true, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id + "&limit=10&FK_CsId=3");
			break;
		}
	}

	private void loading(final boolean flag, final String url) {
		page++;
		if (flag) {
			progress = ProgressDialog.show(this, null, "正在查询...", true, false);
		}
		new Thread() {
			public void run() {
				serverCase = OKHttp.getData(
						url + "&StateLTime=" + start + "&EndLTime=" + end + "&Cname=" + title + "&start=" + page);
				System.out.println(serverCase);
				if (flag) {
					handler.sendEmptyMessage(QUERY);
				} else {
					handler.sendEmptyMessage(LOADING);
				}
			}
		}.start();
	}

	private void showListView(final List<Case> data, boolean flag) {
		adapter = new ReusableAdapter<Case>(data, R.layout.item_case_list) {
			@Override
			public void bindView(com.lzf.waterpolicy.util.ReusableAdapter.ViewHolder holder, Case obj) {
				holder.setText(R.id.title, obj.getCnumber());
				holder.setText(R.id.dateTime, obj.getCtime());
			}
		};
		listView.setAdapter(adapter);
		if (flag) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent intent = new Intent(ResultActivity.this, CaseingActivity.class);
					intent.putExtra("case", data.get(arg2));
					startActivity(intent);
				}
			});
		} else {
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent intent = new Intent(ResultActivity.this, CaseActivity.class);
					intent.putExtra("case", data.get(arg2));
					startActivity(intent);
				}
			});
		}
	}

	// 恢复出厂设置
	private void reset() {
		caseClosed.setSelected(false);
		noAccept.setSelected(false);
		processing.setSelected(false);
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
