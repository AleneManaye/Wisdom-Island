package com.drcom.drpalm.Activitys.fragments;

import com.wisdom.island.R;

/**
 * Created by 梁炜杰 on 2014/5/28.
 * 导航页4
 */
public class StartFragment4 extends SimpleStartFragment {

    @Override
    public int[] getPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p4_a;
        pic[1] = R.drawable.p4_b;
        pic[2] = R.drawable.p4_c;
        return pic;
    }

    @Override
    public int[] getTPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p4_a_t;
        pic[1] = R.drawable.p4_b_t;
        pic[2] = R.drawable.p4_c_t;
        return pic;
    }

    @Override
    public int[] getEnPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p4_a_en;
        pic[1] = R.drawable.p4_b_en;
        pic[2] = R.drawable.p4_c_en;
        return pic;
    }
}
