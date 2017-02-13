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

	private String serverSubjectClass; // 服务端返回的主题类型
	private String serverLaw; // 服务端返回的法律法规
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
	private final int LOADING = 2; // 上拉加载中（加载更多）
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
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(getActivity(), "服务器异常。", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverLaw);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							lawsData.removeAll(lawsData);
							if (jArray.length() <= 0) {
								Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(getActivity(), "服务器异常。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case LOADING: // 下拉加载中（加载更多）
				if (serverLaw.equals("fail")) {
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(getActivity(), "服务器异常。", Toast.LENGTH_SHORT).show();
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

		// 按标题查询
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

		// 按发布时间查询
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

		// 按主题分类查询
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

		// 点击查询
		query = (Button) view.findViewById(R.id.query);
		query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				page = 0; // 一定要初始化page
				loading(true);
			}
		});
		query.performClick();

		// 法律法规列表
		laws = (ListView) view.findViewById(R.id.laws);
		footerView = LayoutInflater.from(getActivity()).inflate(R.layout.loading, null, false);
		laws.addFooterView(footerView);
		laws.setOnScrollListener(new OnScrollListener() {

			/** 记录第一行Item的数值 */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当滑动到底部时
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem != 0) {
					if (totalCount > lawsData.size()) {
						loading(false);
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
				if (visibleItemCount == totalItemCount || totalCount <= lawsData.size()) {
					// removeFooterView(footerView);
					footerView.setVisibility(View.GONE);// 隐藏底部布局
				} else {
					// addFooterView(footerView);
					footerView.setVisibility(View.VISIBLE);// 显示底部布局
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

	// 弹出框
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

		// 这些为了点击非PopupWindow区域，PopupWindow会消失的
		popWindow.setTouchable(true);
		popWindow.setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});
		popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000)); // 要为popWindow设置一个背景才有效
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
		final String start = releaseTime.getText().toString().trim(); // 小（开始时间）
		final String end = releaseTime0.getText().toString().trim(); // 大（结束时间）
		final String title = titleET.getText().toString().trim(); // 大（结束时间）
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
			Toast.makeText(getActivity(), "结束时间不能早于开始时间", Toast.LENGTH_SHORT).show();
		} else {
			if (flag) {
				progress = ProgressDialog.show(getActivity(), null, "正在查询...", true, false);
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
