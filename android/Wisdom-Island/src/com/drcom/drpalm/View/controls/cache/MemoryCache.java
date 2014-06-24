package com.drcom.drpalm.View.controls.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

	private static final String TAG = "MemoryCache";
	// 放入缓存时是个同步操作
	// LinkedHashMap构造方法的最后一个参数true代表这个map里的元素将按照最近使用次数由少到多排列，即LRU
	// 这样的好处是如果要将缓存中的元素替换，则先遍历出最近最少使用的元素来替换以提高效率
	// 使用软引用
	private Map<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
	// 缓存中图片所占用的字节，初始0，将通过此变量严格控制缓存所占用的堆内存
	private long size = 0;// current allocated size
	// 缓存只能占用的最大堆内存
	private long limit = 1000000;// max memory in bytes

	/** 软应用缓存 */

	public MemoryCache() {
		// use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory() / 4);
	}

	public void setLimit(long new_limit) {
		limit = new_limit;
		Log.i(TAG, "MemoryCache will use up to " + limit);
	}

	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id))
				return null;
			SoftReference<Bitmap> value = cache.get(id);
			if (null != value && null != value.get()) {
				return value.get();
			} else {
				return null;
			}
		} catch (NullPointerException ex) {
			return null;
		}
	}

	public void put(String id, Bitmap bitmap) {
		try {
			Log.i(TAG, "put " + id + " into cache " + getSizeInBytes(bitmap));
			if (cache.containsKey(id)) {
				Log.i("xpf", "put 之前缓存中的=" + id + " itemsize= " + getSizeInBytes(cache.get(id).get()));
				size -= getSizeInBytes(cache.get(id).get());
			}
			size += getSizeInBytes(bitmap);
			checkSize();
			cache.put(id, new SoftReference<Bitmap>(bitmap));
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	public void remove(String id) {
		if (cache.containsKey(id)) {
			Log.i(TAG, "cache 移除前size=" + size + " length=" + cache.size());
			size -= getSizeInBytes(cache.get(id).get());
			cache.remove(id);
			Log.i(TAG, "cache size移除了" + cache.size());
		}
	}

	/**
	 * 严格控制堆内存，如果超过将首先替换最近最少使用的那个图片缓存
	 * 
	 */
	private void checkSize() {
		Log.i(TAG, "cache size=" + size + " length=" + cache.size());
		if (size > limit) {
			// 先遍历最近最少使用的元素
			Iterator<Entry<String, SoftReference<Bitmap>>> iter = cache.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, SoftReference<Bitmap>> entry = iter.next();
				size -= getSizeInBytes(entry.getValue().get());
				iter.remove();
				if (size <= limit)
					break;
			}
			Log.i(TAG, "Clean cache. New size " + cache.size());
		}
	}

	public void clear() {
		size = 0;
		cache.clear();
	}

	/**
	 * 图片占用的内存
	 * 
	 * [url=home.php?mod=space&uid=2768922]@Param[/url] bitmap
	 * 
	 * @return
	 */
	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null)
			return 0;

		// Log.i("xpf", "scale limit= "+limit+" size= "+size);
		Log.i("xpf", "scale BitmapRowBytes= " + bitmap.getRowBytes() + " heigth= " + bitmap.getHeight());
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

}
