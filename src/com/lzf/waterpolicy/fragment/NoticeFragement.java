package com.lzf.waterpolicy.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.col.di.j;
import com.amap.api.col.v;
import com.lzf.waterpolicy.LawsActivity;
import com.lzf.waterpolicy.NoticeActivity;
import com.lzf.waterpolicy.R;
import com.lzf.waterpolicy.bean.Laws;
import com.lzf.waterpolicy.bean.Notice;
import com.lzf.waterpolicy.http.GetData;
import com.lzf.waterpolicy.http.OKHttp;
import com.lzf.waterpolicy.util.DateTime;
import com.lzf.waterpolicy.util.ReusableAdapter;
import com.lzf.waterpolicy.util.ReusableAdapter.ViewHolder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class NoticeFragement extends Fragment {

	private String serverReleaseNotice; // ����˷��صķ���ʱ��
	private String ip;
	private int totalCount;

	private View footerView;
	private ListView notices;
	private EditText releaseTime;
	private EditText releaseTime0;
	private EditText titleET;
	private Button query;
	private ProgressDialog progress = null;

	private final int QUERY = 0;
	private final int LOADING = 1; // �������ظ���
	private int page;
	private List<Notice> noticeData = new ArrayList<Notice>();
	private ReusableAdapter<Notice> adapter;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case QUERY:
				if (progress != null) {
					progress.dismiss();
				}
				if (serverReleaseNotice.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverReleaseNotice);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							noticeData.removeAll(noticeData);
							if (jArray.length() <= 0) {
								Toast.makeText(getActivity(), "��������", Toast.LENGTH_SHORT).show();
							} else {
								for (int i = 0; i < jArray.length(); i++) {
									JSONObject jData = jArray.getJSONObject(i);
									noticeData.add(new Notice(jData.getString("Id"), jData.getString("Ndes"),
											jData.getString("Ndate"), jData.getString("Nnumber"),
											jData.getString("Ntitle")));
								}
							}
							showListView(noticeData);
						} else {
							Toast.makeText(getActivity(), "�������쳣��", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case LOADING:
				if (serverReleaseNotice.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverReleaseNotice);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								adapter.add(new Notice(jData.getString("Id"), jData.getString("Ndes"),
										jData.getString("Ndate"), jData.getString("Nnumber"),
										jData.getString("Ntitle")));
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
		View view = inflater.inflate(R.layout.fragment_notice, container, false);

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		ip = sp.getString("ip", "192.168.2.89:4001");

		// �������ѯ
		titleET = (EditText) view.findViewById(R.id.titleET);
		titleET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				showListView(null);
			}
		});

		// ������ʱ���ѯ
		releaseTime = (EditText) view.findViewById(R.id.releaseTime);
		releaseTime.addTextChangedListener(new TextWatcher() {
			String temp;

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				temp = releaseTime.getText().toString().trim();
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (!temp.equals(releaseTime.getText().toString().trim())) {
					showListView(null);
				}
			}
		});
		releaseTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					DateTime.getDate(getActivity(), releaseTime, releaseTime.getText().toString().trim());
				}
			}
		});
		releaseTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateTime.getDate(getActivity(), releaseTime, releaseTime.getText().toString().trim());
			}
		});
		releaseTime0 = (EditText) view.findViewById(R.id.releaseTime0);
		releaseTime0.addTextChangedListener(new TextWatcher() {
			String temp;

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				temp = releaseTime0.getText().toString().trim();
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (!temp.equals(releaseTime0.getText().toString().trim())) {
					showListView(null);
				}
			}
		});
		releaseTime0.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					DateTime.getDate(getActivity(), releaseTime0, releaseTime0.getText().toString().trim());
				}
			}
		});
		releaseTime0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateTime.getDate(getActivity(), releaseTime0, releaseTime0.getText().toString().trim());
			}
		});

		// �����ѯ
		query = (Button) view.findViewById(R.id.query);
		query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				page = 0;
				loading(true);
			}
		});

		notices = (ListView) view.findViewById(R.id.notices);
		footerView = LayoutInflater.from(getActivity()).inflate(R.layout.loading, null, false);
		notices.addFooterView(footerView);
		notices.setOnScrollListener(new OnScrollListener() {

			/** ��¼��һ��Item����ֵ */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// ���������ײ�ʱ
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem != 0) {
					if (totalCount > noticeData.size()) {
						loading(false);
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
				if (visibleItemCount == totalItemCount || totalCount <= noticeData.size()) {
					// removeFooterView(footerView);
					footerView.setVisibility(View.GONE);// ���صײ�����
				} else {
					// addFooterView(footerView);
					footerView.setVisibility(View.VISIBLE);// ��ʾ�ײ�����
				}
			}
		});

		query.performClick();
		return view;
	}

	private void showListView(final List<Notice> noticeData) {
		adapter = new ReusableAdapter<Notice>(noticeData, R.layout.item_law_list) {
			@Override
			public void bindView(ViewHolder holder, Notice obj) {
				holder.setText(R.id.lawsName, obj.getNtitle());
				holder.setText(R.id.lawsDate, obj.getNdate());
			}
		};
		notices.setAdapter(adapter);
		notices.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long l) {
				Intent intent = new Intent(getActivity(), NoticeActivity.class);
				intent.putExtra("notice", noticeData.get(position));
				startActivity(intent);
			}
		});
	}

	private void loading(final boolean flag) {
		page++;
		final String start = releaseTime.getText().toString().trim(); // С����ʼʱ�䣩
		final String end = releaseTime0.getText().toString().trim(); // �󣨽���ʱ�䣩
		final String title = titleET.getText().toString().trim(); // �󣨽���ʱ�䣩
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
			Toast.makeText(getActivity(), "����ʱ�䲻�����ڿ�ʼʱ��", Toast.LENGTH_SHORT).show();
		} else {
			if (flag) {
				progress = ProgressDialog.show(getActivity(), null, "���ڲ�ѯ...", true, false);
			}
			new Thread() {
				public void run() {
					serverReleaseNotice = OKHttp
							.getData("http://" + ip + "/Phone/Phone/NoticeAction?lxid=29&StateLTime=" + start
									+ "&EndLTime=" + end + "&limit=10&start=" + page + "&Ntitle=" + title);
					if (flag) {
						handler.sendEmptyMessage(QUERY);
					} else {
						handler.sendEmptyMessage(LOADING);
					}
				}
			}.start();
		}
	}
}
