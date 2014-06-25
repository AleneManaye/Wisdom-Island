package com.drcom.drpalm.Activitys.news;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.FileManagement;
import com.drcom.drpalm.View.controls.MyImageView;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.wisdom.island.R;

public class NewsImageAdapter extends BaseAdapter {
	private Context context;
	private List<String> data;
	private ImageLoader mImageLoader;
	private LayoutInflater mLayoutInflater;
	private Bitmap no_pic;
	private boolean hasDown = false;

	public NewsImageAdapter(Context context, List<String> data, ImageLoader mImageLoader) {
		super();
		this.context = context;
		this.data = data;
		this.mImageLoader = mImageLoader;
		this.mLayoutInflater = LayoutInflater.from(context);
		this.no_pic = GlobalVariables.no_pic;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public MyImageView img;
		public ProgressBar prb;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// 预加载
		// Log.i("xpf", "ppp pontxi=" + position);
		if (position - 3 >= 0) {
			mImageLoader.remove(data.get(position - 3));
		}
		if (position + 2 < data.size()) {
			mImageLoader.remove(data.get(position + 2));
		}
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.album_gallery_item, null);
			vh = new ViewHolder();
			vh.img = (MyImageView) convertView.findViewById(R.id.img);
			vh.prb = (ProgressBar) convertView.findViewById(R.id.prb);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		String url = data.get(position);
		if (isPic(url)) {
			Log.i("zjj", "个人相册 Adapter url" + data.get(position));
			Bitmap bitmap = mImageLoader.getBitmapFromCache(data.get(position));
			if (null == bitmap) {
				setHasDown(false);
				vh.prb.setVisibility(View.VISIBLE);
				vh.img.setImageBitmap(no_pic);
			} else {
				setHasDown(true);
				vh.prb.setVisibility(View.GONE);
				vh.img.setImageBitmap(bitmap);
			}
		} else {
			vh.prb.setVisibility(View.GONE);
			vh.img.setImageBitmap(no_pic);
			String fileExt = url.substring(url.lastIndexOf(".") + 1, url.length());
			BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(FileManagement.getFileIcon(context, fileExt));
			Bitmap bmp = bd.getBitmap();
			vh.img.setImageBitmap(bmp);
		}
		return convertView;
	}

	public boolean isPic(String fileName) {
		boolean flag = false;
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		if ("jpg".equalsIgnoreCase(fileExt) || "png".equalsIgnoreCase(fileExt) || "gif".equalsIgnoreCase(fileExt) || "jpeg".equalsIgnoreCase(fileExt)) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	public boolean isHasDown() {
		return hasDown;
	}

	public void setHasDown(boolean hasDown) {
		this.hasDown = hasDown;
	}

}
