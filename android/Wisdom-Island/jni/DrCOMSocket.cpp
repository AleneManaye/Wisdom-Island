/*
 * File         : DrCOMSocket.cpp
 * Date         : 2011-07-11
 * Author       : Keqin Su
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrCOM socket
 */

#include <unistd.h>
#include <resolv.h>
#include <netdb.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/utsname.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <sys/un.h>
#include <netinet/tcp.h>
#include <netinet/in.h>
#include <net/if_arp.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <arpa/nameser.h>
#include "DrCOMSocket.h"
//#include "DrLog.h"
#include <android/log.h>
#define showLog(tag, format, ...);// __android_log_print(ANDROID_LOG_INFO, tag, format, ## __VA_ARGS__);
tcpSocket::tcpSocket() {
	m_iSocket = -1;
	m_strAddress = "";
	m_uiPort = 0;
}

tcpSocket::~tcpSocket() {
	Close();
}
tcpSocket& tcpSocket:: operator=(const tcpSocket &ojb){

}

long tcpSocket::Connect(string strAddress, unsigned long uiPort) {
	return BaseConnect(strAddress, uiPort, false);
}
long tcpSocket::getSocketId(){
	return m_iSocket;
}
long tcpSocket::SendData(char* pBuffer, unsigned long uiSendLen, unsigned long uiTimeout) {
	unsigned long uiBegin = GetTick();
	long iOrgLen = uiSendLen;
	long iRet = -1, iRetS = -1;
	long iSendedLen = 0;
	fd_set wset;

	while (true) {
		struct timeval tout;
		tout.tv_sec = uiTimeout / 1000;
		tout.tv_usec = (uiTimeout % 1000) * 1000;
		FD_ZERO(&wset);
		FD_SET(m_iSocket, &wset);
		iRetS = select(m_iSocket + 1, NULL, &wset, NULL, &tout);
		if (iRetS == -1) {
			return -1;
		} else if (iRetS == 0) {
			return iSendedLen;
		}
		iRet = send(m_iSocket, pBuffer, uiSendLen, 0);
		if (iRet == -1 || (iRetS == 1 && iRet == 0)) {
			usleep(100);
			if (EWOULDBLOCK == errno) {
				if (isTimeout(uiBegin, uiTimeout)) {
					return iSendedLen;
				}
				continue;
			} else {
				if (iSendedLen == 0){
					return -1;
				}
				return iSendedLen;
			}
		} else if (iRet == 0) {
			return iSendedLen;
		}
		pBuffer += iRet;
		iSendedLen += iRet;
		uiSendLen -= iRet;
		if (iSendedLen >= iOrgLen) {
			return iSendedLen;
		}
		if (isTimeout(uiBegin, uiTimeout)) {
			return iSendedLen;
		}
	}
	return 0;
}

long tcpSocket::RecvData(char* pBuffer, unsigned long uiRecvLen, bool bRecvAll, unsigned long uiTimeout) {
	unsigned long uiBegin = GetTick();
	long iOrgLen = uiRecvLen;
	long iRet = -1, iRetS = -1;
	long iRecvedLen = 0;
	fd_set rset;

	while (true) {
		timeval tout;
		tout.tv_sec = uiTimeout / 1000;
		tout.tv_usec = (uiTimeout % 1000) * 1000;
		FD_ZERO(&rset);
		FD_SET(m_iSocket, &rset);
		iRetS = select(m_iSocket + 1, &rset, NULL, NULL, &tout);

		if (iRetS == -1) {
			showLog("Jni.tcpSocket.RecvData", "iRetS == -1 return -1");
			return -1;
		} else if (iRetS == 0) {
			showLog("Jni.tcpSocket.RecvData", "iRetS == 0 return iRecvedLen:%ld", iRecvedLen);
			return iRecvedLen;
		}
		//showLog("Jni.tcpSocket.RecvData", "pBuffer(%ld):%s", pBuffer, uiRecvLen);
		iRet = recv(m_iSocket, pBuffer, uiRecvLen, 0);
		showLog("Jni.tcpSocket.RecvData", "m_iSocket:%ld, iRetS:%x, iRet:%ld", m_iSocket, iRetS, iRet);
		if (iRet==-1 || (iRetS == 1 && iRet == 0)) {
			usleep(1000);
			if (EWOULDBLOCK == errno){
				if (isTimeout(uiBegin, uiTimeout)){
					return iRecvedLen;
				}
				continue;
			}else{
				if (iRecvedLen == 0){
					showLog("Jni.tcpSocket.RecvData", "EWOULDBLOCK != errno.");
					return -1;
				}
				return iRecvedLen;
			}
		} else if (iRet == 0) {
			return iRecvedLen;
		}
		pBuffer += iRet;
		iRecvedLen += iRet;
		uiRecvLen -= iRet;
		if (iRecvedLen >= iOrgLen) {
			return iRecvedLen;
		}
		if (!bRecvAll) {
			return iRecvedLen;
		}
		if (isTimeout(uiBegin, uiTimeout)){
			return iRecvedLen;
		}
	}
	return 0;
}

