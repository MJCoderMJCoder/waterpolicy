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
	 * �õ�һ��ViewHolder����
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
	 * ͨ���ؼ���Id��ȡ��Ӧ�Ŀؼ������û�������views
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
	 * ΪTextView�����ַ���
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * ΪImageView����ͼƬ
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * ΪImageView����ͼƬ
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * ΪImageView����ͼƬ
	 */
	public ViewHolder setImageByUrl(int viewId, String url) {
		ImageLoader.getInstance(3, Type.LIFO).loadImage(url, (ImageView) getView(viewId));
		return this;
	}

	public int getPosition() {
		return position;
	}

}
