package com.lzf.gallery.imgloder;

import java.util.List;

import com.lzf.gallery.bean.ImgFolder;
import com.lzf.gallery.utils.BasePopupWindowForListView;
import com.lzf.gallery.utils.CommonAdapter;
import com.lzf.gallery.utils.ViewHolder;
import com.lzf.waterpolicy.R;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ImgFolderPopup extends BasePopupWindowForListView<ImgFolder> {
	private ListView mListDir;

	public ImgFolderPopup(int width, int height, List<ImgFolder> datas, View convertView) {
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews() {
		mListDir = (ListView) findViewById(R.id.popupList);
		mListDir.setAdapter(new CommonAdapter<ImgFolder>(context, mDatas, R.layout.item_folderlist) {
			@Override
			public void convert(ViewHolder helper, ImgFolder item) {
				helper.setText(R.id.folderName, item.getName());
				helper.setImageByUrl(R.id.imgFolder, item.getFirstImagePath());
				helper.setText(R.id.folderImgTotal, item.getCount() + "уе");
			}
		});
	}

	public interface OnImageDirSelected {
		void selected(ImgFolder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents() {
		mListDir.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (mImageDirSelected != null) {
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init() {

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params) {
	}

}
