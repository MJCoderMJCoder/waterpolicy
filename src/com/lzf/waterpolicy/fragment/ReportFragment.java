package com.lzf.waterpolicy.fragment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.lzf.gallery.imgloder.GalleryActivity;
import com.lzf.gallery.utils.ImageLoader;
import com.lzf.gallery.utils.ImageLoader.Type;
import com.lzf.waterpolicy.MapActivity;
import com.lzf.waterpolicy.R;
import com.lzf.waterpolicy.bean.CaseClass;
import com.lzf.waterpolicy.bean.LawArea;
import com.lzf.waterpolicy.http.GetData;
import com.lzf.waterpolicy.http.OKHttp;
import com.lzf.waterpolicy.util.DateTime;
import com.lzf.waterpolicy.util.ReusableAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ReportFragment extends Fragment {

	private Map<String, String> location;
	private String serverCaseClass; // 服务端返回的案件类型信息
	private String serverLawArea; // 服务端返回的执法区域信息
	private String serverSubmit; // 服务端返回的提交信息
	private List<CaseClass> caseClasses;
	private List<LawArea> lawAreas;
	private String ip;

	private String id; // 用户ID
	private String fk_role_id; // 用户角色ID
	private String caseClassId; // 案件类型ID
	private String lawAreaId; // 执法区域ID
	private String latitude;
	private String longitude;
	private File currentImageFile;
	private File currentVideoFile;
	private Map<String, File> files = new HashMap<String, File>();
	private Map<String, String> params = new HashMap<String, String>();

	private final int CUSTOM_GALLERY = 0; // 选择图片标识
	private final int GALLERY = 1;
	private final int VIDEO = 2; // 选择视频标识
	private final int CASE_CLASS = 3; // 选择案件類型标识
	private final int SUBMIT = 4; // 选择案件類型标识
	private final int LAW_AREA = 5; // 选择执法区域标识
	private final int MAP_LOCATION = 6; // 地图地点选择标识（案件地点）
	private final int PHOTO_SD = 7; // 拍摄照片标识
	private final int VIDEO_SD = 8; // 拍摄视频标识

	private ProgressDialog progress = null;
	private EditText timeET;
	private EditText photoET;
	private EditText videoET;
	private EditText caseClassET;
	private EditText caseNameET;
	private EditText reportPersonNameET;
	private EditText siteET;
	private EditText briefET;
	private EditText lawAreaET; // 执法区域
	private Button submit;
	private AlertDialog aDialog;
	private TextView topTitle;
	private LinearLayout multimedia; // 多媒体选择器
	private TextView photograph;
	private TextView gallery;
	private TextView cancelPG;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CASE_CLASS:
				if (serverCaseClass.equals("fail")) {
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverCaseClass);
						if (jObject.getBoolean("success")) {
							JSONArray jArray = jObject.getJSONArray("data");
							caseClasses = new ArrayList<CaseClass>();
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject data = jArray.getJSONObject(i);
								CaseClass caseClass = new CaseClass(data.getString("id"), data.getString("name"));
								caseClasses.add(caseClass);
							}
						} else {
							Toast.makeText(getActivity(), "服务器异常。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					initCaseClassPopWindow(caseClassET, caseClasses);
				}
				break;
			case LAW_AREA:
				if (serverLawArea.equals("fail")) {
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverLawArea);
						if (jObject.getBoolean("success")) {
							JSONArray jArray = jObject.getJSONArray("data");
							lawAreas = new ArrayList<LawArea>();
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject data = jArray.getJSONObject(i);
								LawArea lawArea = new LawArea(data.getString("id"), data.getString("name"));
								lawAreas.add(lawArea);
							}
						} else {
							Toast.makeText(getActivity(), "服务器异常。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					initLawAreaPopWindow(lawAreaET, lawAreas);
				}
				break;
			case SUBMIT:
				if (progress != null) {
					progress.dismiss();
				}
				if (serverSubmit.equals("fail")) {
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverLawArea);
						if (jObject.getBoolean("success")) {
							Builder builder = new Builder(getActivity());
							aDialog = builder.setIcon(R.drawable.success).setTitle("提交成功")
									.setNegativeButton("继续提交", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											aDialog.dismiss();
										}
									}).setPositiveButton("返回主页", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											topTitle.setClickable(true);
											topTitle.setText("水政移动执法平台");
											topTitle.performClick();
										}
									}).setCancelable(false).create();
							aDialog.show(); // 显示对话框
						} else {
							Toast.makeText(getActivity(), "提交失败，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
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

	public ReportFragment(Map<String, String> location, TextView topTitle) {
		super();
		this.location = location;
		this.topTitle = topTitle;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_report, container, false);

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		ip = sp.getString("ip", "192.168.2.89:4001");
		id = sp.getString("id", "37");
		fk_role_id = sp.getString("fk_role_id", "0");

		multimedia = (LinearLayout) getActivity().findViewById(R.id.multimedia); // 多媒体选择器
		cancelPG = (TextView) getActivity().findViewById(R.id.cancelPG);
		gallery = (TextView) getActivity().findViewById(R.id.gallery); // 从本地库选择
		photograph = (TextView) getActivity().findViewById(R.id.photograph); // 拍照/拍摄视频
		cancelPG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				multimedia.setVisibility(View.GONE);
			}
		});
		gallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (photoET.hasFocus()) {
					getPhoto();
				} else if (videoET.hasFocus()) {
					getVideo();
				}
				multimedia.setVisibility(View.GONE);
			}
		});
		photograph.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (photoET.hasFocus()) {
					photograph();
				} else if (videoET.hasFocus()) {
					video();
				}
				multimedia.setVisibility(View.GONE);
			}
		});

		// 案发地点
		siteET = ((EditText) view.findViewById(R.id.siteET));
		siteET.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					Intent intent = new Intent(getActivity(), MapActivity.class);
					intent.putExtra("latitude", location.get("latitude"));
					intent.putExtra("longitude", location.get("longitude"));
					intent.putExtra("address", location.get("address"));
					startActivityForResult(intent, MAP_LOCATION);
				}
			}
		});
		siteET.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), MapActivity.class);
				intent.putExtra("latitude", location.get("latitude"));
				intent.putExtra("longitude", location.get("longitude"));
				intent.putExtra("address", location.get("address"));
				startActivityForResult(intent, MAP_LOCATION);
			}
		});
		siteET.setText(location.get("address"));
		latitude = location.get("latitude");
		longitude = location.get("longitude");

		// 案发时间
		timeET = (EditText) view.findViewById(R.id.timeET);
		timeET.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					DateTime.getDate(getActivity(), timeET, timeET.getText().toString().trim());
				}
			}
		});
		timeET.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateTime.getDate(getActivity(), timeET, timeET.getText().toString().trim());
			}
		});

		// 上传照片
		photoET = (EditText) view.findViewById(R.id.photoET);
		photoET.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					multimedia.setVisibility(View.VISIBLE);
					// getPhoto();
				}
			}
		});
		photoET.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				multimedia.setVisibility(View.VISIBLE);
				// getPhoto();
			}
		});

		// 上传视频
		videoET = (EditText) view.findViewById(R.id.videoET);
		videoET.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					multimedia.setVisibility(View.VISIBLE);
					// getVideo();
				}
			}
		});
		videoET.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				multimedia.setVisibility(View.VISIBLE);
				// getVideo();
			}
		});

		// 选择案件类型
		caseClassET = (EditText) view.findViewById(R.id.caseClassET);
		caseClassET.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					new Thread() {
						public void run() {
							if (serverCaseClass == null || serverCaseClass.equals("")) {
								serverCaseClass = GetData.getHtml("http://" + ip + "/Phone/Phone/DataMGAction?lxid=91");
							}
							handler.sendEmptyMessage(CASE_CLASS);
						}
					}.start();
				}
			}
		});
		caseClassET.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new Thread() {
					public void run() {
						if (serverCaseClass == null || serverCaseClass.equals("")) {
							serverCaseClass = GetData.getHtml("http://" + ip + "/Phone/Phone/DataMGAction?lxid=91");
						}
						handler.sendEmptyMessage(CASE_CLASS);
					}
				}.start();
			}
		});

		// 选择执法区域
		lawAreaET = (EditText) view.findViewById(R.id.lawAreaET);
		lawAreaET.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					new Thread() {
						public void run() {
							if (serverLawArea == null || serverLawArea.equals("")) {
								serverLawArea = GetData
										.getHtml("http://" + ip + "/Phone/Phone/LegalJobAreaAction?id=37");
							}
							handler.sendEmptyMessage(LAW_AREA);
						}
					}.start();
				}
			}
		});
		lawAreaET.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new Thread() {
					public void run() {
						if (serverLawArea == null || serverLawArea.equals("")) {
							serverLawArea = GetData.getHtml("http://" + ip + "/Phone/Phone/LegalJobAreaAction?id=37");
						}
						handler.sendEmptyMessage(LAW_AREA);
					}
				}.start();
			}
		});

		// 提交
		submit = (Button) view.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				caseNameET = ((EditText) view.findViewById(R.id.caseNameET));
				String caseName = caseNameET.getText().toString();
				String caseClass = caseClassET.getText().toString();
				reportPersonNameET = ((EditText) view.findViewById(R.id.reportPersonNameET));
				String reportPersonName = reportPersonNameET.getText().toString();
				String site = siteET.getText().toString();
				String date = timeET.getText().toString();
				String photo = photoET.getText().toString();
				String video = videoET.getText().toString();
				briefET = ((EditText) view.findViewById(R.id.briefET));
				String btief = briefET.getText().toString();
				String lawArea = lawAreaET.getText().toString();
				if (caseName.equals("") || caseName == null) {
					Toast.makeText(getActivity(), "案件名称不能为空。", Toast.LENGTH_SHORT).show();
					caseNameET.requestFocus();
				} else if (caseClass.equals("") || caseClass == null) {
					Toast.makeText(getActivity(), "请选择案件类型。", Toast.LENGTH_SHORT).show();
					caseClassET.requestFocus();
				} else if (reportPersonName.equals("") || reportPersonName == null) {
					Toast.makeText(getActivity(), "上报人姓名不能为空。", Toast.LENGTH_SHORT).show();
					reportPersonNameET.requestFocus();
				} else if (site.equals("") || site == null) {
					Toast.makeText(getActivity(), "案发地点不能为空。", Toast.LENGTH_SHORT).show();
					siteET.requestFocus();
				} else if (date.equals("") || date == null) {
					Toast.makeText(getActivity(), "案发时间不能为空。", Toast.LENGTH_SHORT).show();
					timeET.requestFocus();
				} else if (photo.equals("") || photo == null) {
					Toast.makeText(getActivity(), "请上传照片。", Toast.LENGTH_SHORT).show();
					photoET.requestFocus();
				} else if (btief.equals("") || btief == null) {
					Toast.makeText(getActivity(), "案发简要不能为空。", Toast.LENGTH_SHORT).show();
					briefET.requestFocus();
				} else if (lawArea.equals("") || lawArea == null) {
					Toast.makeText(getActivity(), "请选择执法区域。", Toast.LENGTH_SHORT).show();
					lawAreaET.requestFocus();
				} else {
					progress = ProgressDialog.show(getActivity(), null, "正在提交...", true, false);
					params.put("id", id);
					params.put("fk_role_id", fk_role_id);
					params.put("caseName", caseName);
					params.put("caseClassId", caseClassId);
					params.put("reportPersonName", reportPersonName);
					params.put("site", site);
					params.put("date", date);
					params.put("btief", btief);
					params.put("LegalJobAreaId", lawAreaId);
					params.put("photoPath", photo);
					params.put("videoPath", video);

					LatLng baidu = aMapToBaidu(Double.parseDouble(latitude), Double.parseDouble(longitude));

					params.put("lat", String.valueOf(baidu.latitude));
					params.put("lon", String.valueOf(baidu.longitude));
					System.out.println(params);
					System.out.println(files);
					new Thread() {
						public void run() {
							serverSubmit = OKHttp.submit("http://" + ip + "/Phone/Phone/CaseAction", params, files);
							handler.sendEmptyMessage(SUBMIT);
						}
					}.start();
				}
			}
		});

		return view;
	}

	// 选择视频
	private void getVideo() {
		// 安卓机没问题（单兵设备不可以）ACTION_GET_CONTENT
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("video/*");
		if ((intent.resolveActivity(getActivity().getPackageManager()) != null)) {
			startActivityForResult(intent, VIDEO);
		} else {
			Toast.makeText(getActivity(), "抱歉，当前设备暂不支持该功能。", Toast.LENGTH_SHORT).show();
		}
	}

	// 选择照片
	private void getPhoto() {
		// 安卓机没问题（单兵设备不可以）ACTION_PICK
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		if ((intent.resolveActivity(getActivity().getPackageManager()) != null)) {
			startActivityForResult(intent, GALLERY);
		} else {
			Intent intentTemp = new Intent(getActivity(), GalleryActivity.class);
			startActivityForResult(intentTemp, CUSTOM_GALLERY);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case GALLERY:
				Uri selectedImage = data.getData(); // 获取系统返回的照片的Uri
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null,
						null); // 从系统表中查询指定Uri对应的照片
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex); // 获取照片路径
				cursor.close();
				files.put("photo", new File(picturePath));
				photoET.setText(picturePath);
				break;
			case CUSTOM_GALLERY:
				String imgPath = data.getStringExtra("imgPath");
				files.put("photo", new File(imgPath));
				photoET.setText(imgPath);
				break;
			case VIDEO:
				Uri selectedVideo = data.getData(); // 获取系统返回的照片的Uri
				String[] videoPathColumn = { MediaStore.Images.Media.DATA };
				Cursor videoCursor = getActivity().getContentResolver().query(selectedVideo, videoPathColumn, null,
						null, null); // 从系统表中查询指定Uri对应的照片
				videoCursor.moveToFirst();
				int vColumnIndex = videoCursor.getColumnIndex(videoPathColumn[0]);
				String videoPath = videoCursor.getString(vColumnIndex); // 获取照片路径
				videoCursor.close();
				files.put("video", new File(videoPath));
				videoET.setText(videoPath);
				break;
			case MAP_LOCATION:
				siteET.setText(data.getStringExtra("address"));
				latitude = data.getStringExtra("latitude");
				longitude = data.getStringExtra("longitude");
				break;
			case PHOTO_SD:
				String currentImagePath = currentImageFile.getAbsolutePath();
				files.put("photo", currentImageFile);
				photoET.setText(currentImagePath);
				break;
			case VIDEO_SD:
				String currentVideoPath = currentVideoFile.getAbsolutePath();
				files.put("video", currentVideoFile);
				videoET.setText(currentVideoPath);
				break;
			default:
				break;
			}
		}

	}

	// 弹出框
	protected void initCaseClassPopWindow(final EditText v, final List<CaseClass> list) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_window, null, false);
		ListView lView = (ListView) view.findViewById(R.id.popupList);
		ReusableAdapter<CaseClass> adapter = new ReusableAdapter<CaseClass>(list, R.layout.item_popup) {
			@Override
			public void bindView(ViewHolder holder, CaseClass obj) {
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
				caseClassId = list.get(position).getId();
				v.setText(list.get(position).getName());
				popWindow.dismiss();
			}
		});
	}

	// 执法区域弹出框
	protected void initLawAreaPopWindow(final EditText v, final List<LawArea> list) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_window, null, false);
		ListView lView = (ListView) view.findViewById(R.id.popupList);
		ReusableAdapter<LawArea> adapter = new ReusableAdapter<LawArea>(list, R.layout.item_popup) {
			@Override
			public void bindView(ViewHolder holder, LawArea obj) {
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
				lawAreaId = list.get(position).getId();
				v.setText(list.get(position).getName());
				popWindow.dismiss();
			}
		});
	}

	// 将amap地图等所用坐标转换成百度坐标
	private LatLng aMapToBaidu(double lat, double lng) {
		SDKInitializer.initialize(getActivity().getApplicationContext());
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.COMMON);
		// sourceLatLng待转换坐标
		converter.coord(new LatLng(lat, lng));
		return (converter.convert());
	}

	@SuppressLint("NewApi")
	private void photograph() { // 拍照方法
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory(), "photograph");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			currentImageFile = new File(dir, System.currentTimeMillis() + ".jpg");
			if (!currentImageFile.exists()) {
				try {
					currentImageFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
			startActivityForResult(it, PHOTO_SD);
		} else {
			String dirTemp = null;
			StorageManager storageManager = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
			Class<?>[] paramClasses = {};
			Method getVolumePathsMethod;
			try {
				getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
				getVolumePathsMethod.setAccessible(true);
				Object[] params = {};
				Object invoke = getVolumePathsMethod.invoke(storageManager, params);
				for (int i = 0; i < ((String[]) invoke).length; i++) {
					if (!((String[]) invoke)[i].equals(Environment.getExternalStorageDirectory().toString())) {
						dirTemp = ((String[]) invoke)[i];
					}
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			System.out.println("The default memory：" + dirTemp);
			File dir = new File(dirTemp, "photograph");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			currentImageFile = new File(dir, System.currentTimeMillis() + ".jpg");
			if (!currentImageFile.exists()) {
				try {
					currentImageFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
			startActivityForResult(it, PHOTO_SD);
		}
	}

	@SuppressLint("NewApi")
	private void video() { // 拍摄视频
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory(), "photograph");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			currentVideoFile = new File(dir, System.currentTimeMillis() + ".mp4");
			if (!currentVideoFile.exists()) {
				try {
					currentVideoFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent it = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentVideoFile));
			startActivityForResult(it, VIDEO_SD);
		} else {
			String dirTemp = null;
			StorageManager storageManager = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
			Class<?>[] paramClasses = {};
			Method getVolumePathsMethod;
			try {
				getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
				getVolumePathsMethod.setAccessible(true);
				Object[] params = {};
				Object invoke = getVolumePathsMethod.invoke(storageManager, params);
				for (int i = 0; i < ((String[]) invoke).length; i++) {
					if (!((String[]) invoke)[i].equals(Environment.getExternalStorageDirectory().toString())) {
						dirTemp = ((String[]) invoke)[i];
					}
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			System.out.println("The default memory：" + dirTemp);
			File dir = new File(dirTemp, "photograph");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			currentVideoFile = new File(dir, System.currentTimeMillis() + ".mp4");
			if (!currentVideoFile.exists()) {
				try {
					currentVideoFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent it = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentVideoFile));
			startActivityForResult(it, VIDEO_SD);
		}
	}
}
