package com.drcom.drpalm.Tool.request;

import java.io.File;

public interface DownLoadCallback {
	public static class DownloadingStruct{
		public DownloadingStruct(){
			//immediatelySpeed = 0.0;
			//averageSpeed = 0.0;
			totalByte = 0;
			readByte = 0;
		}
		public Double immediatelySpeed;	
		public Double averageSpeed;
		public Integer totalByte;
		public Integer readByte;
	};
	public void onError(String err);
	public void onDownloading(DownloadingStruct obj);
	public void onFinished(File file);
}
