package com.drcom.drpalm.Activitys.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import com.drcom.drpalm.Tool.LanguageManagement;

import java.io.InputStream;

/**
 * Created by 梁炜杰 on 2014/5/27.
 * 导航页基本类
 */
public abstract class BaseStartFragment extends Fragment {

    /**
     * 动画是否已经启动
     */
    protected boolean isAnimationStart;

    /**
     * 屏幕宽度
     */
    protected int screenWidth;

    /**
     * 屏幕高度
     */
    protected int screenHeight;

    /**
     * 该页所显示的图片数组
     */
    protected int[] pic;

    /**
     * 用于反射
     */
    public BaseStartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        //根据系统语言获取不同图片
        if (LanguageManagement.getSysLanguage(getActivity()) == LanguageManagement.CurrentLan.COMPLES_CHINESE) {
            pic = getTPic();
        } else if (LanguageManagement.getSysLanguage(getActivity()) == LanguageManagement.CurrentLan.ENGLISH) {
            pic = getEnPic();
        } else {
            pic = getPic();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setImages();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isAnimationStart()) {
            animationStart();
        }
        setImages();
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 动画是否完毕
     *
     * @return
     */
    public boolean isAnimationStart() {
        return isAnimationStart;
    }

    public void setAnimationStart(boolean isAnimationStart) {
        this.isAnimationStart = isAnimationStart;
    }

    /**
     * 开启导航页动画效果
     */
    public abstract void animationStart();

    /**
     * 获取简体图片
     */
    public abstract int[] getPic();

    /**
     * 获取繁体图片
     */
    public abstract int[] getTPic();

    /**
     * 获取英文图片
     */
    public abstract int[] getEnPic();

    /**
     * 设置图片
     */
    public abstract void setImages();

}
