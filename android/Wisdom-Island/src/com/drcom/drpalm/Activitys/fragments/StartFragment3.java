package com.drcom.drpalm.Activitys.fragments;

import com.wisdom.island.R;

/**
 * Created by 梁炜杰 on 2014/5/28.
 * 导航页3
 */
public class StartFragment3 extends SimpleStartFragment {

    @Override
    public int[] getPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p3_a;
        pic[1] = R.drawable.p3_b;
        pic[2] = R.drawable.p3_c;
        return pic;
    }

    @Override
    public int[] getTPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p3_a_t;
        pic[1] = R.drawable.p3_b_t;
        pic[2] = R.drawable.p3_c_t;
        return pic;
    }

    @Override
    public int[] getEnPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p3_a_en;
        pic[1] = R.drawable.p3_b_en;
        pic[2] = R.drawable.p3_c_en;
        return pic;
    }
}
