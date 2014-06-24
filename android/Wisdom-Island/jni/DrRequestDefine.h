 /* File         : DrRequestDefine.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrRequestDefine include
 */

#ifndef _INC_DRREQUESTDEFINE_
#define _INC_DRREQUESTDEFINE_

/*
 * Gateway
 */
//gettours
#define TOURS_PATH 				"webapi"
#define TOURS_GETCLIPKG 		"getclipkg/"
#define TOURS_LASTMDATE 		"lastmdate"
#define TOURS_NUMNO 			"numno"
#define TOURS_DSPWIDTH 			"dspwidth"
#define TOURS_DSPHEIGHT 		"dspheight"
#define TOURS_OS 				"os"
#define TOURS_MODELNO 			"modelno"
#define TOURS_DPI 				"dpi"
#define TOURS_RESLIST 			"reslist"
#define TOURS_RESNAME 			"resname"
#define TOURS_URL				"url"
#define TOURS_VERIFYCODE 		"verifycode"

//getnetworkgate
#define NETAPP_PATH				"drpalm4/netapp"
#define WEBAPI_PATH 			"Ebaby4/mobileapi"
#define WEBAPI4ATTENDANCE_PATH 	"ebservice/mobileapi"
#define GET_GATE_PATH			"getAppGw"
#define GET_SCHOOLKEY_PATH      "org.drcom.drpalm.getSchoolKey.flow"
#define SCHOOLKEY				"schoolkey"
#define SESSIONKEY				"sessionkey"
#define REQUEST_TOKENID         "tokenid"
#define SCHOOLID				"schoolid"
#define GW_NUMNO				"numno"
#define GW_DPI					"dpi"
#define GW_DSPWIDTH				"dspwidth"
#define GW_DSPHEIGHT			"dspheight"
#define GW_OS					"os"
#define GW_MODELNO				"modelno"
#define GW_APPPVER				"appver"
#define GW_TOKENID				"tokenid"
#define GW_APPNAME				"appname"

//getschoolkey
#define SEQID                   "seqid"

//login
#define LOGIN_PATH			 	"login/"
#define LOGIN_USERID			"userid"
#define LOGIN_PWD               "pwd"
#define LOGIN_TOKEN 			"devicetoken"
#define LOGIN_DEVICETYPE        "devicetype"

/*
 * Common
 */
//logout
#define LOGOUT_PATH             "logout/"
///keepalive
#define KEEPALIVE_PATH          "keep/"
//pushinfo
#define PUSH_PATH 				"pushinfo/"
#define PUSH_SWITCH 			"ifpush"
#define PUSH_SOUND 				"ifsound"
#define PUSH_SHAKE 				"ifshake"
#define PUSH_TIME 				"pushtime"

/*
 * News
 */
#define GETNEWSLIST_PATH 		"newslist"
#define NEWS_CHANNEL 			"channel"
#define NEWS_LASTUPDATE 		"lastupdate"

/*
 * Events
 */
// Get Event Request
#define GETEVENTSLIST_PATH      "geteventlist"
#define GETEVENTSLIST_CATEGORY  "type"
#define GETEVENTSLIST_LASTUPDATE "lastupdate"
#define GETEVENTSLIST_LASTREADTIME "lastreadtime"

#define GETEVENTDETAIL_PATH     "geteventcontent"
#define EVENT_ID                "eventid"
#define ALBUM_ID                "albumid"
#define EVENT_IDS                "eventids"
#define ALBUM_IDS                "albumids"
#define GETEVENTDETAIL_ALLFIELD "allfield"

#define GETEVENTREADINFO_PATH   "geteventreadinfo"

#define EVENTS_START			"start"
#define EVENTS_END  			"end"
#define EVENTS_START_POST		"startpost"

//GetPublistEvent
#define GETPUBLISHEVENTSLIST_PATH  "getsenteventlist"
#define GETPUBLISSHEVENTDETAIL_PATH "getsenteventcontent"

// Get EventsReplyInfo Request
#define EVENTS_GETREPLYINFO_PATH      "getaws"
#define EVENTS_GETREPLYINFO_ASWPUBID   "aswpubid"
#define EVENTS_GETREPLYINFO_LASTAWSTIME   "lastawstime"

// Send
#define EVENTS_SUBMIT_PATH 		"submitevent/"
#define EVENTS_ID 				"id"
#define EVENTS_ORISTATUS 		"oristatus"
#define EVENTS_OBJTYPE 			"objtype"
#define EVENTS_OWNERID 			"ownerid"
#define EVENTS_OWNER 			"owner"
#define EVENTS_SHORTLOC 		"location"
#define EVENTS_TITLE 			"title"
#define EVENTS_BODY 			"body"
#define EVENTS_EMERGENT			"ifeshow"
#define EVENTS_FILE 			"file"
#define EVENTS_COMMENTS         "templatetype"
#define EVENTS_FILETITLE        "filetitle"
#define EVENTS_FILEFROM         "filefrom"
#define EVENTS_TYPE             "type"
#define EVENTS_ITEM             "item"
#define EVENTS_FILEDES          "filedes"
#define EVENTS_LOCURL           "locationurl"
#define EVENTS_THUMBNAME        "thumbname"

