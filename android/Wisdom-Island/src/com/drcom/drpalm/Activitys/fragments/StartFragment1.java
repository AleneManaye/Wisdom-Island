package com.drcom.drpalm.Activitys.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import com.wisdom.island.R;

/**
 * Created by 梁炜杰 on 2014/5/27.
 * 导航页1
 */
public class StartFragment1 extends SimpleStartFragment {

    private ImageView imageView1;

    public StartFragment1(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.start_fragment1, null);
        if (root != null) {
            imageView1 = (ImageView) root.findViewById(R.id.left_iv);
        }
        return root;
    }

    @Override
    public void animationStart() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(500);
        imageView1.startAnimation(alphaAnimation);
        setAnimationStart(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(imageView1 != null){
            imageView1.setImageBitmap(null);
        }
    }

    @Override
    public void setImages() {
        if(imageView1 != null){
            imageView1.setImageBitmap(readBitMap(getActivity(),pic[0]));
        }
    }

    @Override
    public int[] getPic() {
        int[] pic = new int[1];
        pic[0] = R.drawable.p1_a;
        return pic;
    }

    @Override
    public int[] getTPic() {
        int[] pic = new int[1];
        pic[0] = R.drawable.p1_a_t;
        return pic;
    }

    @Override
    public int[] getEnPic() {
        int[] pic = new int[1];
        pic[0] = R.drawable.p1_a_en;
        return pic;
    }
}