long tcpSocket::Close() {
	if (m_iSocket != -1) {
		shutdown(m_iSocket, SHUT_RDWR);
		close(m_iSocket);
		m_iSocket = -1;
	}
	return 1;
}

bool tcpSocket::CompareLocalAddress(string strAddress) {
	long fd = 0, intrface = 0;
	struct ifconf ifc;
	string strTmp = "";
	struct ifreq buf[DrCOM_MAX_INFERFACE];
	bool bRet = false;

	if ((fd = socket (AF_INET, SOCK_DGRAM, 0)) >= 0) {
		ifc.ifc_len = sizeof(buf);
		ifc.ifc_buf = (caddr_t)buf;

		if (!ioctl(fd, SIOCGIFCONF, (char*)&ifc)) {
			intrface = ifc.ifc_len / sizeof (struct ifreq);
			while (intrface-- > 0){
				if (!(ioctl(fd, SIOCGIFADDR, (char *) &buf[intrface]))) {
					strTmp = inet_ntoa(((struct sockaddr_in*)(&buf[intrface].ifr_addr))->sin_addr);
					if (strTmp == strAddress) {
						bRet = true;
						break;
					}
				}
			}
		}
	}
	close(fd);

	return bRet;
}

string tcpSocket::GetFirstMacAddress() {
	long fd = 0, intrface = 0;
	struct ifconf ifc;
	struct ifreq buf[DrCOM_MAX_INFERFACE];
	char cBuffer[DrCOM_BUFFER_256B] = {'\0'};
	string strRet = "";

	if ((fd = socket (AF_INET, SOCK_DGRAM, 0)) >= 0) {
		ifc.ifc_len = sizeof(buf);
		ifc.ifc_buf = (caddr_t)buf;

		if (!ioctl(fd, SIOCGIFCONF, (char*)&ifc)) {
			intrface = ifc.ifc_len / sizeof (struct ifreq);
			while (intrface-- > 0){
				if (!(ioctl(fd, SIOCGIFHWADDR, (char *) &buf[intrface]))) {
					sprintf(cBuffer, "%02x:%02x:%02x:%02x:%02x:%02x", buf[intrface].ifr_hwaddr.sa_data[0], buf[intrface].ifr_hwaddr.sa_data[1],
							buf[intrface].ifr_hwaddr.sa_data[2], buf[intrface].ifr_hwaddr.sa_data[3], buf[intrface].ifr_hwaddr.sa_data[4],
							buf[intrface].ifr_hwaddr.sa_data[5]);
					if (strcmp(cBuffer, "00:00:00:00:00:00")) {
						strRet = cBuffer;
					}
					if (strRet.length() > 0) {
						break;
					}
				}
			}
		}
	}
	close(fd);

	return strRet;
}

string tcpSocket::GetFirstIpAddress() {
	long fd = 0, intrface = 0;
	struct ifconf ifc;
	struct ifreq buf[DrCOM_MAX_INFERFACE];
	string strRet = "";

	if ((fd = socket (AF_INET, SOCK_DGRAM, 0)) >= 0) {
		ifc.ifc_len = sizeof(buf);
		ifc.ifc_buf = (caddr_t)buf;

		if (!ioctl(fd, SIOCGIFCONF, (char*)&ifc)) {
			intrface = ifc.ifc_len / sizeof (struct ifreq);
			while (intrface-- > 0){
				if (!(ioctl(fd, SIOCGIFADDR, (char *) &buf[intrface]))) {
					strRet = inet_ntoa(((struct sockaddr_in*)(&buf[intrface].ifr_addr))->sin_addr);
					if ((strRet.length() > 0) && (strRet != "127.0.0.1")) {
						break;
					}
				}
			}
		}
	}
	close(fd);

	return strRet;
}

