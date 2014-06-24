package com.drcom.drpalm.Tool.service;
import com.drcom.drpalm.Tool.service.IDrPalmRequestCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestLoginCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestResourceCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestGetStatusCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestGetEventListCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestOrganizationCallback;
import com.drcom.drpalm.objs.OrganizationItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.ConsultDraftItem;
interface IDrPalmRequest
{
	boolean initDB();
    boolean GetNetworkGate(String domain, String schoolKey, in IDrPalmRequestCallback listener);
    boolean GetSchoolList(String domain, String parentid, in IDrPalmRequestCallback listener);
    boolean SearchSchool(String domain, String appid, String searchkey, in IDrPalmRequestCallback listener);
    boolean GetSchoolKey(String domain, String seqid, in IDrPalmRequestCallback listener);
    boolean GetTours(in IDrPalmRequestResourceCallback listener, in IDrPalmRequestCallback pswListener);
    boolean LoginGateway(in String userId, in String pass,in String identfy,in String packagename,in IDrPalmRequestLoginCallback listener);
    boolean Logout(in IDrPalmRequestCallback listener);
    boolean PushInfo(boolean bPush, boolean bSound, boolean bShake, String time, in IDrPalmRequestCallback listener);
    boolean KeepAlive(in IDrPalmRequestCallback listener);
    boolean SetUserEmail(String email, in IDrPalmRequestCallback listener);
    boolean GetNewsInfoList(in IDrPalmRequestCallback listener);
    boolean GetEventsInfoList(in IDrPalmRequestCallback callback);
    boolean GetNewsLastUpdate(in IDrPalmRequestCallback listener);
    boolean GetEventsLastUpdate(in IDrPalmRequestCallback listener);
    
    boolean GetEventsList(int category_id, String lastupdate, String lastreadtime, in IDrPalmRequestGetEventListCallback listener);
    boolean GetEventDetail(String eventid, int allfield, in IDrPalmRequestCallback listener);
    boolean GetPublishEventList(int category_id, String lastupdate, in IDrPalmRequestCallback listener);
    boolean GetPublishEventDetail(String eventid, in IDrPalmRequestCallback listener);
    boolean GetReplyInfo(String eventid, String awspubid, String lastawstime, in IDrPalmRequestCallback listener);
    boolean ReplyPost(int eventid, String aswpubid, String replyContent, in IDrPalmRequestCallback listener);
    boolean GetObjectType(in IDrPalmRequestCallback listener);
    boolean GetPublishEvents(long curpost, in IDrPalmRequestCallback listener);
    boolean GetNewPublishEvents(long startTime, long endTime, long startPost, in IDrPalmRequestCallback listener);
    boolean GetOrganization(in IDrPalmRequestOrganizationCallback listener);
    boolean GetParentOrganization(in OrganizationItem item, in IDrPalmRequestCallback listener);
  	boolean GetEventReadStatus(int eventID, in IDrPalmRequestGetStatusCallback listener);
    boolean AutoAnwserEvent(int eventID, in IDrPalmRequestCallback listener);
    boolean AutoAnwserEventList(in int[] listId, in IDrPalmRequestCallback listener);
    boolean SubmitEvent(in EventDraftItem item, in IDrPalmRequestCallback listener);
    
    boolean GetNews(int category, String lastupdate, in IDrPalmRequestCallback listener);
    boolean GetNewsDetail(int story_id, int allfield, in IDrPalmRequestCallback listener);
    boolean SearchNews(String start, String searchkey,in IDrPalmRequestCallback IDrPalmRequestCallback);
    boolean SubmitProblem(String problem, String suggestion, in IDrPalmRequestCallback IDrPalmRequestCallback);
    boolean PutConSult(in ConsultDraftItem item, in IDrPalmRequestCallback listener);
    
    boolean GetContactList(in IDrPalmRequestCallback listener);
    boolean GetContactMsgs(String contact_id, String lastupdate, in IDrPalmRequestCallback listener);
    boolean SendContactMsg(String contactid, String body, in IDrPalmRequestCallback listener);
    
    boolean GetSysMsgs(String lastid, in IDrPalmRequestGetEventListCallback listener);
    boolean GetSysMsgContent(String sysmsgid, in IDrPalmRequestCallback listener);
    
    boolean GetEventReadInfo(String eventid, in IDrPalmRequestCallback listener);
    
    void initRequest(String username,String sessionKey);
}
    