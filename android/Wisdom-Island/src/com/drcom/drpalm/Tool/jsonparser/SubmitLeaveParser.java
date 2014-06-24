package com.drcom.drpalm.Tool.jsonparser;

import android.util.Log;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.ResultItem;
import org.json.JSONObject;

/**
 * Created by Administrator on 2014/5/8.
 */
public class SubmitLeaveParser implements IParser {
    @Override
    public Object parser(JSONObject json) {

        Log.i("json_result",json.toString());


        if (json != null) {
            ResultItem resultItem = new ResultItem();
            try {
                JSONObject opretObject = json.getJSONObject("opret");
                int opFlag = opretObject.getInt("opflag");
                if (opFlag == 1) {
                    resultItem.setResult(true);
                }else{
                    resultItem.setResult(false);
                    resultItem.setErrorcode(opretObject.getString("code"));
                    resultItem.setErrordes(BaseParse.getErrorString(resultItem.getErrorcode()));
                }

                return resultItem;

            } catch (Exception e) {
                resultItem.setResult(false);
                resultItem.setErrorcode(RequestDefine.JsonParserError);
                resultItem.setErrordes(BaseParse.getErrorString(resultItem.getErrorcode()));
                return resultItem;
            }
        }
        return null;
    }
}
