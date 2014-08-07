package com.yzh.multiplechoicealbun;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yzh.multiplechoicealbun.adapter.AlbumGridViewAdapter;

public class AlbumActivity extends AbsActivity {

	private GridView gridView;
	private ArrayList<String> dataList;
	private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
	private ArrayList<String> selectedDataList = new ArrayList<String>();
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	private LinearLayout selectedImageLayout;
	private TextView okButton;
	private HorizontalScrollView scrollview;
	private String editContent;
	private String imgLocation;
	private boolean booleanExtra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_album);
		loadPic();
		
//		updateList();
//		booleanExtra = mActThis.getIntent().getBooleanExtra("album", false);
		updateList(getIntent());
	}

	@SuppressWarnings("unchecked")
	private void updateList(Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			ArrayList<String> selList1 = (ArrayList<String>) bundle.getSerializable("dataList");
			ArrayList<String> pathList = bundle.getStringArrayList("listPath");
			ArrayList<String> selList2 = bundle.getStringArrayList("selectedDataList");
			editContent = bundle.getString("editContent");	
			imgLocation = bundle.getString("name");
			
			if (pathList != null) {
				dataList = pathList;
			}
			
			if (selList2 != null) {
				selectedDataList = selList2;
			} else if (selList1 != null) {
				selectedDataList = selList1;
			}
			booleanExtra = bundle.getBoolean("album");
			Log.i("youzh", booleanExtra+"----");
		}
		init();
		initListener();
	}

	@SuppressWarnings("deprecation")
	private void loadPic() {
		final String[] columns = { BaseColumns._ID, ImageColumns.MINI_THUMB_MAGIC, MediaColumns.DATA, ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.BUCKET_ID };
		final String orderBy = MediaColumns.DATE_ADDED;
		Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
		this.dataList = new ArrayList<String>();

		for (int i = 0; i < imagecursor.getCount(); i++) {
			imagecursor.moveToPosition(i);
			int dataColumnIndex = imagecursor.getColumnIndexOrThrow(MediaColumns.DATA);
			dataList.add(imagecursor.getString(dataColumnIndex));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == -1 && data != null) {
			updateList(data);
		}
	}

	private void init() {
		TextView cancelBT = (TextView) findViewById(R.id.cancel_button);
		cancelBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlbumActivity.this.finish();
			}
		});
		TextView chanceBT = (TextView) findViewById(R.id.chance_button);
		chanceBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActThis, AlbumChanceActivity.class);
				intent.putExtra("selectedDataList", selectedDataList);
				intent.putExtra("album", booleanExtra);
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
//				AlbumActivity.this.finish();
			}
		});
		TextView mImgLocation = (TextView) findViewById(R.id.imageLocation);
		if(!TextUtils.isEmpty(imgLocation)){
			mImgLocation.setText(imgLocation);
		} else {
			mImgLocation.setText("最近照片");
		}
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(R.id.myGrid);

		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, selectedDataList, loader, options);
		gridView.setAdapter(gridImageAdapter);
		selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
		okButton = (TextView) findViewById(R.id.ok_button);
		scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);

		initSelectImage();
	}

	private void initSelectImage() {
		if (selectedDataList == null)
			return;
		selectedImageLayout.removeAllViews();
		for (final String path : selectedDataList) {
			ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
			selectedImageLayout.addView(imageView);
			hashMap.put(path, imageView);
//			DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.pic_loading).cacheInMemory(true).cacheOnDisk(true).build();
			loader.displayImage("file://" + path, imageView, options);
			imageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					removePath(path);
					gridImageAdapter.notifyDataSetChanged();
				}
			});
		}
		okButton.setText("完成(" + selectedDataList.size() + ")");
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(final CheckBox toggleButton, int position, final String path, boolean isChecked) {
					if (selectedDataList.size() >= 9) {
						toggleButton.setChecked(false);
						if (!removePath(path)) {
							Toast.makeText(AlbumActivity.this, "只能选择9张图片", Toast.LENGTH_SHORT).show();
						}
						return;
					}
					if (isChecked) {
						if (!hashMap.containsKey(path)) {
							ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
							selectedImageLayout.addView(imageView);
							imageView.postDelayed(new Runnable() {

								@Override
								public void run() {

									int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
									if (off > 0) {
										scrollview.smoothScrollTo(off, 0);
									}
								}
							}, 100);

							hashMap.put(path, imageView);
							selectedDataList.add(path);
//							DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.group_item_pic_bg).cacheInMemory(true).cacheOnDisc(true).build();
							loader.displayImage("file://" + path, imageView, options);
							imageView.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									toggleButton.setChecked(false);
									removePath(path);
								}
							});
							okButton.setText("完成(" + selectedDataList.size() + ")");
						}
					} else {
						removePath(path);
					}
				
			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				
				if (booleanExtra) {
//					Log.i("youzh", booleanExtra+"---OK");
//					Log.i("youzh", selectedDataList+"---OK");
					intent = new Intent();
					intent.putExtra("datalist", selectedDataList);
					setResult(RESULT_OK, intent);
				} else {
					intent = new Intent(mActThis, AlbumEditActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("dataList", selectedDataList);
					Log.i("youzh", selectedDataList+"---OK");
					bundle.putString("editContent", editContent);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				
				mActThis.finish();
			}
		});
	}

	private boolean removePath(String path) {
		if (hashMap.containsKey(path)) {
			selectedImageLayout.removeView(hashMap.get(path));
			hashMap.remove(path);
			removeOneData(selectedDataList, path);
			okButton.setText("完成(" + selectedDataList.size() + ")");
			return true;
		} else {
			return false;
		}
	}

	private void removeOneData(ArrayList<String> arrayList, String s) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals(s)) {
				arrayList.remove(i);
				return;
			}
		}
	}

}
