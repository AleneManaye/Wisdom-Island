package com.drcom.drpalm.objs;

/**
 * Created by Administrator on 2014/5/7.
 */
public class ResultItem {
    private boolean result = false;		//请求是否成功
    private String errorcode = "0";		//错误代码
    private String errordes = "";		//错误描述
    private Object data;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrordes() {
        return errordes;
    }

    public void setErrordes(String errordes) {
        this.errordes = errordes;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
