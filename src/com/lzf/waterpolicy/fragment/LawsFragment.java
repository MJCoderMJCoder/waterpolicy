package com.lzf.waterpolicy.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.col.di.j;
import com.lzf.waterpolicy.LawsActivity;
import com.lzf.waterpolicy.R;
import com.lzf.waterpolicy.bean.LawSubject;
import com.lzf.waterpolicy.bean.Laws;
import com.lzf.waterpolicy.http.GetData;
import com.lzf.waterpolicy.http.OKHttp;
import com.lzf.waterpolicy.util.DateTime;
import com.lzf.waterpolicy.util.ReusableAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class LawsFragment extends Fragment {

	private String serverSubjectClass; // ����˷��ص���������
	private String serverLaw; // ����˷��صķ��ɷ���
	private int totalCount;
	private String ip;

	private View footerView;
	private ListView laws;
	private EditText subjectClass;
	private EditText releaseTime;
	private EditText releaseTime0;
	private EditText titleET;
	private Button query;
	private ProgressDialog progress = null;

	private final int SUBJECT_CLASS = 0;
	private final int QUERY = 1;
	private final int LOADING = 2; // ���������У����ظ��ࣩ
	private int page;
	private String lawSubjectId = "";
	private List<LawSubject> lawSubjectes;
	private List<Laws> lawsData = new ArrayList<Laws>();
	private ReusableAdapter<Laws> adapter;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUBJECT_CLASS:
				if (serverSubjectClass.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverSubjectClass);
						if (jObject.getBoolean("success")) {
							JSONArray jArray = jObject.getJSONArray("data");
							lawSubjectes = new ArrayList<LawSubject>();
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								lawSubjectes.add(new LawSubject(jData.getString("id"), jData.getString("name")));
							}
						} else {
							Toast.makeText(getActivity(), "�������쳣��", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					initPopWindow(subjectClass, lawSubjectes);
				}
				break;
			case QUERY:
				if (progress != null) {
					progress.dismiss();
				}
				if (serverLaw.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverLaw);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							lawsData.removeAll(lawsData);
							if (jArray.length() <= 0) {
								Toast.makeText(getActivity(), "��������", Toast.LENGTH_SHORT).show();
							} else {
								for (int i = 0; i < jArray.length(); i++) {
									JSONObject jData = jArray.getJSONObject(i);
									lawsData.add(new Laws(jData.getString("Lid"), jData.getString("LText"),
											jData.getString("LTime"), jData.getString("LTitle"),
											jData.getString("lxname")));
								}
							}
							showListView(lawsData);
						} else {
							Toast.makeText(getActivity(), "�������쳣��", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case LOADING: // ���������У����ظ��ࣩ
				if (serverLaw.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverLaw);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								adapter.add(new Laws(jData.getString("Lid"), jData.getString("LText"),
										jData.getString("LTime"), jData.getString("LTitle"),
										jData.getString("lxname")));
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
		View view = inflater.inflate(R.layout.fragment_laws, container, false);

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

		// ����������ѯ
		subjectClass = (EditText) view.findViewById(R.id.subjectClassET);
		subjectClass.addTextChangedListener(new TextWatcher() {
			String temp;

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				temp = subjectClass.getText().toString().trim();
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (!temp.equals(subjectClass.getText().toString().trim())) {
					showListView(null);
				}
			}
		});
		subjectClass.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					new Thread() {
						public void run() {
							if (serverSubjectClass == null || serverSubjectClass.equals("")) {
								serverSubjectClass = GetData
										.getHtml("http://" + ip + "/Phone/Phone/DataMGAction?lxid=27");
							}
							handler.sendEmptyMessage(SUBJECT_CLASS);
						}
					}.start();
				}
			}
		});
		subjectClass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new Thread() {
					public void run() {
						if (serverSubjectClass == null || serverSubjectClass.equals("")) {
							serverSubjectClass = GetData.getHtml("http://" + ip + "/Phone/Phone/DataMGAction?lxid=27");
						}
						handler.sendEmptyMessage(SUBJECT_CLASS);
					}
				}.start();
			}
		});

		// �����ѯ
		query = (Button) view.findViewById(R.id.query);
		query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				page = 0; // һ��Ҫ��ʼ��page
				loading(true);
			}
		});
		query.performClick();

		// ���ɷ����б�
		laws = (ListView) view.findViewById(R.id.laws);
		footerView = LayoutInflater.from(getActivity()).inflate(R.layout.loading, null, false);
		laws.addFooterView(footerView);
		laws.setOnScrollListener(new OnScrollListener() {

			/** ��¼��һ��Item����ֵ */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// ���������ײ�ʱ
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem != 0) {
					if (totalCount > lawsData.size()) {
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
				if (visibleItemCount == totalItemCount || totalCount <= lawsData.size()) {
					// removeFooterView(footerView);
					footerView.setVisibility(View.GONE);// ���صײ�����
				} else {
					// addFooterView(footerView);
					footerView.setVisibility(View.VISIBLE);// ��ʾ�ײ�����
				}
			}
		});

		return view;

	}

	private void showListView(final List<Laws> lawsData) {
		adapter = new ReusableAdapter<Laws>(lawsData, R.layout.item_law_list) {
			@Override
			public void bindView(ViewHolder holder, Laws obj) {
				holder.setText(R.id.lawsName, obj.getLTitle());
				holder.setText(R.id.lawsDate, obj.getLTime());
			}
		};
		laws.setAdapter(adapter);
		laws.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long l) {
				Intent intent = new Intent(getActivity(), LawsActivity.class);
				intent.putExtra("law", lawsData.get(position));
				startActivity(intent);
			}
		});
	}

	// ������
	protected void initPopWindow(final TextView v, final List<LawSubject> list) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_window, null, false);
		ListView lView = (ListView) view.findViewById(R.id.popupList);
		ReusableAdapter<LawSubject> adapter = new ReusableAdapter<LawSubject>(list, R.layout.item_popup) {
			@Override
			public void bindView(ViewHolder holder, LawSubject obj) {
				holder.setText(R.id.textPopup, obj.getName());
			}

		};
		lView.setAdapter(adapter);

		final PopupWindow popWindow = new PopupWindow(view, v.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);

		// ��ЩΪ�˵����PopupWindow����PopupWindow����ʧ��
		popWindow.setTouchable(true);
		popWindow.setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
				// �����������true�Ļ���touch�¼���������
				// ���غ� PopupWindow��onTouchEvent�������ã���������ⲿ�����޷�dismiss
			}
		});
		popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000)); // ҪΪpopWindow����һ����������Ч
		popWindow.showAsDropDown(v, 0, 0);

		lView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				lawSubjectId = list.get(position).getId();
				v.setText(list.get(position).getName());
				popWindow.dismiss();
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
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		if (!(start.equals("")) && start != null && !(end.equals("")) && end != null && dateS.after(dateE)) {
			Toast.makeText(getActivity(), "����ʱ�䲻�����ڿ�ʼʱ��", Toast.LENGTH_SHORT).show();
		} else {
			if (flag) {
				progress = ProgressDialog.show(getActivity(), null, "���ڲ�ѯ...", true, false);
			}
			new Thread() {
				public void run() {
					String str = "http://" + ip + "/Phone/Phone/LawsRegulationAction?lxid=" + lawSubjectId
							+ "&StateLTime=" + start + "&EndLTime=" + end + "&limit=10&start=" + page + "&LTitle="
							+ title;
					System.out.println(str);
					serverLaw = OKHttp.getData(str);
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
