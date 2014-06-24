package com.drcom.drpalm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CommonFileManagent {
	static public boolean copyFile(String relativeFilePath, String destFileWholePath, boolean isRecover){	
		// destination
		//File destFileDir = new File(destFile);
//		if(!destFileDir.exists()){
//			destFileDir.mkdir(); 
//		}
		String fileName = getFileNameFromPath(relativeFilePath);
		if(null == fileName || "" == fileName)
			return false;
//		if(null == destDir || "" == destDir)
//			return false;
		//String destFileName = destFile;//destDir + "/" + fileName;		
		File destFile = new File(destFileWholePath);
		mkdir(destFile.getParent());
		// cover it if exist
		if(destFile.exists())
			if(isRecover)
				destFile.delete();
			else
				return true;
		// source file is not exist
		File orginFile = new File(relativeFilePath);	
		if(null == orginFile)
			return false;
		if(!orginFile.exists())
			return false;
		
		// open asset file and copy
		try {			
			FileInputStream in = new FileInputStream(orginFile);
			FileOutputStream out = new FileOutputStream(destFile); 
            byte[] buffer = new byte[4*1024];  
            int byteRead = 0;
            while( (byteRead = in.read(buffer)) != -1){  
            	out.write(buffer,0,byteRead);  
            }  
            out.flush(); 
            out.close();
            in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;	

	}

	
	static public boolean isDirectory(String path){
		return true?false:(-1 == path.lastIndexOf('.'));
	}
	static public String getFileNameFromPath(String relativeFilePath){
		String value;
		int start = relativeFilePath.lastIndexOf('/');
		if(-1 != start)
			value = relativeFilePath.substring(start + 1);
		else
			value = relativeFilePath;
		return value;
	}
	private static void mkdir(String dirPath){
		File dir = new File(dirPath);
		if(null != dir){
			File parDir = new File(dir.getParent());
			if(null != parDir){
				if(!parDir.exists()){
					mkdir(parDir.getPath());
				}
				else {
					File newDir = new File(dirPath);
					newDir.mkdir();
				}
			}
		}
	}
}