string tcpSocket::GetMacAddressList() {
	long fd = 0, intrface = 0, iCount = 1;
	struct ifconf ifc;
	struct ifreq buf[DrCOM_MAX_INFERFACE];
	char cBuffer[DrCOM_BUFFER_256B] = {'\0'};
	char cBufferTmp[DrCOM_BUFFER_256B] = {'\0'};
	string strRet = "";

	if ((fd = socket (AF_INET, SOCK_DGRAM, 0)) >= 0) {
		ifc.ifc_len = sizeof(buf);
		ifc.ifc_buf = (caddr_t)buf;

		if (!ioctl(fd, SIOCGIFCONF, (char*)&ifc)) {
			intrface = ifc.ifc_len / sizeof (struct ifreq);
			while (intrface-- > 0){
				if (!(ioctl(fd, SIOCGIFHWADDR, (char *) &buf[intrface]))) {
					sprintf(cBuffer, "%02X:%02X:%02X:%02X:%02X:%02X", buf[intrface].ifr_hwaddr.sa_data[0], buf[intrface].ifr_hwaddr.sa_data[1],
							buf[intrface].ifr_hwaddr.sa_data[2], buf[intrface].ifr_hwaddr.sa_data[3], buf[intrface].ifr_hwaddr.sa_data[4],
							buf[intrface].ifr_hwaddr.sa_data[5]);
					if (strcmp(cBuffer, "00:00:00:00:00:00")) {
						sprintf(cBufferTmp, "&m%ld=%s", iCount++, cBuffer);
						strRet += cBufferTmp;
					}
				}
			}
		}
	}
	close(fd);
	if (strRet.length() > 0) {
		strRet = strRet.substr(1, strRet.length());
	}

	return strRet;
}