// Get Event Callback
#define EVENTS_CURCOUNT 		"curcount";
#define EVENTS_RETCOUNT 		"retcount";
#define EVENTS_CURMAXPOST 		"curmaxpost"
#define EVENTS_EVNETLIST		"eventlist"
#define EVENTS_TYPE 			"type"
#define EVENTS_ORIEVENTID		"orieventid"
#define EVENTS_EVENTID 			"eventid"
#define EVENTS_EVENTIDS 	    "eventids"
#define EVENTS_PUBNAME 			"pubname"
#define EVENTS_POST 			"post"
#define EVENTS_LOCATION 		"location"
#define EVENTS_STATUS			"status"
#define EVENTS_CANCELLED		"cancelled"
#define EVENTS_IMGS				"imgs"
#define EVENTS_IMGURL 		    "imgurl"

#define OLD_EVENTS_ID           "id"

// Object Type
#define OBJTYPE_PATH 			"objtypeget/"
#define OBJTYPE_CS 				"cs"
#define OBJTYPE_TYPES 			"types"
#define OBJTYPE_OBJTYPE 		"objtype"
#define OBJTYPE_OBJTYPEDES 		"objtypedes"

// Publish Event
#define EVENTPUB_PATH 			"eventpubget"
#define EVENTPUB_READCOUNT 		"readcount"
#define EVENTPUB_TOTALCOUNT 	"totalcount"

// Organization
#define USEROWNORGSINFO_PATH	"userownorgsinfo"
#define USEROWNORGSINFO_ORGLIST	"orglist"
#define ORGINFO_PATH			"getorginfo"
#define ORGINFO_LASTUPDATE      "lastupdate"
#define ORGINFO_LIMIT 			"limit"
#define ORGINFO_RECTYPE 		"rectype"
#define ORGINFO_MORE 			"more"
#define ORGINFO_END 			"end"
#define ORGINFO_RECOUNT 		"recount"
#define ORGINFO_RECLIST 		"reclist"
#define ORGINFO_ID 				"id"
#define ORGINFO_NAME 			"name"
#define ORGINFO_TYPE 			"type"

// Auto Respone
#define AUTOAWSEVENT 			"autoaws"
#define BATAWSEVENT 			"bataws/"

//ReplyEvents
#define REPLYPOST_PATH        "aws/"
#define REPLYPOST_AWSPUBID    "aswpubid"
#define REPLYPOST_TITLE       "title"
#define REPLYPOST_BODY        "body"

// EventReadStatus
#define EVENTRET 				"eventret"
#define EVENTS_VENTID 			"eventid"
#define EVENTS_FBCOUNT 			"fbcount"
#define EVENTS_LASTFBTIME 		"lastfbtime"

//SubmitProblem
#define SUBMITPROBLEM_PATH      "submitproblem/"
#define SUBMITPROBLEM_PROBLEM   "problem"
#define SUBMITPROBLEM_SUGGESTION "suggestion"

//SetUserEmail
#define SETUSEREMAIL_PATH       "setusermail/"
#define EMAIL                   "mail"

//GetInfoList
#define GETNEWSINFOLIST_PATH    "getnewinfolist"

//GetNewsModuleInfoList
#define GETNEWSMODULEINFOLIST_PATH    "getnewsmodulesinfolist"

//GetEventsModuleInfoList
#define GETEVENTSMODULEINFOLIST_PATH    "geteventmodulesinfolist"

//GetLastUpdate
#define GETLASTUPDATE_PATH      "getlastupdate"

//SearchNews
#define SEARCHNEWS_PATH          "searchnews"
#define SEARCHNEWS_SEARCHWORD    "searchword"
#define SEARCHNEWS_START         "start"
#define SEARCHNEWS_LIMIT         "limit"

// PUSH_SERVER
#define PUSH_NAME 				"param"
#define PUSH_CMD 				"cmd"
#define PUSH_RET 				"ret"
#define PUSH_GETCHANLLENGE		"getclientchanllenge"
#define PUSH_CHANLLENGE 		"chanllenge"
#define PUSH_REGISTER 		    "register"
#define PUSH_TOKENID 			"tokenid"
#define PUSH_BODY				"body"
#define PUSH_APPID 				"appid"
#define PUSH_CHECKCODE			"checkcode"
#define PUSH_MODEL 				"model"
#define PUSH_SYSTEM 			"system"
#define PUSH_APPVER				"appver"
#define PUSH_BODY 				"body"
#define PUSH_GETMESSAGE 		"getpushmessage"

//GetSchoolList
#define GETSCHOOLLIST_PATH      "getschoollist"
#define GETSCHOOLLIST_PARENTID  "localid"

//SearchSchool
#define SEARCHSCHOOL_PATH       "searchschool"
#define SEARCHSCHOOL_KEYWORD    "keyword"
#define SEARCHSCHOOL_APPID      "appid"
#define SEARCHSCHOOL_LANG       "lang"

