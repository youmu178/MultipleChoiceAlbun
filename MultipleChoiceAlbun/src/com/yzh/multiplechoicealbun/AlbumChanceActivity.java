package com.yzh.multiplechoicealbun;

import java.util.ArrayList;
import java.util.List;

import com.yzh.multiplechoicealbun.media.MediaStoreBucket;
import com.yzh.multiplechoicealbun.media.MediaStoreCursorHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumChanceActivity extends AbsActivity implements OnItemClickListener {
	private ListView mLVChancePhoto;
	private ChanceAdapter adapter;
	private ArrayList<MediaStoreBucket> mBuckets = new ArrayList<MediaStoreBucket>();
	private ArrayList<String> selectedDataList;
	private boolean booleanExtra;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_album_chance);
		TextView cancelTV = (TextView) findViewById(R.id.cancel_button);
		cancelTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(AlbumChanceActivity.this, AlbumActivity.class);
//				intent.putExtra("selectedDataList", selectedDataList);
//				startActivity(intent);
				AlbumChanceActivity.this.finish();
			}
		});
		Bundle extras = getIntent().getExtras();
		selectedDataList = extras.getStringArrayList("selectedDataList");
		booleanExtra = extras.getBoolean("album");
		Log.i("youzh", booleanExtra+"---Chance");
		mLVChancePhoto = (ListView) findViewById(R.id.chance_photo_lv);
		adapter = new ChanceAdapter(mActThis, mBuckets);
		mLVChancePhoto.setAdapter(adapter);
		mLVChancePhoto.setOnItemClickListener(this);
		
	}
	
    @Override
    protected void onStart() {
    	super.onStart();
    	loadBuckets();
    }
    
	private void loadBuckets() {
		List<MediaStoreBucket> buckets = MediaStoreCursorHelper.getBucket(mActThis);
		if (null != buckets && !buckets.isEmpty()) {
			mBuckets.clear();
			mBuckets.addAll(buckets);
			adapter.notifyDataSetChanged();
		}
	}

	private class ChanceAdapter extends BaseAdapter {
		private Context mActThis;
		private ArrayList<MediaStoreBucket> mBuckets;

		public ChanceAdapter(Context mActThis, ArrayList<MediaStoreBucket> mBuckets) {
			this.mActThis = mActThis;
			this.mBuckets = mBuckets;
		}

		@Override
		public int getCount() {
			return mBuckets.size();
		}

		@Override
		public Object getItem(int position) {
			return mBuckets.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemAlbum itemAlbum = null;
			if (convertView == null) {
				itemAlbum = new ItemAlbum();
				convertView = LayoutInflater.from(mActThis).inflate(R.layout.item_album_chance, null);
				itemAlbum.itemIVAlbum = (ImageView) convertView.findViewById(R.id.item_album_iv);
				itemAlbum.itemTVAlbum = (TextView) convertView.findViewById(R.id.item_album_tv);
				convertView.setTag(itemAlbum);
			} else {
				itemAlbum = (ItemAlbum) convertView.getTag();
			}
			MediaStoreBucket mediaStoreBucket = mBuckets.get(position);
			String id = mediaStoreBucket.getId();
			if( id != null){
				ArrayList<String> listPath = MediaStoreCursorHelper.queryPhoto((Activity) mActThis, id);
				String firstImgPath = listPath.get(0);
				
				loader.displayImage("file://" + firstImgPath, itemAlbum.itemIVAlbum, options);
			} else {
				ArrayList<String> list = MediaStoreCursorHelper.queryAllPhoto((Activity) mActThis);
				String string = list.get(0);
//				DisplayImageOptions options = new DisplayImageOptions.Builder()
//				        .bitmapConfig(Bitmap.Config.RGB_565)
//				        .imageScaleType(ImageScaleType.EXACTLY)
//						.showStubImage(R.drawable.group_item_pic_bg)
//						.cacheInMemory(true)
//						.cacheOnDisc(true)
//						.build();
				loader.displayImage("file://" + string, itemAlbum.itemIVAlbum, options);
			}
			String name = mediaStoreBucket.getName();
			if (name.contains("All Photos")) {
				itemAlbum.itemTVAlbum.setText("最近照片");
			} else {
				itemAlbum.itemTVAlbum.setText(name);
			}
			return convertView;
		}
	}

	class ItemAlbum {
		ImageView itemIVAlbum;
		TextView itemTVAlbum;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MediaStoreBucket item = (MediaStoreBucket) parent.getItemAtPosition(position);
		if (null != item) {
			loadBucketId(item);
		}
	}

	private void loadBucketId(MediaStoreBucket item) {
		Intent intent = new Intent(this, AlbumActivity.class);
		if (item.getName().contains("All Photos")) {
			ArrayList<String> list = MediaStoreCursorHelper.queryAllPhoto(this);
			intent.putExtra("listPath", list);
			intent.putExtra("selectedDataList", selectedDataList);
			intent.putExtra("name", "最近照片");
			intent.putExtra("album", booleanExtra);
//			startActivity(intent);
		} else {
			String id = item.getId();
			ArrayList<String> listPath = MediaStoreCursorHelper.queryPhoto(this, id);
			intent.putExtra("listPath", listPath);
			intent.putExtra("selectedDataList", selectedDataList);
			intent.putExtra("name", item.getName());
			intent.putExtra("album", booleanExtra);
//			startActivity(intent);
		}
		setResult(-1, intent);
		AlbumChanceActivity.this.finish();
	}

	
}
