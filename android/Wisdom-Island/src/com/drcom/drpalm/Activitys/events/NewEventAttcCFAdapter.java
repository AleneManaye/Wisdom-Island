package com.drcom.drpalm.Activitys.events;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.drcom.drpalm.Tool.FileManagement;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.CoverFlow.CoverFlow;
import com.drcom.drpalm.objs.EventDraftItem.Attachment;
import com.wisdom.island.R;


public class NewEventAttcCFAdapter extends ArrayAdapter<Attachment> {
	int mGalleryItemBackground;
	private Context mContext;
	
	private static int ITEM_WIDTH_DP = 208;	//控制距离
	private static int ITEM_HEIGHT_DP = 208;	//控制大小

	private List<Attachment> mModulelist;
	private Attachment info;
	private LayoutInflater listContainer;  
	
	//业务常量
	final static String TourModuleName = "GrandviewModule";
	final static String NewsModuleName = "MessageModule";
	final static String EventsModuleName = "GuideModule";
	final static String SettingModuleName = "ClubModule";
	final static String AboutModuleName = "FavModule";
	final static String DrCOMWSModule   = "AboutModule";
	//业务变量
//	private List<Bitmap> mNormalBitmapList = new ArrayList<Bitmap>();
//	private List<Bitmap> mSelectBitmapList = new ArrayList<Bitmap>();
	
	public NewEventAttcCFAdapter(Context c, List<Attachment> modulelist) {
		super(c, 0, modulelist);
		mContext = c;
//		mImageIds = ImageIds;
		mModulelist = modulelist;
		this.listContainer = LayoutInflater.from(c); 
//		if(ResourceManagement.getResourceManagement()== null)
//			ResourceManagement.getResourceManagement() = ResourceManagement.getResourceManagement();
//		for(int i = 0 ; i < mModulelist.size(); i++){
//			try{
//				Drawable d = ResourceManagement.getResourceManagement().getDrawableByFileName(mModulelist.get(i).strPicPath);
//				BitmapDrawable bd = (BitmapDrawable) d; 
//				Bitmap bm = bd.getBitmap(); 
				
	//			mNormalBitmapList.add(createReflectedImagesBitmap(mContext,bm));
//				mNormalBitmapList.add(MyMothod.Byte2Bitmap(mModulelist.get(i).data));
				
//				Drawable select_d = ResourceManagement.getResourceManagement().getDrawableByFileName(mModulelist.get(i).strSelectedPicPath);
//				BitmapDrawable select_bd = (BitmapDrawable) select_d; 
//				Bitmap select_bm = select_bd.getBitmap(); 
//				
//	//			mSelectBitmapList.add(createReflectedImagesBitmap(mContext,select_bm));
//				mSelectBitmapList.add(select_bm);
//			}
//			catch (Exception e) {
//				// TODO: handle exception
//				break;
//			}
//		}
}

	public int getCount() {
		return mModulelist.size();
//		return Integer.MAX_VALUE;
	}