inline long tcpSocket::BaseConnect(string strAddress, unsigned long uiPort, bool bBlock) {
	m_strAddress = strAddress;
	m_uiPort = uiPort;

	long iRet = -1, iFlag = 1;
	struct sockaddr_in dest;
	hostent* hent = NULL;
	if ((m_iSocket = socket(AF_INET, SOCK_STREAM, 0)) >= 0) {
		bzero(&dest, sizeof(dest));
		dest.sin_family = AF_INET;
		dest.sin_port = htons(uiPort);

		dest.sin_addr.s_addr = inet_addr((const char*)strAddress.c_str());
		if (dest.sin_addr.s_addr == INADDR_NONE) {
			if ((hent = gethostbyname((const char*)strAddress.c_str())) != NULL) {
				dest.sin_family = hent->h_addrtype;
				memcpy((char*)&dest.sin_addr, hent->h_addr, hent->h_length);
			} else {
				showLog("DrServiceJni:tcpSocket", "BaseConnect_GETHOSTBYNAME_ERROR");
				showLog("DrServiceJni:tcpSocket", "BaseConnect_GETHOSTBYNAME_ERROR host=%s:%s\n",
						(const char*)strAddress.c_str(), hstrerror(h_errno));
				goto EXIT_ERROR_TCP;
			}
		}

		setsockopt(m_iSocket, IPPROTO_TCP, TCP_NODELAY, (const char *)&iFlag, sizeof(iFlag));
		iFlag = 1500;
		setsockopt(m_iSocket, SOL_SOCKET, SO_RCVBUF, (const char *)&iFlag, sizeof(iFlag));
		iFlag = 1;
		setsockopt(m_iSocket, SOL_SOCKET, SO_REUSEADDR, (const char *)&iFlag, sizeof(iFlag));

		if (connect(m_iSocket, (struct sockaddr *)&dest, sizeof(dest)) != -1) {
			// 设置非阻塞
			if (!bBlock) {
				if ((iFlag = fcntl(m_iSocket, F_GETFL, 0)) != -1) {
					if (fcntl(m_iSocket, F_SETFL, iFlag | O_NONBLOCK) != -1) {
						iRet = 1;
					}
					else {
						showLog("DrServiceJni:tcpSocket", "BaseConnect_SET_NONBLOCKING_ERROR");
					}
				}
				else {
					showLog("DrServiceJni:tcpSocket", "BaseConnect_GET_NONBLOCKING_ERROR");
				}
			} else {
				iRet = 1;
			}
			// 设置心跳检测
		    /*deal with the tcp keepalive
		      iKeepAlive = 1 ( check keepalive)
		      iKeepIdle = 600 (active keepalive after socket has idled for 10 minutes)
		      Keeplong = 60 (send keepalive every 1 minute after keepalive was actived)
		      iKeepCount = 3 (send keepalive 3 times before disconnect from peer)
		     */
		    long iKeepAlive = 1, iKeepIdle = 30, KeepInt = 30, iKeepCount = 2;
		    if (setsockopt(m_iSocket, SOL_SOCKET, SO_KEEPALIVE, (void*)&iKeepAlive, sizeof(iKeepAlive)) < 0) {
		    	iRet = -1;
		    	showLog("DrServiceJni:tcpSocket", "BaseConnect_SO_KEEPALIVE_ERROR");
		    	goto EXIT_ERROR_TCP;
		    }
		    if (setsockopt(m_iSocket, SOL_TCP, TCP_KEEPIDLE, (void*)&iKeepIdle, sizeof(iKeepIdle)) < 0) {
		    	iRet = -1;
		    	showLog("DrServiceJni:tcpSocket", "BaseConnect_TCP_KEEPIDLE_ERROR");
		    	goto EXIT_ERROR_TCP;
		    }
		    if (setsockopt(m_iSocket, SOL_TCP, TCP_KEEPINTVL, (void *)&KeepInt, sizeof(KeepInt)) < 0) {
		    	iRet = -1;
		    	showLog("DrServiceJni:tcpSocket", "BaseConnect_TCP_KEEPINTVL_ERROR");
		    	goto EXIT_ERROR_TCP;
		    }
		    if (setsockopt(m_iSocket, SOL_TCP, TCP_KEEPCNT, (void *)&iKeepCount, sizeof(iKeepCount)) < 0) {
		    	iRet = -1;
		    	showLog("DrServiceJni:tcpSocket", "BaseConnect_TCP_KEEPCNT_ERROR");
		    	goto EXIT_ERROR_TCP;
		    }
		}
		//else
			//showLog("DrServiceJni:tcpSocket", "BaseConnect_connect:error");

	}

EXIT_ERROR_TCP:
	if (iRet != 1) {
		Close();
	}
	return iRet;
}

inline unsigned long tcpSocket::GetTick()
{
	timeval tNow;
	gettimeofday(&tNow, NULL);
	if (tNow.tv_usec != 0){
		return (tNow.tv_sec * 1000 + (unsigned int)(tNow.tv_usec / 1000));
	}else{
		return (tNow.tv_sec * 1000);
	}
}

inline bool tcpSocket::isTimeout(unsigned long uiStart, unsigned long uiTimeout)
{
	unsigned long uiCurr = GetTick();
	unsigned long uiDiff;

	if (uiTimeout == 0)
		return false;
	if (uiCurr >= uiStart) {
		uiDiff = uiCurr - uiStart;
	}else{
		uiDiff = (0xFFFFFFFF - uiStart) + uiCurr;
	}
	if(uiDiff >= uiTimeout){
		return true;
	}
	return false;
}

/*****************************************************************/
sslSocket::sslSocket() {
	m_pCTX = NULL;
	m_pSSL = NULL;
}

sslSocket::~sslSocket() {
	Close();
}

