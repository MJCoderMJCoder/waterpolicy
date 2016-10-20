package com.lzf.waterpolicy;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MapActivity extends Activity {

	private MapView mapView;

	private Marker marker;

	private UiSettings uiSettings;

	private AMap aMap;

	private String address;
	private double latitude;
	private double longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);// 设置对应的XML布局文件

		Intent intent = getIntent();
		latitude = Double.parseDouble(intent.getStringExtra("latitude"));
		longitude = Double.parseDouble(intent.getStringExtra("longitude"));
		address = intent.getStringExtra("address");
		LatLng latLngLocation = new LatLng(latitude, longitude); // 定位的中心点坐标

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		if (aMap == null) {
			aMap = mapView.getMap();
		}
		uiSettings = aMap.getUiSettings();// 实例化UiSettings类
		uiSettings.setScaleControlsEnabled(true); // 显示比例尺控件
		aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLngLocation, 16, // 新的缩放级别
				0, // 俯仰角0°~45°（垂直与地图时为0）
				0 //// 偏航角 0~360° (正北方为0)
		)));

		marker = aMap.addMarker(new MarkerOptions().position(latLngLocation).draggable(true).title(address));
		marker.showInfoWindow();

		aMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				marker.hideInfoWindow();
				marker.setPosition(latLng);
				latitude = latLng.latitude;
				longitude = latLng.longitude;

				GeocodeSearch geocoderSearch = new GeocodeSearch(MapActivity.this);

				geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {

					@Override
					public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
						if (rCode == 1000) {
							if (result != null && result.getRegeocodeAddress() != null
									&& result.getRegeocodeAddress().getFormatAddress() != null) {
								address = result.getRegeocodeAddress().getFormatAddress() + "附近";
								marker.setTitle(address);
								marker.showInfoWindow();
							} else {
								Toast.makeText(MapActivity.this, "该位置暂无相关地址描述", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(MapActivity.this, "获取地址描述失败", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});
				RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 200,
						GeocodeSearch.AMAP);
				geocoderSearch.getFromLocationAsyn(query);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	public void back(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra("address", address);
		data.putExtra("latitude", String.valueOf(latitude));
		data.putExtra("longitude", String.valueOf(longitude));
		setResult(MapActivity.RESULT_OK, data);
		finish();
	}

}
