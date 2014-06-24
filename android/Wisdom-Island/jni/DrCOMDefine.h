/*
 * File         : DrCOMDefine.h
 * Date         : 2011-07-11
 * Author       : Keqin Su
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrCOM define
 */

#ifndef _INC_DRCOMDEFINE_
#define _INC_DRCOMDEFINE_

#define HTTP_SEND_TIMEOUT		3000
#define HTTP_RECV_TIMEOUT		10000
#define HTTP_PROT				80
#define HTTPS_PROT				443

#define DrCOM_BUFFER_16B		16
#define DrCOM_BUFFER_32B		32
#define DrCOM_BUFFER_256B		256
#define DrCOM_BUFFER_512B		512
#define DrCOM_BUFFER_1K			1024
#define DrCOM_BUFFER_64K		65536
#define DrCOM_MAX_INFERFACE		16

#define DrCOM_FAIL				-1
#define DrCOM_SUCCESS			1
#define DrCOM_CHECK				2

#define DrCOM_0_html			"<!--Dr.COMWebLoginID_0.htm-->"
#define DrCOM_1_html			"<!--Dr.COMWebLoginID_1.htm-->"
#define DrCOM_2_html			"<!--Dr.COMWebLoginID_2.htm-->"
#define DrCOM_3_html			"<!--Dr.COMWebLoginID_3.htm-->"
#define DrCOM_Server			"DrcomServer1.0"
#define DrCOM_Server_IIS		"DRCOM-IIS-2.00"
#define DrCOM_Logout			"/F.htm"
#define DrCOM_Login				"DDDDD=%s&upass=%s&%s&0MKKey=%s"
#define DrCOM_TestUrl			"www.baidu.com"
#define DrCOM_Def_Num			"0123456789"
#define DrCOM_UIP				"va5=1.2.3.4."
#define DrCOMWS					"DrCOMWS"

#define DrCOM_HTTP_TYPE_0000    "0000"
#define DrCOM_HTTP_TYPE_0003    "0003"
#define DrCOM_HTTP_TYPE_0006    "0006"
#define DrCOM_Hash              "4a6bb2a07eb9472e9e5bcccd0571d52c"//DrCOMWS
#define DrCOM_Version           "1.0.2.201112121.G.L.A"
#define DrCOM_Update_Svr        "update.doctorcom.com"
#define DrCOM_Update_Parm       "/DRCLIENT/UPDATE?TIME=%s&TYPE=%s&KEY=%s&HASH=%s&VER=%s&RND=%s&CHK=%s&M=%s"

#define DrCOM_POST				"POST %s HTTP/1.1\r\nContent-Type: text/xml\r\nCharset: utf-8\r\nDate: %s\r\nUip: %s\r\nContent-Length: %ld\r\nHost: %s\r\nUser-Agent: DrCOM-HttpClient\r\n\r\n%s"
#define DrCOM_GET				"GET %s HTTP/1.1\r\nContent-Type: text/xml\r\nCharset: utf-8\r\nHost: %s\r\nUser-Agent: DrCOM-HttpClient\r\n\r\n"
#define DrCOM_HTTP_LINE_END		"\r\n"
#define DrCOM_HTTP_END			"\r\n\r\n"
#define DrCOM_HTTP_CON_LEN		"CONTENT-LENGTH: "
#define DrCOM_HTTP_SERVER		"SERVER: "
#define DrCOM_HTTP_LOCATION		"LOCATION: HTTP://"
#define DrCOM_HTTP_200			200
#define DrCOM_HTTP_302			302

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
