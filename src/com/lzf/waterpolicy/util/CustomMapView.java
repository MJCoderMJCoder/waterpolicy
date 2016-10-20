package com.lzf.waterpolicy.util;

import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.MapView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * ��ͼ��ʾ��
 */
public class CustomMapView extends MapView {

	public CustomMapView(Context arg0) {
		super(arg0);
		init(arg0);
	}

	public CustomMapView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		init(arg0);
	}

	public CustomMapView(Context arg0, AMapOptions arg1) {
		super(arg0, arg1);
		init(arg0);
	}

	public CustomMapView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		init(arg0);
	}

	private void init(Context context) {
		// view�������ʱ�ص�
		this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ViewGroup child = (ViewGroup) getChildAt(0);// ��ͼ���
				child.getChildAt(2).setVisibility(View.GONE);// logo
			}
		});
	}

}
