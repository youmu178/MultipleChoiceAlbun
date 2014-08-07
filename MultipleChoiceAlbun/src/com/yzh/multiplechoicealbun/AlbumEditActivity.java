package com.yzh.multiplechoicealbun;

/**
 * 相机，相册选择  发表
 */
import java.io.File;
import java.util.ArrayList;

import com.yzh.multiplechoicealbun.adapter.GridImageAdapter;
import com.yzh.multiplechoicealbun.util.CommonDefine;
import com.yzh.multiplechoicealbun.util.FileUtils;
import com.yzh.multiplechoicealbun.util.ImageUtils;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AlbumEditActivity extends AbsActivity {
	private EditText mETGroupPhotoContent;
	private String locationMsg;
	String objectKey = null;
	private GridView gridView;
	private ArrayList<String> dataList;
	private GridImageAdapter gridImageAdapter;
	private ArrayList<String> tDataList;
	private String photoContent;
	private String intranetID;
	private String cameraImagePath = "";
	private int finishCount = -1;
	private StringBuilder builder;
	private Uri uri;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_album_edit);
		
		RelativeLayout topRl = (RelativeLayout) findViewById(R.id.top_title);
		TextView mTVCancel = (TextView) topRl.findViewById(R.id.set_cancel);
		TextView mTVOk = (TextView) topRl.findViewById(R.id.set_ok);
		mTVOk.setVisibility(View.VISIBLE);
		mTVCancel.setVisibility(View.VISIBLE);
		mTVCancel.setText("返 回");
		mTVOk.setText("发 送");
		mTVCancel.setOnClickListener(mCancelListener);
		mTVOk.setOnClickListener(mOkListener);
       		
		dataList = new ArrayList<String>();
		init();
		initListener();
		photoContent = mETGroupPhotoContent.getText().toString();
	
		Bundle extras = getIntent().getExtras();
		String path = extras.getString("path");
		
		tDataList = (ArrayList<String>)extras.getSerializable("dataList");
		String editContent = extras.getString("editContent");
		if(editContent != null){
			mETGroupPhotoContent.setText(editContent);
		}
		if(path != null) {
			dataList.add(path);
			if(dataList.size() < 9){
				dataList.add("camera_default");
			}
			gridImageAdapter.notifyDataSetChanged();
		}
		if (tDataList != null) {
			for (int i = 0; i < tDataList.size(); i++) {
				String string = tDataList.get(i);
				dataList.add(string);
			}
			if (dataList.size() < 9) {
				dataList.add("camera_default");
			}
			gridImageAdapter.notifyDataSetChanged();
		}
	}
	
	private void init() {
		mETGroupPhotoContent = (EditText) findViewById(R.id.group_camera_photo_content);
		gridView = (GridView) findViewById(R.id.gridview_image);
//		dataList.add("camera_default");
		gridImageAdapter = new GridImageAdapter(this, dataList, loader, options);
		gridView.setAdapter(gridImageAdapter);
	}
	
	private void initListener() {

		gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                String path = dataList.get(position);
				if (path.contains("default") && position == dataList.size() -1 && dataList.size() -1 != 9) {
					showSelectImageDialog();
				} else {
					Intent intent = new Intent(mActThis, ImageDelActivity.class);
					intent.putExtra("position", position);
					intent.putExtra("path", dataList.get(position));
					startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
				}
			}
		});
	}
	
	private OnClickListener mCancelListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Dialog dialog = new Dialog(mActThis, R.style.dialog);
			View inflate = View.inflate(mActThis, R.layout.dialog_del, null);
			TextView dialogTitle = (TextView) inflate.findViewById(R.id.dialog_title);
			dialogTitle.setText("放弃此次编辑？");
			TextView dialogCancel = (TextView) inflate.findViewById(R.id.del_cancel);
			dialogCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			TextView dialogConfirm = (TextView) inflate.findViewById(R.id.confirm_del);
			dialogConfirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlbumEditActivity.this.finish();
				}
			});
			dialog.setContentView(inflate);
			dialog.show();
		}
	};
	
	private OnClickListener mOkListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO 发送，带图
			StringBuilder stringBuilder = new StringBuilder();
    		if((TextUtils.isEmpty(mETGroupPhotoContent.getText().toString()) && dataList.size() == 1)){
    			
    			Toast.makeText(mActThis, "一无所有，不能发表", Toast.LENGTH_SHORT).show();
    		} else {
    			for (int i = 0; i < dataList.size(); i++) {
    				
    				String imgPath = dataList.get(i);
    			    if(imgPath.contains("camera_default")) {
    			    	imgPath = imgPath.replace("camera_default", "");
    				}
    			    if(!TextUtils.isEmpty(imgPath)) {
    			    	stringBuilder.append(imgPath);
    			    }
    			}
    			Toast.makeText(mActThis, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
    		}
		}
	};
	

	

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			final Dialog dialog = new Dialog(this, R.style.dialog);
			View inflate = View.inflate(this, R.layout.dialog_del, null);
			TextView dialogTitle = (TextView) inflate.findViewById(R.id.dialog_title);
			dialogTitle.setText("放弃此次编辑？");
			TextView dialogCancel = (TextView) inflate.findViewById(R.id.del_cancel);
			dialogCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			TextView dialogConfirm = (TextView) inflate.findViewById(R.id.confirm_del);
			dialogConfirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
			dialog.setContentView(inflate);
			dialog.show();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CommonDefine.TAKE_PICTURE_FROM_CAMERA:
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					return;
				}
				Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
				String cameraImagePath = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis()+"");
				