//GetNewsDetail
#define GETNEWSDETAIL_PATH      "newscontent"
#define GETNEWSDETAIL_NEWSID    "storyid"
#define GETNEWSDETAIL_ALLFIELD  "allfield"

//GetAlbumList
#define GETALBUMLIST_PATH       "getAlbumList"
#define ALBUMID                 "albumid"

#define GETALBUMCONTENT_PATH    "getAlbumContent"
#define IMAGEID                 "imageid"

//PutConsult
#define PUTCONSULT_PATH          "submitconsult"
#define PUTCONSULT_USERNAME      "username"
#define PUTCONSULT_EMAIL         "email"
#define PUTCONSULT_PHONE         "phone"
#define PUTCONSULT_TITLE         "title"
#define PUTCONSULT_CONTENT       "content"
#define PUTCONSULT_TYPE          "type"

//GetContactList
#define GETCONTACTLIST_PATH      "getcontactlist"
#define CN_ID   "cid"
#define GETCONTACTLIST_PARAM   "clist"

#define GETCONTACTMSGS_PATH      "getcontactmsgs"
#define GETCONTACTMSGS_CONTACTID "cid"
#define GETCONTACTMSGS_LASTUPDATE "lastupid"

#define SENDCONTACTMSGS_PATH     "sendmsg"
#define SENDCONTACTMSGS_BODY     "body"

#define GETSYSMSGS_PATH          "getsysmsgs"
#define GETSYSMSGSCONTENT_PATH   "getsysmsgcontent"
#define GETSYSMSGSCONTENT_SYSMSGID "sysmsgid"

//PUBLIC
#define LASTUPDATE "lastupdate"

//SubmitClassfav
#define FAV_EVENT_ID   "eventid"
#define FAV_EVENT_STATUS   "status"
#define FAV_EVENT_PARAM    "param"
#define SUBMITCLASSFAV_PATH   "submitclassfavorite"

//GetClassFavorite
#define GETCLASSFAVORITE_PATH      "getclassfavorite"

//SubmitAccountinfo
#define SUBMITACCOUNTINFO_PATH      "submitaccountinfo"

//GetAccountinfo
#define GETACCOUNTINFO_PATH      "getaccountinfo"

//GetNavigation
#define APPID      "appid"
#define GETNAVIGATION_PATH      "getnavigation"

//SubmitUseralbum
#define USERALBUM_IMGID      "imgid"
#define USERALBUM_FILENAME      "filename"
#define USERALBUM_DES      "des"
#define USERALBUM_STATUS      "status"
#define SUBIMT_USERALBUM_PATH      "submituseralbum"
#define USERALBUM_PARAM    "param"

//GetUseralbum
#define GETUSERALBUM_PATH      "getuseralbum"

//SubmitGrowdiary
#define GROWDIARY_ID  "diaryid"
#define GROWDIARY_TITLE  "title"
#define GROWDIARY_CONTECT  "contect"
#define GROWDIARY_STATUS  "status"
#define SUBMITGROWDIARY_PATH      "submitgrowdiary"

//GetGrowdiary
#define GETGROWDIARY_PATH      "getgrowdiary"

//GetNavigationList
#define GETNAVIGATIONLIST_PATH      "getnavigation"
#define GETNAVIGATIONLIST_APPID      "appid"

//SubmitDelEvent
#define SUBMITDELEVENT_PATH   "delevent"

//SubmitClassreview
#define SUBMITCLASSREVIEW_PATH      "submitclassreview"
#define REVIEW_ID  "id"
#define REVIEW_TYPE  "type"
#define REVIEW_VALUE  "value"

//GetAttendanceinfo
#define GETATTENDANCEINFO_PATH      "getattendanceinfo"
#define ATTENDANCETIME 				"attendancetime"
#define VERIFICATIONKEY				"verificationkey"
#define CHALLENGE					"challenge"
#define USERID						"userid"

//GetLeavelist
#define GETLEAVELIST_PATH			"getleavelist"
#define GETLEAVEINFO_PATH			"getleaveinfo"
#define GETLEAVE_LEAVEID			"leaveid"
#define GETLEAVE_ALLFIELD			"allfield"

//GetSubmitleave
#define GETSUBMITLEAVE_PATH			"submitleave"
#define GETSUBMITLEAVE_CONTENT		"content"

//GetLeaveownerlist
#define GETLEAVEOWNERLIST_PATH		"getleaveownerlist"

//GetEventtitlelist
#define GETEVENTTITLELIST_PATH		"geteventtitlelist"

//Getrelationaccount
#define GETRELATIONACCOUNT_PATH		"getrelationaccount"

//Addrelationaccount
#define ADDRELATIONACCOUNT_PATH     "addrelationaccount"
#define ADDRELATIONACCOUNT_ACCOUNT  "account"
#define ADDRELATIONACCOUNT_PASSWORD "password"

//Delrelationaccount
#define DELRELATIONACCOUNT_PATH     "delrelationaccount"
#define DELRELATIONACCOUNT_PARAM    "accountlist"

//Getaccountsinfo
#define GETACCOUNTSINFO_PATH        "getaccountsinfo"
#define GETACCOUNTSINFO_PARAM       "accounts"

#endif
