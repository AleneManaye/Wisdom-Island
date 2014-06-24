package com.drcom.drpalm.View.controls.cache;

import com.drcom.drpalm.View.controls.util.FileManager;

public class FileCache extends AbstractFileCache {

	@Override
	public String getSavePath(String url) {
		String filename = String.valueOf(url.hashCode());
		return getCacheDir() + filename;
	}

	@Override
	public String getCacheDir() {

		return FileManager.getSaveFilePath();
	}

}