//				Bundle bundle = data.getExtras();
//				Bitmap bitmap = (Bitmap) bundle.get("data");
//				String cameraImagePath = ImageUtils.setCameraImage(bitmap);
				
				for (int i = 0; i < dataList.size(); i++) {
					String path = dataList.get(i);
					if(path.contains("default")) {
						dataList.remove(dataList.size() - 1);
					}
				}
				dataList.add(cameraImagePath);
				if(dataList.size() < 9) {
					dataList.add("camera_default");
				}
				gridImageAdapter.notifyDataSetChanged();
				break;
			case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
				Bundle bundle2 = data.getExtras();
				tDataList = (ArrayList<String>) bundle2.getSerializable("dataList");
				if (tDataList != null) {
					for (int i = 0; i < tDataList.size(); i++) {
						String string = tDataList.get(i);
						dataList.add(string);
					}
					if (dataList.size() < 9) {
						dataList.add("camera_default");
					}
					gridImageAdapter.notifyDataSetChanged();
				}
				
				break;
			case CommonDefine.DELETE_IMAGE:
				int position = data.getIntExtra("position", -1);
				dataList.remove(position);
				if(dataList.size() < 9 ) {
					dataList.add(dataList.size(), "camera_default");
					for (int i = 0; i < dataList.size(); i++) {
						String path = dataList.get(i);
						if(path.contains("default")) {
							dataList.remove(dataList.size() - 2);
						}
					}
				}
				gridImageAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	// 选择相册，相机
	private void showSelectImageDialog() {
		final Dialog picAddDialog = new Dialog(mActThis, R.style.dialog);
		View picAddInflate = View.inflate(mActThis, R.layout.item_dialog_camera, null);
		TextView camera = (TextView) picAddInflate.findViewById(R.id.camera);
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// 选择相机
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
				picAddDialog.dismiss();
			}
		});
		TextView mapStroge = (TextView) picAddInflate.findViewById(R.id.mapstorage);
		mapStroge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// 选择图库
				Intent intent = new Intent(mActThis, AlbumActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
				bundle.putString("editContent", mETGroupPhotoContent.getText().toString());
				intent.putExtras(bundle);
				startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
				
				picAddDialog.dismiss();
				AlbumEditActivity.this.finish();
			}
		});
		TextView cancel = (TextView) picAddInflate.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				picAddDialog.dismiss();
			}
		});
		picAddDialog.setContentView(picAddInflate);
		picAddDialog.show();

	}
	
	private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

		ArrayList<String> tDataList = new ArrayList<String>();

		for (String s : dataList) {
			if (!s.contains("default")) {
				tDataList.add(s);
			}
		}
		return tDataList;
	}
	
}
