package com.lzf.gallery.imgloder;

import java.util.List;

import com.lzf.gallery.utils.CommonAdapter;
import com.lzf.gallery.utils.ViewHolder;
import com.lzf.waterpolicy.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MyAdapter extends CommonAdapter<String> {

	private GalleryActivity galleryActivity;
	private String mDirPath; // �ļ���·��

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId, String dirPath, Activity galleryActivity) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.galleryActivity = (GalleryActivity) galleryActivity;
	}

	@Override
	public void convert(final ViewHolder helper, final String item) {
		helper.setImageResource(R.id.itemImg, R.drawable.pictures_no); // ����no_pic
		helper.setImageByUrl(R.id.itemImg, mDirPath + "/" + item); // ����ͼƬ

		final ImageView mImageView = helper.getView(R.id.itemImg);
		mImageView.setColorFilter(null);
		// ����ImageView�ĵ���¼�
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("imgPath", mDirPath + "/" + item);
				galleryActivity.setResult(galleryActivity.RESULT_OK, resultIntent);
				galleryActivity.finish();
			}
		});
	}
}
