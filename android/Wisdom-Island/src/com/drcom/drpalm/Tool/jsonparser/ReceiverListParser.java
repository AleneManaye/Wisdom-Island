package com.drcom.drpalm.Tool.jsonparser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.ReceiverItem;
import com.drcom.drpalm.objs.ReceiverResultItem;

/**
 * 解析接收人列表
 */
public class ReceiverListParser implements IParser {

    @Override
    public Object parser(JSONObject json) {

        Log.i("result","receiverlist-->" + json);

        if (json != null) {
        	ReceiverResultItem resultItem = new ReceiverResultItem();
            try {
                JSONObject opretObject = json.getJSONObject("opret");
                int opFlag = opretObject.getInt("opflag");
                if (opFlag == 1) {
                    resultItem.setResult(true);
                    
                    JSONArray jsonArray = json.getJSONArray("ownerlist");
                    if(jsonArray != null){
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            ReceiverItem ri = new ReceiverItem();
                            ri.receiver_id = jsonObject.getString("ownerid");
                            Log.i("result", " ri.receiver_id "+ ri.receiver_id );
                            ri.receiver_name = jsonObject.getString("ownername");
                            resultItem.mReceiver.add(ri);
                        }
                    }

                }else {
                    resultItem.setResult(false);
                    resultItem.setErrorcode(opretObject.getString("code"));
                    resultItem.setErrordes(BaseParse.getErrorString(resultItem.getErrorcode()));
                }

                return resultItem;

            }catch (Exception e)
            {
                resultItem.setResult(false);
                resultItem.setErrorcode(RequestDefine.JsonParserError);
                resultItem.setErrordes(BaseParse.getErrorString(resultItem.getErrorcode()));
                return resultItem;
            }
        }

        return null;
    }
}
