/*
 * File         : DrCOMSocket.h
 * Date         : 2011-07-11
 * Author       : Keqin Su
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrCOM socket include
 */

#ifndef _INC_DRCOMSOCKET_
#define _INC_DRCOMSOCKET_

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string>
#include <string.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <openssl/md5.h>
#include "DrCOMDefine.h"

using namespace std;

class tcpSocket
{
	public:
		tcpSocket();
		~tcpSocket();
		tcpSocket & operator=(const tcpSocket &ojb);

		virtual long Connect(string strAddress, unsigned long uiPort = HTTP_PROT);
		virtual long SendData(char* pBuffer, unsigned long uiSendLen, unsigned long uiTimeout = HTTP_SEND_TIMEOUT);
		virtual long RecvData(char* pBuffer, unsigned long uiRecvLen, bool bRecvAll = true, unsigned long uiTimeout = HTTP_RECV_TIMEOUT);
		virtual long Close();

		long getSocketId();
	public:
		static bool CompareLocalAddress(string strAddress);
		static string GetFirstMacAddress();
		static string GetFirstIpAddress();
		static string GetMacAddressList();

	protected:
		long BaseConnect(string strAddress, unsigned long uiPort, bool bBlock);
		unsigned long GetTick();
		bool isTimeout(unsigned long uiStart, unsigned long uiTimeout);

	protected:
		long m_iSocket;
		string m_strAddress;
		unsigned long m_uiPort;
};

class sslSocket : public tcpSocket
{
	public:
		sslSocket();
		~sslSocket();

		long Connect(string strAddress, unsigned long uiPort = HTTPS_PROT);
		long SendData(char* pBuffer, unsigned long uiSendLen, unsigned long uiTimeout = HTTP_SEND_TIMEOUT);
		long RecvData(char* pBuffer, unsigned long uiRecvLen, bool bRecvAll = true, unsigned long uiTimeout = HTTP_RECV_TIMEOUT);
		long Close();

	protected:
		SSL_CTX* m_pCTX;
		SSL* m_pSSL;
};

#endif
