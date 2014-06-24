package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drcom.drpalm4tianzhujiao.R;

public class MyMainItem extends LinearLayout{
	Context mContext;
	ImageView img;
	ImageView imgdel;
	TextView txt;
	TextView numTxt;
	RelativeLayout mRelativeLayoutMain;
	View mView;
	
	public MyMainItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.main_item, this);
		
		mRelativeLayoutMain = (RelativeLayout)findViewById(R.id.main_item_mainLayout);
		img = (ImageView)findViewById(R.id.main_item_imageView);
		imgdel = (ImageView)findViewById(R.id.main_item_del_imageView);
		txt = (TextView)findViewById(R.id.main_item_textView);
		numTxt = (TextView)findViewById(R.id.main_item_number_txtv);
	}
	
	/**
	 * 设置大小
	 */
	public void setSize(int width,int height){
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(mContext, width) , MyMothod.Dp2Px(mContext, height));
		mRelativeLayoutMain.setLayoutParams(p);
	}
	
	/**
	 * 设置图片
	 * @param rsid
	 */
	public void setImgRsid(int rsid){
		if(rsid != -1 || rsid != 0)
//			img.setBackgroundResource(rsid);
//			mRelativeLayoutMain.setBackgroundResource(rsid);
			mView.setBackgroundResource(rsid);
	}
	
	/**
	 * 设置数字
	 * @param num
	 */
	public void setNum(String num){
		if(num.equals("") || num.equals("0")){
			numTxt.setVisibility(View.GONE);
		}else{
			numTxt.setText(num);
			numTxt.setVisibility(View.VISIBLE);
		}
		
	}
	
	/**
	 * 设置删除按钮是否可视
	 * @param b
	 */
	public void setDelVisible(boolean b){
		if(b)
			imgdel.setVisibility(View.VISIBLE);
		else
			imgdel.setVisibility(View.GONE);
	}
	
	/**
	 * 设置文字
	 * @param txt
	 */
	public void setText(String text){
		if(text.equals(""))
			txt.setVisibility(View.GONE);
		else
			txt.setVisibility(View.VISIBLE);
	}
	
}
