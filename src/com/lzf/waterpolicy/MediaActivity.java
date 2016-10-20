package com.lzf.waterpolicy;

import com.lzf.gallery.utils.ImageLoader;
import com.lzf.gallery.utils.ImageLoader.Type;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Ã½Ìå²¥·ÅÆ÷
 */
public class MediaActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media);

		String videoPath = getIntent().getStringExtra("video");
		String imagePath = getIntent().getStringExtra("image");

		if (videoPath != null && !videoPath.equals("")) {
			VideoView videoView = (VideoView) findViewById(R.id.video);
			MediaController mediaController = new MediaController(this);
			videoView.setVideoPath(videoPath);
			videoView.setMediaController(mediaController);
			videoView.start();
			mediaController.setMediaPlayer(videoView);
			videoView.setVisibility(View.VISIBLE);
		} else if (imagePath != null && !imagePath.equals("")) {
			ImageView imageView = (ImageView) findViewById(R.id.image);
			ImageLoader.getInstance(3, Type.LIFO).loadImage(imagePath, imageView);
			imageView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
