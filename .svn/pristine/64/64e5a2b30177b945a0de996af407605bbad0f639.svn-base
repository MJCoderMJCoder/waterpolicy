package com.lzf.waterpolicy.util;

import java.util.HashMap;
import java.util.Map;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import android.content.Context;

import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;

/**
 * 定位专用类
 */
public class LocationManager {

	private static Map<String, String> location = new HashMap<String, String>();
	private static AMapLocationClient mLocationClient = null;

	public static Map<String, String> getLocation(final Context context) {
		// 声明AMapLocationClient类对象，并初始化定位
		mLocationClient = new AMapLocationClient(context);
		// 声明定位回调监听器
		AMapLocationListener mLocationListener = new AMapLocationListener() {
			@Override
			public void onLocationChanged(AMapLocation aMapLocation) {
				if (aMapLocation != null) {
					if (aMapLocation.getErrorCode() == 0) {
						// 定位成功
						location.put("latitude", String.valueOf(aMapLocation.getLatitude())); // 获取纬度
						location.put("longitude", String.valueOf(aMapLocation.getLongitude())); // 获取经度
						location.put("accuracy", String.valueOf(aMapLocation.getAccuracy())); // 精度(米)
						location.put("address", aMapLocation.getAddress()); // 地址
						location.put("country", aMapLocation.getCountry()); // 国家信息
						location.put("province", aMapLocation.getProvince()); // 省信息
						location.put("city", aMapLocation.getCity()); // 城市信息
						location.put("district", aMapLocation.getDistrict()); // 城区信息
						location.put("street", aMapLocation.getStreet()); // 街道信息
						location.put("streetNum", aMapLocation.getStreetNum()); // 街道门牌号信息
						location.put("poiName", aMapLocation.getPoiName()); // 获取当前位置的POI名称
						location.put("aoiName", aMapLocation.getAoiName()); // 获取当前定位点的AOI信息
						location.put("locationDetail", aMapLocation.getLocationDetail()); // 获取定位信息描述
					} else {
						// 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息。
						location.put("Error：", aMapLocation.getErrorCode() + "：" + aMapLocation.getErrorInfo());
					}
				}

			}
		};
		// 设置定位回调监听
		mLocationClient.setLocationListener(mLocationListener);
		// 声明并初始化AMapLocationClientOption对象
		AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
		mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
		// 设置强制刷新WIFI。
		mLocationOption.setWifiActiveScan(true);
		// 给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		// 启动定位
		mLocationClient.startLocation();
		return location;
	}

	public static void stopLocation() {
		if (mLocationClient != null) {
			mLocationClient.stopLocation();// 停止定位后，本地定位服务并不会被销毁
			mLocationClient.onDestroy();// 销毁定位客户端，同时销毁本地定位服务。
		}
	}
}
