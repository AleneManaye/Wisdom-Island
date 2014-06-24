package com.drcom.drpalm.Activitys.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.drcom.drpalm.Activitys.Navigation.NavigationMainActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * Created by 梁炜杰 on 2014/5/28.
 * 导航页6
 */
public class StartFragment6 extends SimpleStartFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        PreviewChildView viewChild = new PreviewChildView(getActivity());
        viewChild.setButtonAreaVisibility(View.VISIBLE);

        if(root instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) root;
            viewGroup.addView(viewChild);
        }

        return root;
    }

    @Override
    public int[] getPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p6_a;
        pic[1] = R.drawable.p6_b;
        pic[2] = R.drawable.p6_c;
        return pic;
    }

    @Override
    public int[] getTPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p6_a_t;
        pic[1] = R.drawable.p6_b_t;
        pic[2] = R.drawable.p6_c_t;
        return pic;
    }

    @Override
    public int[] getEnPic() {
        int[] pic = new int[3];
        pic[0] = R.drawable.p6_a_en;
        pic[1] = R.drawable.p6_b_en;
        pic[2] = R.drawable.p6_c_en;
        return pic;
    }

    /**
     * 进入主页按钮
     */
    public class PreviewChildView extends LinearLayout {
        private ImageView bgImageView = null;
        private Button shareBtn = null;
        private Button startBtn = null;
        private LinearLayout buttonArea = null;
        public PreviewChildView(Context context) {
            super(context);
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.first_preview, this);
            bgImageView = (ImageView)findViewById(R.id.bg_image);
            shareBtn = (Button)findViewById(R.id.share_button);
            startBtn = (Button)findViewById(R.id.start_button);
            buttonArea = (LinearLayout)findViewById(R.id.attach_area);
            shareBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String subject = getActivity().getString(R.string.app_name) ;
                    String title = getActivity().getString(R.string.sharetitle);
                    String url  = title + getActivity().getString(R.string.shareurl);
                    shareContent(getActivity(), subject, url);
                }
            });

            if(LanguageManagement.getSysLanguage(getActivity()) == LanguageManagement.CurrentLan.COMPLES_CHINESE){
                shareBtn.setBackgroundResource(R.drawable.btn_welcome_share_t_selector);
                startBtn.setBackgroundResource(R.drawable.btn_welcome_enter_t_selector);
            }else if(LanguageManagement.getSysLanguage(getActivity()) == LanguageManagement.CurrentLan.ENGLISH){
                shareBtn.setBackgroundResource(R.drawable.btn_welcome_share_en_selector);
                startBtn.setBackgroundResource(R.drawable.btn_welcome_enter_en_selector);
            }

            startBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(GlobalVariables.getAppDefaultSchoolKey()){
                        //保存为默认的SCHOOL
                        SharedPreferences preferences = getActivity().getSharedPreferences("default_school", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("school_key", GlobalVariables.gSchoolKey);
                        editor.commit();

                        Intent sendIntent = new Intent();
                        sendIntent.setClass(getActivity(),MainActivity.class);
                        startActivity(sendIntent);
                    }else{
                        Intent sendIntent = new Intent();
                        sendIntent.setClass(getActivity(),NavigationMainActivity.class);
//						sendIntent.putExtra(SchoolNavigation.NAVIGATION_PARENT_ID, GlobalVariables.getAgentID());	//根目录ID(代理商ID)
                        startActivity(sendIntent);
                    }

                    //释放资源
//                    mViewGroup.removeAllViews();
                    getActivity().finish();
                }
            });
        }

        public void setBgImage(int res_id){
            bgImageView.setBackgroundResource(res_id);
        }

        public void setButtonAreaVisibility(int visibility){
            buttonArea.setVisibility(visibility);
        }

        public void shareContent(Context context, String subject, String url) {

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, url);
            context.startActivity(Intent.createChooser(intent, "Share"));

        }
    }
}
