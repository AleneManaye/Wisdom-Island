package com.drcom.drpalm.Activitys.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * Created by 梁炜杰 on 2014/5/27.
 * 实现了基本的左右滑出动画效果
 * 1.假如需要复杂的效果就需要重写animationStart()方法
 * 2.通常需要重写
 *      getTPic()
 *      getEnPic()
 *      getPic()
 *      三种语言环境下获取图片的方法
 *      还有setImages()方法
 *
 */
public abstract class SimpleStartFragment extends BaseStartFragment {

    private ImageView centerImageView;
    private ImageView leftImageView;
    private ImageView rightImageView;

    private RelativeLayout.LayoutParams leftLayoutParams;
    private RelativeLayout.LayoutParams rightLayoutParams;

    //渐现的时间
    private final static int SHOW_ALPHA = 500;
    //滑动的时间
    private final static int SHOW_TRANS = 250;

    public SimpleStartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.start_fragment2,null);
        if(root != null) {
            centerImageView = (ImageView) root.findViewById(R.id.p2_a);
            leftImageView = (ImageView) root.findViewById(R.id.p2_b);
            rightImageView = (ImageView) root.findViewById(R.id.p2_c);
            setImages();
            leftLayoutParams = (RelativeLayout.LayoutParams) leftImageView.getLayoutParams();
            leftLayoutParams.leftMargin = -screenWidth;
            rightLayoutParams = (RelativeLayout.LayoutParams) rightImageView.getLayoutParams();
            rightLayoutParams.rightMargin = -screenWidth;
        }
        return root;
    }

    @Override
    public void setImages(){
        if(centerImageView != null && leftImageView != null && rightImageView != null){
            centerImageView.setImageBitmap(readBitMap(getActivity(), pic[0]));
            leftImageView.setImageBitmap(readBitMap(getActivity(), pic[1]));
            rightImageView.setImageBitmap(readBitMap(getActivity(), pic[2]));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setAnimationStart(false);
        if(centerImageView != null && leftImageView != null && rightImageView != null){
            centerImageView.setImageBitmap(null);
            leftImageView.setImageBitmap(null);
            rightImageView.setImageBitmap(null);
        }
    }

    @Override
    public void animationStart() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(SHOW_ALPHA);
        alphaAnimation.setFillAfter(true);
        centerImageView.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                TranslateAnimation translateFromLeftToRight = new TranslateAnimation(0,-screenWidth,0,0);
                translateFromLeftToRight.setDuration(SHOW_TRANS);
                translateFromLeftToRight.setFillAfter(true);
                rightImageView.startAnimation(translateFromLeftToRight);

                translateFromLeftToRight.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        TranslateAnimation translateFromRightToLeft = new TranslateAnimation(0,screenWidth,0,0);
                        translateFromRightToLeft.setDuration(SHOW_TRANS);
                        translateFromRightToLeft.setFillAfter(true);
                        leftImageView.startAnimation(translateFromRightToLeft);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        setAnimationStart(true);
    }
}
