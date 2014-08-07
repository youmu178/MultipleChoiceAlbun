package com.yzh.multiplechoicealbun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public abstract class AbsActivity extends FragmentActivity {
	protected AbsActivity mActThis = null;
	protected ImageLoader loader;
	protected DisplayImageOptions options;
	
//	@SuppressLint("HandlerLeak")
//	public final Handler mHandlerEx = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			onHandleMessage(msg);
//		}
//	};
	
//	protected abstract void onHandleMessage(Message message);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActThis = this;
		
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
	    .imageScaleType(ImageScaleType.EXACTLY)
	    .bitmapConfig(Bitmap.Config.RGB_565)
	    .showImageOnLoading(R.drawable.pic_loading)
	    .cacheInMemory(true)
	    .cacheOnDisc(true)
	    .build();
	}
	
	
//
//	protected void sendMessageWithArg(int what, int arg) {
//		Message msg = mHandlerEx.obtainMessage(what);
//		msg.what = what;
//		msg.arg1 = arg;
//		mHandlerEx.sendMessage(msg);
//	}
//		
//	protected void sendMessageWithObj(int what, Object obj) {
//		Message msg = mHandlerEx.obtainMessage(what);
//		msg.what = what;
//		msg.obj = obj;
//		mHandlerEx.sendMessage(msg);
//	}
	
}
