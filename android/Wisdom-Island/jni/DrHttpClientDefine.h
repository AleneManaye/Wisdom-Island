/*
 * File         : DrHttpClientDefine.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrHttpClientDefine include
 */

#ifndef _INC_DrHttpClientDefine_
#define _INC_DrHttpClientDefine_

#define LOG_SEP "/****************************************************/"

//#define HTTP_SEND_TIMEOUT		3000
//#define HTTP_RECV_TIMEOUT		4000
#define HTTP_PROT				80
#define HTTPS_PROT				443

#define HTTP_URL_START "http://"
#define HTTP_THREAD_ERROR "Thread Busy!"
#define HTTP_CONNECTION_ERROR "Connection error!"
#define HTTP_SEND_REQUEST_ERROR "Send Request error!"
#define HTTP_NETWORK_REQUEST_ERROR "Network error!"
#define HTTP_KEPPALIVE_REQUEST_ERROR "Http keepalive error!"
#define HTTP_HEADER_ERROR "Http Header Error!"

#define DrHttpClient_BUFFER_16B		16
#define DrHttpClient_BUFFER_32B		32
#define DrHttpClient_BUFFER_256B		256
#define DrHttpClient_BUFFER_512B		512
#define DrHttpClient_BUFFER_1K			1024
#define DrHttpClient_BUFFER_2K			2046
#define DrHttpClient_BUFFER_4K		4096
#define DrHttpClient_BUFFER_32K		32768
#define DrHttpClient_BUFFER_64K		65535
#define DrHttpClient_MAX_INFERFACE		16

#define DrHttpClient_FAIL				-1
#define DrHttpClient_SUCCESS			1
#define DrHttpClient_CHECK				2

#define DrHttpClient_RESPONE_HDADER     "HTTP/1.0"

#define DrHttpClient_GET						  "GET %s HTTP/1.0\r\nUser-Agent: DrPalm-DrHttpClient\r\nAccept: text/html,application/xml,application/xhmtl\r\nAccept-Charset: UTF-8\r\nAccept-Language: %s\r\nAccept-Encoding: gzip\r\nContent-Type: text/xml\r\nHost: %s\r\nConection:Close\r\n\r\n"
#define DrHttpClient_POST						  "POST %s HTTP/1.0\r\nUser-Agent: DrPalm-DrHttpClient\r\nAccept: text/html,application/xml,application/xhmtl\r\nAccept-Charset: UTF-8\r\nAccept-Language: %s\r\nAccept-Encoding: gzip\r\nContent-Type: multipart/form-data; boundary=%s\r\nContent-Length: %ld\r\nHost: %s\r\nConection: %s\r\n\r\n"
#define DrHttpClient_POST_CONTENT_KEEPALIVE		  "Keep-alive"
#define DrHttpClient_POST_CONTENT_CLOSE			  "Close"
#define DrHttpClient_POST_TEXT_HTML				  "text/html;"
#define DrHttpClient_POST_TEXT_PLAIN			  "text/plain;"
#define DrHttpClient_POST_APPLICATION			  "application/octet-stream"
#define DrHttpClient_POST_MULTIPART_FORM_DATA 	  "multipart/form-data"
#define DrHttpClient_POST_MULTIPART_MULI_FILE 	  "multipart/mixed"
#define DrHttpClient_POST_BOUNDARY				  "boundary"
#define DrHttpClient_POST_BOUNDARY_DEF  		  "D7RdP4AaL6Md158c9cDsH37mDhEd"
#define DrHttpClient_POST_BOUNDARY_FILE_DEF  	  "FILED7RdP4AaL6Md158c9"
#define DrHttpClient_POST_CONTENT_DISPOSITION 	  "Content-Disposition: form-data;"
#define DrHttpClient_POST_CONTENT_DISPOSITION_ATT "Content-Disposition: attachment;"
#define DrHttpClient_HTTP_CONTENT_TRANS_8_BIT	  "Content-Transfer-Encoding: 8bit"
#define DrHttpClient_HTTP_CONTENT_TRANS_BINARY	  "Content-Transfer-Encoding: binary"
#define DrHttpClient_HTTP_HEADER_TRANS_CHUNKED    "Transfer-Encoding: chunked"
#define DrHttpClient_POST_FILE_PRE				  "file"

#define DrHttpClient_HTTP_CONTEXT_LENGYH "Context-Length:"
#define DrHttpClient_HTTP_LINE_END		"\r\n"
#define DrHttpClient_HTTP_END			"\r\n\r\n"
#define DrHttpClient_HTTP_CON_LEN		"CONTENT-LENGTH: "
#define DrHttpClient_HTTP_CONTENT_TYPE  "Content-Type: "
#define DrHttpClient_HTTP_CHARSET_UTF8	"charset=UTF-8"
#define DrHttpClient_HTTP_LANGUALE      "Accept-Language:"
#define DrHttpClient_HTTP_LANGUALE_CN   "zh-CN"
#define DrHttpClient_HTTP_SERVER		"SERVER: "
#define DrHttpClient_HTTP_LOCATION		"LOCATION: HTTP://"
#define DrHttpClient_HTTP_200			200
#define DrHttpClient_HTTP_302			302

#define Network_connection_interruption_check_the_network_configuration_please	-101
#define This_account_does_not_allow_use_NAT										-102
#define Can_not_find_Dr_COM_web_protocol										-103
#define This_equipment_already_online_do_not_need_to_log_in						-104
#define The_IP_does_not_allow_login_base_Dr_COM_web_technology					-105
#define The_account_does_not_allow_login_base_Dr_COM_web_technology				-106
#define The_account_does_not_allow_change_password								-107
#define Invalid_account_or_password_please_login_again							-108
#define This_account_is_tie_up_please_contact_network_administration_IP_MAC		-109
#define This_account_use_on_appointed_address_only_IP							-110
#define This_account_charge_be_overspend_or_flux_over							-111
#define This_account_be_break_off												-112
#define System_buffer_full														-113
#define This_account_is_tie_up_can_not_amend									-114
#define The_new_and_the_confirm_password_are_differ_can_not_amend				-115
#define The_password_amend_successed											-116
#define This_account_use_on_appointed_address_only_MAC							-117

#define Undefine_error															-130

#endif
