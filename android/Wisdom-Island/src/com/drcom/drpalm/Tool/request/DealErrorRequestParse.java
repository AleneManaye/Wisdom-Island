package com.drcom.drpalm.Tool.request;

public class DealErrorRequestParse {
	protected String mString = null;
	
	public DealErrorRequestParse(String strdata)
	{
		byte[] data = strdata.getBytes();
		mString = filterErrorData(data);
	}
	
	public DealErrorRequestParse(byte[] data)
	{
		mString = filterErrorData(data);
	}
	
	private String  filterErrorData(byte[] data)
	{
		String strReturn = "";
		try{
			int iBegin = 0, iEnd = data.length;
			for (iBegin = 0; iBegin < data.length; iBegin++) {
				if (data[iBegin] == '{') {
					break;
				}
			}
			for (iEnd = data.length - 1; iEnd >= 0; iEnd--) {
				if (data[iEnd] == '}') {
					break;
				}
			}
			
			
			if(iEnd>iBegin){
			byte[] valueN = new byte[iEnd - iBegin + 1];
			System.arraycopy(data, iBegin, valueN, 0, iEnd - iBegin + 1);
			strReturn = new String(valueN);
			System.out.println("DealErrorRequestParse.DealErrorRequestParse()");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			strReturn = new String(data);
			System.out.println("DealErrorRequestParse.DealErrorRequestParse()");
		}
		return strReturn;
	}
}
