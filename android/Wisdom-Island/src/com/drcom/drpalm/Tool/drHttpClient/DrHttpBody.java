package com.drcom.drpalm.Tool.drHttpClient;


public class DrHttpBody {
	static final String MULTIPART_FORM_DATA = "multipart/form-data";
	static final String BOUNDARY = "---------7d4a6d158c9"; 
	byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();
	private StringBuilder mStringBuilder;
	public DrHttpBody(){		
		mStringBuilder = new StringBuilder();
	}
	public void addString(String key, String value){		
		//mStringBuilder.append("--");
		//mStringBuilder.append(BOUNDARY);
		//mStringBuilder.append("\r\n");
		//mStringBuilder.append("Content-Disposition: form-data; name=\""+ key + "\"\r\n\r\n");
		mStringBuilder.append(value);
		mStringBuilder.append("\r\n");
	}
	public byte[] getData(){
		return mStringBuilder.toString().getBytes();
	}
	
	
}
