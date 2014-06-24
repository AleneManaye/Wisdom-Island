package com.drcom.drpalm.View.controls.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.View.controls.util.FileManager;

public class ImageLoader {

    private MemoryCache memoryCache = new MemoryCache();
    private AbstractFileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    // 线程池
    private ExecutorService executorService;
    private ExecutorService bigImageExecutorService;
    private FileCallback fileCallback;
    private Set<String> urls = new HashSet<String>();

    public ImageLoader(String filePath) {
        FileManager fm = new FileManager();
        fm.setFilePath(filePath);
        fileCache = new FileCache();
        executorService = Executors.newFixedThreadPool(5);
        bigImageExecutorService = Executors.newFixedThreadPool(5);
    }

    /**
     * 加载小图片
     *
     * @param url
     * @param imageView
     * @param isLoadOnlyFromCache
     */
    public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
        if (isPic(url)) {// 根据url判断是不是图片
            imageViews.put(imageView, url);
            // 先从内存缓存中查找
            Bitmap bitmap = memoryCache.get(url);
            if (bitmap != null) {
//                Log.i("IMAGE_URL", "是图片!!而且有缓存!!");
                imageView.setImageBitmap(bitmap);
            } else if (!isLoadOnlyFromCache) {
                // 若没有的话则开启新线程加载图片
                queuePhoto(url, imageView);
//                Log.i("IMAGE_URL", "是图片!!但是没有缓存!!");
            }
        }else {
//            Log.i("IMAGE_URL", "不是图片!!");
//            imageView.setImageBitmap(GlobalVariables.no_pic);
        }
    }

    /**
     * 下载文件，带回调接口
     *
     * @param url
     * @param isLoadOnlyFromCache
     * @param fileName
     * @param fileCallback
     */
    public void loadFile(String url, boolean isLoadOnlyFromCache, String fileName, FileCallback fileCallback) {
        if (url != null) {
            this.fileCallback = fileCallback;
            String fileName1 = url;
            if (url.contains("/") && url.lastIndexOf("/") + 1 < url.length()) {
                fileName1 = url.substring(url.lastIndexOf("/") + 1, url.length());
            } else if (url.lastIndexOf("/") + 1 == url.length()) {
                fileName1 = url;
            }
            // 先从内存缓存中查找
            if (!isLoadOnlyFromCache) {
                // 若没有的话则开启新线程加载图片
                executorService.submit(new QueueFile(url, fileName1));
            }
        }
    }

    /**
     * 下载大图片 ，带回调接口
     *
     * @param url
     * @param bigImageCallback
     */
    public void loadBigPic(String url, BigImageCallback bigImageCallback) {
        if (url != null) {
            // 先从内存缓存中查找
            Bitmap bitmap = memoryCache.get(url);
            if (bitmap != null) {
                bigImageCallback.imageLoaded(url);
            } else {
                // 从文件缓存中读取
                // 若没有的话则开启新线程加载图片
                bigImageExecutorService.submit(new LoadPic(url, bigImageCallback));
            }
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap = memoryCache.get(url);
        if (null == bitmap) {
            bitmap = readCacheFromDisk(url);
        }
        return bitmap;
    }

    class QueueFile implements Runnable {
        String url;
        String fileName;

        QueueFile(String url, String fileName) {
            this.url = url;
            this.fileName = fileName;
        }

        QueueFile(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            File f = fileCache.getFile(url);

            // 先从文件缓存中查找是否有
            boolean b = false;
            if (f != null && f.exists()) {
                b = copyFile(f, fileName);
            }

            if (b && fileCallback != null) {
                fileCallback.fileLoaded(b);
                return;
            }

            if (urls.contains(url)) {
                // 在下载队列中，不下载
                return;
            } else {
                // 没有下载，添加到下载队列
                urls.add(url);
            }
            // 最后从指定的url中下载图片
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                CopyStream(is, os);
                os.close();
                if (f != null && f.exists()) {
                    b = copyFile(f, fileName);
                }

                if (b && fileCallback != null) {
                    fileCallback.fileLoaded(b);
                    urls.remove(url);
                    return;
                }
                return;
            } catch (Exception ex) {
                urls.remove(url);
            }

        }

    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            // 更新的操作放在UI线程中
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    // 下载图片，保存到cache中
    class LoadPic implements Runnable {
        String url;
        BigImageCallback bigImageCallback;

        LoadPic(String url, BigImageCallback bigImageCallback) {
            this.url = url;
            this.bigImageCallback = bigImageCallback;
        }

        @Override
        public void run() {
            File f = fileCache.getFile(url);
            // 先从文件缓存中查找是否有
            Bitmap b = null;
            if (f != null && f.exists()) {

                b = decodeBigFile(f);
            }
            if (b != null) {
                memoryCache.put(url, b);
                bigImageCallback.imageLoaded(url);
                return;
            }

            //URL为空
            if (url.equals("") || url.equals("null")) {
                memoryCache.put("none", GlobalVariables.null_pic);
                bigImageCallback.imageLoaded("none");
                return;
            }

            if (urls.contains(url)) {
                // 在下载队列中，不下载
                return;
            } else {
                // 没有下载，添加到下载队列
                urls.add(url);
            }
            // 最后从指定的url中下载图片
            try {
                Bitmap bitmap = null;
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                CopyStream(is, os);
                os.close();

                bitmap = decodeBigFile(f);
                if (bitmap != null) {
                    memoryCache.put(url, bitmap);
                    bigImageCallback.imageLoaded(url);
                    urls.remove(url);
                    return;
                }
            } catch (Exception ex) {
                memoryCache.put(url, GlobalVariables.wrong_pic);
                bigImageCallback.imageLoaded(url);
                urls.remove(url);
            }
        }
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        // 先从文件缓存中查找是否有
        Bitmap b = null;
        if (f != null && f.exists()) {
            b = decodeFile(f);
        }
        if (b != null) {
            return b;
        }

        //URL为空
        if (url.equals("") || url.equals("null")) {
            return GlobalVariables.null_pic;
        }

        // 最后从指定的url中下载图片
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex) {
            return GlobalVariables.wrong_pic;
        }
    }

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private Bitmap decodeFile(File f) {
        // decode image size
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 100;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            Log.i("xpf", "scale" + scale);
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private Bitmap decodeBigFile(File f) {
        // decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(f.getAbsolutePath(), o);

        // Find the correct scale value. It should be the power of 2.

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        Log.i("xpf", "scale Big width_tmp " + width_tmp + "  scale outHeight " + height_tmp);
        long limit = Runtime.getRuntime().maxMemory() / 16;
        // if (width_tmp * height_tmp * 4 < limit) {
        // Log.i("xpf", "scale=" + 1);
        // return BitmapFactory.decodeFile(f.getAbsolutePath());
        // } else {
        int scale = 1;
        while (true) {
            if (width_tmp * height_tmp * 4 < limit)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        o.inJustDecodeBounds = false;
        o.inSampleSize = scale;
        o.inPurgeable = true;
        Log.i("xpf", "scale=" + scale);
        return BitmapFactory.decodeFile(f.getAbsolutePath(), o);
        // }
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    /**
     * 防止图片错位
     *
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // 用于在UI线程中更新界面
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);

            }

        }
    }

    // 清除理缓存，内存
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
        imageViews.clear();
        urls.clear();
    }

    // 清除内存中的文件
    public void clearMemoryCache() {
        memoryCache.clear();
        imageViews.clear();
        urls.clear();
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            Log.e("", "CopyStream catch Exception...");
        }
    }

    // 对外界开放的回调接口
    public interface BigImageCallback {
        // 注意 此方法是用来设置目标对象的图像资源
        public void imageLoaded(String bmp);
    }

    // 对外界开放的回调接口,返回bytes;
    public interface FileCallback {
        // 注意 此方法是用来设置目标对象的图像资源
        public void fileLoaded(boolean isDone);
    }

    // 复制文件
    public boolean copyFile(File sourceFile, String fileName) {
        Log.i("xpf", "scale 复制图");
        File dirFile = new File(GlobalVariables.ALBUM_SAVEDIRECT_PATH);
        if (!dirFile.exists())
            dirFile.mkdirs();
        File imgFile = new File(dirFile, fileName);
        if (imgFile.exists() && imgFile.canRead() && imgFile.length() > 0) {
            // 已经有了就不用再下载
            return true;
        }
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(imgFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                if (inBuff != null)
                    inBuff.close();
                if (outBuff != null)
                    outBuff.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    public void remove(String url) {
        if (url != null) {
            memoryCache.remove(url);
        }
    }

    public boolean isPic(String fileName) {
        if (fileName == null || fileName.equals("") || fileName.equals("null")) {
            return false;
        }else {
//		boolean flag = false;
//		if (fileName == null)
//			return false;
//		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
//		if ("jpg".equalsIgnoreCase(fileExt) || "png".equalsIgnoreCase(fileExt) || "gif".equalsIgnoreCase(fileExt) || "jpeg".equalsIgnoreCase(fileExt)) {
//			flag = true;
//		} else {
//			flag = false;
//		}
//		return flag;
            return true;
        }
    }

    // 从SD卡读取图片
    protected Bitmap readCacheFromDisk(String key) {
        File file = fileCache.getFile(key);
        if (!file.exists() || !file.canRead()) {
            Log.i("xpf", "has file not exsit!");
            // 没找到缓存对应的文件
            return null;
        } else {
            Log.i("xpf", "has file  exsit^^");
            return decodeBigFile(file);
        }
    }
}