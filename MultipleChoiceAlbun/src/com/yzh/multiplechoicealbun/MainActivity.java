package com.yzh.multiplechoicealbun;


import java.io.File;

import com.yzh.multiplechoicealbun.util.CommonDefine;
import com.yzh.multiplechoicealbun.util.FileUtils;
import com.yzh.multiplechoicealbun.util.ImageUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private Uri uri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button bt = (Button) findViewById(R.id.bt);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(MainActivity.this).setItems(new String[] { "拍照", "从图库选择" }, new DialogInterface.OnClickListener() {


					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							Intent cameraIntent = new Intent();
							cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
							cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
							// 根据文件地址创建文件
					        File file = new File(CommonDefine.FILE_PATH);
					        if (file.exists()) {
					             file.delete();
					        }
					        uri = Uri.fromFile(file);
					        // 设置系统相机拍摄照片完成后图片文件的存放地址
					        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							// 开启系统拍照的Activity
							startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
							dialog.dismiss();
							break;
						case 1:
							startActivity(new Intent(MainActivity.this, AlbumActivity.class));
							dialog.dismiss();
							break;

						default:
							break;
						}
					}
				}).show();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CommonDefine.TAKE_PICTURE_FROM_CAMERA:// 相机返回图片，再进入图片发表页面
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					return;
				}
				Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
				String cameraImagePath = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis()+"");
				Intent intentCamera = new Intent(MainActivity.this, AlbumEditActivity.class);
				intentCamera.putExtra("path", cameraImagePath);
				startActivity(intentCamera);
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
