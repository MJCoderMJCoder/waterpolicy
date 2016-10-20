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

	private File tempImgFolder; // ��ʱ���ļ���
	private List<String> tempImgs; // ��ʱ�洢��Ӧ�ļ����µ�����ͼƬ
	private ImgFolder imgMixFolder = new ImgFolder(null, null, null, 0); // ͼƬ���������ļ���

	private HashSet<String> tempFolderPathes = new HashSet<String>(); // ��ʱ�ĸ����࣬���ڷ�ֹͬһ���ļ��еĶ��ɨ��
	private List<ImgFolder> imgFloders = new ArrayList<ImgFolder>(); // ɨ���õ����е�ͼƬ�ļ���

	private MyAdapter myAdapter;
	private GridView gridView;
	private TextView folder;
	private TextView imgTotal;

	private int screenHeight;
	private final int IMG_SCAN = 1; // ͼƬɨ��ı�ʶ

	private ImgFolderPopup imgFolderPopup;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case IMG_SCAN:
				if (progress != null) {
					progress.dismiss();
				}
				initGridView(); // ΪGridView��ʼ������
				initImgFolderPopup(); // ��ʼ��չʾ�ļ��е�popupWindw
				break;
			default:
				break;
			}
		}
	};

	/**
	 * ΪGridView��ʼ������
	 */
	private void initGridView() {
		if (imgMixFolder.getCount() <= 0) {
			Toast.makeText(getApplicationContext(), "���ֻ�������ͼƬ", Toast.LENGTH_SHORT).show();
			return;
		}
		selected(imgMixFolder);
	};

	/**
	 * ��ʼ��չʾ�ļ��е�popupWindw
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
		// ����ѡ���ļ��еĻص�
		imgFolderPopup.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		// ��ȡ�ֻ���Ļ�ֱ��ʵ���
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
	 * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳��� ���ͼƬ��ɨ�裬���ջ��jpg�����Ǹ��ļ���
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "û�м�⵽SD��", Toast.LENGTH_SHORT).show();
			return;
		}
		progress = ProgressDialog.show(this, null, "����ɨ��...");
		new Thread(new Runnable() {
			@Override
			public void run() {

				Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver contentResolver = GalleryActivity.this.getContentResolver();

				// ֻ��ѯjpeg��png��ͼƬ
				Cursor cursor = contentResolver.query(imgUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png", "image/jpg" }, MediaStore.Images.Media.DATE_MODIFIED);
				while (cursor.moveToNext()) {
					// ��ȡͼƬ��·��
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					// ��ȡ��ͼƬ�ĸ�·����
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImgFolder imageFloder = null;
					// ����һ��HashSet��ֹ���ɨ��ͬһ���ļ��У���������жϣ�ͼƬ�����������൱�ֲ���~~��
					if (tempFolderPathes.contains(dirPath)) {
						continue;
					} else {
						tempFolderPathes.add(dirPath);
						// ��ʼ��imageFloder
						imageFloder = new ImgFolder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					/**
					 * �������·��������ʾһ��Ŀ¼�����߷��� I/O �����򷵻� null��
					 * �׳�SecurityException���ܾ���Ŀ¼���ж�����
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
				// ɨ����ɣ�������HashSetҲ�Ϳ����ͷ��ڴ���
				tempFolderPathes = null;
				handler.sendEmptyMessage(IMG_SCAN);
			}
		}).start();

	}

	private void initEvent() {
		/**
		 * Ϊ�ײ���folder���õ���¼�������popupWindow
		 */
		folder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imgFolderPopup.showAsDropDown(folder, 0, 0);

				// ���ñ�����ɫ�䰵
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
		 * �ļ��е�·����ͼƬ��·���ֿ����棬�Ա�����ڴ�����ģ�
		 */
		myAdapter = new MyAdapter(getApplicationContext(), tempImgs, R.layout.item_imgrid,
				tempImgFolder.getAbsolutePath(), this);
		gridView.setAdapter(myAdapter);
		imgTotal.setText(floder.getCount() + "��");
		folder.setText(floder.getName());
		if (imgFolderPopup != null) {
			imgFolderPopup.dismiss();
		}

	}

}
