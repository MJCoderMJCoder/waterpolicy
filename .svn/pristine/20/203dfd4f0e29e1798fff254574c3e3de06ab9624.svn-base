package com.lzf.gallery.utils;

import com.lzf.gallery.utils.ImageLoader.Type;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	private final SparseArray<View> views;
	private int position;
	private View convertView;

	private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
		this.position = position;
		this.views = new SparseArray<View>();
		convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		// setTag
		convertView.setTag(this);
	}

	/**
	 * 拿到一个ViewHolder对象
	 */
	public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder(context, parent, layoutId, position);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.position = position;
		}
		return holder;
	}

	public View getConvertView() {
		return convertView;
	}

	/**
	 * 通过控件的Id获取对应的控件，如果没有则加入views
	 */
	public <T extends View> T getView(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * 为ImageView设置图片
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 */
	public ViewHolder setImageByUrl(int viewId, String url) {
		ImageLoader.getInstance(3, Type.LIFO).loadImage(url, (ImageView) getView(viewId));
		return this;
	}

	public int getPosition() {
		return position;
	}

}
