package com.drcom.drpalm.Tool.request;

public class EventsDefine {
	// Events Request
	// Get Event Request
	public static final String EVENTS_PATH = "eventget";
	public static final String EVENTS_CURPOST = "curpost";			
	public static final String EVENTS_START_POST = "startpost";	
	
	// Get Event Callback
	public static final String EVENTS_CURCOUNT = "curcount";
	public static final String EVENTS_RETCOUNT = "retcount";	
	public static final String EVENTS_EVNETLIST = "eventlist";
	public static final String EVENTS_EVENTID = "eventid";
	public static final String EVENTS_TYPE = "type";
	public static final String EVENTS_ORIEVENTID = "orieventid";
	public static final String EVENTS_ORISTATUS = "oristatus";
	public static final String EVENTS_PUBID = "pubid";
	public static final String EVENTS_PUBNAME = "pubname";
	public static final String EVENTS_POST = "post";
	public static final String EVENTS_START = "start";
	public static final String EVENTS_END = "end";
	public static final String EVENTS_STATUS = "status";
	public static final String EVENTS_IFESHOW = "ifeshow";
	public static final String EVENTS_TITLE = "title";
	public static final String EVENTS_LOCATION = "location";
	public static final String EVENTS_LOCATIONURL = "locationurl";
	public static final String EVENTS_CANCELLED = "cancelled";
	public static final String EVENTS_THUMBURL = "thumburl";
	public static final String EVENTS_LASTAWSTIME = "lastawstime";
	public static final String EVENTS_LASTUPDATE = "lastupdate";
	public static final String EVENTS_HASATT = "hasatt";
	public static final String EVENTS_SUMMARY = "summary";
	public static final String EVENTS_RECVTOTAL = "recvtotal";
	public static final String EVENTS_READCOUNT = "readcount";
	public static final String EVENTS_SENTTOTAL = "senttotal";	//2013-05-09
	public static final String EVENTS_READTOTAL = "readtotal";	//2013-05-09
	public static final String EVENTS_LASTAWSUSERID = "lastawsuserid";	//2013-11-07
//	public static final String EVENTS_AWSCOUNT = "awscount";	//2013-11-07

	public static final String EVENTS_ISREAD = "isread";
	public static final String EVENTS_LASTREADTIME = "lastreadtime";	//05-13
	public static final String EVENTS_NEEDREVIEW = "needreview";	//2013-12-19
//	public static final String EVENTS_IMGS = "imgs";
//	public static final String EVENTS_IMGURL = "imgurl";
//	public static final String EVENTS_IMGDES = "imgdes";
	
	//Get Event Detail
	public static final String EVENTS_BODY = "body";
	public static final String EVENTS_CLEAN_BODY = "cleanbody";
	public static final String EVENTS_OWNERID = "ownerid";
	public static final String EVENTS_OWNER = "owner";
	public static final String EVENTS_ATTS = "atts";
	public static final String EVENTS_ATTDES = "attdes";
	public static final String EVENTS_ATTURL = "atturl";
	public static final String EVENTS_ATTID = "attid";
	public static final String EVENTS_PREVIEW = "attpreview";
	public static final String EVENTS_SIZE = "attsize";
	public static final String EVENTS_ATTNAME = "attname";		//2014-03-31
	public static final String EVENTS_AWSORGLIST = "awsorglist";
	public static final String EVENTS_ASWPUBID   = "aswpubid";
	public static final String EVENTS_AWSPUBNAME = "awspubname";
	public static final String EVENTS_AWSCOUNT   = "awscount";
	public static final String EVENTS_REVIEWTEMP = "reviewtemp";	//2013-12-20
	public static final String EVENTS_REVIEWTEMP_ID = "id";
	public static final String EVENTS_REVIEWTEMP_TITLE = "title";
	public static final String EVENTS_REVIEWTEMP_TYPE = "type";
	public static final String EVENTS_REVIEWTEMP_MAX = "max";
	public static final String EVENTS_REVIEWTEMP_DEFAULT = "default";
	public static final String EVENTS_REVIEWTEMP_REQUIRED = "required";
	public static final String EVENTS_REVIEWTEMP_TEXT = "text";
	
	public static final String EVENTS_MEMBERLIST = "memberlist";	//2013-12-24
	public static final String EVENTS_MEMBER_ID = "id";
	public static final String EVENTS_MEMBER_NAME = "name";
	public static final String EVENTS_MEMBER_IMGURL = "headimgurl";
	public static final String EVENTS_MEMBER_LASTUPDATE = "headimglastupdate";
	
	public static final String EVENTS_AWSLIST    = "awslist";
	public static final String EVENTS_ASWID      = "awsid";
	public static final String EVENTS_AWSTIME    = "awstime";
	public static final String EVENTS_AWTYPE     = "awstype";
	public static final String REPLY_AWSPUBID   = "pubid";
	public static final String REPLY_NAME = "name";
	//public static final String EVENTS_AWSPUBNAME    = "awspubname";
	public static final String EVENTS_AWSRECID   = "recid";
	public static final String EVENTS_AWSRECNAME = "recname";
	public static final String EVENTS_AWSTITLE   = "awstitle";
	public static final String EVENTS_AWSBODY    = "awsbody";
	
	// Send
	public static final String EVENTS_SUBMIT_PATH = "submitevent/";	
	public static final String EVENTS_ID = "id";
	public static final String EVENTS_OBJTYPE = "objtype";
	public static final String EVENTS_SHORTLOC = "shortloc";
	public static final String EVENTS_FILE = "file";	
	
	// Object Type
	public static final String OBJTYPE_PATH = "objtypeget/";
	public static final String OBJTYPE_CS = "cs";
	public static final String OBJTYPE_TYPES = "types";
	public static final String OBJTYPE_OBJTYPE = "objtype";
	public static final String OBJTYPE_OBJTYPEDES = "objtypedes";
	
	// Publish Event
	public static final String EEVENTPUB_PATH = "eventpubget";
	public static final String EEVENTPUB_READCOUNT = "readcount";
	public static final String EEVENTPUB_TOTALCOUNT = "totalcount";
	
	// Auto Respone
	public static final String AUTOAWSEVENT = "autoawsevent/";
	public static final String BATAWSEVENT = "batawsevent/";
	

	
	// EventReadStatus
	public static final String EVENTRET = "eventret";
	public static final String EVENTS_VENTID = "eventid";
	public static final String EVENTS_FBCOUNT = "fbcount";
	public static final String EVENTS_LASTFBTIME = "lastfbtime";
	
	//EventReadInfo
	public static final String EVENT_READ_LIST = "readlist";
	public static final String EVENT_UNREAD_LIST = "unreadlist";
	public static final String EVENT_NAME = "name";
}
