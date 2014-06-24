package com.drcom.drpalm.Tool.jsonparser;

import android.util.Log;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.LeaveApplicationMainItem;
import com.drcom.drpalm.objs.ResultItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 解析告假列表和告假详细
 */
public class LeaveApplicationListParser implements IParser {

    @Override
    public Object parser(JSONObject json) {

        if (json != null) {
            Log.i("result_json_", json.toString());

            ResultItem resultItem = new ResultItem();
            try {
                JSONObject opretObject = json.getJSONObject("opret");
                int opFlag = opretObject.getInt("opflag");
                if (opFlag == 1) {
                    resultItem.setResult(true);
                    Object object = null;
                    try {
                        JSONArray leaveArray = json.getJSONArray("leavelist");
                        //解析列表
                        ArrayList<LeaveApplicationMainItem> leaveApplicationMainItemArrayList = new ArrayList<LeaveApplicationMainItem>();

                        object = leaveApplicationMainItemArrayList;

                        for (int i = 0; i < leaveArray.length(); i++) {
                            JSONObject leaveObject = (JSONObject) leaveArray.get(i);
                            LeaveApplicationMainItem leaveApplicationMainItem = getMainItem(leaveObject);
                            leaveApplicationMainItemArrayList.add(leaveApplicationMainItem);
                        }
                    } catch (Exception e) {
                        //解析详细
                        LeaveApplicationMainItem leaveApplicationMainItem = getMainItem(json);

                        object = leaveApplicationMainItem;

                        JSONArray attsArray = json.getJSONArray("atts");

                        ArrayList<EventDetailsItem.Imags> imagses = new ArrayList<EventDetailsItem.Imags>();

                        for (int i = 0; i < attsArray.length(); i++) {
                            JSONObject attsObject = (JSONObject) attsArray.get(i);

                            String attdes = attsObject.getString("attdes");
                            String atturl = attsObject.getString("atturl");
                            String attsize = attsObject.getString("attsize");
                            String attid = attsObject.getString("attid");
                            String attpreview = attsObject.getString("attpreview");
                            String attname = attsObject.getString("attname");

                            EventDetailsItem.Imags imags = new EventDetailsItem.Imags();

                            if (attpreview != null) {
                                imags.preview = attpreview;
                            }
                            imags.imgDescription = attdes;
                            imags.URL = atturl;
                            imags.attid = Integer.parseInt(attid);
                            imags.size = attsize;
                            imags.attname = attname;
                            imagses.add(imags);
                        }

                        leaveApplicationMainItem.setAttr(imagses);
                    }

                    resultItem.setData(object);

                } else {
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

    public LeaveApplicationMainItem getMainItem(JSONObject jsonObject) throws JSONException {

        Calendar calendar = Calendar.getInstance();

        LeaveApplicationMainItem leaveApplicationMainItem = new LeaveApplicationMainItem();

        leaveApplicationMainItem.setLeaveid(jsonObject.getInt("leaveid"));
        leaveApplicationMainItem.setType(jsonObject.getString("type"));
        leaveApplicationMainItem.setPubid(jsonObject.getString("pubid"));
        leaveApplicationMainItem.setPubname(jsonObject.getString("pubname"));
        leaveApplicationMainItem.setOwnerid(jsonObject.getString("ownerid"));
        leaveApplicationMainItem.setOwner(jsonObject.getString("owner"));
        Long post = jsonObject.getLong("post");
        calendar.setTimeInMillis(post);
        leaveApplicationMainItem.setPost(calendar.getTime());
        Long start = jsonObject.getLong("start");
        calendar.setTimeInMillis(start);
        leaveApplicationMainItem.setStart(calendar.getTime());
        Long end = jsonObject.getLong("end");
        calendar.setTimeInMillis(end);
        leaveApplicationMainItem.setEnd(calendar.getTime());
        leaveApplicationMainItem.setTitle(jsonObject.getString("title"));
        Long lastupdate = jsonObject.getLong("lastupdate");
        calendar.setTimeInMillis(lastupdate);
        leaveApplicationMainItem.setLastupdate(calendar.getTime());
        leaveApplicationMainItem.setIsread(jsonObject.getInt("isread"));
//        Log.i("zhr","json isread:"+leaveApplicationMainItem.isread);
        leaveApplicationMainItem.setHasatt(jsonObject.getInt("hasatt"));
        try {
            leaveApplicationMainItem.setContent(jsonObject.getString("content"));
        } catch (Exception e) {
        }
        return leaveApplicationMainItem;
    }
}
