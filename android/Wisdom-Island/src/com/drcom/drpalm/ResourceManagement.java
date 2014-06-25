package com.drcom.drpalm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.drcom.drpalm.Tool.zip.ZipUtils;
import com.wisdom.island.R;

public class ResourceManagement {	
	private static class Picture{
		private Bitmap bitmap = null;
		private int count = 0;
		Picture(){};
		Picture(Bitmap bitmap){
			this.bitmap = bitmap;
			count = 1;
		}
		public Bitmap getBitmap(){
			count++;
			return bitmap;
		}
		public boolean releaseBitmap(){
			if(0 == --count){				
				bitmap.recycle();
				bitmap = null;
				return true;
			}			
			return false;		
		}
		public int getCount(){
			return count;
		}
	};
	private Options mOptions;
	private String mResourceDirectory = GlobalVariables.getResourceDirectory();	
	private String mResourceZipDirectory = GlobalVariables.getResourceZipDirectory();	
	//private String mResourceUpdateZip = GlobalVariables.gAppContext.getString(R.string.ResourceUpdateZip);
	private Map<String,Picture> mFileMap;
	private Map<String,Picture> mConnerBitmapMap;
	static private ResourceManagement mResourceManagement = null;	
	public static ResourceManagement getResourceManagement()
	{
		if(null == mResourceManagement){
			mResourceManagement = new ResourceManagement();	
		}    	
    	return mResourceManagement;
	}	
	public ResourceManagement() {
		mFileMap = new HashMap<String,Picture>();
		mConnerBitmapMap = new HashMap<String,Picture>();
	}	
	public void setOptions(Options options){
		mOptions = options;
	}
	public boolean isEmpty(String path){
		File dir = new File(path);
		if(dir.exists()){
			String[] files = dir.list();
			if(0 < files.length)
				return false;
		}
		return true;
	}
	public boolean initResource(String zipName, boolean isForce){
		if(isForce){
			//freeDrawableMemory();
		}		
		if("" != zipName){
			return initResourceWithZip(zipName, isForce);				
		}	
		return initDefaultResource(isForce);		
	}
	public boolean initResourceWithZip(String zipName, boolean isForce){
		// unzip /files/tourzip/"zipName" to /files/tours/
		//String tourZipPath = GlobalVariables.getResourceZipDirectory();
		String toursWholeFileName = mResourceZipDirectory + "/" + zipName;
		//String toursWholePath = GlobalVariables.getResourceDirectory();		
		if(!isEmpty(mResourceDirectory) && !isForce){
			return true;
		}
		//clearDirectroy(new File(mResourceDirectory),false);
	 	File zipFile = new File(toursWholeFileName);
	 	try {
			ZipUtils.upZipFile(zipFile, mResourceDirectory);
			//deleteAllOtherFile(new File(zipFile));
			//zipFile.delete();
			return true;
		} catch (ZipException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean initDefaultResource(boolean isForce){ 		
 		// copy tours.zip to /files/tourzip/tours.zip 		
 		//String tourZipPath = GlobalVariables.getResourceZipDirectory(); 		 		
 		AssetManager am = GlobalVariables.gAppContext.getAssets();
 		String zipName = GlobalVariables.gAppResource.getString(R.string.DefaultResourceZip_160);
 		switch(GlobalVariables.nDensity){
 		case 160:
 			zipName = GlobalVariables.gAppResource.getString(R.string.DefaultResourceZip_160);
 			break;
 		case 240:
 			zipName = GlobalVariables.gAppResource.getString(R.string.DefaultResourceZip_240);
 			break;
 		case 320:
 			zipName = GlobalVariables.gAppResource.getString(R.string.DefaultResourceZip_320);
 			break;
 		default:
 			zipName = GlobalVariables.gAppResource.getString(R.string.DefaultResourceZip_240);
 			break;
 		} 		
 		//String destZipName = mResourceZipDirectory + '/' + mResourceUpdateZip;
 		if(AssetsFileManagement.copysplitAssetsFile(am, zipName, mResourceZipDirectory, isForce)){
 			// unzip /files/tourzip/tours.zip to /files/tours/ 			
 			String toursWholeFileName = mResourceZipDirectory + "/" + zipName;
 			//String toursWholeFileName = destZipName;		
 			if(!isEmpty(mResourceDirectory) && !isForce){
 				return true;
 			}
 			clearDirectroy(new File(mResourceDirectory),false);
 	 		File zipFile = new File(toursWholeFileName);
 	 		try {
 				ZipUtils.upZipFile(zipFile, mResourceDirectory);
 				// delete zip
 				zipFile.delete();
 				return true;
 			} catch (ZipException e) {
 				e.printStackTrace();
 				return false;
 			} catch (IOException e) {
 				e.printStackTrace();
 				return false;
 			}
 		}
 		return false;
 		
	}
	private void deleteAllOtherFile(File file){
		File parent = file.getParentFile();
		if(null != parent){
			if(parent.exists() && parent.isDirectory()){
				File[] fileList = parent.listFiles();
				for(File currentLevelFile : fileList){
					if(!currentLevelFile.equals(file)){
						clearDirectoryRecursion(currentLevelFile);
					}
				}				
			}
		}		
	}
	public void clearZipDirectroy(){
		clearDirectroy(new File(mResourceZipDirectory),false);
	}
	private void clearDirectroy(File dir, boolean isDeleteItself){
		if(dir.exists()){
			clearDirectoryRecursion(dir);
			if(isDeleteItself)
				dir.delete();
		}
	}
	private void clearDirectoryRecursion(File dir){
		if(dir.exists()){
			if(dir.isDirectory()){
				File[] fileList = dir.listFiles();
				for(File file : fileList){			
					clearDirectoryRecursion(file);
				}
			}else{
				dir.delete();				
			}			
		}
	}
	public void freefreeBitmapByDrawable(Drawable drawable){
		BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
		Bitmap bitmap = bitmapDrawable.getBitmap();
		Picture picture;
		if(null != bitmap){
			for(Map.Entry<String, Picture> entry : mFileMap.entrySet()){
				picture = entry.getValue();
				if(null != picture){
					if(picture.bitmap.equals(bitmap)){
						if(picture.releaseBitmap()){
							mConnerBitmapMap.remove(entry.getKey());
						}
						return;
					}
				}
			}
			for(Map.Entry<String, Picture> entry : mConnerBitmapMap.entrySet()){
				picture = entry.getValue();
				if(null != picture){
					if(picture.bitmap.equals(bitmap)){
						if(picture.releaseBitmap()){
							mConnerBitmapMap.remove(entry.getKey());
						}
						return;
					}
				}
			}
		}
	}
	public void freeBitmapByFileName(String fileName){
		if(mFileMap.containsKey(fileName)){
			if(mFileMap.get(fileName).releaseBitmap()){				
				mFileMap.remove(fileName);
			}
		}
		if(mConnerBitmapMap.containsKey(fileName)){
			if(mConnerBitmapMap.get(fileName).releaseBitmap()){
				mConnerBitmapMap.remove(fileName);
			}
		}
	}
	public Drawable getDrawableByFileName(String fileName){
		BitmapDrawable bitmapDrawable = null;
		Picture picture = (Picture)mFileMap.get(fileName);
		Bitmap bitmap;
		if(null == picture){			
			String strFileName = mResourceDirectory + fileName;
			bitmap = getBitmapFromFile(strFileName);
			if(null != bitmap){
				picture = new Picture(bitmap);
				mFileMap.put(fileName, picture);
			}
		}
		else{
			bitmap = picture.getBitmap();
		}
		if(null != bitmap){
			bitmapDrawable = new BitmapDrawable(bitmap);	
		}
		return bitmapDrawable;
	}
	public Drawable getDrawableByID(int id){
		return GlobalVariables.gAppResource.getDrawable(id);
	}	
	public void freeDrawableMemory(){		
		try{
			if(mFileMap.isEmpty())
				return;
			for(Map.Entry<String, Picture> entry : mFileMap.entrySet()){			
				Bitmap bitmap = (Bitmap)entry.getValue().getBitmap();
				bitmap.recycle();
			}
			mFileMap.clear();
			
			if(mConnerBitmapMap.isEmpty())
				return;
			for(Map.Entry<String, Picture> entry : mConnerBitmapMap.entrySet()){			
				Bitmap bitmap = (Bitmap)entry.getValue().getBitmap();
				bitmap.recycle();
			}
			mConnerBitmapMap.clear();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	private Bitmap getBitmapFromFile(String fileAbsolutePath){
		Bitmap bitmap = null;						
		bitmap = BitmapFactory.decodeFile(fileAbsolutePath, mOptions);		
		return bitmap;
	}
	//生成圆角图片  
	public Drawable getRoundedCornerBitmap(String fileName){
		BitmapDrawable bitmapDrawable = null;
		Picture picture = (Picture)mConnerBitmapMap.get(fileName);		
		Bitmap bitmap = null;
		if(null == picture){
			String strFileName = mResourceDirectory + fileName;
			BitmapDrawable drawable = (BitmapDrawable)getDrawableByFileName(strFileName);
			bitmap = GetRoundedCornerBitmap(drawable.getBitmap());
			if(null != bitmap){
				picture = new Picture(bitmap);
				mConnerBitmapMap.put(fileName, picture);
			}
		}
		else{
			bitmap = picture.getBitmap();
		}
		if(null != bitmap){
			bitmapDrawable = new BitmapDrawable(bitmap);	
		}
		return bitmapDrawable;
	}
	private Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {  
	    try {  
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),  
	                bitmap.getHeight(), Config.ARGB_8888);  
	        Canvas canvas = new Canvas(output);                  
	        final Paint paint = new Paint();  
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(),  
	                bitmap.getHeight());         
	        final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),  
	                bitmap.getHeight()));  
	        final float roundPx = 6;  
	        paint.setAntiAlias(true);  
	        canvas.drawARGB(0, 0, 0, 0);  
	        paint.setColor(Color.BLACK);         
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));              
	   
	        final Rect src = new Rect(0, 0, bitmap.getWidth(),  
	                bitmap.getHeight());  
	           
	        canvas.drawBitmap(bitmap, src, rect, paint);     
	        return output;  
	    } catch (Exception e) {          
	        return bitmap;  
	    }  
	} 	
}
