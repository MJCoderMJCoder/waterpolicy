package com.lzf.waterpolicy.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.waterpolicy.CaseActivity;
import com.lzf.waterpolicy.CaseingActivity;
import com.lzf.waterpolicy.R;
import com.lzf.waterpolicy.bean.Case;
import com.lzf.waterpolicy.bean.Notice;
import com.lzf.waterpolicy.http.OKHttp;
import com.lzf.waterpolicy.util.ReusableAdapter;

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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class ChargeSheetFragment extends Fragment implements OnClickListener {

	private String serverCase; // ����˷��صİ���
	private String id;
	private String ip;
	private int totalCount;

	private View view;
	private View footerView;
	private TextView caseClosed; // �ѽ᰸
	private TextView noAccept; // δ����
	private TextView processing; // ���ڴ���
	private ListView listView;
	private ReusableAdapter<Case> adapter;
	private ImageView search;
	private ProgressDialog progress;

	private List<Case> caseData = new ArrayList<Case>();
	private int page;

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
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverCase);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							caseData.removeAll(caseData);
							if (jArray.length() <= 0) {
								Toast.makeText(getActivity(), "��������", Toast.LENGTH_SHORT).show();
							} else {
								for (int i = 0; i < jArray.length(); i++) {
									JSONObject jData = jArray.getJSONObject(i);
									caseData.add(new Case(jData.getString("Cid"), jData.getString("Cnumber"),
											jData.getString("FK_CsId"), jData.getString("Ctime"),
											jData.getString("Cname"), jData.getString("Caddress"),
											jData.getString("Ctype"), jData.getString("pic"),
											jData.getString("video")));
								}
							}
							showListView(caseData, processing.isSelected());
						} else {
							Toast.makeText(getActivity(), "�������쳣��", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case LOADING:
				if (serverCase.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverCase);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								adapter.add(new Case(jData.getString("Cid"), jData.getString("Cnumber"),
										jData.getString("FK_CsId"), jData.getString("Ctime"), jData.getString("Cname"),
										jData.getString("Caddress"), jData.getString("Ctype"), jData.getString("pic"),
										jData.getString("video")));
							}
						} else {
							Toast.makeText(getActivity(), "�������쳣��", Toast.LENGTH_SHORT).show();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_charge_sheet, container, false);

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		ip = sp.getString("ip", "192.168.2.89:4001");
		id = sp.getString("id", "37");

		initView();

		return view;
	}

	private void initView() {
		search = (ImageView) getActivity().findViewById(R.id.search);
		search.setVisibility(View.VISIBLE);
		caseClosed = (TextView) view.findViewById(R.id.caseClosed);
		noAccept = (TextView) view.findViewById(R.id.noAccept);
		processing = (TextView) view.findViewById(R.id.processing);
		caseClosed.setOnClickListener(this);
		noAccept.setOnClickListener(this);
		processing.setOnClickListener(this);
		search.setOnClickListener(this);
		caseClosed.performClick();

		listView = (ListView) view.findViewById(R.id.listView);
		footerView = LayoutInflater.from(getActivity()).inflate(R.layout.loading, null, false);
		listView.addFooterView(footerView);
		listView.setOnScrollListener(new OnScrollListener() {

			/** ��¼��һ��Item����ֵ */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// ���������ײ�ʱ
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
			 * firstVisibleItem��ʾ����ʱ��Ļ��һ��ListItem(������ʾ��ListItemҲ��)������ListView��λ��
			 * ���±��0��ʼ��
			 * 
			 * visibleItemCount��ʾ����ʱ��Ļ���Լ�����ListItem(������ʾ��ListItemҲ��)����
			 * 
			 * totalItemCount��ʾListView��ListItem����
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				this.firstVisibleItem = firstVisibleItem;

				// �жϿ���Item�Ƿ����ڵ�ǰҳ����ȫ��ʾ
				if (visibleItemCount == totalItemCount || totalCount <= caseData.size()) {
					// removeFooterView(footerView);
					footerView.setVisibility(View.GONE);// ���صײ�����
				} else {
					// addFooterView(footerView);
					footerView.setVisibility(View.VISIBLE);// ��ʾ�ײ�����
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		reset();
		page = 0;
		switch (v.getId()) {
		case R.id.caseClosed:
			caseClosed.setSelected(true);
			loading(true, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id + "&limit=10&FK_CsId=1");
			break;
		case R.id.noAccept:
			noAccept.setSelected(true);
			loading(true, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id + "&limit=10&FK_CsId=2");
			break;
		case R.id.processing:
			processing.setSelected(true);
			loading(true, "http://" + ip + "/Phone/Phone/CaseInformationAction?id=" + id + "&limit=10&FK_CsId=3");
			break;
		case R.id.search:
			if (search.getVisibility() == View.VISIBLE) {
			}
			break;
		default:
			break;
		}
	}

	private void loading(final boolean flag, final String url) {
		page++;
		if (flag) {
			progress = ProgressDialog.show(getActivity(), null, "���ڲ�ѯ...", true, false);
		}
		new Thread() {
			public void run() {
				serverCase = OKHttp.getData(url + "&StateLTime=&EndLTime=&&Cname=&start=" + page);
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
				holder.setText(R.id.title, obj.getCname());
				holder.setText(R.id.dateTime, obj.getCtime());
			}
		};
		listView.setAdapter(adapter);
		if (flag) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent intent = new Intent(getActivity(), CaseingActivity.class);
					intent.putExtra("case", data.get(arg2));
					startActivity(intent);
				}
			});
		} else {

		}
	}

	// �ָ���������
	private void reset() {
		caseClosed.setSelected(false);
		noAccept.setSelected(false);
		processing.setSelected(false);
	}
}
