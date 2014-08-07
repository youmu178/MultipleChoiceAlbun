package com.yzh.multiplechoicealbun.adapter;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yzh.multiplechoicealbun.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridImageAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> dataList;
    private ImageLoader loader;
    private DisplayImageOptions options;
    
	public GridImageAdapter(Context c, ArrayList<String> dataList, ImageLoader loader, DisplayImageOptions options) {
		this.mContext = c;
		this.dataList = dataList;
		this.loader = loader;
		this.options = options;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;

		if (convertView != null) {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		} else {
			view = View.inflate(mContext, R.layout.item_grid_img, null);
			holder = new ViewHolder();
			holder.imageview = (ImageView) view.findViewById(R.id.row_gridview_imageview);

			view.setTag(holder);
		}
		String path;
		if (dataList != null && position < dataList.size())
			path = dataList.get(position);
		else
			path = "camera_default";
		if (path.contains("camera_default"))
			holder.imageview.setImageResource(R.drawable.addphoto_button_pressed);
		else{
//			ImageLoader imageLoader = ImageLoader.getInstance();
//			DisplayImageOptions options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.group_item_pic_bg)
//			.cacheInMemory(true)
//			.cacheOnDisc(true)
//			.build();
			loader.displayImage("file://"+path, holder.imageview, options);
		}
		return view;
	}
	
	static class ViewHolder {
		ImageView imageview;
	}
}
