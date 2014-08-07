package com.yzh.multiplechoicealbun;


import com.yzh.multiplechoicealbun.util.ImageUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ImageDelActivity extends AbsActivity {

	private ImageView image_show;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.item_img_del);
		Intent intent = getIntent();
		position = intent.getIntExtra("position", -1);
		String path = intent.getStringExtra("path");
		image_show = (ImageView) findViewById(R.id.image_show);
		Bitmap bitmap = ImageUtils.getSmallBitmap(path);
//		Bitmap bitmap = Utils.getImageFromLocal(path);
		image_show.setImageBitmap(bitmap);

		findViewById(R.id.group_photo_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});
		findViewById(R.id.group_photo_del).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				delete();
			}
		});
	}

	public void delete() {
		Intent intent = new Intent();
		intent.putExtra("position", position);
		setResult(RESULT_OK, intent);
		finish();
	}

}
