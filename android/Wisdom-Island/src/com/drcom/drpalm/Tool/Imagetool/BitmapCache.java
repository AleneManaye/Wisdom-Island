package com.drcom.drpalm.Tool.Imagetool;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.drcom.drpalm.DB.ImagesDB;
import com.drcom.drpalm.View.controls.MyMothod;
/**
 * 防止溢出
 *
 */
public class BitmapCache {
	static private BitmapCache cache;
	/** 用于Chche内容的存储 */
	private Hashtable<String, BtimapRef> bitmapRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Bitmap> q;

	private final static boolean LOG_ENABLED = false;
	
	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class BtimapRef extends SoftReference<Bitmap> {
		private String _key = "";

		public BtimapRef(Bitmap bmp, ReferenceQueue<Bitmap> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}

	private BitmapCache() {
		bitmapRefs = new Hashtable<String, BtimapRef>();
		q = new ReferenceQueue<Bitmap>();

	}

	/**
	 * 取得缓存器实例
	 */
	public static BitmapCache getInstance() {
		if (cache == null) {
			cache = new BitmapCache();
		}
		return cache;

	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	public void addCacheBitmap(Bitmap bmp, String key) {
		cleanCache();// 清除垃圾引用
		BtimapRef ref = new BtimapRef(bmp, q, key);
		bitmapRefs.put(key, ref);
	}

	/**
	 * 依据所指定的文件名获取图片
	 */
	public Bitmap getBitmap(String filename, AssetManager assetManager) {

		Bitmap bitmapImage = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs.containsKey(filename)) {
			BtimapRef ref = (BtimapRef) bitmapRefs.get(filename);
			bitmapImage = (Bitmap) ref.get();
		}
		// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
		// 并保存对这个新建实例的软引用
		if (bitmapImage == null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[16 * 1024];

			// bitmapImage = BitmapFactory.decodeFile(filename, options);
			BufferedInputStream buf;
			try {
				buf = new BufferedInputStream(assetManager.open(filename));
				bitmapImage = BitmapFactory.decodeStream(buf);
				this.addCacheBitmap(bitmapImage, filename);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		return bitmapImage;
	}
	
	/**
	 * 依据所指定的文件名获取图片
	 * @param filename 图片名字
	 * @param picbyte 图片流
	 * @param roundedCorner 是否圆角
	 */
	public Bitmap getBitmap(String filename,byte[] b,boolean roundedCorner ) {

		Bitmap bitmapImage = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs.containsKey(filename)) {
			BtimapRef ref = (BtimapRef) bitmapRefs.get(filename);
			bitmapImage = (Bitmap) ref.get();
		}
		// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
		// 并保存对这个新建实例的软引用
		if (bitmapImage == null) {
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inTempStorage = new byte[16 * 1024];
//			options.inSampleSize = 2;

			// bitmapImage = BitmapFactory.decodeFile(filename, options);
//			BufferedInputStream buf;
//			try {
//				buf = new BufferedInputStream(assetManager.open(filename));
//				bitmapImage = BitmapFactory.decodeStream(buf);
				bitmapImage = MyMothod.Byte2Bitmap(b,false);
				if(roundedCorner){
					bitmapImage = MyMothod.getRoundedCornerBitmap(bitmapImage, 8.0f);	//转成圆角
				}
				this.addCacheBitmap(bitmapImage, filename);
//			} catch (IOException e) {
//
//				e.printStackTrace();
//			}

		}

		return bitmapImage;
	}
	
	/**
	 * 依据所指定的文件名获取图片(从库中取)
	 * @param filename 图片名字/url
	 * @param imagesDB 图片数据库
	 * @param categoryid 分类ID
	 */
	public Bitmap getBitmap(String filename,ImagesDB imagesDB ,int categoryid ) {

		Bitmap bitmapImage = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs.containsKey(filename)) {
			BtimapRef ref = (BtimapRef) bitmapRefs.get(filename);
			bitmapImage = (Bitmap) ref.get();
		}
		// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
		// 并保存对这个新建实例的软引用
		if (bitmapImage == null) {
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inTempStorage = new byte[16 * 1024];
//			options.inSampleSize = 2;

			// bitmapImage = BitmapFactory.decodeFile(filename, options);
//			BufferedInputStream buf;
//			try {
//				buf = new BufferedInputStream(assetManager.open(filename));
//				bitmapImage = BitmapFactory.decodeStream(buf);
				//缓存没有,则从数据库中读取
				byte[] attachmentBytes = imagesDB.getImageByCategory(filename, categoryid);
				if(attachmentBytes != null){
					System.out.println(":::::缓存URL,从数据库读取:" + filename + "," + attachmentBytes.length);
					bitmapImage = MyMothod.Byte2Bitmap(attachmentBytes,false);
					this.addCacheBitmap(bitmapImage, filename);
				}
//			} catch (IOException e) {
//
//				e.printStackTrace();
//			}

		}

		return bitmapImage;
	}
    
	/**
	 * 
	 * @param key
	 * @return
	 */
    public Bitmap getBitmapFromMemCache(String key) {
        Bitmap b = null ;//= bitmapRefs.get(key);
        // 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs.containsKey(key)) {
			BtimapRef ref = (BtimapRef) bitmapRefs.get(key);
			b = (Bitmap) ref.get();
		}
        
        if (LOG_ENABLED)
            Log.d("zjj", (b == null) ? "Cache miss" : "Cache found");
        if (b != null && b.isRecycled()) {
            /* A recycled bitmap cannot be used again */
        	bitmapRefs.remove(key);
            return null;
        }
        return b;
    }
    
	private void cleanCache() {
		BtimapRef ref = null;
		while ((ref = (BtimapRef) q.poll()) != null) {
			bitmapRefs.remove(ref._key);
		}
	}

	// 清除Cache内的全部内容
	public void clearCache() {
		cleanCache();
		bitmapRefs.clear();
		System.gc();
		System.runFinalization();
	}

}
