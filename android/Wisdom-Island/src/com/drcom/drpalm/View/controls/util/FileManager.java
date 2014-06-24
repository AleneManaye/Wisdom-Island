package com.drcom.drpalm.View.controls.util;

import com.drcom.drpalm.GlobalVariables;

public class FileManager {
	private static String filePath = "files";

	public static String getSaveFilePath() {
		String dirPath = CommonUtil.getRootFilePath() + "EbabyAlbum/";
		if (CommonUtil.hasSDCard()) {
			dirPath = CommonUtil.getRootFilePath() + "EbabyAlbum/" + GlobalVariables.ALBUM_CACHE + "/";
			FileHelper.createDirectory(dirPath);
			return dirPath;
		} else {
			dirPath = CommonUtil.getRootFilePath() + "EbabyAlbum/" + GlobalVariables.ALBUM_CACHE;
			FileHelper.createDirectory(dirPath);
			return dirPath;
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		FileManager.filePath = filePath;
	}
}
