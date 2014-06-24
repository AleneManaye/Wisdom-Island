package com.drcom.drpalm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import android.content.res.AssetManager;

public class AssetsFileManagement {
	// only one file and can't be directory
	static public boolean copyAssetsFile(AssetManager am, String relativeFilePath, String destDir, boolean isRecover){	
		// destination
		
		File destFileDir = new File(destDir);
		if(!destFileDir.exists()){
			destFileDir.mkdir(); 
		}		
		String fileName = getFileNameFromPath(relativeFilePath);
		if(null == fileName || "" == fileName)
			return false;
		if(null == destDir || "" == destDir)
			return false;
		String destFileName = destDir + "/" + fileName;		
		File destFile = new File(destFileName);
		//mkdir(destFile.getParent());
		
		// cover it if exist
		if(destFile.exists())
			if(isRecover)
				destFile.delete();
			else
				return true;
				
		// open asset file and copy
		try {			
			InputStream in = am.open(relativeFilePath);
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
	
	//add by JiangBo 
	static public boolean copysplitAssetsFile(AssetManager am, String relativeFilePath, String destDir, boolean isRecover)
	{
		File destFileDir = new File(destDir);
		if(!destFileDir.exists()){
			destFileDir.mkdir(); 
		}		
		String fileName = getFileNameFromPath(relativeFilePath);
		if(null == fileName || "" == fileName)
			return false;
		if(null == destDir || "" == destDir)
			return false;
		String destFileName = destDir + "/" + fileName;		
		File destFile = new File(destFileName);
		//mkdir(destFile.getParent());
		
		// cover it if exist
		if(destFile.exists())
			if(isRecover)
				destFile.delete();
			else
				return true;
		try{
			FileOutputStream os = new FileOutputStream(destFile);
		    byte []buffer = new byte[1024];
		    int i, byteRead;
		    String []Files = am.list("");
		    Arrays.sort(Files);
		    
		    int nSize = Files.length;
		    if(Arrays.binarySearch(Files, relativeFilePath) >= 0)  //δ�ָ��ļ�
		    {
		    	 InputStream is = am.open(relativeFilePath);
			     while((byteRead = is.read(buffer)) != -1)
			            os.write(buffer, 0, byteRead);
			        is.close();
		    }
		    else   //�ָ�
		    {
			    for(i=1;i<Files.length;i++)
			    {
			           String fn = String.format(relativeFilePath+".hx%d", i);
			        if(Arrays.binarySearch(Files, fn) < 0)
			               break;
			        InputStream is = am.open(fn);
			        while((byteRead = is.read(buffer)) != -1)
			            os.write(buffer, 0, byteRead);
			        is.close();
			    }
		    }
		    os.close();
		}
		catch (IOException e) {
			// TODO: handle exception
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
