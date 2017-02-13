package com.lzf.gallery.imgloder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.lzf.gallery.bean.ImgFolder;
import com.lzf.gallery.imgloder.ImgFolderPopup.OnImageDirSelected;
import com.lzf.waterpolicy.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryActivity extends Activity implements OnImageDirSelected {

	private ProgressDialog progress = null;

	private File tempImgFolder; // 临时的文件夹
	private List<String> tempImgs; // 临时存储对应文件夹下的所有图片
	private ImgFolder imgMixFolder = new ImgFolder(null, null, null, 0); // 图片数量最多的文件夹

	private HashSet<String> tempFolderPathes = new HashSet<String>(); // 临时的辅助类，用于防止同一个文件夹的多次扫描
	private List<ImgFolder> imgFloders = new ArrayList<ImgFolder>(); // 扫描拿到所有的图片文件夹

	private MyAdapter myAdapter;
	private GridView gridView;
	private TextView folder;
	private TextView imgTotal;

	private int screenHeight;
	private final int IMG_SCAN = 1; // 图片扫描的标识

	private ImgFolderPopup imgFolderPopup;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case IMG_SCAN:
				if (progress != null) {
					progress.dismiss();
				}
				initGridView(); // 为GridView初始化数据
				initImgFolderPopup(); // 初始化展示文件夹的popupWindw
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 为GridView初始化数据
	 */
	private void initGridView() {
		if (imgMixFolder.getCount() <= 0) {
			Toast.makeText(getApplicationContext(), "您手机内暂无图片", Toast.LENGTH_SHORT).show();
			return;
		}
		selected(imgMixFolder);
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initImgFolderPopup() {
		imgFolderPopup = new ImgFolderPopup(LayoutParams.MATCH_PARENT, (int) (screenHeight * 0.7), imgFloders,
				LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_window, null));

		imgFolderPopup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		imgFolderPopup.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		// 获取手机屏幕分辨率的类
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenHeight = outMetrics.heightPixels;

		gridView = (GridView) findViewById(R.id.imgGrid);
		folder = (TextView) findViewById(R.id.imgFolder);
		imgTotal = (TextView) findViewById(R.id.imgTotal);

		getImages();
		initEvent();

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "没有检测到SD卡", Toast.LENGTH_SHORT).show();
			return;
		}
		progress = ProgressDialog.show(this, null, "正在扫描...");
		new Thread(new Runnable() {
			@Override
			public void run() {

				Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver contentResolver = GalleryActivity.this.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor cursor = contentResolver.query(imgUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png", "image/jpg" }, MediaStore.Images.Media.DATE_MODIFIED);
				while (cursor.moveToNext()) {
					// 获取图片的路径
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImgFolder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (tempFolderPathes.contains(dirPath)) {
						continue;
					} else {
						tempFolderPathes.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImgFolder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					/**
					 * 如果抽象路径名不表示一个目录，或者发生 I/O 错误，则返回 null。
					 * 抛出SecurityException：拒绝对目录进行读访问
					 */
					int picSize = (parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")) {
								return true;
							} else {
								return false;
							}
						}
					})).length;

					imageFloder.setCount(picSize);
					imgFloders.add(imageFloder);

					if (picSize > imgMixFolder.getCount()) {
						imgMixFolder = imageFloder;
					}
				}
				cursor.close();
				// 扫描完成，辅助的HashSet也就可以释放内存了
				tempFolderPathes = null;
				handler.sendEmptyMessage(IMG_SCAN);
			}
		}).start();

	}

	private void initEvent() {
		/**
		 * 为底部的folder设置点击事件，弹出popupWindow
		 */
		folder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imgFolderPopup.showAsDropDown(folder, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImgFolder floder) {
		tempImgFolder = new File(floder.getDir());
		tempImgs = Arrays.asList(tempImgFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")) {
					return true;
				} else {
					return false;
				}
			}
		}));
		/**
		 * 文件夹的路径和图片的路径分开保存，以便减少内存的消耗；
		 */
		myAdapter = new MyAdapter(getApplicationContext(), tempImgs, R.layout.item_imgrid,
				tempImgFolder.getAbsolutePath(), this);
		gridView.setAdapter(myAdapter);
		imgTotal.setText(floder.getCount() + "张");
		folder.setText(floder.getName());
		if (imgFolderPopup != null) {
			imgFolderPopup.dismiss();
		}

	}

}