	public Attachment getItem(int position) {
		return mModulelist.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
//		ImageView i = new ImageView(mContext);
//		i.setImageBitmap(mNormalBitmapList.get(position % mModulelist.size()));
//		i.setTag(R.id.tag_first,mNormalBitmapList.get(position % mModulelist.size()));		//未选中图标
//		i.setTag(R.id.tag_second,mSelectBitmapList.get(position % mModulelist.size()));		//选中图标
//		
//		i.setLayoutParams(new CoverFlow.LayoutParams(MyMothod.Dp2Px(mContext, ITEM_WIDTH_DP),MyMothod.Dp2Px(mContext, ITEM_HEIGHT_DP)));//(110,130));//(180, 200));
//		i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//		
//		// 设置的抗锯齿
//		BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
//		drawable.setAntiAlias(true);
//		
//		return i;
		
		
		MainItemViewHolder holder = null;
		info = this.getItem(position);
		final int index = position;
		if (convertView == null) {
			convertView =  listContainer.inflate(R.layout.attc_item_view, null);
			holder = new MainItemViewHolder();
			convertView.setTag(holder);
			
			holder.imgBg = (ImageView)convertView.findViewById(R.id.main_item_imageView);
			holder.imgDel = (ImageView)convertView.findViewById(R.id.main_item_del_imageView);
//			holder.imgDel.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Log.i("zjj", "删掉第:" + index);
//					mModulelist.remove(index);
//					notifyDataSetChanged();
//				}
//			});
//			holder.txtSum = (TextView)convertView.findViewById(R.id.main_item_msgnum_txtView);
			
		}else {
			holder = (MainItemViewHolder) convertView.getTag();
		}
		
//		holder.imgBg.setImageBitmap(mNormalBitmapList.get(position % mModulelist.size()));
		holder.imgBg.setBackgroundResource(R.drawable.white_kk);
		//判断是否为图片，非图片应该显示文件图标
		if(info.fileType.equals("png")||info.fileType.equals("jpg")){
			if(info.data != null){
				holder.imgBg.setImageBitmap(MyMothod.Byte2Bitmap(info.data,true));	
			}else{
				holder.imgBg.setImageResource(FileManagement.getFileIcon(mContext, info.fileType));
//				mImageLoader.DisplayImage(info., holder.mImageView, false);
			}
		}else{
			holder.imgBg.setImageResource(FileManagement.getFileIcon(mContext, info.fileType));
		}
		// 设置的抗锯齿
		BitmapDrawable drawable = (BitmapDrawable) holder.imgBg.getDrawable();
		drawable.setAntiAlias(true);
		
		holder.imgDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mModulelist.remove(index);
				notifyDataSetChanged();
				
				((NewEventAttc2Activity)mContext).ReflashUI();
			}
		});
		
//		holder.txtSum.setVisibility(View.GONE);
//		holder.index = position;
		
		convertView.setLayoutParams(new CoverFlow.LayoutParams(MyMothod.Dp2Px(mContext, ITEM_WIDTH_DP),MyMothod.Dp2Px(mContext, ITEM_HEIGHT_DP)));

		return convertView;
	}
	
	
    public class MainItemViewHolder{
    	ImageView imgBg;
//    	TextView txtSum;
    	int mIndex = 0;
    	public ImageView imgDel;
    	
//    	public MainItemViewHolder(int index){
//    		mIndex = index;
//    		
//    		imgDel.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Log.i("zjj", "删掉第:" + mIndex);
//					mModulelist.remove(mIndex);
//					notifyDataSetChanged();
//				}
//			});
//    	}
    	
//    	public void setImgBgSelected(int position){
//    		imgBg.setImageBitmap(mSelectBitmapList.get(position % mModulelist.size()));
//    	}
//    	
//    	public void setImgBgNormal(){
//    		imgBg.setImageBitmap(mNormalBitmapList.get(index));
//    	}
    	
//    	public void setMsgSum(int sum){
//    		if(sum != 0){
//    			txtSum.setText(String.valueOf(sum));
//    			txtSum.setVisibility(View.VISIBLE);
//    		}else{
//    			txtSum.setVisibility(View.GONE);
//    		}
//    	}
    }
	
	
	
	

	private float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}
	
	/**
	 * 设置镜像图像
	 * @param mContext
	 * @param imageId
	 * <A href="http://my.oschina.net/u/556800" rel=nofollow target=_blank>@return</A> 
	 */
	private ImageView createReflectedImages(Context mContext,Bitmap originalImage) {
		final int reflectionGap = 4;
		
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 4, width, height / 4, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 4), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(originalImage, 0, 0, null);

		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originalImage
				.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);

		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		ImageView imageView = new ImageView(mContext);
		imageView.setImageBitmap(bitmapWithReflection);

		return imageView;
	}
	
	/**
	 * 设置镜像图像
	 * @param mContext
	 * @param imageId
	 * <A href="http://my.oschina.net/u/556800" rel=nofollow target=_blank>@return</A> 
	 */
	private Bitmap createReflectedImagesBitmap(Context mContext,Bitmap originalImage) {
		final int reflectionGap = 4;
		
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(originalImage, 0, 0, null);

		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originalImage
				.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);

		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

//		ImageView imageView = new ImageView(mContext);
//		imageView.setImageBitmap(bitmapWithReflection);

		return bitmapWithReflection;
	}

}
