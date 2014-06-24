package org.videolan.vlc.betav7neon;

import org.videolan.libvlc.LibVlcException;
import org.videolan.vlc.betav7neon.video.VideoPlayerActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class VlcManagement {
	private Context mContext;
//	private SharedPreferences mSettings;
	
	public void init(Context c){
//        mSettings = PreferenceManager.getDefaultSharedPreferences(c);
//        LibVLC.useIOMX(c);
		
		mContext = c;
        try {
            // Start LibVLC
            Util.getLibVlcInstance();
        } catch (LibVlcException e) {
            e.printStackTrace();
            return;
        }
        
//        AudioServiceController.getInstance().bindAudioService(c);
        
//        MediaLibrary.getInstance(c).loadMediaItems(c);
	}
	
	/**
	 * 播放视频
	 * @param url
	 */
	public void OpenVideo(Context c ,final String url,final String urlperview){
		//1
//		ProgressDialog pd = ProgressDialog.show(
//                c,
//                "loading",
//                "Please wait...", true);
//        pd.setCancelable(true);
//        
//		VLCCallbackTask t = new VLCCallbackTask(
//                /* Task to run */
//                new VLCCallbackTask.CallbackListener() {
//                    @Override
//                    public void callback() {
//                        AudioServiceController c = AudioServiceController.getInstance();
//                        String s = url;// input.getText().toString();
//
//                        /* Use the audio player by default. If a video track is
//                         * detected, then it will automatically switch to the video
//                         * player. This allows us to support more types of streams
//                         * (for example, RTSP and TS streaming) where ES can be
//                         * dynamically adapted rather than a simple scan.
//                         */
//                        ArrayList<String> media = new ArrayList<String>();
//                        media.add(s);
//                        c.append(media);
//                    }
//
//                    @Override
//                    public void callback_object(Object o) {
//                        ProgressDialog pd = (ProgressDialog)o;
//                        pd.dismiss();
//                    }
//                }, pd);
//
//            /* Start this in a new friend as to not block the UI thread */
//            new Thread(t).start();
		
		//2 ok
//		if(AudioServiceController.getInstance().IsServiceBind()){
//			play(url);
//		}else{
//			Handler h = new Handler(){
//				@Override
//				public void handleMessage(Message msg) {
//					// TODO Auto-generated method stub
//					super.handleMessage(msg);
//					
//					play(url);
//				}
//			};
//			
//			AudioServiceController.getInstance().setHandlerbindAudioServiceConnected(h);
//			AudioServiceController.getInstance().bindAudioService(c.getApplicationContext());
//		}
		
		//3
//		ArrayList<String> urls = new ArrayList<String>();
//		urls.add(url);
//		
//        AudioServiceController audioController = AudioServiceController.getInstance();
//
//        audioController.load(urls, 0, true, true);
//        Intent intent = new Intent(mContext, AudioPlayerActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        mContext.startActivity(intent);
		
		//4
		Intent intent = new Intent();
		intent.setClass(c, VideoPlayerActivity.class);
		intent.putExtra("path",url);// fileItemList.get(position).getFilePath());
		intent.putExtra("url",urlperview);
		
//		intent.putExtra("name", "test");	//fileItemList.get(position).getFileName()
		c.startActivity(intent);
	}
	
	private void play(final String url){
		/* Start this in a new thread as to not block the UI thread */
        VLCCallbackTask task = new VLCCallbackTask(mContext)
        {
            @Override
            public void run() {
              AudioServiceController c = AudioServiceController.getInstance();
//              String s = input.getText().toString();

              /* Use the audio player by default. If a video track is
               * detected, then it will automatically switch to the video
               * player. This allows us to support more types of streams
               * (for example, RTSP and TS streaming) where ES can be
               * dynamically adapted rather than a simple scan.
               */
              c.append(url);
            }
        };
        task.execute();
	}
	
	/**
	 * 停止VLC视频服务
	 * @param c
	 */
	public void destory(Context c){
//		AudioServiceController.getInstance().unbindAudioService(c);
	}
}
