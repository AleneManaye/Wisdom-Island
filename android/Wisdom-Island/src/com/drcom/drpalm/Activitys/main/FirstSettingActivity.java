package com.drcom.drpalm.Activitys.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.drcom.drpalm.Activitys.fragments.BaseStartFragment;
import com.drcom.drpalm.Activitys.util.AnimationFragmentUtil;
import com.drcom.drpalm4tianzhujiao.R;
import com.drcom.ui.View.tool.MyMothod;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FirstSettingActivity extends FragmentActivity {

    private ViewPager viewPager;

    private LinearLayout navigationView;

    private FragmentManager fragmentManager;

    private StartFragmentAdapter fragmentPagerAdapter;

    private List<Fragment> fragmentList;

    //是否被选中
    private boolean[] check;

    private int lastIndex = 0;

    //索引点的大小
    private int indexSize ;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);	//一定要放在第一行
        super.onCreate(savedInstanceState);

        indexSize = com.drcom.ui.View.tool.MyMothod.Px2Dp(getApplicationContext(),32);
        setContentView(R.layout.welcome_main);
        findByIds();
        initFragments();
        initViews();
    }

    public void findByIds() {
        viewPager = (ViewPager) findViewById(R.id.start_view);
        navigationView = (LinearLayout) findViewById(R.id.navigation_view);
    }

    public void initViews() {
        fragmentManager = getSupportFragmentManager();
        fragmentPagerAdapter = new StartFragmentAdapter(fragmentList, fragmentManager);

        viewPager.setAdapter(fragmentPagerAdapter);

        for (int i = 0; i < check.length; i++) {
            View view = new ImageView(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(indexSize, indexSize);
            layoutParams.setMargins(5, 5, MyMothod.Px2Dp(getApplicationContext(),20), MyMothod.Px2Dp(getApplicationContext(),20));
            view.setLayoutParams(layoutParams);

            if (check[i]) {
                view.setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                view.setBackgroundResource(R.drawable.page_indicator);
            }
            navigationView.addView(view);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                BaseStartFragment baseStartFragment = (BaseStartFragment) fragmentList.get(i);
                if (!baseStartFragment.isAnimationStart()) {
                    baseStartFragment.animationStart();
                }
                check[i] = true;
                check[lastIndex] = false;
                View focusView = navigationView.getChildAt(i);
                View normalView = navigationView.getChildAt(lastIndex);
                if (focusView != null && normalView != null) {
                    focusView.setBackgroundResource(R.drawable.page_indicator_focused);
                    normalView.setBackgroundResource(R.drawable.page_indicator);
                }

                lastIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        viewPager.setCurrentItem(0);
    }

    public void initFragments() {
        try {
            InputStream inputStream = getResources().getAssets().open("fragment_map.xml");
            fragmentList = AnimationFragmentUtil.loadAnimationFragments(inputStream);
            if (fragmentList != null) {
                check = new boolean[fragmentList.size()];
                check[0] = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class StartFragmentAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList;

        public StartFragmentAdapter(List<Fragment> fragmentList, FragmentManager fm) {
            super(fm);
            this.fragmentList = fragmentList;
        }
        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

}