long sslSocket::Connect(string strAddress, unsigned long uiPort) {
	long iRet = -1, iFlag = -1;

	SSL_library_init();
	OpenSSL_add_all_algorithms();
	SSL_load_error_strings();
	m_pCTX = SSL_CTX_new(SSLv23_client_method());
	if (m_pCTX) {
		if (BaseConnect(strAddress, uiPort, true) == 1) {
			m_pSSL = SSL_new(m_pCTX);
			if (m_pSSL) {
				SSL_set_fd(m_pSSL, m_iSocket);
				if (SSL_connect(m_pSSL) != -1) {
					if ((iFlag = fcntl(m_iSocket, F_GETFL, 0)) != -1) {
						if (fcntl(m_iSocket, F_SETFL, iFlag | O_NONBLOCK) != -1) {
							iRet = 1;
						}
					}
				}
			}
		}
	}

EXIT_ERROR_SSL:
	if (iRet != 1) {
		Close();
	}
	return iRet;
}

long sslSocket::SendData(char* pBuffer, unsigned long uiSendLen, unsigned long uiTimeout) {
	unsigned long uiBegin = GetTick();
	long iOrgLen = uiSendLen;
	long iRet = -1, iRetS = -1;
	long iSendedLen = 0;
	fd_set wset;

	while (true) {
		struct timeval tout;
		tout.tv_sec = uiTimeout / 1000;
		tout.tv_usec = (uiTimeout % 1000) * 1000;
		FD_ZERO(&wset);
		FD_SET(m_iSocket, &wset);
		iRetS = select(m_iSocket + 1, NULL, &wset, NULL, &tout);
		if (iRetS == -1) {
			return -1;
		} else if (iRetS == 0) {
			return iSendedLen;
		}
		iRet = SSL_write(m_pSSL, pBuffer, uiSendLen);
		if (iRet == -1 || (iRetS == 1 && iRet == 0)) {
			usleep(100);
			if (EWOULDBLOCK == errno) {
				if (isTimeout(uiBegin, uiTimeout)) {
					return iSendedLen;
				}
				continue;
			} else {
				if (iSendedLen == 0){
					return -1;
				}
				return iSendedLen;
			}
		} else if (iRet == 0) {
			return iSendedLen;
		}
		pBuffer += iRet;
		iSendedLen += iRet;
		uiSendLen -= iRet;
		if (iSendedLen >= iOrgLen) {
			return iSendedLen;
		}
		if (isTimeout(uiBegin, uiTimeout)) {
			return iSendedLen;
		}
	}
	return 0;
}

long sslSocket::RecvData(char* pBuffer, unsigned long uiRecvLen, bool bRecvAll, unsigned long uiTimeout) {
	unsigned long uiBegin = GetTick();
	long iOrgLen = uiRecvLen;
	long iRet = -1, iRetS = -1;
	long iRecvedLen = 0;
	fd_set rset;

	while (true) {
		timeval tout;
		tout.tv_sec = uiTimeout / 1000;
		tout.tv_usec = (uiTimeout % 1000) * 1000;
		FD_ZERO(&rset);
		FD_SET(m_iSocket, &rset);
		iRetS = select(m_iSocket + 1, &rset, NULL, NULL, &tout);
		if (iRetS == -1) {
			return -1;
		} else if (iRetS == 0) {
			return iRecvedLen;
		}
		iRet = SSL_read(m_pSSL, pBuffer, uiRecvLen);
		if (iRet==-1 || (iRetS == 1 && iRet == 0)) {
			usleep(100);
			if (EWOULDBLOCK == errno){
				if (isTimeout(uiBegin, uiTimeout)){
					return iRecvedLen;
				}
				continue;
			}else{
				if (iRecvedLen == 0){
					return -1;
				}
				return iRecvedLen;
			}
		} else if (iRet == 0) {
			return iRecvedLen;
		}
		pBuffer += iRet;
		iRecvedLen += iRet;
		uiRecvLen -= iRet;
		if (iRecvedLen >= iOrgLen) {
			return iRecvedLen;
		}
		if (!bRecvAll) {
			return iRecvedLen;
		}
		if (isTimeout(uiBegin, uiTimeout)){
			return iRecvedLen;
		}
	}
	return 0;
}

long sslSocket::Close() {
	if (m_pSSL) {
		SSL_shutdown(m_pSSL);
		SSL_free(m_pSSL);
		m_pSSL = NULL;
	}
	if (m_pCTX) {
		SSL_CTX_free(m_pCTX);
		m_pCTX = NULL;
	}
	tcpSocket::Close();
	return 1;
}
