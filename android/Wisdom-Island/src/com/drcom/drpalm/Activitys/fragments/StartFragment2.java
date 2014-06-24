package com.drcom.drpalm.Activitys.fragments;

import com.drcom.drpalm4tianzhujiao.R;

/**
 * Created by 梁炜杰 on 2014/5/27.
 * 导航页2
 */
public class StartFragment2 extends SimpleStartFragment {
    @Override
    public int[] getPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p2_a;
        pic[1] = R.drawable.p2_b;
        pic[2] = R.drawable.p2_c;
        return pic;
    }

    @Override
    public int[] getTPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p2_a_t;
        pic[1] = R.drawable.p2_b_t;
        pic[2] = R.drawable.p2_c_t;
        return pic;
    }

    @Override
    public int[] getEnPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p2_a_en;
        pic[1] = R.drawable.p2_b_en;
        pic[2] = R.drawable.p2_c_en;
        return pic;
    }
}
